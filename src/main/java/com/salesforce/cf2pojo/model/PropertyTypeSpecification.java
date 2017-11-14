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
 * The specification for a subproperty type, such as AWS::EC2::Instance.BlockDeviceMapping.  Each CloudFormation
 * specification file contains zero or more of these, in its PropertyTypes section.
 */
public class PropertyTypeSpecification implements TypeSpecification {
    @JsonProperty("Documentation")
    private String documentation;

    @JsonProperty("Properties")
    private Map<String, PropertySpecification> properties;

    @Override
    public String getDocumentation() {
        return documentation;
    }

    public PropertyTypeSpecification withDocumentation(String documentation) {
        this.documentation = documentation;
        return this;
    }

    @Override
    public Map<String, PropertySpecification> getProperties() {
        return properties;
    }

    public PropertyTypeSpecification withProperties(Map<String, PropertySpecification> properties) {
        this.properties = properties;
        return this;
    }
}
