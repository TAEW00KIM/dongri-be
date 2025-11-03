package com.hufs.dongri.global.exception;

// (RuntimeException을 상속)
public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}