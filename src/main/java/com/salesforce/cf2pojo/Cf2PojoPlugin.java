/*
 * Copyright (c) 2017, salesforce.com, inc.
 * All rights reserved.
 * Licensed under the BSD 3-Clause license.
 * For full license text, see LICENSE.txt file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.cf2pojo;

import com.salesforce.cf2pojo.tasks.GenerateTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class Cf2PojoPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getTasks().create("cf2pojo", GenerateTask.class).setDescription("Generates Java classes representing"
            + " CloudFormation resource types from the AWS CF spec.");
    }
}
