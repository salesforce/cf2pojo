package com.salesforce.cf2pojo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.salesforce.cf2pojo.model.ResourceBase;

import java.util.Map;

public class CloudFormationTemplate {
    @JsonProperty("AWSTemplateFormatVersion")
    private String awsTemplateFormatVersion;

    @JsonProperty("Description")
    private String description;

    @JsonProperty("Metadata")
    private Map<String, Object> metadata;

    @JsonProperty("Parameters")
    private Map<String, Parameter> parameters;

    @JsonProperty("Mappings")
    private Map<String, Object> mappings;

    @JsonProperty("Conditions")
    private Map<String, Object> conditions;

    @JsonProperty("Transform")
    private String transform;

    @JsonProperty("Resources")
    private Map<String, ResourceBase> resources;

    @JsonProperty("Outputs")
    private Map<String, Object> outputs;

    public String getAwsTemplateFormatVersion() {
        return awsTemplateFormatVersion;
    }

    public void setAwsTemplateFormatVersion(String awsTemplateFormatVersion) {
        this.awsTemplateFormatVersion = awsTemplateFormatVersion;
    }

    public Map<String, Object> getConditions() {
        return conditions;
    }

    public void setConditions(Map<String, Object> conditions) {
        this.conditions = conditions;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, Object> getMappings() {
        return mappings;
    }

    public void setMappings(Map<String, Object> mappings) {
        this.mappings = mappings;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public Map<String, Object> getOutputs() {
        return outputs;
    }

    public void setOutputs(Map<String, Object> outputs) {
        this.outputs = outputs;
    }

    public Map<String, Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Parameter> parameters) {
        this.parameters = parameters;
    }

    public Map<String, ResourceBase> getResources() {
        return resources;
    }

    public void setResources(Map<String, ResourceBase> resources) {
        this.resources = resources;
    }

    public String getTransform() {
        return transform;
    }

    public void setTransform(String transform) {
        this.transform = transform;
    }
}
