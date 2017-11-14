# cf2pojo
A Gradle plugin that automatically generates classes for creating and manipulating CloudFormation templates in Java.  You can use these generated classes in conjunction with [Jackson](https://github.com/FasterXML/jackson) to serialize and deserialize entire CloudFormation templates, or parts thereof, in JSON format.

## Usage
To use **cf2pojo** in your own Gradle project, download the [CloudFormation Resource Specification](http://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/cfn-resource-specification.html) from Amazon (choose the All Files version for your desired region) and move it to a location in your project's directory, such as `src/main/resources/CloudFormationResourceSpecification.zip`.

Next, add something like this to your project's `build.gradle` file:

```groovy
apply plugin: 'com.salesforce.cf2pojo'

task unzipCfSpec(type: Copy) {
    from zipTree('src/main/resources/CloudFormationResourceSpecification.zip')
    into file('src/main/resources/CloudFormationResourceSpecification')
}

cf2pojo {
    dependsOn unzipCfSpec
    inputDir = file('src/main/resources/CloudFormationResourceSpecification')
    generatedFileDir = file('src/main/generated/com/salesforce/cf2pojo/model/')
}

compileJava {
    dependsOn cf2pojo
    source += cf2pojo.generatedFileDir
}
```

This imports the `cf2pojo` task into your Gradle project, adds a new task to unzip the .zip file you downloaded earlier from Amazon into a scratch directory, and tells cf2pojo to use this scratch directory as its input.  The final bit tells Gradle to run the `cf2pojo` task before compiling its Java code and registers the generated code as a source location, so that the Gradle Java plugin knows to include this generated code when it compiles your Java code.
