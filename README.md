# ares-client-resttemplate
Client application using RestTemplate to make API calls

## ToDo
- [ ] set up client UI to manage data

----

### Main dependencies and components
* Actuator
* Web
* DevTools
* Lombok
* HATEOAS
```xml
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-actuator</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-devtools</artifactId>
			<scope>runtime</scope>
		</dependency>
		<dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
			<optional>true</optional>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-hateoas</artifactId>
		</dependency>
	</dependencies>

	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
		</plugins>
	</build>

	<repositories>
		<repository>
			<id>spring-milestones</id>
			<name>Spring Milestones</name>
			<url>https://repo.spring.io/milestone</url>
			<snapshots>
				<enabled>false</enabled>
			</snapshots>
		</repository>
	</repositories>
```

### bootstrap.yml
```yaml
server:
  port: 8091
  
spring:
  application:
    name: ares-client-resttemplate
  cloud:
    config:
      uri: http://localhost:8900
```

### ares-client-resttemplate.yml
```yaml
ares-service-h2:
  ribbon:
    eureka:
      enabled: false
    listOfServers: ares-service-h2
```

### Main Application class
```java
@EnableHypermediaSupport(type=EnableHypermediaSupport.HypermediaType.HAL)
@SpringBootApplication
public class AresClientRestTemplateApplication {

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	public static void main(String[] args) {
		SpringApplication.run(AresClientRestTemplateApplication.class, args);
	}

}
```

### Model class for response object
```java
@Data
public class Hero {

    private String firstName;
    private String lastName;
    private String codeName;
    private String email;
    private String team;

    public Hero() {}

    public Hero(String firstName, String lastName, String codeName, String email, String team) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.codeName = codeName;
        this.email = email;
        this.team = team;
    }

}
```

### Controller class
```java
@RestController
@Slf4j
@RequestMapping("/heroes")
public class HeroController {

    @Autowired
    private RestTemplate restTemplate;

    private static final String BASE_URL = "http://localhost:8080";

    // return all objects
    @RequestMapping(value = "", method = RequestMethod.GET)
    public Collection<Hero> getHeroes() {
        String url = BASE_URL + "/heroes";
        ResponseEntity<Resources<Hero>> responseEntity = restTemplate
                .exchange(url, HttpMethod.GET, null,
                        new ParameterizedTypeReference<Resources<Hero>>() {});
        return responseEntity.getBody().getContent();
    }

/*    @RequestMapping(value = "/{firstName}", method = RequestMethod.GET)
    public Collection<Hero> getHeroByFirstName(@PathVariable("firstName") String firstName) {
        String url = BASE_URL + "/heroes/search/findByFirstName?firstname=" + firstName;
        ResponseEntity<Resources<Hero>> responseEntity = restTemplate.exchange(url, HttpMethod.GET,
                null, new ParameterizedTypeReference<Resources<Hero>>() {});
        return responseEntity.getBody().getContent();
    }*/

    // Return single object by ID
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Hero getHeroById(@PathVariable("id") Long id) {
        String url = BASE_URL + "/heroes/" + id;
        ResponseEntity<Resource<Hero>> responseEntity = restTemplate.exchange(url, HttpMethod.GET,
                null, new ParameterizedTypeReference<Resource<Hero>>() {},
                Collections.emptyMap());
        return responseEntity.getBody().getContent();
    }

    // TODO: POST single object
    // TODO: POST list of objects
    // TODO: PUT update single object
    // TODO: PUT update list of object
    // TODO: DELETE single object
    // TODO: DELETE list of objects
    // TODO: DELETE all objects

    // TODO: UI javascript to present data
}
```

### Dockerfile
```dockerfile
FROM openjdk:8-jdk-alpine
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
EXPOSE 8900
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
```

### Jenkinsfile
```groovy
def mvnTool
def prjName = "ares-client-resttemplate"
def imageTag = "latest"

pipeline {
    agent { label 'maven' }
    options {
        buildDiscarder(logRotator(numToKeepStr: '2'))
        disableConcurrentBuilds()
    }
    stages {
        stage('Build && Test') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    script {
                        mvnTool = tool 'Maven'
                        sh "${mvnTool}/bin/mvn -B clean verify sonar:sonar -Prun-its,coverage"
                    }
                }
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                    jacoco(execPattern: 'target/jacoco.exec')
                }
            }
        }
        stage('Quality Gate') {
            steps {
                timeout(time: 1, unit: 'HOURS') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }
        stage('Release && Publish Artifact') {

        }
        stage('Create Image') {
            steps {
                sh "docker build --build-arg JAR_FILE=target/${prjName}-${releaseVersion}.jar -t ${prjName}:${releaseVersion}"
            }
        }
        stage('Publish Image') {
            steps {
                withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'JENKINS_ID', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
                    sh """
                        docker login -u ${USERNAME} -p ${PASSWORD} dockerRepoUrl
                        docker push ...
                    """
                }
            }
        }
    }
}
```