package com.orchid241.bizorder.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateProductRequest(
        @NotBlank @Size(max = 64) @Pattern(regexp = "[A-Z0-9_]+") String code,
        @NotBlank @Size(max = 100) String name,
        @NotNull ProductCategory category,
        @Size(max = 2000) String description) {

    public CreateProductRequest {
        code = Product.normalizeCode(code);
        name = Product.normalizeName(name);
    }
}
