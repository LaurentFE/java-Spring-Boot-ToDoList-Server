# To Do List - Server - Spring Boot edition

## Requirements

A MySQL database

Java version 23

## Usage

Make sure your Java home environment variable is set to the Java version 23, as it is the version used to develop 
this application.

### Building the application yourself

Maven is not required to package this application, a Maven wrapper is provided.

In the Command Line Interface of your choice, from the root folder of the project, run : 

```./mvnw clean package```

Tha JAR application file will be generated in the ```./target``` folder

### Running the application

Whether you built the JAR file, or you downloaded it directly from this repository, you can run it with the following 
command :

```java -jar {filepath}/todolistspringbootserver<version_number>.jar```

The webserver will run on the machine, and you will be able to send requests to the API on this machine.

The resources handled by the API are described on the landing page of the server (eg: ```localhost:8080/```). 

The API documentation is also accessible in ```./target/generated-docs/index.html``` or inside the packaged JAR file in
```BOOT-INF\classes\static\docs\index.html```

Upon start of the application, the database is currently always reset to its empty state.

## What

This is a Java Spring Boot server that connects to a MySQL Database to store todo lists for users, and expose API 
endpoints to provide the to do lists to a client application.

A client application with a GUI is available, making use of this server's API resources. Please user the branch 
"compatible-with-Spring-Boot-server-version" of the Java-ToDoList-Client repository 
(https://github.com/LaurentFE/Java-ToDoList-Client/tree/compatible-with-Spring-Boot-server-version).

Currently available endpoints allow :
- Restitution of all users
- Restitution of all of a user's todo lists
- Restitution of a specific todo list
- Creation of a user 
- Creation of a list for a user, with or without items
- Creation of a list item for a list attached to a user
- Update of a list name
- Update of a list item name
- Update of a list item status (checked/unchecked)

DELETE endpoints will **not** be implemented, as they simply introduce respect of the table's constraints with the many
foreign keys referenced between tables, introducing "difficulty" only in the sense that deletion of data should be 
handled in the proper order. Not exactly a lot to learn in this exercise, and this is not exactly a real product that
will have real users, so the feature will not be missed.

Tests do neither cover all the code nor all the exceptions that the application can throw, as a full coverage would not 
mean much for this application that is not intended to have users, and writing tests was simply a mean to learn a new 
skill, and have a better toolset for my next coding experience. 

## How

Programmed in Java 23 using IntelliJ IDEA Community Edition, storing data on a MySQL Database. 

Built with Maven, and Spring Boot v3.4.1

Logging through log4j

Tests with JUnit and Mockito

Documentation with restDocs and asciiDoctor.
