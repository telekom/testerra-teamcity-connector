# Testerra TeamCity Connector

This module for Testerra provides a simple notification service for TeamCity. It uses the build script interaction of TeamCity to update build progress messages  
on test method updates and update general build status on report generation.  

It will register automatically by using Testera `ModuleHook`.

---- 

## Usage

Include the following dependency in your project.

Gradle:
````groovy
implementation 'eu.tsystems.mms.tic.testerra:teamcity-connector:1-SNAPSHOT'
````

Maven:
````xml
<dependency>
    <groupId>eu.tsystems.mms.tic.testerra</groupId>
    <artifactId>teamcity-connector</artifactId>
    <version>1-SNAPSHOT</version>
</dependency>
````

## TeamCity configuration

Please ensure that you have `Failure Conditions > Common Failure Conditions > at least one test failed` deactivated on your TeamCity build configuration,  
because Testerra TeamCity Connector will announce the build status on report generation based on test execution statistics. 

## Gradle / Maven configuration

When using Testerra TeamCity Connector you have to ensure that Gradle/Maven will ignore test failures.

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
