Simple Apache Apollo Examples
=============

Requirements
======
To build, you need 

* Java 8 or higher 
* Maven 3 or higher (http://maven.apache.org/)

Setup
======
Before cloning this repository, be sure to enable automatic conversion of CRLF/LF on your machine using "git config --global core.autocrlf input". For more information, please  refer to http://help.github.com/dealing-with-lineendings/

Building
======
To build the server side, run "mvn package", this will build the program and place the generated jar file in the directory "target/".

Running
======
To Run "mvn exec:java". 

For help on available command line options, run mvn exec:java -Dexec.args="--help" (for more details, cf. http://mojo.codehaus.org/exec-maven-plugin/java-mojo.html).
