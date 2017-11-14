/*
 * Copyright (c) 2017, salesforce.com, inc.
 * All rights reserved.
 * Licensed under the BSD 3-Clause license.
 * For full license text, see LICENSE.txt file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.cf2pojo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResourceSpecificationAttribute {
    @JsonProperty("ItemType")
    private String itemType;

    @JsonProperty("PrimitiveItemType")
    private String primitiveItemType;

    @JsonProperty("PrimitiveType")
    private String primitiveType;

    @JsonProperty("Type")
    private String type;
}
