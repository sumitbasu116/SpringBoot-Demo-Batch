package co.sumit.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.sumit.service.ProductService;

@RestController
@RequestMapping("/api/products")
public class ProductController {

	@Autowired
	private ProductService productService;
	
	@PostMapping("/reset")
	public ResponseEntity<String> resetProductRecords(){
		String response = productService.resetRecords();
		
		return ResponseEntity.ok(response);
	}
	@PostMapping("/process")
	public ResponseEntity<String> processProductIds(@RequestBody List<Long> productIds){
		productService.processProductIds(productIds);
		
		return ResponseEntity.ok("Products processed and events published.");
	}
	@PostMapping("/v2.0/process")
	public ResponseEntity<String> processProductIdsV2(@RequestBody List<Long> productIds){
		productService.processProductIdsV2(productIds);
		
		return ResponseEntity.ok("Products processed and events published.");
	}
	@PostMapping("/v3.0/process")
	public ResponseEntity<String> processProductIdsV3(@RequestBody List<Long> productIds){
		productService.processProductIdsV3(productIds);
		
		return ResponseEntity.ok("Products processed and events published.");
	}
}
