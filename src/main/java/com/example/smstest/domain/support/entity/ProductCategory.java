package com.example.smstest.domain.support.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Table(name = "product_대분류")
public class ProductCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "제품_id")
    private Long id;

    @Column(name = "제품명", nullable = false)
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "대분류", fetch = FetchType.EAGER) // 즉시 로딩으로 변경
    private List<Product> products;

    /**
     * 순환 참조 문제로 만든 별도의 toString 메소드
     */
    public String toString() {
        return "ProductCategory{" +
                "Id=" + id +
                ", Name='" + name + '\'' +
                '}';
    }

}
