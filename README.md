# Standby project
## Building and deploying
You will need Maven to build this project
### Run development
### 1. Backend
1. Open console at root folder and type the following commands:
2. `mvn spring-boot:run`
### 2. Frontend
1. Open console at root folder and type the following commands:
2. `cd src/main/webdev`
3. `npm install`
4. `npm start`
5. Open a browser and go to `http://localhost:1234`
### Deploying the app
1. Got to the root folder
2. Type `mvn package spring-boot:repackage`. The final JAR file will be located at `/target/`.