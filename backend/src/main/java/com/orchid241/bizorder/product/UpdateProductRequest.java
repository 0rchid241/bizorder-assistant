package com.orchid241.bizorder.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// PUT: 수정 가능한 필드를 모두 교체한다. description 생략/null은 설명 삭제를 의미한다.
public record UpdateProductRequest(
        @NotBlank @Size(max = 100) String name,
        @NotNull ProductCategory category,
        @Size(max = 2000) String description,
        @NotNull Boolean active) {

    public UpdateProductRequest {
        name = Product.normalizeName(name);
    }
}
