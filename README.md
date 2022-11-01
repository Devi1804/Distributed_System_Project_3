# Project3
   Implementation of 2-phase distributed commit(2PC) protocol 

## Prerequisites:
   Minimum JDK 8
   
## How to compile and run the project.
1. Go to the 'TransactionControl' folder and run the following command to compile the project
```
javac  -d ./bin -cp ./bin ./src/main/java/com/ds/project3/client/*.java
javac  -d ./bin -cp ./bin ./src/main/java/com/ds/project3/log/*.java
javac  -d ./bin -cp ./bin ./src/main/java/com/ds/project3/server/controller/*.java
javac  -d ./bin -cp ./bin ./src/main/java/com/ds/project3/server/nodes/*.java
```
2. After compiling the code run the following commands <B> on separate</B> terminals:
```
java -classpath ./bin com.ds.project3.server.controller.TransactionController
java -classpath ./bin com.ds.project3.server.nodes.NodeA
java -classpath ./bin com.ds.project3.server.nodes.NodeB
java -classpath ./bin com.ds.project3.client.Client
```
