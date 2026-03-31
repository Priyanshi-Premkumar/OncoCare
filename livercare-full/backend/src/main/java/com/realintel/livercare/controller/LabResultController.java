package com.realintel.livercare.controller;

import com.realintel.livercare.dto.*;
import com.realintel.livercare.service.LabResultService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/labs")
@Tag(name = "Lab Results", description = "Lab ingestion - triggers drift analysis automatically")
public class LabResultController {

    private final LabResultService labResultService;

    public LabResultController(LabResultService labResultService) {
        this.labResultService = labResultService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Submit a lab result - triggers drift analysis + Bedrock narrative")
    public ApiResponse<LabResultResponse> ingest(@Valid @RequestBody LabResultRequest req) {
        return ApiResponse.ok("Lab result ingested and analysed", labResultService.ingest(req));
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Get all lab results for a patient")
    public ApiResponse<List<LabResultResponse>> getByPatient(@PathVariable Long patientId) {
        return ApiResponse.ok(labResultService.getByPatient(patientId));
    }
}
