/*
 * Copyright (c) 2017, salesforce.com, inc.
 * All rights reserved.
 * Licensed under the BSD 3-Clause license.
 * For full license text, see LICENSE.txt file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.cf2pojo.tasks;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.salesforce.cf2pojo.model.PropertySpecification;
import com.salesforce.cf2pojo.model.ResourceTypeSpecification;
import com.salesforce.cf2pojo.model.TypeSpecification;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

public class GeneratePropertiesClassTests {

    private CompilationUnit testGeneratePropertiesClassFromTypeSpec(TypeSpecification typeSpecification) {
        final String resourceName = "AWS::FakeService::FakeResource";
        final String propertiesClassName = "PropertiesClass";
        final CompilationUnit compilationUnit = new CompilationUnit();

        ClassOrInterfaceDeclaration propertiesClass = GenerateTask.generatePropertiesClassFromTypeSpec(
            propertiesClassName, compilationUnit, resourceName, typeSpecification);

        Assert.assertEquals(propertiesClassName, propertiesClass.getNameAsString());
        Optional<Comment> propertiesClassComment = propertiesClass.getComment();
        Assert.assertTrue(propertiesClassComment.isPresent());
        Assert.assertThat(propertiesClassComment.get().getContent(), CoreMatchers.containsString(resourceName));

        for (Map.Entry<String, PropertySpecification> property : typeSpecification.getProperties().entrySet()) {
            final String propertyName = property.getKey();
            final PropertySpecification propertySpecification = property.getValue();
            final String annotationText = String.format("@JsonProperty(\"%s\")", propertyName);
            final String expectedType = GenerateTask.getPropertyClassName(propertySpecification, propertyName)
                .replace(" ", "");

            Optional<FieldDeclaration> field = propertiesClass.getFieldByName(GenerateTask.uncapitalize(propertyName));
            Assert.assertTrue("Field is missing for property " + propertyName, field.isPresent());
            Assert.assertEquals(expectedType, field.get().getVariables().get(0).getType().asString());
            Optional<AnnotationExpr> propertyAnnotation = field.get().getAnnotationByClass(JsonProperty.class);
            Assert.assertTrue(propertyAnnotation.isPresent());
            Assert.assertEquals(annotationText, propertyAnnotation.get().toString());
            Assert.assertEquals(1, field.get().getAnnotations().size());

            List<MethodDeclaration> getters = propertiesClass.getMethodsByName("get" + propertyName);
            Assert.assertEquals(1, getters.size());
            MethodDeclaration getter = getters.get(0);
            Assert.assertTrue(getter.getParameters().isEmpty());
            Assert.assertEquals(expectedType, getter.getType().asString());
            Optional<AnnotationExpr> getterAnnotation = getter.getAnnotationByClass(JsonProperty.class);
            Assert.assertTrue(getterAnnotation.isPresent());
            Assert.assertEquals(annotationText, getterAnnotation.get().toString());
            Assert.assertEquals(1, getter.getAnnotations().size());

            List<MethodDeclaration> setters = propertiesClass.getMethodsByName("set" + propertyName);
            Assert.assertEquals(1, setters.size());
            MethodDeclaration setter = setters.get(0);
            Assert.assertEquals(1, setter.getParameters().size());
            Assert.assertEquals(expectedType, setter.getParameter(0).getType().asString());
            Assert.assertEquals("void", setter.getType().asString());
            Optional<AnnotationExpr> setterAnnotation = setter.getAnnotationByClass(JsonProperty.class);
            Assert.assertTrue(setterAnnotation.isPresent());
            Assert.assertEquals(annotationText, setterAnnotation.get().toString());
            Assert.assertEquals(1, setter.getAnnotations().size());
        }

        return compilationUnit;
    }

    private Set<String> getImportNameSet(CompilationUnit compilationUnit) {
        return compilationUnit.getImports().stream().map(ImportDeclaration::getNameAsString)
            .collect(Collectors.toSet());
    }

    @Test
    public void testGeneratePropertiesClassFromTypeSpecWithOnePrimitiveProperty() {
        Map<String, PropertySpecification> properties = new HashMap<>();
        properties.put("Property0", new PropertySpecification().withPrimitiveType("Double").withRequired(true));

        TypeSpecification typeSpecification = new ResourceTypeSpecification().withProperties(properties);
        CompilationUnit compilationUnit = testGeneratePropertiesClassFromTypeSpec(typeSpecification);
        Set<String> importNames = getImportNameSet(compilationUnit);
        Assert.assertFalse(importNames.contains(List.class.getCanonicalName()));
        Assert.assertFalse(importNames.contains(Map.class.getCanonicalName()));
    }

    @Test
    public void testGeneratePropertiesClassFromTypeSpecWithTwoPrimitiveProperties() {
        Map<String, PropertySpecification> properties = new HashMap<>();
        properties.put("Property0", new PropertySpecification().withPrimitiveType("Double").withRequired(true));
        properties.put("Property1", new PropertySpecification().withPrimitiveType("Integer").withRequired(false));

        TypeSpecification typeSpecification = new ResourceTypeSpecification().withProperties(properties);
        CompilationUnit compilationUnit = testGeneratePropertiesClassFromTypeSpec(typeSpecification);
        Set<String> importNames = getImportNameSet(compilationUnit);
        Assert.assertFalse(importNames.contains(List.class.getCanonicalName()));
        Assert.assertFalse(importNames.contains(Map.class.getCanonicalName()));
    }

    @Test
    public void testGeneratePropertiesClassFromTypeSpecWithOneListProperty() {
        Map<String, PropertySpecification> properties = new HashMap<>();
        properties.put("Property0", new PropertySpecification().withType("List").withItemType("Subtype"));

        TypeSpecification typeSpecification = new ResourceTypeSpecification().withProperties(properties);
        CompilationUnit compilationUnit = testGeneratePropertiesClassFromTypeSpec(typeSpecification);
        Set<String> importNames = getImportNameSet(compilationUnit);
        Assert.assertTrue(importNames.contains(List.class.getCanonicalName()));
        Assert.assertFalse(importNames.contains(Map.class.getCanonicalName()));
    }

    @Test
    public void testGeneratePropertiesClassFromTypeSpecWithOneListPropertyAndOneMapProperty() {
        Map<String, PropertySpecification> properties = new HashMap<>();
        properties.put("Property0", new PropertySpecification().withType("List").withItemType("Subtype"));
        properties.put("Property1", new PropertySpecification().withType("Map").withPrimitiveItemType("Timestamp"));

        TypeSpecification typeSpecification = new ResourceTypeSpecification().withProperties(properties);
        CompilationUnit compilationUnit = testGeneratePropertiesClassFromTypeSpec(typeSpecification);
        Set<String> importNames = getImportNameSet(compilationUnit);
        Assert.assertTrue(importNames.contains(List.class.getCanonicalName()));
        Assert.assertTrue(importNames.contains(Map.class.getCanonicalName()));
    }

    @Test
    public void testGeneratePropertiesClassFromTypeSpecWithOneMapProperty() {
        Map<String, PropertySpecification> properties = new HashMap<>();
        properties.put("Property0", new PropertySpecification().withType("Map").withPrimitiveItemType("Json"));

        TypeSpecification typeSpecification = new ResourceTypeSpecification().withProperties(properties);
        CompilationUnit compilationUnit = testGeneratePropertiesClassFromTypeSpec(typeSpecification);
        Set<String> importNames = getImportNameSet(compilationUnit);
        Assert.assertFalse(importNames.contains(List.class.getCanonicalName()));
        Assert.assertTrue(importNames.contains(Map.class.getCanonicalName()));
    }
}
