package com.orchid241.bizorder.product;

public class DuplicateProductCodeException extends RuntimeException {
    public DuplicateProductCodeException() {
        super("이미 사용 중인 상품 코드입니다.");
    }
}
