package com.orchid241.bizorder.product;

import java.sql.SQLException;
import java.util.Optional;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTests {
    @Mock
    private ProductRepository repository;
    private ProductService service;

    @BeforeEach
    void setUp() {
        service = new ProductService(repository);
    }

    @Test
    void createNormalizesCodeAndDefaultsToActive() {
        when(repository.saveAndFlush(any())).thenAnswer(invocation -> invocation.getArgument(0));
        var result = service.create(new CreateProductRequest(" biz_wifi ", " 가상 와이파이 ",
                ProductCategory.WIFI, null));
        assertThat(result.code()).isEqualTo("BIZ_WIFI");
        assertThat(result.name()).isEqualTo("가상 와이파이");
        assertThat(result.active()).isTrue();
        verify(repository).existsByCode("BIZ_WIFI");
    }

    @Test
    void rejectsDuplicateBeforeSaving() {
        when(repository.existsByCode("BIZ_WIFI")).thenReturn(true);
        assertThatThrownBy(() -> service.create(new CreateProductRequest("biz_wifi", "가상 상품",
                ProductCategory.WIFI, null))).isInstanceOf(DuplicateProductCodeException.class);
        verify(repository, never()).saveAndFlush(any());
    }

    @Test
    void convertsUniqueConstraintRaceToDuplicateError() {
        var violation = new ConstraintViolationException("duplicate", new SQLException("duplicate", "23505"),
                "uk_products_code");
        when(repository.saveAndFlush(any())).thenThrow(new DataIntegrityViolationException("duplicate", violation));
        assertThatThrownBy(() -> service.create(new CreateProductRequest("BIZ_WIFI", "가상 상품",
                ProductCategory.WIFI, null))).isInstanceOf(DuplicateProductCodeException.class);
    }

    @Test
    void doesNotMisreportOtherDatabaseErrorsAsDuplicate() {
        var failure = new DataIntegrityViolationException("other constraint");
        when(repository.saveAndFlush(any())).thenThrow(failure);
        assertThatThrownBy(() -> service.create(new CreateProductRequest("BIZ_WIFI", "가상 상품",
                ProductCategory.WIFI, null))).isSameAs(failure);
    }

    @Test
    void missingProductThrowsNotFound() {
        assertThatThrownBy(() -> service.findById(1L)).isInstanceOf(ProductNotFoundException.class);
        assertThatThrownBy(() -> service.update(1L,
                new UpdateProductRequest("상품", ProductCategory.ETC, null, true)))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void updateChangesMutableFieldsAndPreservesCode() {
        var product = new Product("BIZ_WIFI", "상품", ProductCategory.WIFI, "설명");
        when(repository.findById(1L)).thenReturn(Optional.of(product));
        var result = service.update(1L, new UpdateProductRequest(" 수정 상품 ", ProductCategory.ETC, null, false));
        assertThat(result.code()).isEqualTo("BIZ_WIFI");
        assertThat(result.name()).isEqualTo("수정 상품");
        assertThat(result.description()).isNull();
        assertThat(result.category()).isEqualTo(ProductCategory.ETC);
        assertThat(result.active()).isFalse();
    }

    @Test
    void entityRejectsInvalidStateWithoutHttpValidation() {
        assertThatThrownBy(() -> new Product(" ", "상품", ProductCategory.ETC, null))
                .isInstanceOf(IllegalArgumentException.class);
        var product = new Product("CODE", "상품", ProductCategory.ETC, null);
        assertThatThrownBy(() -> product.update(" ", ProductCategory.ETC, null, false))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(product.getName()).isEqualTo("상품");
        assertThat(product.isActive()).isTrue();
    }
}
