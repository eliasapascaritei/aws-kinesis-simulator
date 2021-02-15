# aws-kinesis-simulator

## System requirements
* java 1.8
* scala 2.13
* docker

Before running the project, start a mongo docker using command

 ``docker run -it -p 27017:27017 mongo``  

Start the project with command

``sbt run``

Use cURL command to test it

``
curl -X POST \
'http://localhost:9000/earth/average?from=1567406975&to=1567407975' \
-H 'content-type: multipart/form-data; \
-F file=@/Users/whitetiger/Desktop/resident-samples.log
``

Alternatively this can be tested with POSTMAN, just make sure the file 
key to named ``file``

