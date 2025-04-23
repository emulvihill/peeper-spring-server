# Peeper - Spring Boot Server

Peeper's backend is a Spring Boot application that provides the server-side functionality using Spring AI, MySQL, and GraphQL.

The application stores snapshots of a video feed into a database, and compares successive frames for activity which is then reported to the user.

## Prerequisites

- Java 23 (JDK)
- MySQL 8.0 or higher
- Maven
- An OpenAI API key (for vision services)
- Optional: Ollama for local LLM support

## Configuration

### Database Setup
- Create a MySQL schema named `peeper`
- Default developer credentials (can be modified in `application.yml`, please DO NOT use these defaults on a production instance!):
    - Username: root
    - Password: password
    - Port: 3306

### Application Properties
The application can be configured through `application.yml`. Key configurations include:

- Server port: 5000
- Database connection details
- OpenAI API key (required for vision services)
- API key can also be set in an environment variable, 
- Spring Boot Admin settings
- CORS settings for frontend applications


## How to Build and Run

1. Clone the repository
2. Configure your OpenAI API key in `application.yml` or set it in the environment variable `SPRING_AI_OPENAI_API_KEY`
3. Build the project:
```bash
mvn clean install
```
4. Run the application:
```bash
mvn spring-boot:run
```

## Features

- GraphQL API
- Spring Security integration (currently configured to permit all requests)
- Spring Boot Admin for monitoring and management
- Async support configured for handling concurrent requests
- Image comparison services using OpenAI or local Ollama model (must support multimodal)
- Flyway database migrations
- Cross-Origin Resource Sharing (CORS) configured for frontend applications
- Supports database-provided settings for vision provider and model. To override application.yml, add the following entries into the `Settings` table:
  - comparison_provider,GLOBAL,openai 
  - comparison_model,GLOBAL,gpt-4o

## Development

- The application uses Flyway for database migrations
- GraphiQL interface is available at `/graphiql` when running in development
- Actuator endpoints are exposed at `/actuator/*`
- Spring Boot Admin console is accessible for monitoring at `/instances`

## Testing

- Test database configuration is provided for integration tests
- JUnit and Spring Boot Test dependencies are included
- TestContainers support for integration testing

## Security Note

The current security configuration permits all requests (`permitAll()`). This should be properly configured before deploying to production. Database credentials should be changed to avoid using root user and default password.

## Monitoring

Spring Boot Admin is configured for monitoring the application. Access the admin console at:
- http://localhost:5000/instances

## Additional Information

For more details about specific components or for contributing guidelines, please contact the project maintainers.