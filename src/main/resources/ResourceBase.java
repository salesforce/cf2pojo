package com.salesforce.cf2pojo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.List;

/**
 * abstract base class for all AWS resource types.
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "Type")
public abstract class ResourceBase<T> {

    @JsonProperty("Properties")
    protected T properties;

    @JsonProperty("DependsOn")
    private List<String> dependsOn;

    @JsonProperty("Condition")
    private String condition;

    // TODO: Add CreationPolicy, DeletionPolicy, UpdatePolicy, and Metadata

    public T getProperties() { return properties; }

    public void setProperties(T properties) {
        this.properties = properties;
    }

    public List<String> getDependsOn() {
        return dependsOn;
    }

    public void setDependsOn(List<String> dependsOn) {
        this.dependsOn = dependsOn;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
}
