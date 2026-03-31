package com.realintel.livercare.dto;

public class BaselineStats {
    private Double altMean; private Double altStd;
    private Double astMean; private Double astStd;
    private Double bilirubinMean; private Double bilirubinStd;
    private Double inrMean; private Double inrStd;
    private Double albuminMean; private Double albuminStd;
    private Integer sampleSize;

    public Double getAltMean() { return altMean; } public void setAltMean(Double v) { altMean = v; }
    public Double getAltStd() { return altStd; } public void setAltStd(Double v) { altStd = v; }
    public Double getAstMean() { return astMean; } public void setAstMean(Double v) { astMean = v; }
    public Double getAstStd() { return astStd; } public void setAstStd(Double v) { astStd = v; }
    public Double getBilirubinMean() { return bilirubinMean; } public void setBilirubinMean(Double v) { bilirubinMean = v; }
    public Double getBilirubinStd() { return bilirubinStd; } public void setBilirubinStd(Double v) { bilirubinStd = v; }
    public Double getInrMean() { return inrMean; } public void setInrMean(Double v) { inrMean = v; }
    public Double getInrStd() { return inrStd; } public void setInrStd(Double v) { inrStd = v; }
    public Double getAlbuminMean() { return albuminMean; } public void setAlbuminMean(Double v) { albuminMean = v; }
    public Double getAlbuminStd() { return albuminStd; } public void setAlbuminStd(Double v) { albuminStd = v; }
    public Integer getSampleSize() { return sampleSize; } public void setSampleSize(Integer v) { sampleSize = v; }
}
