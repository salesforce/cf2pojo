/*
 * Copyright (c) 2017, salesforce.com, inc.
 * All rights reserved.
 * Licensed under the BSD 3-Clause license.
 * For full license text, see LICENSE.txt file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.cf2pojo.tasks;

import org.junit.Assert;
import org.junit.Test;

public class HelperFunctionTests {
    @Test
    public void testUncapitalize() {
        Assert.assertEquals("a", GenerateTask.uncapitalize("a"));
        Assert.assertEquals("a", GenerateTask.uncapitalize("A"));
        Assert.assertEquals("abc", GenerateTask.uncapitalize("Abc"));
        Assert.assertEquals("aBC", GenerateTask.uncapitalize("ABC"));
        Assert.assertEquals("123", GenerateTask.uncapitalize("123"));
        Assert.assertEquals("1ABC", GenerateTask.uncapitalize("1ABC"));
        Assert.assertEquals("aBC123", GenerateTask.uncapitalize("ABC123"));
        Assert.assertEquals("aBC_123", GenerateTask.uncapitalize("ABC_123"));
        Assert.assertEquals("123ABC45", GenerateTask.uncapitalize("123ABC45"));
        Assert.assertEquals(".A123BC45", GenerateTask.uncapitalize(".A123BC45"));
        Assert.assertEquals("_A123BC45", GenerateTask.uncapitalize("_A123BC45"));
    }

    @Test
    public void testSubstringAfterFinalPeriod() {
        Assert.assertEquals("bar", GenerateTask.substringAfterFinalPeriod("foo.bar"));
        Assert.assertEquals("", GenerateTask.substringAfterFinalPeriod("foo."));
        Assert.assertEquals("bar", GenerateTask.substringAfterFinalPeriod(".bar"));
        Assert.assertEquals("baz", GenerateTask.substringAfterFinalPeriod("foo.bar.baz"));
        Assert.assertEquals("foo", GenerateTask.substringAfterFinalPeriod("foo"));
        Assert.assertEquals("", GenerateTask.substringAfterFinalPeriod("foo.bar."));
        Assert.assertEquals("", GenerateTask.substringAfterFinalPeriod(""));
        Assert.assertEquals("", GenerateTask.substringAfterFinalPeriod("."));
        Assert.assertEquals("foo", GenerateTask.substringAfterFinalPeriod(".foo"));
    }
}
