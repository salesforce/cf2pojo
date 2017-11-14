/*
 * Copyright (c) 2017, salesforce.com, inc.
 * All rights reserved.
 * Licensed under the BSD 3-Clause license.
 * For full license text, see LICENSE.txt file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.cf2pojo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * The specification for a resource type, such as AWS::EC2::Instance.  Each CloudFormation specification file should
 * contain exactly one of these, in its ResourceType section.
 */
public class ResourceTypeSpecification implements TypeSpecification {
    @JsonProperty("AdditionalProperties")
    private Boolean additionalProperties;

    @JsonProperty("Attributes")
    private Map<String, ResourceSpecificationAttribute> attributes;

    @JsonProperty("Documentation")
    private String documentation;

    @JsonProperty("Properties")
    private Map<String, PropertySpecification> properties;

    public String getDocumentation() {
        return documentation;
    }

    public ResourceTypeSpecification withDocumentation(String documentation) {
        this.documentation = documentation;
        return this;
    }

    public Map<String, PropertySpecification> getProperties() {
        return properties;
    }

    public ResourceTypeSpecification withProperties(Map<String, PropertySpecification> properties) {
        this.properties = properties;
        return this;
    }
}
