## Email API ##
The email API exposes a set of REST endpoints to manage a specific user's emails.

Following functions are exposed as endpoints:
* Retrieve the contents of the user's inbox.
* Retrieve the contents of a single email.
* Write a draft email and save it for later.
* Send an email.
* Update one or more properties of draft email e.g., recipients

Swagger integration is done to provide Javadoc for the API, so it is easy to invoke the API 
once the SpringBoot application is up and running.

### Software versions
* JDK: 17
* Gradle: 8.4
* SpringBoot: 3.1.5

### Host the API
#### Option 1
Build and run the Main class EmailApplication using a run configuration in a Java compatible IDE
```bash
./gradlew build
```
Can see that the application has started and is hosted at port 8080
```bash
[main] nz.co.airnz.email.EmailApplication       : Starting EmailApplication using Java 17 with PID 179888 
[main] n.c.a.email.service.EmailServiceImpl     : Loaded 3 emails in memory for pJo001
[main] n.c.a.email.service.EmailServiceImpl     : Loaded 2 emails in memory for iDa001
[main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
[main] nz.co.airnz.email.EmailApplication       : Started EmailApplication in 4.584 seconds (process running for 5.059)
```

### Option 2
#### Build and run the executable jar
```bash
./gradlew bootJar
java -jar build/libs/email-api-0.0.1-SNAPSHOT.jar
```
Can see the following in console:
```bash
 :: Spring Boot ::                (v3.1.5)

[main] nz.co.airnz.email.EmailApplication       : Starting EmailApplication v0.0.1-SNAPSHOT using Java 17 with PID 210992
[main] n.c.a.email.service.EmailServiceImpl     : Loaded 3 emails in memory for pJo001
[main] n.c.a.email.service.EmailServiceImpl     : Loaded 2 emails in memory for iDa001
[main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
[main] nz.co.airnz.email.EmailApplication       : Started EmailApplication in 4.423 seconds (process running for 5.472)
```

### Access the API using bundled Swagger UI
Swagger UI location:
* http://localhost:8080/swagger-ui/index.html#

#### Example accounts and email references to work with API
* pJo001
  * emailRef1
  * emailRef2
  * emailRef3
* iDa001
    * emailRef4
    * emailRef5
 
 
* When a new draft email is added - it always bears the reference emailRef-draft
* When a new email is sent - it always bears the reference emailRef-sent
* When a draft email is created or an email sent - they are added to memory and can be retrieved by
    the subsequent GET allEmails call.


### Unit & Integration (functional) tests
```bash
./gradlew test
```
#### Test Results
Test results can be found in `[project directory]/build/reports/tests`

To see all available tasks `./gradlew tasks`