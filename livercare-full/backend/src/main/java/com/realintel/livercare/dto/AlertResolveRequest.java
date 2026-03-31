package com.realintel.livercare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AlertResolveRequest {
    @NotBlank private String resolvedBy;
    @Size(max = 500) private String resolutionNotes;

    public String getResolvedBy() { return resolvedBy; }
    public void setResolvedBy(String v) { this.resolvedBy = v; }
    public String getResolutionNotes() { return resolutionNotes; }
    public void setResolutionNotes(String v) { this.resolutionNotes = v; }
}
