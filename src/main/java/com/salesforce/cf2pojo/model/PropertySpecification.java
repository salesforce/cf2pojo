/*
 * Copyright (c) 2017, salesforce.com, inc.
 * All rights reserved.
 * Licensed under the BSD 3-Clause license.
 * For full license text, see LICENSE.txt file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.cf2pojo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The specification for a single property of a resource or subproperty.  For example, BlockDeviceMappings is a
 * property of the AWS::EC2::Instance resource type, and Ebs is a property of the AWS::EC2::Instance.BlockDeviceMapping
 * subproperty type.
 */
public class PropertySpecification {
    @JsonProperty("Documentation")
    private String documentation;

    @JsonProperty("DuplicatesAllowed")
    private boolean duplicatesAllowed;

    @JsonProperty("ItemType")
    private String itemType;

    @JsonProperty("PrimitiveItemType")
    private String primitiveItemType;

    @JsonProperty("PrimitiveType")
    private String primitiveType;

    @JsonProperty("Required")
    private boolean required;

    @JsonProperty("Type")
    private String type;

    @JsonProperty("UpdateType")
    private String updateType;

    public String getDocumentation() {
        return documentation;
    }

    public PropertySpecification withDocumentation(String documentation) {
        this.documentation = documentation;
        return this;
    }

    public String getItemType() {
        return itemType;
    }

    public PropertySpecification withItemType(String itemType) {
        this.itemType = itemType;
        return this;
    }

    public String getPrimitiveItemType() {
        return primitiveItemType;
    }

    public PropertySpecification withPrimitiveItemType(String primitiveItemType) {
        this.primitiveItemType = primitiveItemType;
        return this;
    }

    public String getPrimitiveType() {
        return primitiveType;
    }

    public PropertySpecification withPrimitiveType(String primitiveType) {
        this.primitiveType = primitiveType;
        return this;
    }

    public boolean isRequired() {
        return required;
    }

    public PropertySpecification withRequired(boolean required) {
        this.required = required;
        return this;
    }

    public String getType() {
        return type;
    }

    public PropertySpecification withType(String type) {
        this.type = type;
        return this;
    }
}
