package co.sumit.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import co.sumit.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long>{

}
