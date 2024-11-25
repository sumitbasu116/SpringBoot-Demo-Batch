package co.sumit.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController {

	private final ProductService productService;
	@PostMapping("/reset")
	public ResponseEntity<String> resetProductRecords(){
		String response = productService.resetRecords();
		
		return ResponseEntity.ok(response);
	}
}
