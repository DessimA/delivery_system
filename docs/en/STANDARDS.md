# Development Standards

## 1. Naming Conventions
- **Language:** 100% English for code, database, and comments.
- **Classes:** PascalCase (e.g., `OrderService`).
- **Methods/Variables:** camelCase (e.g., `createOrder`).
- **Database Tables:** snake_case plural (e.g., `order_products`).

## 2. Coding Patterns

### 2.1. Dependency Injection
- **Rule:** Always use **Constructor Injection**.
- **Tool:** Use Lombok `@RequiredArgsConstructor` and `private final` fields.
```java
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository; // Correct
}
```

### 2.2. DTO Strategy
- **Rule:** Use **Java Records** for all DTOs.
- **Isolation:** Never expose Entities directly to the API.

### 2.3. Error Handling
- **Global Advice:** Use `@RestControllerAdvice` to catch exceptions.
- **Custom Exceptions:** Create domain-specific exceptions (e.g., `ResourceNotFoundException`).

## 3. Input Validation
- Use **Jakarta Bean Validation** (`@NotNull`, `@NotBlank`, `@Size`).
- **Fail-Fast:** Validate at the Controller level using `@Valid`.