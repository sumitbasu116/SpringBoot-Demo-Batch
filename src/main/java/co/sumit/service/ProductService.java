package co.sumit.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import co.sumit.entity.Product;
import co.sumit.repository.ProductRepository;
import jakarta.transaction.Transactional;

@Service
public class ProductService {

	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate ;
	
	@Value("${product.discount.update.topic}") 
	private String topicName;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private Executor executor;
	
	

	public String resetRecords() {
		productRepository.findAll().forEach(product -> {
			product.setOfferApplied(false);
			product.setDiscountPercentage(0);
			product.setPriceAfterDiscount(product.getPrice());
			productRepository.save(product);
		});

		return "Data Reset To Database";
	}

	@Transactional
	public void processProductIds(List<Long> productIds) {
		productIds.stream().forEach(this::fetchUpdateAndPublish);
	}
	
	@Transactional
	public void processProductIdsV2(List<Long> productIds) {
		productIds.parallelStream().forEach(this::fetchUpdateAndPublish);
	}
	
	@Transactional
	public void processProductIdsV3(List<Long> productIds) {
		List<List<Long>> batches=splitIntoBatches(productIds,50);
		List<CompletableFuture<Void>> futures = batches
                .stream()
                .map(
                        batch -> CompletableFuture.runAsync(() -> processProductIds(batch),executor))
                .collect(Collectors.toList());
		CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
	}

	private List<List<Long>> splitIntoBatches(List<Long> productIds, int batchSize) {
		int totalRecords = productIds.size();
		int numOfBatches=totalRecords/batchSize;
		List<List<Long>> batches = new ArrayList<>();
		for(int i=0;i<numOfBatches;i++) {
			int start = i * batchSize;
			int end = Math.min(totalRecords, ((i+1)*batchSize)-1);
			batches.add(productIds.subList(start, end));
		}
		return batches;
	}
	

	private void fetchUpdateAndPublish(Long productId) {
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new IllegalArgumentException("Product ID does not exist in the system"));
		updateDiscountedPrice(product);
		
		productRepository.save(product);
		
		publishProductEvent(product);
	}

	private void publishProductEvent(Product product) {
		String productJson = null;
		try {
			productJson = objectMapper.writeValueAsString(product);
		}catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert product to JSON", e);
        }
		kafkaTemplate.send(topicName,productJson);
	}

	private void updateDiscountedPrice(Product product) {
		double price = product.getPrice();
		int discountPercentage = (price >= 1000) ? 10 : (price > 500 ? 5 : 0);
		double priceAfterDiscount = price - (price * discountPercentage / 100);
		if (discountPercentage > 0) {
			product.setOfferApplied(true);
		}
		product.setDiscountPercentage(discountPercentage);
		product.setPriceAfterDiscount(priceAfterDiscount);
	}

}
