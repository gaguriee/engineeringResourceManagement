package com.example.smstest.domain.support.repository;

import com.example.smstest.domain.support.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
