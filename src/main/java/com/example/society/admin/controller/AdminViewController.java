package com.example.society.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminViewController {

    @GetMapping("/admin/login")
    public String loginPage() {
        return "admin/login";
    }

    @GetMapping("/admin/dashboard")
    public String dashboardPage() {
        return "admin/dashboard";
    }

    @GetMapping("/admin/visitors")
    public String visitorListingPage() {
        return "admin/visitor-listing";
    }

    @GetMapping("/admin/residences")
    public String showAdminResidencesPage() {
        return "admin/residence-list";
    }

    // --- New method to load the residence registration page ---
    @GetMapping("/admin/residences/register") // This maps the URL for the registration page
    public String showRegisterResidencePage() {
        return "admin/residence-register"; // This returns the Thymeleaf view name
    }
}