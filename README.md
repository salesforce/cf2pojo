# cf2pojo
**cf2pojo** is a Gradle plugin that automatically generates classes for creating and manipulating CloudFormation templates in Java.  You can use these generated classes in conjunction with [Jackson](https://github.com/FasterXML/jackson) to serialize and deserialize entire CloudFormation templates, or parts thereof, in JSON format.

# Why Do I Need This?
If you use CloudFormation, you may need to create and/or manipulate CloudFormation templates dynamically.  This functionality is not provided by the AWS SDKs, so you might think of using Jackson to serialize and deserialize CloudFormation templates in Java.  Unfortunately, this requires you to write a POJO class for every CloudFormation resource type you plan to use in your templates, which is time-consuming and error-prone.

To solve this problem, we created **cf2pojo**.  cf2pojo is a Gradle plugin that reads in the CloudFormation Resource Specification published by Amazon and outputs a set of Java classes (POJO), including one for each CloudFormation resource type and any nested subproperties.  Taken together, these generated classes provide everything you need to create and manipulate CloudFormation templates as Java objects as well as serialize and deserialize them to/from JSON format using Jackson.

# Getting Started
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

# Contributing
Found a bug? Think you've got an awesome feature you want to add?  We welcome contributions!

## Submitting a Contribution
1. Search for an existing issue.  If none exists, create a new issue so that other contributors can keep track of what you are trying to add/fix and offer suggestions (or let you know if there is already an effort in progress).  Be sure to clearly state the problem you are trying to solve and an explanation of why you want to use the strategy you're proposing to solve it.
1. Fork this repository on GitHub and create a branch for your feature.
1. Clone your fork and branch to your local machine.
1. Commit changes to your branch.
1. Push your work up to GitHub.
1. Submit a pull request so that we can review your changes.

*Make sure that you rebase your branch off of master before opening a new pull request. We might also ask you to rebase it if master changes after you open your pull request.*

## Acceptance Criteria
We love contributions, but it's important that your pull request adhere to the standards that we maintain in this repository.

- All tests must be passing
- All code changes require tests
- All code changes must be consistent with our Checkstyle rules.  We use the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html) with a few small alterations:
    - Indent size of 4
    - Column limit of 120
    - Import statements from standard Java packages (e.g. `import java.util.Map;`) belong in their own section after import statements from third-party libraries
- Code should have great inline comments

# Future
For now, cf2pojo is only available as a Gradle plugin.  In the future it may be extended or modified to support Maven-based projects and/or Java projects without a build tool.
