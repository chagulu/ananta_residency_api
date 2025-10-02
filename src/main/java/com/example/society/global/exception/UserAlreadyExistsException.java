
package com.example.society.global.exception; // Adjust package based on your project structure

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}