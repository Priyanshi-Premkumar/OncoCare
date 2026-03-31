package com.realintel.livercare.controller;

import com.realintel.livercare.dto.AlertResponse;
import com.realintel.livercare.dto.AlertResolveRequest;
import com.realintel.livercare.dto.ApiResponse;
import com.realintel.livercare.service.DriftAlertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alerts")
@Tag(name = "Drift Alerts", description = "Clinical drift alerts with Bedrock narratives")
public class AlertController {

    private final DriftAlertService alertService;

    public AlertController(DriftAlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping
    @Operation(summary = "All unresolved alerts ordered by drift score")
    public ApiResponse<List<AlertResponse>> getUnresolved() {
        return ApiResponse.ok(alertService.getUnresolved());
    }

    @PatchMapping("/{id}/resolve")
    @Operation(summary = "Resolve an alert with clinician notes")
    public ApiResponse<Void> resolve(
        @PathVariable Long id,
        @Valid @RequestBody AlertResolveRequest req
    ) {
        alertService.resolve(id, req);
        return ApiResponse.ok("Alert resolved", null);
    }
}
