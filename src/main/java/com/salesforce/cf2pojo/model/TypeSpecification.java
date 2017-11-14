/*
 * Copyright (c) 2017, salesforce.com, inc.
 * All rights reserved.
 * Licensed under the BSD 3-Clause license.
 * For full license text, see LICENSE.txt file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.cf2pojo.model;

import java.util.Map;

public interface TypeSpecification {
    String getDocumentation();
    Map<String, PropertySpecification> getProperties();
}
