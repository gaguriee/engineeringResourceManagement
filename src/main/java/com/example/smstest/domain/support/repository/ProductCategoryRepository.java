package com.example.smstest.domain.support.repository;

import com.example.smstest.domain.support.entity.Product;
import com.example.smstest.domain.support.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
    // Add custom query methods as needed
}
