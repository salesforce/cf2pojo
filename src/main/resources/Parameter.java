package com.salesforce.cf2pojo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Parameter {

    @JsonProperty("AllowedPattern")
    private String allowedPattern;

    @JsonProperty("AllowedValues")
    private List<String> allowedValues;

    @JsonProperty("ConstraintDescription")
    private String constraintDescription;

    @JsonProperty("Default")
    private String defaultValue;

    @JsonProperty("Description")
    private String description;

    @JsonProperty("MaxLength")
    private Integer maxLength;

    @JsonProperty("MaxValue")
    private Double maxValue;

    @JsonProperty("MinLength")
    private Integer minLength;

    @JsonProperty("MinValue")
    private Double minValue;

    @JsonProperty("NoEcho")
    private Boolean noEcho;

    @JsonProperty("Type")
    private String type;

    public String getAllowedPattern() {
        return allowedPattern;
    }

    public void setAllowedPattern(String allowedPattern) {
        this.allowedPattern = allowedPattern;
    }

    public List<String> getAllowedValues() {
        return allowedValues;
    }

    public void setAllowedValues(List<String> allowedValues) {
        this.allowedValues = allowedValues;
    }

    public String getConstraintDescription() {
        return constraintDescription;
    }

    public void setConstraintDescription(String constraintDescription) {
        this.constraintDescription = constraintDescription;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }

    public Double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Double maxValue) {
        this.maxValue = maxValue;
    }

    public Integer getMinLength() {
        return minLength;
    }

    public void setMinLength(Integer minLength) {
        this.minLength = minLength;
    }

    public Double getMinValue() {
        return minValue;
    }

    public void setMinValue(Double minValue) {
        this.minValue = minValue;
    }

    public Boolean getNoEcho() {
        return noEcho;
    }

    public void setNoEcho(Boolean noEcho) {
        this.noEcho = noEcho;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
