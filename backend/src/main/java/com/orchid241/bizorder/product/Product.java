package com.orchid241.bizorder.product;

import java.util.Locale;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "products", uniqueConstraints = @UniqueConstraint(name = "uk_products_code", columnNames = "code"))
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 64, updatable = false)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private ProductCategory category;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private boolean active;

    protected Product() {
    }

    public Product(String code, String name, ProductCategory category, String description) {
        this.code = normalizeCode(code);
        if (this.code == null || !this.code.matches("[A-Z0-9_]{1,64}")) {
            throw new IllegalArgumentException("상품 코드는 영문 대문자, 숫자, 밑줄로 구성된 1~64자여야 합니다.");
        }
        update(name, category, description, true);
    }

    public void update(String name, ProductCategory category, String description, boolean active) {
        String normalizedName = normalizeName(name);
        if (normalizedName == null || normalizedName.isBlank() || normalizedName.length() > 100) {
            throw new IllegalArgumentException("상품명은 1~100자여야 합니다.");
        }
        if (category == null) {
            throw new IllegalArgumentException("상품 카테고리는 필수입니다.");
        }
        if (description != null && description.length() > 2000) {
            throw new IllegalArgumentException("상품 설명은 2000자 이하여야 합니다.");
        }
        this.name = normalizedName;
        this.category = category;
        this.description = description;
        this.active = active;
    }

    static String normalizeCode(String code) {
        return code == null ? null : code.strip().toUpperCase(Locale.ROOT);
    }

    static String normalizeName(String name) {
        return name == null ? null : name.strip();
    }

    public Long getId() { return id; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public ProductCategory getCategory() { return category; }
    public String getDescription() { return description; }
    public boolean isActive() { return active; }
}
