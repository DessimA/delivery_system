# Security Module

## Files

- `config/SecurityConfig.java`: `POST /api/payments/confirm/**` is public (`permitAll()`) since the confirmation link is accessed without a session. `POST /api/establishments` accepts both `ROLE_ADMIN` and `ROLE_RESTAURANT`.

- `security/WebSocketAuthInterceptor.java`: Handles both `CONNECT` and `SUBSCRIBE` STOMP frames. On `CONNECT`, it extracts the `Authorization: Bearer <jwt>` header, validates the token via `JwtTokenProvider`, loads the user via `CustomUserDetailsService`, and sets the `Authentication` on the `StompHeaderAccessor`. On `SUBSCRIBE` to `/topic/orders/{orderId}`, it verifies the authenticated user is the customer, assigned courier, or an admin.

- `controller/RestauranteController.java` -> `RestaurantController.java`: Renamed from Portuguese to English. The request mapping `/api/restaurant` remains the same.

## Design Decisions

- **Public confirm endpoint.** The payment confirmation flow requires the user to access a link without being logged in (they may scan the QR code on another device). The confirm endpoint therefore cannot require authentication. The transaction ID acts as a bearer token -- it is a UUID that is infeasible to guess.

- **CONNECT authentication.** Without authentication on `CONNECT`, STOMP subscriptions to order topics had no user context, making authorization impossible. The JWT is extracted from the same header used by the REST API, maintaining a consistent auth mechanism.
