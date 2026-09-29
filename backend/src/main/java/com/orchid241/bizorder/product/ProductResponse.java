package com.orchid241.bizorder.product;

public record ProductResponse(Long id, String code, String name, ProductCategory category,
                              String description, boolean active) {

    static ProductResponse from(Product product) {
        return new ProductResponse(product.getId(), product.getCode(), product.getName(),
                product.getCategory(), product.getDescription(), product.isActive());
    }
}
