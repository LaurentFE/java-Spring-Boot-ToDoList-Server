# To Do List - Server - Spring Boot edition

## Requirements

A MySQL database

Java version 23

## Usage

Make sure your Java home environment variable is set to the Java version 23, as it is the version used to develop 
this application.

Maven is not required to package this application, a Maven wrapper is provided.

In the Command Line Interface of your choice, from the root folder of the project, run : 

```./mvnw clean package```

Tha JAR application file will be generated in the ```./target``` folder, and you can run it with the following 
command :

```java -jar ./target/todolistspringbootserver<version_number>.jar```

The webserver will run on the machine, and you will be able to send requests to the API on this machine.

The resources handled by the API will be described on the landing page of the server (Not implemented yet)

Upon start of the application, the database is currently always reset to its empty state.

## What

This is a Java Spring Boot server that connects to a MySQL Database to store todo lists for users, and expose API 
endpoints to provide the to do lists to a client application.

Client application will be a branch of https://github.com/LaurentFE/Java-ToDoList-Client, that has not yet been 
developed, the client's current version doesn't use the proper endpoints and request body for this server version.

Currently available endpoints allow :
- Restitution of all users
- Restitution of all of a user's todo lists
- Restitution of a user's specific todo list
- Creation of a user 
- Creation of a list for a user, with or without items
- Creation of a list item for a list attached to a user
- Update of a list name
- Update of a list item name
- Update of a list item status (checked/unchecked)

Tests do neither cover all the code nor all the exceptions that the application can throw, as a full coverage would not 
mean much for this application that is not intended to have users, and writing tests was simply a mean to learn a new 
skill, and have a better toolset for my next coding experience. 

## How

Programmed in Java 23 using IntelliJ IDEA Community Edition, storing data on a MySQL Database. 

Built with Maven, and Spring Boot v3.4.1

Logging through log4j

Tests with JUnit and Mockito
