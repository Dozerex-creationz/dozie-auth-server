# Authentication Server - Test Suite

This document describes the comprehensive test suite for the Dozie Authentication Server.

## Test Framework

The project uses **REST Assured + JUnit 5** for API testing with the following components:

- **REST Assured**: For HTTP API testing and assertions
- **JUnit 5**: Test framework with lifecycle management
- **H2 Database**: In-memory database for testing
- **Quarkus Test**: Integration testing framework

## Test Structure

### Base Test Class

- `BaseApiTest.java`: Common setup and utility methods for all API tests

### Test Classes (in execution order)

1. **LoginApiTest.java** (`@Order(1)`)

   - Login endpoint health check
   - Successful login with token generation
   - Invalid credentials handling
   - Empty/null credential handling
   - Response structure validation

2. **UserApiTest.java** (`@Order(2)`)

   - User CRUD operations
   - Duplicate email handling
   - Invalid data validation
   - User data structure validation

3. **RoleApiTest.java** (`@Order(3)`)

   - Role CRUD operations
   - Permission management
   - Duplicate role handling
   - Role structure validation

4. **TokenApiTest.java** (`@Order(4)`)

   - Token refresh functionality
   - Invalid token handling
   - Expired token scenarios
   - Token structure validation

5. **IntegrationTest.java** (`@Order(5)`)
   - Complete authentication flow
   - Error handling scenarios
   - Concurrent operations

## Running Tests

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

### Run All Tests

```bash
mvn test
```

### Run Specific Test Class

```bash
mvn test -Dtest=LoginApiTest
mvn test -Dtest=UserApiTest
mvn test -Dtest=RoleApiTest
mvn test -Dtest=TokenApiTest
mvn test -Dtest=IntegrationTest
```

### Run Tests with Coverage

```bash
mvn test jacoco:report
```

### Run Tests in Development Mode

```bash
mvn quarkus:dev
# Then run tests in another terminal
mvn test
```

## Test Configuration

### Database Configuration

Tests use H2 in-memory database configured in `src/test/resources/application.properties`:

```properties
quarkus.datasource.db-kind=h2
quarkus.datasource.jdbc.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
quarkus.hibernate-orm.database.generation=drop-and-create
```

### JWT Configuration

Test JWT settings:

```properties
mp.jwt.verify.publickey.location=publicKey.pem
mp.jwt.verify.issuer=dozie-auth-server
```

## Test Coverage

The test suite covers:

### Login API (`/login`)

- ✅ Successful authentication
- ✅ Invalid username/password
- ✅ Empty/null credentials
- ✅ Response structure validation
- ✅ JWT token generation

### User Management API (`/users`)

- ✅ Create user
- ✅ Get all users
- ✅ Get user by ID
- ✅ Update user
- ✅ Delete user
- ✅ Duplicate email handling
- ✅ Invalid data validation

### Role Management API (`/roles`)

- ✅ Create role
- ✅ Get all roles
- ✅ Get role by ID
- ✅ Update role
- ✅ Delete role
- ✅ Permission management
- ✅ Duplicate role handling

### Token Refresh API (`/tokens/refresh`)

- ✅ Valid refresh token
- ✅ Invalid/expired tokens
- ✅ Empty/null tokens
- ✅ New access token generation
- ✅ Error handling

### Integration Scenarios

- ✅ Complete authentication flow
- ✅ Error handling across APIs
- ✅ Concurrent operations
- ✅ Data consistency

## Test Data Management

### Test Isolation

- Each test class runs independently
- Database is reset between test classes
- Test data is cleaned up after each test

### Test Data Creation

Helper methods in `BaseApiTest`:

- `createUser()`: Creates test users
- `createRole()`: Creates test roles
- `loginUser()`: Performs login
- `refreshToken()`: Refreshes tokens

### Data Cleanup

- Tests clean up their own data
- Integration tests perform comprehensive cleanup
- H2 database is dropped and recreated between test runs

## Assertions and Validations

### Response Status Codes

- 200: Successful operations
- 201: Resource created
- 204: Resource deleted
- 400: Bad request
- 401: Unauthorized
- 404: Not found

### Response Structure

- JSON content type validation
- Required fields presence
- Data type validation
- JWT token format validation

### Business Logic

- Duplicate prevention
- Data integrity
- Error message accuracy
- Token expiration handling

## Debugging Tests

### Enable Detailed Logging

```bash
mvn test -Dquarkus.log.level=DEBUG
```

### View Test Reports

```bash
mvn test jacoco:report
# Reports available in target/site/jacoco/
```

### Run Single Test Method

```bash
mvn test -Dtest=LoginApiTest#testSuccessfulLogin
```

### IDE Integration

- Import as Maven project
- Run tests directly from IDE
- Debug tests with breakpoints
- View test results in IDE test runner

## Continuous Integration

### GitHub Actions (Recommended)

```yaml
name: Tests
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: "17"
      - run: mvn test
```

### Local CI Setup

```bash
# Pre-commit hook
mvn clean test

# Pre-push hook
mvn clean test jacoco:report
```

## Performance Testing

### Load Testing (Optional)

For performance testing, consider adding:

- JMeter test plans
- Gatling scenarios
- Load test configurations

### Memory Testing

- Monitor memory usage during tests
- Check for memory leaks
- Validate garbage collection

## Troubleshooting

### Common Issues

1. **Port Conflicts**

   ```bash
   # Kill processes on test port
   netstat -ano | findstr :8081
   taskkill /PID <PID> /F
   ```

2. **Database Lock Issues**

   ```bash
   # Clean and rebuild
   mvn clean test
   ```

3. **JWT Key Issues**
   ```bash
   # Regenerate test keys
   mvn clean test -Dquarkus.smallrye-jwt.enabled=false
   ```

### Test Environment

- Ensure Java 17+ is installed
- Verify Maven configuration
- Check network connectivity (for external dependencies)
- Validate file permissions

## Contributing

### Adding New Tests

1. Follow existing naming conventions
2. Use appropriate `@Order` annotation
3. Extend `BaseApiTest` for common functionality
4. Add proper cleanup in tests
5. Update this README with new test coverage

### Test Guidelines

- Write descriptive test method names
- Use meaningful test data
- Include both positive and negative test cases
- Validate response structure and content
- Clean up test data after tests

### Code Coverage

- Aim for >80% code coverage
- Focus on critical business logic
- Test error scenarios
- Validate edge cases
