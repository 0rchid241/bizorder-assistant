package com.orchid241.bizorder.product;

import java.util.List;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ProductService {
    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ProductResponse create(CreateProductRequest request) {
        Product product = new Product(request.code(), request.name(), request.category(), request.description());
        if (repository.existsByCode(product.getCode())) {
            throw new DuplicateProductCodeException();
        }
        try {
            return ProductResponse.from(repository.saveAndFlush(product));
        } catch (DataIntegrityViolationException exception) {
            // 동시 등록은 사전 조회만으로 막을 수 없으므로 DB UNIQUE 위반도 409로 변환한다.
            for (Throwable cause = exception; cause != null; cause = cause.getCause()) {
                if (cause instanceof ConstraintViolationException violation
                        && "uk_products_code".equals(violation.getConstraintName())) {
                    throw new DuplicateProductCodeException();
                }
            }
            throw exception;
        }
    }

    public List<ProductResponse> findAll() {
        return repository.findAll(Sort.by("id")).stream().map(ProductResponse::from).toList();
    }

    public ProductResponse findById(long id) {
        return ProductResponse.from(requireProduct(id));
    }

    @Transactional
    public ProductResponse update(long id, UpdateProductRequest request) {
        Product product = requireProduct(id);
        product.update(request.name(), request.category(), request.description(), request.active());
        return ProductResponse.from(product);
    }

    private Product requireProduct(long id) {
        return repository.findById(id).orElseThrow(ProductNotFoundException::new);
    }
}
