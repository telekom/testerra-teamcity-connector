# TeamCity Connector

<p align="center">
    <a href="https://mvnrepository.com/artifact/io.testerra/teamcity-connector" title="MavenCentral"><img src="https://img.shields.io/maven-central/v/io.testerra/teamcity-connector/2?label=Maven%20Central"></a>
    <a href="/../../commits/" title="Last Commit"><img src="https://img.shields.io/github/last-commit/telekom/testerra-teamcity-connector?style=flat"></a>
    <a href="/../../issues" title="Open Issues"><img src="https://img.shields.io/github/issues/telekom/testerra-teamcity-connector?style=flat"></a>
    <a href="./LICENSE" title="License"><img src="https://img.shields.io/badge/License-Apache%202.0-green.svg?style=flat"></a>
</p>

<p align="center">
  <a href="#setup">Setup</a> •
  <a href="#documentation">Documentation</a> •
  <a href="#support-and-feedback">Support</a> •
  <a href="#how-to-contribute">Contribute</a> •
  <a href="#contributors">Contributors</a> •
  <a href="#licensing">Licensing</a>
</p>

## About this module

This module provides additional features to [Testerra Framework](https://github.com/telekom/testerra) for automated tests.

This module provides a simple notification service for Jetbrains TeamCity. It uses the build script interaction of TeamCity to update build progress messages
on test method updates and update general build status on report generation.
It will register automatically by using the Testerra ModuleHook.

----

## Setup

### Requirements

| TeamCity connector | Testerra       |
|--------------------|----------------|
| `1.0`              | `1.0.0..1.8`   |
| `1.1`              | `1.9`          |
| `2.0-RC-2`         | `>= 2.0-RC-19` |
| `2.0`              | `>= 2.0`       |
| `2.1`              | `>= 2.11`      |

### Usage

Include the following dependency in your project.

Gradle:

````groovy
implementation 'io.testerra:teamcity-connector:2.1'
````

Maven:

````xml

<dependency>
    <groupId>io.testerra</groupId>
    <artifactId>teamcity-connector</artifactId>
    <version>2.1</version>
</dependency>
````

## Documentation

### Improvement of build status in TeamCity

#### TeamCity configuration

Please ensure that you have `Failure Conditions > Common Failure Conditions > at least one test failed` deactivated on your TeamCity
build configuration,  
because TeamCity Connector will announce the build status on report generation based on test execution statistics.

#### Gradle / Maven configuration

When using TeamCity Connector you have to ensure that Gradle/Maven will ignore test failures.

Gradle:

````groovy
test {
    ignoreFailures = true
}
````

Maven:

````xml

<build>
    <plugins>
        <plugin>
            <artifactId>maven-surefire-plugin</artifactId>
            <configuration>
                <skip>true</skip>
                <testFailureIgnore>true</testFailureIgnore>
            </configuration>
        </plugin>
    </plugins>
</build>
````

#### Impacts on TeamCity

*Changes in TeamCity*

|TeamCity default|With TeamCity connector|Description|
|---|---|---|
|![](doc/teamcity_default_running.png)|![](teamcity_connector_running.png)|TeamCity connector set the current test result at runtime.|
|![](doc/teamcity_default_result.png)|![](teamcity_connector_result.png)|TeamCity connector set the correct test result and build status.|

The TeamCity connector not only set the correct test results but also add useful information. Based on the screenshots in the table
above the following information are added.

*Meaning o shown information*

|Part|Description|
|---|---|
|`TT-Skeleton`|The value of the property `tt.report.name`| 
|`regression`|The value of the property `tt.runcfg`. This could be the test set (regression, smoketest, etc.)|
|`3 Passed, 1 Failed`|The result of all test cases of the complete run. At runtime it shows the current result up to that point.|
|`-NOT OK-`|The status of the complete test run. If all tests passed or the `Failure Corridor` was matched, the status would be `-OK-`|

The following tables shows some more examples how the result could be.

|Test result|Description|
|---|---|
| ![](doc/teamcity_connector_result_skipped.png) | A test cases was skipped because the corresponding setup method failed. The result of the test run is failed.|
| ![](doc/teamcity_connector_result_failure_corr.png) | The Failure corridor was matched, the status is OK although a test failed.|
| ![](doc/teamcity_connector_result_exp_failed.png) | A test was marked as expected failed, all other tests passed. The restult of the test run is still passed.|

### Support of Testrun history of Testerra Report

TeamCity connector can load the testrun history information of the latest build to get the report history in TeamCity-based Testerra executions. See more details in https://docs.testerra.io/.

To get the latest testrun history, the selected TeamCity build job:
- is not running
- was not canceled
- should be of the same build configuration ;-)
- must match the [branch specification](#selecting-the-branch)

#### Properties

Specify the following properties in `test.properties` to control the history file download for Testerra Report:

| Property                              | Description                                                                                                                                                |
|---------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `tt.teamcity.history.download.active` | Activate the history file download, default: `false`                                                                                                       |
| `tt.teamcity.url`                     | URL of your TeamCity server                                                                                                                                |
| `tt.teamcity.rest.token`              | TeamCity Access token needed for REST API                                                                                                                  |
| `tt.teamcity.buildTypeId`             | BuildType ID for the current Build configuration                                                                                                           |
| `tt.teamcity.build.branch`            | Specify the branch of the build job from which the history file has to be downloaded, default: `all`. See [below](#selecting-the-branch) for more details. |
| `tt.teamcity.build.count`             | Number of last build jobs for getting the latest history file, default: `3`                                                                                |

#### TeamCity configuration

It is recommended that the REST token is stored in a Configuration Parameter in your Build Configuration.

![teamcity_connector_resttoken_parameter.png](doc/teamcity_connector_resttoken_parameter.png)

All the other properties can be setup as 'Additional Maven command line parameters':

![teamcity_connector_history_parameter.png](doc/teamcity_connector_history_parameter.png)

#### Selecting the branch

You can specify the Git branch of the build job which is used to download the latest history file.

Add the property `tt.teamcity.build.branch` to your setup: 

| Value         | Description                                                                                                        |
|---------------|--------------------------------------------------------------------------------------------------------------------|
| `all`         | TeamCity connector takes the last build job independent of the used branch. This is default.                       |
| `default`     | Only build jobs of the default branch are used for download. The default branch is configured in your VCS setting. |
| `<any other>` | Any other value is used as a branch name, eg. `development`, `fix/a-bug`                                           |

#### Set the number of latest build jobs

With `tt.teamcity.build.count` you can set the number of latest builds, where TeamCity connector will search of the latest history file. 

This could help to prevent interrupted history reports in case of broken jobs or incomplete report artifacts. 

---

## Publication

This module is deployed and published to Maven Central. All JAR files are signed via Gradle signing plugin. 

For the ``signing`` plugin the following properties have to be set via command line or ``~/.gradle/gradle.properties``

| Property                    | Description                     |
|-----------------------------|---------------------------------|
| `signing.keyId`             | GPG private key ID (short form) |
| `signing.password`          | GPG private key password        |
| `signing.secretKeyRingFile` | Path to GPG private key         |

### Publish snapshots

Publishing snapshots the default ``maven-publish`` plugin is used:

````shell
export MAVEN_CENTRAL_PORTAL_USERNAME=<username>
export MAVEN_CENTRAL_PORTAL_PASSWORD=<token>
gradle publish
````

### Publish release

Publishing releases the ``org.danilopianini.publish-on-central`` plugin is used:

````shell
export MAVEN_CENTRAL_PORTAL_USERNAME=<username>
export MAVEN_CENTRAL_PORTAL_PASSWORD=<token>
gradle publishMavenJavaPublicationToProjectLocalRepository zipMavenCentralPortalPublication releaseMavenCentralPortalPublication -DmoduleVersion=2.1 
````

## Code of Conduct

This project has adopted the [Contributor Covenant](https://www.contributor-covenant.org/) in version 2.0 as our code of conduct. Please see the details in our [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md). All contributors must abide by the code of conduct.

## Working Language

We decided to apply _English_ as the primary project language.  

Consequently, all content will be made available primarily in English. We also ask all interested people to use English as language to create issues, in their code (comments, documentation etc.) and when you send requests to us. The application itself and all end-user faing content will be made available in other languages as needed.

## Support and Feedback

The following channels are available for discussions, feedback, and support requests:

| Type                     | Channel                                                |
| ------------------------ | ------------------------------------------------------ |
| **Issues**   | <a href="/../../issues/new/choose" title="Issues"><img src="https://img.shields.io/github/issues/telekom/testerra-teamcity-connector?style=flat"></a> |
| **Other Requests**    | <a href="mailto:testerra@t-systems-mms.com" title="Email us"><img src="https://img.shields.io/badge/email-CWA%20team-green?logo=mail.ru&style=flat-square&logoColor=white"></a>   |

## How to Contribute

Contribution and feedback is encouraged and always welcome. For more information about how to contribute, the project structure, as well as additional contribution information, see our [Contribution Guidelines](./CONTRIBUTING.md). By participating in this project, you agree to abide by its [Code of Conduct](./CODE_OF_CONDUCT.md) at all times.

## Contributors

At the same time our commitment to open source means that we are enabling -in fact encouraging- all interested parties to contribute and become part of its developer community.

## Licensing

Copyright (c) 2021 Deutsche Telekom AG.

Licensed under the **Apache License, Version 2.0** (the "License"); you may not use this file except in compliance with the License.

You may obtain a copy of the License at https://www.apache.org/licenses/LICENSE-2.0.

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the [LICENSE](./LICENSE) for the specific language governing permissions and limitations under the License.
