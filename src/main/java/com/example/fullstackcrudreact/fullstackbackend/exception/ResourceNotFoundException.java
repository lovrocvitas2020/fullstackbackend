package com.example.fullstackcrudreact.fullstackbackend.exception;

public class ResourceNotFoundException  extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
