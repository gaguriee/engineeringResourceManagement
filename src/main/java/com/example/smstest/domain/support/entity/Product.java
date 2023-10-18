package com.example.smstest.domain.support.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "제품_id")
    private Long id;

    @Column(name = "제품명", nullable = false)
    private String name;

    @Column(name = "참조")
    private String description;

    @ManyToOne
    @JoinColumn(name = "대분류_제품_id")
    private ProductCategory 대분류;

    @Override
    public String toString() {
        return "Product{" +
                "Id=" + id +
                ", Name='" + name + '\'' +
                '}';
    }

}
