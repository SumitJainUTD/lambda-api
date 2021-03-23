1. mvn clean install
2. Now the target folder is created, 'lambda-function-1.0-SNAPSHOT.jar' file is also created.
3. Login to aws, create lambda function with language java 8.
4. Click on add trigger
   a. Select the API Gateway from drop-down.
   b. Create a new API or attach an existing one. - choose create new API.
   c. Select API type - REST 
   d. ( provide security, for learning, choose easiest - open).
   e. Click add
5. HandlerInfo (from function main page) - enter
   'MockServer::handleRequest'
6. Call the api.   