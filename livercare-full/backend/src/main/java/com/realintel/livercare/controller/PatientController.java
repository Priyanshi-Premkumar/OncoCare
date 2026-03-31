package com.realintel.livercare.controller;

import com.realintel.livercare.dto.*;
import com.realintel.livercare.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/patients")
@Tag(name = "Patients", description = "Patient registration and management")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a new patient")
    public ApiResponse<PatientResponse> register(@Valid @RequestBody PatientRequest req) {
        return ApiResponse.ok("Patient registered", patientService.register(req));
    }

    @GetMapping
    @Operation(summary = "List all patients")
    public ApiResponse<List<PatientResponse>> listAll() {
        return ApiResponse.ok(patientService.getAll());
    }

    @GetMapping("/at-risk")
    @Operation(summary = "List patients with MODERATE or HIGH drift risk")
    public ApiResponse<List<PatientResponse>> atRisk() {
        return ApiResponse.ok(patientService.getAtRisk());
    }

    @GetMapping("/{code}")
    @Operation(summary = "Get patient by code")
    public ApiResponse<PatientResponse> getByCode(@PathVariable String code) {
        return ApiResponse.ok(patientService.getByCode(code));
    }

    @GetMapping("/{code}/dashboard")
    @Operation(summary = "Full patient dashboard")
    public ApiResponse<PatientDashboard> getDashboard(@PathVariable String code) {
        return ApiResponse.ok(patientService.getDashboard(code));
    }
}
