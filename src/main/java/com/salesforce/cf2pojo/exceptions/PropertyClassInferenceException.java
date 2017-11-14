/*
 * Copyright (c) 2017, salesforce.com, inc.
 * All rights reserved.
 * Licensed under the BSD 3-Clause license.
 * For full license text, see LICENSE.txt file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.cf2pojo.exceptions;

public class PropertyClassInferenceException extends RuntimeException {
    public PropertyClassInferenceException(String message, String propertyName) {
        super(String.format("%s for property %s", message, propertyName));
    }
}
