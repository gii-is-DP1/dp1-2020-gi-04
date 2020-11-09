# Standby project
## Building and deploying
You will need Maven and Node.js in order to build this project.
### 1. Building the frontend
1. Open a console
2. Go to `/src/main/webdev`
3. Type npm run build. This will build the web bundle at `/src/main/resources/static`.
4. If you only want to run the project locally, go to 2.1. If you want a production build, go to 2.2.
### 2.1 Building and running the app
1. Go to the root folder
2. Type  `mvn spring-boot:run`
### 2.2 Deploying the entire app
1. Got to the root folder
2. Type `mvn package spring-boot:repackage`. The final JAR file will be located at `/target/`.