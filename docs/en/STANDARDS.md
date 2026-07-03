# Development Standards

## 1. Naming Conventions

- **Language:** 100% English for code (Backend and Frontend), database, and comments.
- **Backend Classes:** PascalCase (e.g., `OrderService`).
- **Backend Methods/Variables:** camelCase (e.g., `createOrder`).
- **Database Tables:** snake_case plural (e.g., `order_items`).
- **Frontend Components:** PascalCase (e.g., `ProductCard.vue`).
- **Frontend Files:** camelCase for non-component files (e.g., `auth.service.js`).

## 2. Backend Coding Patterns

- **Dependency Injection:** Constructor injection with Lombok `@RequiredArgsConstructor`.
- **DTO Strategy:** Java Records. API contract in English.
- **Error Handling:** `@RestControllerAdvice` with domain-specific exceptions.

## 3. Frontend Coding Patterns (Vue 3)

- **Composition API:** `<script setup>` and composables.
- **Service Layer:** All API calls through `/src/services/`.
- **State Persistence:** `storage.js` utility (sessionStorage).

## 4. UI Components Library

| Component | Key Props | Description |
| :--- | :--- | :--- |
| **BaseButton** | `label`, `variant`, `loading`, `icon` | Supports primary, secondary, danger, light variants |
| **BaseInput** | `id` (req), `label`, `modelValue`, `error`, `readonly` | Form field with validation error support |
| **BaseModal** | `modelValue`, `title` | Overlay dialog with `default` and `footer` slots |
| **LoadingSpinner** | `size`, `color` | Async processing indicator |

## 5. Security Best Practices

- **XSS Prevention:** Vue auto-escapes `{{ }}`. Avoid `v-html` without sanitization.
- **Sensitive Data:** Mask CPF in UI. Never log sensitive information.

## 6. Real-time Communication

- **Standard:** STOMP over WebSocket for live updates.
- **Topics:** English naming (e.g., `/topic/orders/{id}`).
