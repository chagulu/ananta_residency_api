package com.example.society.guest.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public class ResponseHelper {

    public static ResponseEntity<Map<String, Object>> success(String message, Object data) {
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", message,
                "data", data
        ));
    }

    public static ResponseEntity<Map<String, Object>> badRequest(String msg) {
        return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", msg,
                "data", null
        ));
    }

    public static ResponseEntity<Map<String, Object>> unauthorized(String msg) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "success", false,
                "message", msg,
                "data", null
        ));
    }

    public static ResponseEntity<Map<String, Object>> forbidden(String msg) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                "success", false,
                "message", msg,
                "data", null
        ));
    }

    public static ResponseEntity<Map<String, Object>> notFound(String msg) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "success", false,
                "message", msg,
                "data", null
        ));
    }

    public static ResponseEntity<Map<String, Object>> serverError(String msg) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", msg,
                "data", null
        ));
    }
}
