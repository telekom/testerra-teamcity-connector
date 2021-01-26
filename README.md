# TeamCity Connector

This module provides a simple notification service for JetBrains TeamCity. It uses the build script interaction of TeamCity to
update build progress messages  
on test method updates and update general build status on report generation.

It will register automatically by using `ModuleHook`.

---- 

## Releases

* Latest Release: `1.0-RC-4`

## Requirements

* Testerra in Version `1.0-RC-10`

## Usage

Include the following dependency in your project.

Gradle:

````groovy
implementation 'eu.tsystems.mms.tic.testerra:teamcity-connector:1.0-RC-4'
````

Maven:

````xml

<dependency>
    <groupId>eu.tsystems.mms.tic.testerra</groupId>
    <artifactId>teamcity-connector</artifactId>
    <version>1.0-RC-4</version>
</dependency>
````

## TeamCity configuration

Please ensure that you have `Failure Conditions > Common Failure Conditions > at least one test failed` deactivated on your TeamCity
build configuration,  
because TeamCity Connector will announce the build status on report generation based on test execution statistics.

## Gradle / Maven configuration

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

---

## Impacts on TeamCity

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

---

## Publication

### ... to a Maven repo

```sh
gradle publishToMavenLocal
```

or pass then properties via. CLI

```sh
gradle publish -DdeployUrl=<repo-url> -DdeployUsername=<repo-user> -DdeployPassword=<repo-password>
```

Set a custom version

```shell script
gradle publish -DmoduleVersion=<version>
```

### ... to Bintray

Upload and publish this module to Bintray:

````sh
gradle bintrayUpload -DmoduleVersion=<version> -DBINTRAY_USER=<bintray-user> -DBINTRAY_API_KEY=<bintray-api-key>
```` 
