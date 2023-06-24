package com.example.FirstSpringRest.util;

public class PersonNotCreatedException extends RuntimeException{
    public PersonNotCreatedException(String msg) {
        super(msg);
    }
}
