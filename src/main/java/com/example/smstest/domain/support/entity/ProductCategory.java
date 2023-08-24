package com.example.smstest.domain.support.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@Table(name = "product_대분류")
public class ProductCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "제품_id")
    private Long id;

    @Column(name = "제품명", nullable = false)
    private String name;

    @OneToMany(mappedBy = "대분류", fetch = FetchType.EAGER) // 즉시 로딩으로 변경
    private List<Product> products;
}
