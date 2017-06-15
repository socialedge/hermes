web: java -Dserver.port=$PORT $JAVA_OPTS -jar backend-application/build/libs/app.jar
web: cd ./frontend-angularjs/ && npm install && gulp deploy --port=$PORT --backend=$BACKEND
