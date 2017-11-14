/*
 * Copyright (c) 2017, salesforce.com, inc.
 * All rights reserved.
 * Licensed under the BSD 3-Clause license.
 * For full license text, see LICENSE.txt file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.cf2pojo.tasks;

import com.salesforce.cf2pojo.exceptions.PropertyClassInferenceException;
import com.salesforce.cf2pojo.model.PropertySpecification;
import org.junit.Assert;
import org.junit.Test;

public class GetPropertyClassNameTests {
    @Test
    public void testGetPropertyClassNameForListsAndMaps() {
        final String propertyName = "TestProperty";
        PropertySpecification propertySpec;

        propertySpec = new PropertySpecification().withType("List").withItemType("BlockMapping");
        Assert.assertEquals("List<BlockMapping>", GenerateTask.getPropertyClassName(propertySpec, propertyName));

        propertySpec = new PropertySpecification().withType("Map").withItemType("BlockMapping");
        Assert.assertEquals("Map<String, BlockMapping>", GenerateTask.getPropertyClassName(propertySpec, propertyName));

        propertySpec = new PropertySpecification().withType("List").withPrimitiveItemType("Integer");
        Assert.assertEquals("List<Integer>", GenerateTask.getPropertyClassName(propertySpec, propertyName));

        propertySpec = new PropertySpecification().withType("Map").withPrimitiveItemType("Integer");
        Assert.assertEquals("Map<String, Integer>", GenerateTask.getPropertyClassName(propertySpec, propertyName));

        propertySpec = new PropertySpecification().withType("List").withPrimitiveItemType("Json");
        Assert.assertEquals("List<Object>", GenerateTask.getPropertyClassName(propertySpec, propertyName));

        propertySpec = new PropertySpecification().withType("Map").withPrimitiveItemType("Json");
        Assert.assertEquals("Map<String, Object>", GenerateTask.getPropertyClassName(propertySpec, propertyName));

        propertySpec = new PropertySpecification().withType("BlockMapping");
        Assert.assertEquals("BlockMapping", GenerateTask.getPropertyClassName(propertySpec, propertyName));

        propertySpec = new PropertySpecification().withType("List").withItemType("BlockMapping");
        Assert.assertEquals("List<BlockMapping>", GenerateTask.getPropertyClassName(propertySpec, propertyName));

        propertySpec = new PropertySpecification().withPrimitiveType("Integer").withRequired(false);
        Assert.assertEquals("Integer", GenerateTask.getPropertyClassName(propertySpec, propertyName));

        propertySpec = new PropertySpecification().withPrimitiveType("Integer").withRequired(true);
        Assert.assertEquals("int", GenerateTask.getPropertyClassName(propertySpec, propertyName));

        propertySpec = new PropertySpecification().withPrimitiveType("Double").withRequired(false);
        Assert.assertEquals("Double", GenerateTask.getPropertyClassName(propertySpec, propertyName));

        propertySpec = new PropertySpecification().withPrimitiveType("Double").withRequired(true);
        Assert.assertEquals("double", GenerateTask.getPropertyClassName(propertySpec, propertyName));

        propertySpec = new PropertySpecification().withPrimitiveType("Boolean").withRequired(false);
        Assert.assertEquals("Boolean", GenerateTask.getPropertyClassName(propertySpec, propertyName));

        propertySpec = new PropertySpecification().withPrimitiveType("Boolean").withRequired(true);
        Assert.assertEquals("boolean", GenerateTask.getPropertyClassName(propertySpec, propertyName));

        propertySpec = new PropertySpecification().withPrimitiveType("Json").withRequired(false);
        Assert.assertEquals("Object", GenerateTask.getPropertyClassName(propertySpec, propertyName));

        propertySpec = new PropertySpecification().withPrimitiveType("Json").withRequired(true);
        Assert.assertEquals("Object", GenerateTask.getPropertyClassName(propertySpec, propertyName));
    }

    @Test
    public void testGetPropertyClassNameForSimpleTypes() {
        final String propertyName = "TestProperty";
        PropertySpecification propertySpec;

        propertySpec = new PropertySpecification().withType("BlockMapping");
        Assert.assertEquals("BlockMapping", GenerateTask.getPropertyClassName(propertySpec, propertyName));
    }

    @Test
    public void testGetPropertyClassNameForPrimitiveTypes() {
        final String propertyName = "TestProperty";
        PropertySpecification propertySpec;

        propertySpec = new PropertySpecification().withPrimitiveType("Integer").withRequired(false);
        Assert.assertEquals("Integer", GenerateTask.getPropertyClassName(propertySpec, propertyName));

        propertySpec = new PropertySpecification().withPrimitiveType("Integer").withRequired(true);
        Assert.assertEquals("int", GenerateTask.getPropertyClassName(propertySpec, propertyName));

        propertySpec = new PropertySpecification().withPrimitiveType("Double").withRequired(false);
        Assert.assertEquals("Double", GenerateTask.getPropertyClassName(propertySpec, propertyName));

        propertySpec = new PropertySpecification().withPrimitiveType("Double").withRequired(true);
        Assert.assertEquals("double", GenerateTask.getPropertyClassName(propertySpec, propertyName));

        propertySpec = new PropertySpecification().withPrimitiveType("Boolean").withRequired(false);
        Assert.assertEquals("Boolean", GenerateTask.getPropertyClassName(propertySpec, propertyName));

        propertySpec = new PropertySpecification().withPrimitiveType("Boolean").withRequired(true);
        Assert.assertEquals("boolean", GenerateTask.getPropertyClassName(propertySpec, propertyName));

        propertySpec = new PropertySpecification().withPrimitiveType("Json").withRequired(false);
        Assert.assertEquals("Object", GenerateTask.getPropertyClassName(propertySpec, propertyName));

        propertySpec = new PropertySpecification().withPrimitiveType("Json").withRequired(true);
        Assert.assertEquals("Object", GenerateTask.getPropertyClassName(propertySpec, propertyName));

        propertySpec = new PropertySpecification().withPrimitiveType("Timestamp").withRequired(false);
        Assert.assertEquals("ValueType", GenerateTask.getPropertyClassName(propertySpec, propertyName));

        propertySpec = new PropertySpecification().withPrimitiveType("Timestamp").withRequired(true);
        Assert.assertEquals("ValueType", GenerateTask.getPropertyClassName(propertySpec, propertyName));

        propertySpec = new PropertySpecification().withPrimitiveType("Long").withRequired(false);
        Assert.assertEquals("Long", GenerateTask.getPropertyClassName(propertySpec, propertyName));

        propertySpec = new PropertySpecification().withPrimitiveType("Long").withRequired(true);
        Assert.assertEquals("long", GenerateTask.getPropertyClassName(propertySpec, propertyName));
    }

    @Test(expected = PropertyClassInferenceException.class)
    public void testGetPropertyClassNameForAbsentListType() {
        final String propertyName = "TestProperty";
        PropertySpecification propertySpec;

        propertySpec = new PropertySpecification().withType("List");
        GenerateTask.getPropertyClassName(propertySpec, propertyName);
    }

    @Test(expected = PropertyClassInferenceException.class)
    public void testGetPropertyClassNameForAbsentMapType() {
        final String propertyName = "TestProperty";
        PropertySpecification propertySpec;

        propertySpec = new PropertySpecification().withType("Map");
        GenerateTask.getPropertyClassName(propertySpec, propertyName);
    }

    @Test(expected = PropertyClassInferenceException.class)
    public void testGetPropertyClassNameForInvalidPrimitiveType() {
        final String propertyName = "TestProperty";
        PropertySpecification propertySpec;

        propertySpec = new PropertySpecification().withPrimitiveType("Foo");
        GenerateTask.getPropertyClassName(propertySpec, propertyName);
    }

    @Test(expected = PropertyClassInferenceException.class)
    public void testGetPropertyClassNameForInvalidPrimitiveItemType() {
        final String propertyName = "TestProperty";
        PropertySpecification propertySpec;

        propertySpec = new PropertySpecification().withType("List").withPrimitiveItemType("Foo");
        GenerateTask.getPropertyClassName(propertySpec, propertyName);
    }

    @Test(expected = PropertyClassInferenceException.class)
    public void testGetPropertyClassNameWhenEverythingIsNull() {
        final String propertyName = "TestProperty";
        PropertySpecification propertySpec;

        propertySpec = new PropertySpecification();
        GenerateTask.getPropertyClassName(propertySpec, propertyName);
    }
}
