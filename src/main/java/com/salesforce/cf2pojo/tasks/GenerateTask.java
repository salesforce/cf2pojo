/*
 * Copyright (c) 2017, salesforce.com, inc.
 * All rights reserved.
 * Licensed under the BSD 3-Clause license.
 * For full license text, see LICENSE.txt file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.cf2pojo.tasks;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.ArrayInitializerExpr;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.javadoc.description.JavadocDescription;
import com.github.javaparser.javadoc.description.JavadocSnippet;
import com.salesforce.cf2pojo.exceptions.PropertyClassInferenceException;
import com.salesforce.cf2pojo.model.CloudFormationSpecification;
import com.salesforce.cf2pojo.model.PropertySpecification;
import com.salesforce.cf2pojo.model.PropertyTypeSpecification;
import com.salesforce.cf2pojo.model.ResourceTypeSpecification;
import com.salesforce.cf2pojo.model.TypeSpecification;
import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;

public class GenerateTask extends DefaultTask {
    private static final Logger LOGGER = Logging.getLogger(GenerateTask.class);

    private static final String[] STATIC_FILES = new String[] {"Base64Function.java", "CloudFormationTemplate.java",
        "FindInMapFunction.java", "GetAttFunction.java", "IfFunction.java", "JoinFunction.java", "Parameter.java",
        "RefValue.java", "SelectFunction.java", "StringValue.java", "SubFunction.java", "ValueDeserializer.java",
        "ValueType.java"};

    private File inputDir;
    private File generatedFileDir;

    @InputDirectory
    public File getInputDir() {
        return inputDir;
    }

    public void setInputDir(File inputDir) {
        this.inputDir = inputDir;
    }

    @OutputDirectory
    public File getGeneratedFileDir() {
        return generatedFileDir;
    }

    public void setGeneratedFileDir(File generatedFileDir) {
        this.generatedFileDir = generatedFileDir;
    }

    static String uncapitalize(String str) {
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    private static String surroundWithQuotes(String str) {
        return String.format("\"%s\"", str);
    }

    // Return the part of str after the last '.', if the string contains one (else return the original string)
    static String substringAfterFinalPeriod(String str) {
        return str.substring(str.lastIndexOf('.') + 1);
    }

    private static void writeFile(File destination, String content) throws IOException {
        try (BufferedWriter output = Files.newBufferedWriter(destination.toPath(), StandardCharsets.UTF_8)) {
            output.write(content);
        }
    }

    // Infer the Java class corresponding to the CloudFormation property type
    static String getPropertyClassName(PropertySpecification propertySpec, String propertyName) {
        String propertyClassName = null;
        if ("List".equals(propertySpec.getType()) || "Map".equals(propertySpec.getType())) {
            String genericType;
            if (propertySpec.getItemType() != null) {
                genericType = propertySpec.getItemType();
            } else if (propertySpec.getPrimitiveItemType() != null) {
                switch (propertySpec.getPrimitiveItemType()) {
                    case "String":
                    case "Timestamp":
                        genericType = "ValueType";
                        break;
                    case "Long":
                    case "Integer":
                    case "Double":
                    case "Boolean":
                        genericType = propertySpec.getPrimitiveItemType();
                        break;
                    case "Json":
                        genericType = Object.class.getSimpleName();
                        break;
                    default:
                        throw new PropertyClassInferenceException(
                            String.format("Invalid primitive item type (%s)", propertySpec.getPrimitiveItemType()),
                            propertyName);
                }
            } else {
                throw new PropertyClassInferenceException("List or Map without an item type", propertyName);
            }
            propertyClassName = String.format("List".equals(propertySpec.getType()) ? "List<%s>" : "Map<String, %s>",
                genericType);
        } else if (propertySpec.getType() != null) {
            propertyClassName = propertySpec.getType();
        } else if (propertySpec.getPrimitiveType() != null) {
            switch (propertySpec.getPrimitiveType()) {
                case "String":
                case "Timestamp":
                    propertyClassName = "ValueType";
                    break;
                case "Long":
                    propertyClassName = (propertySpec.isRequired() ? long.class : Long.class).getSimpleName();
                    break;
                case "Integer":
                    propertyClassName = (propertySpec.isRequired() ? int.class : Integer.class).getSimpleName();
                    break;
                case "Double":
                    propertyClassName = (propertySpec.isRequired() ? double.class : Double.class).getSimpleName();
                    break;
                case "Boolean":
                    propertyClassName = (propertySpec.isRequired() ? boolean.class : Boolean.class).getSimpleName();
                    break;
                case "Json":
                    propertyClassName = Object.class.getSimpleName();
                    break;
                default:
                    throw new PropertyClassInferenceException(
                        String.format("Invalid primitive type (%s)", propertySpec.getPrimitiveType()), propertyName);
            }
        }

        if (propertyClassName == null) {
            throw new PropertyClassInferenceException("Could not infer type", propertyName);
        }

        return propertyClassName;
    }

    // Creates a Properties nested class from a resource type specification or subproperty nested class from a property
    // type specification
    static ClassOrInterfaceDeclaration generatePropertiesClassFromTypeSpec(String className,
        CompilationUnit compilationUnit, String typeName, TypeSpecification typeSpec) {

        // Create a new class to represent the object defined by typeSpec
        final ClassOrInterfaceDeclaration propertiesClass = new ClassOrInterfaceDeclaration().setName(className)
            .addModifier(Modifier.PUBLIC, Modifier.STATIC);

        // Create the Javadoc for the class
        JavadocDescription classJavadocDescription = new JavadocDescription();
        classJavadocDescription.addElement(new JavadocSnippet(
            String.format("Properties of an <a href=\"%s\" target=\"_blank\">%s</a>.%n", typeSpec.getDocumentation(),
                typeName)));
        propertiesClass.setJavadocComment("    ", new Javadoc(classJavadocDescription));

        // For each property in the type specification, add a private field to the class
        for (Map.Entry<String, PropertySpecification> propertySpecEntry : typeSpec.getProperties().entrySet()) {
            String propertyName = propertySpecEntry.getKey();
            PropertySpecification propertySpec = propertySpecEntry.getValue();

            // Infer the Java class to use for the property from the PropertySpecification
            String propertyClassName = getPropertyClassName(propertySpec, propertyName);

            // If needed, add import for java.util.List or java.util.Map
            if (propertyClassName.startsWith("List<")) {
                compilationUnit.addImport(List.class);
            } else if (propertyClassName.startsWith("Map<")) {
                compilationUnit.addImport(Map.class);
            }

            // Add an @JsonProperty annotation to the field
            FieldDeclaration fieldDeclaration = propertiesClass
                .addPrivateField(propertyClassName, uncapitalize(propertyName))
                .addSingleMemberAnnotation(JsonProperty.class, surroundWithQuotes(propertyName));

            // Create the Javadoc for the field
            JavadocDescription fieldJavadocDescription = new JavadocDescription();
            fieldJavadocDescription.addElement(new JavadocSnippet(
                String.format("@see <a href=\"%s\" target=\"_blank\">%s</a>%n", propertySpec.getDocumentation(),
                    propertySpec.getDocumentation())));
            fieldDeclaration.setJavadocComment("        ", new Javadoc(fieldJavadocDescription));
        }

        // Add a getter and setter for each field, with the same @JsonProperty annotation as the field itself.
        // Done in a separate loop so the getters and setters come after all of the fields in the class.
        for (FieldDeclaration fieldDeclaration : propertiesClass.getFields()) {
            AnnotationExpr jsonPropertyAnnotation = fieldDeclaration.getAnnotationByClass(JsonProperty.class)
                .orElseThrow(RuntimeException::new);
            fieldDeclaration.createGetter().addAnnotation(jsonPropertyAnnotation);
            fieldDeclaration.createSetter().addAnnotation(jsonPropertyAnnotation);
        }

        return propertiesClass;
    }

    static CompilationUnit generateCompilationUnitFromCloudFormationSpec(CloudFormationSpecification spec,
        ArrayInitializerExpr jsonSubTypesArrayExpr) {
        if (spec.getResourceType().size() != 1) {
            throw new RuntimeException("Expected exactly 1 resource per file");
        }
        Map.Entry<String, ResourceTypeSpecification> resourceTypeNameAndSpec =
            spec.getResourceType().entrySet().iterator().next();

        // resourceCanonicalName is a name like "AWS::EC2::Instance", used in the JsonSubTypes.Type annotation and
        // Javadoc
        String resourceCanonicalName = resourceTypeNameAndSpec.getKey();

        // resourceClassName is a shortened form, like "EC2Instance", to serve as the name of the generated Java
        // class
        String resourceClassName = resourceCanonicalName.substring("AWS::".length()).replace("::", "");

        CompilationUnit compilationUnit = new CompilationUnit();
        compilationUnit.setPackageDeclaration("com.salesforce.cf2pojo.model")
            .addImport(JsonProperty.class);

        final ClassOrInterfaceDeclaration resourceClass = compilationUnit.addClass(resourceClassName);

        // The class declaration is something like "public class EC2Instance extends
        // ResourceBase<EC2Instance.Properties>"
        resourceClass.setName(resourceClassName).addModifier(Modifier.PUBLIC)
            .addExtendedType(String.format("ResourceBase<%s.Properties>", resourceClassName));

        // Add an @Generated annotation to the class
        final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        resourceClass.addAndGetAnnotation(Generated.class)
            .addPair("value", surroundWithQuotes(GenerateTask.class.getCanonicalName()))
            .addPair("date", surroundWithQuotes(dateFormat.format(new Date())));

        // Add constructor that initializes the properties field to a new Properties object
        resourceClass.addConstructor(Modifier.PUBLIC).setBody(new BlockStmt(NodeList.nodeList(
            new ExpressionStmt(
                new AssignExpr(new NameExpr("properties"),
                    new ObjectCreationExpr(null, JavaParser.parseClassOrInterfaceType("Properties"),
                        new NodeList<>()), AssignExpr.Operator.ASSIGN)))));

        // Generate the Properties nested class
        ResourceTypeSpecification resourceSpec = resourceTypeNameAndSpec.getValue();
        ClassOrInterfaceDeclaration resourcePropertiesClass = generatePropertiesClassFromTypeSpec("Properties",
            compilationUnit, resourceCanonicalName, resourceSpec);
        resourceClass.addMember(resourcePropertiesClass);

        // Add a @JsonSubTypes.Type annotation for this resource type to ResourceBase class
        jsonSubTypesArrayExpr.getValues().add(new NormalAnnotationExpr()
            .addPair("value", resourceClassName + ".class")
            .addPair("name", surroundWithQuotes(resourceCanonicalName))
            .setName("JsonSubTypes.Type"));

        // Generate nested classes for any subproperties of this resource type (e.g.,
        // "EC2Instance.BlockDeviceMapping").  These are found in the "PropertyTypes" section of the spec file.
        if (spec.getPropertyTypes() != null) {
            for (Map.Entry<String, PropertyTypeSpecification> propertyTypeSpecEntry :
                spec.getPropertyTypes().entrySet()) {
                String subpropertyClassName = substringAfterFinalPeriod(propertyTypeSpecEntry.getKey());

                ClassOrInterfaceDeclaration subpropertyClass = generatePropertiesClassFromTypeSpec(
                    subpropertyClassName, compilationUnit, propertyTypeSpecEntry.getKey(),
                    propertyTypeSpecEntry.getValue());

                // Add the generated subproperty class as a static nested class of the resource class
                resourceClass.addMember(subpropertyClass);
            }
        }

        return compilationUnit;
    }

    @TaskAction
    void generateJavaClasses() throws IOException, ClassNotFoundException {
        if (!inputDir.isDirectory()) {
            throw new RuntimeException("inputDir needs to be a directory");
        }

        CompilationUnit baseResourceCompilationUnit = JavaParser.parseResource("ResourceBase.java");
        ClassOrInterfaceDeclaration baseResourceClass = baseResourceCompilationUnit.getClassByName("ResourceBase")
            .orElseThrow(() -> new RuntimeException("Could not find ResourceBase class"));

        // Add an @JsonSubTypes annotation to the ResourceBase class
        ArrayInitializerExpr jsonSubTypesArrayExpr = new ArrayInitializerExpr();
        SingleMemberAnnotationExpr jsonSubTypesAnnotation = new SingleMemberAnnotationExpr(
            new Name(JsonSubTypes.class.getSimpleName()), jsonSubTypesArrayExpr);
        baseResourceClass.addAnnotation(jsonSubTypesAnnotation);
        baseResourceCompilationUnit.addImport(JsonSubTypes.class);

        File[] inputFiles = inputDir.listFiles((dir, name) -> name.endsWith("Specification.json"));
        if (inputFiles == null || inputFiles.length == 0) {
            throw new RuntimeException(String.format("Did not find any spec files in directory %s",
                inputDir.getName()));
        }

        ObjectMapper objectMapper = new ObjectMapper();
        for (File inputFile : inputFiles) {

            // Deserialize specification from JSON file
            CloudFormationSpecification spec;
            try (FileInputStream inputStream = new FileInputStream(inputFile)) {
                spec = objectMapper.readValue(inputStream, CloudFormationSpecification.class);
            } catch (JsonProcessingException e) {
                LOGGER.info("Error deserializing file: {}.  Skipping...", inputFile.getName());
                continue;
            }

            // Generate the compilation unit from the specification
            CompilationUnit resourceCompilationUnit = generateCompilationUnitFromCloudFormationSpec(spec,
                jsonSubTypesArrayExpr);

            // Write the compilation unit out to a file
            String resourceClassName = resourceCompilationUnit.getType(0).getNameAsString();
            File resourceClassFile = new File(generatedFileDir, resourceClassName + ".java");
            writeFile(resourceClassFile, resourceCompilationUnit.toString());
        }

        // Write ResourceBase.java file
        writeFile(new File(generatedFileDir, "ResourceBase.java"), baseResourceCompilationUnit.toString(
            new CustomPrettyPrinterConfiguration().setArrayLiteralMembersOnSeparateLines(true)));

        // Copy over static files like ValueType.java without modification
        for (String fileName : STATIC_FILES) {
            InputStream stream = getClass().getClassLoader().getResourceAsStream(fileName);
            Files.copy(stream, new File(generatedFileDir, fileName).toPath());
            stream.close();
        }
    }
}
