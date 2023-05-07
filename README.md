# Promedicus coding assignment

# Design / Architecture
I have chosen Spring Boot to create this application. The reason for choosing Spring Boot is its convention over configuration approach and ability to create standalone applications. Project structure was generated using Spring Initializr.

# Tech-Stack Used to build application:

1. Spring boot embedded Tomcat Server
2. In-Memory h2 Database (but can be used with any jdbc compliant database)
3. Spring Rest
4. Jackson : Java to Json mapping 
5. JPA (Hibernate) : For persistence layer 
6. JUnit : Unit Test Framework 
7. Mockito : Mocking framework 
8. Gradle : Build integration
9. Postman : testing Rest Services.

# Application Assumptions
* For Internal Admissions, source is not allowed in request payload. It is to handle silent ignore source for internal admissions.

# How to run this application
* Clone the git repo using following command

```git clone https://github.com/jverma1/promedicus.git```
	
   This will create a folder promedicus in your current working directory.
* Execute command:

``` cd promedicus ```
* Compile code using following command

``` ./gradlew clean build ```
* Run the application using following command

``` ./gradlew bootRun```
* Now application is started.

# Postman collection
I have provided postman collection (if you are using postman as REST client) for testing endpoints fast. 

# Using application
* **Create admission endpoint** for **internal** system using any Rest client (I have used Postman)
* endpoint URL: http://localhost:8080/api/admission 
	* http method= POST
	* add following http headers:
 		* key=Content-Type  value=application/json
	* Paste the following content to the body section then click send
```
{
    "name":"John",
    "dob":"08/01/1980",
    "sex": "MALE",
    "category":"NORMAL"
}
```

* **Create admission endpoint** for **external** admission using any Rest client (I have used Postman)
* endpoint URL: http://localhost:8080/api/admission
	* http method= POST
	* add following http headers:
		* key=Content-Type  value=application/json
	* Paste the following content to the body section then click send
```
{
    "name":"Victoria",
    "dob":"08/01/1990",
    "sex": "FEMALE",
    "category":"EMERGENCY",
    "external": true,
    "source": "Westside Hospital"
}
```
* curl command example for **creating external admission**
```
curl --location 'localhost:8080/api/admission' \
--header 'Content-Type: application/json' \
--data '{
    "name":"zxcv",
    "dob":"05/04/1981",
    "sex": "MALE",
    "category": "EMERGENCY",
    "source": "abc hospital",
    "external": true
}'
```
* **get Admission List** endpoint
* endpoint URL: http://localhost:8080/api/admission
	* http method=GET
  
click send and you should get the following results
```
[
    {
    	"id": 1,
    	"admissionDate":"05/05/2023 12:10:34",
    	"name":"John",
    	"dob":"08/01/1980",
    	"sex": "MALE",
    	"category":"NORMAL"
    },
    {
    	"id": 2,
    	"admissionDate":"05/05/2023 12:11:10",
    	"name":"Victoria",
    	"dob":"08/01/1990",
    	"sex": "FEMALE",
    	"category":"EMERGENCY",
    	"external": true,
    	"source": "Westside Hospital"
	}
]
```
* Run the following endpoint to  **update existing admission** 
* endpoint URL: http://localhost:8080/api/admission/2
	* http method=PUT
	* add following http header:
		* key=Content-Type  value=application/json
* Paste the following content to the body section 

```
{
    "name":"Victoria",
    "dob":"03/01/1990",
    "sex": "FEMALE",
    "category": "EMERGENCY"
}
```
* Click send, you should get the following results
```
{
    "id": 2,
    "admissionDate": "05/05/2023 12:11:10",
    "name": "Victoria",
    "dob": "03/01/1990",
    "sex": "FEMALE",
    "category": "EMERGENCY",
    "source": "Westside Hospital",
    "external": true
}
```
* Run the following endpoint to  **Delete admission** 
* endpoint URL: http://localhost:8080/api/admission/1
	* http method=DELETE
* click send and you should receive http status 200 Ok 
* if admission record is not found, you will see http status 404 and **Admission not found** message in response body



# Testing
JUnit 100% code coverage is provided for controller class(AdmissionController.java) and exception handler class(AdmissionExceptionHandler.java) only. 
There is no JUnit coverage for repository class(AdmissionRepository.java) methods as repository is just an Interface and implementation is provided by JPA.

# Out Of Scope
* Handling POST method from creating same record
* No special handling for deleting internal/external admission, any request for delete will delete admission.
* Security

## Author
* **Jyoti Verma**
