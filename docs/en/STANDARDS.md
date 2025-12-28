# Development Standards

## 1. Naming Conventions
- **Language:** 100% English for code (Backend & Frontend), database, and comments.
- **Backend Classes:** PascalCase (e.g., `OrderService`).
- **Backend Methods/Variables:** camelCase (e.g., `createOrder`).
- **Database Tables:** snake_case plural (e.g., `order_products`).
- **Frontend Components:** PascalCase (e.g., `ProductCard.vue`).
- **Frontend Files:** camelCase for non-component files (e.g., `auth.service.js`).

## 2. Backend Coding Patterns
- **Dependency Injection:** Always use **Constructor Injection** with Lombok `@RequiredArgsConstructor`.
- **DTO Strategy:** Use **Java Records**. The API contract must be native English.
- **Error Handling:** Use `@RestControllerAdvice` and domain-specific exceptions.

## 3. Frontend Coding Patterns (Vue 3)
- **Composition API:** Use `<script setup>` and Composables.
- **Service Layer:** All API calls must pass through `/src/services/`.
- **State Persistence:** Use `storage.js` utility (sessionStorage).

## 4. UI Components Library
Standardized base components for consistent UX:

| Component | Key Props | Description |
| :--- | :--- | :--- |
| **BaseButton** | `label`, `variant`, `loading`, `icon` | Supports primary, secondary, danger, and light variants. |
| **BaseInput** | `id` (req), `label`, `modelValue`, `error` | Standardized form field with error support. |
| **BaseModal** | `modelValue`, `title` | Overlay for dialogs with `default` and `footer` slots. |
| **LoadingSpinner**| `size`, `color` | Visual indicator for async processing. |

## 5. Security Best Practices
- **XSS Prevention**: Vue automatically escapes `{{ }}`. Avoid `v-html` without sanitization.
- **CSRF**: Axios includes CSRF tokens if provided by the backend.
- **Sensitive Data**: Mask CPF/Credit Cards in UI. Never log sensitive information.

## 6. Real-time Communication
- **Standard:** Use STOMP over WebSocket for live updates.
- **Topics:** Use English naming (e.g., `/topic/orders/{id}`).

## 7. Evolution and Refactoring History
The system underwent a major refactoring to address the following previously identified issues:
- **Security:** Migrated JWT storage from `localStorage` to `sessionStorage` to mitigate XSS risks.
- **Architecture:** Eliminated direct Axios calls in components by implementing a mandatory **Service Layer**.
- **Consistency:** Removed mixed Portuguese/English naming, establishing a 100% English code and API contract.
- **Modularity:** Broke down "God Components" into specialized features in `/src/components/features/`.