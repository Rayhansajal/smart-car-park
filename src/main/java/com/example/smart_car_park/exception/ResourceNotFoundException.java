package com.example.smart_car_park.exception;

public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(String resource, String field, Object value) {
        super(String.format("%s not found with %s: '%s'", resource, field, value));
    }
}
