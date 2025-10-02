package com.example.society.controller; // adjust as needed

import com.example.society.jwt.JwtUtil; // correct this import if needed
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class JwtTestController {

    private final JwtUtil jwtUtil;

    public JwtTestController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/generate")
    public ResponseEntity<String> generateToken() {
        String token = jwtUtil.generateToken("9971530966");
        return ResponseEntity.ok(token);
    }
}
