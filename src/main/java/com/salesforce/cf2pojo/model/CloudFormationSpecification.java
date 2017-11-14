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
 * Represents a single CloudFormation specification file.  Each such file includes the definition of exactly one
 * CloudFormation resource type (e.g., AWS::EC2::Instance), along with the definitions of any subproperties used by that
 * resource type (e.g., AWS::EC2::Instance.BlockDeviceMapping).
 */
public class CloudFormationSpecification {
    @JsonProperty("PropertyTypes")
    private Map<String, PropertyTypeSpecification> propertyTypes;

    @JsonProperty("ResourceSpecificationVersion")
    private String resourceSpecificationVersion;

    @JsonProperty("ResourceType")
    private Map<String, ResourceTypeSpecification> resourceType;

    public Map<String, PropertyTypeSpecification> getPropertyTypes() {
        return propertyTypes;
    }

    public void setPropertyTypes(Map<String, PropertyTypeSpecification> propertyTypes) {
        this.propertyTypes = propertyTypes;
    }

    public String getResourceSpecificationVersion() {
        return resourceSpecificationVersion;
    }

    public void setResourceSpecificationVersion(String resourceSpecificationVersion) {
        this.resourceSpecificationVersion = resourceSpecificationVersion;
    }

    public Map<String, ResourceTypeSpecification> getResourceType() {
        return resourceType;
    }

    public void setResourceType(Map<String, ResourceTypeSpecification> resourceType) {
        this.resourceType = resourceType;
    }
}
