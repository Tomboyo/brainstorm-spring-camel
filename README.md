# Overview

This is a practice project to create a small event-driven application in Spring,
Camel, and Java 14 (using preview features).

The application, "Brainstorm," is metadata service that tracks references
between documents in an Asciidoc notebook. As a user creates, modifies, and
deletes files, Brainstorm updates a graph data structure with nodes for each
page of notes and edges for each inter-document reference. This graph is exposed
via an HTTP endpoint so that the user can see all inbound and outbound
references to and from a queried document.

# Development Setup

## Gradlew

We use ./gradlew for building. Your IDE may request that you whitelist the
./gradlew jar's checksum for security reasons. Consult the following website to
verify the checksum: https://gradle.org/release-checksums/.

## Java Preview Features

This gradle project has Java preview features enabled. However, your IDE
requires additional configuration to correctly compile the code. If using
VScode, execute the `./gradlew eclipseJdt` task, which will perform necessary
configuration for you. For other IDEs, see
https://github.com/redhat-developer/vscode-java/issues/671.

## Cypher Shell

The embedded Neo4J database is exposed on `localhost:7687` by default. You can
install `cypher-shell` to query the embedded database while the application is
running.

## Reference Documentation
For further reference, please consider the following sections:

* [Official Gradle documentation](https://docs.gradle.org)
* [Spring Boot Gradle Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.3.0.RELEASE/gradle-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/2.3.0.RELEASE/gradle-plugin/reference/html/#build-image)

## Additional Links
These additional references should also help you:

* [Gradle Build Scans – insights for your project's build](https://scans.gradle.com#gradle)

