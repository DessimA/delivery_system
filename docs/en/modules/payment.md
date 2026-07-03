# Payment Module

## Files

- `controller/PaymentController.java`: REST controller exposing `POST /api/payments/pix/generate`, `GET /api/payments/{orderId}/status`, and `POST /api/payments/confirm/{transactionId}`. The generate and status endpoints require authentication. The confirm endpoint is public (no session required), as it is accessed via the QR code link.

- `service/PaymentService.java`: Core business logic for PIX payments. `createPixPayment` is idempotent per order -- if a `PENDING` payment already exists for the order, it reuses it instead of creating another. `getPaymentStatusByOrderId` looks up by order ID. `confirmPayment` validates expiration and is idempotent (re-confirming a CONFIRMED payment returns success silently).

- `dto/PixRequestDTO.java`: Input DTO with `orderId` and `amount` (BigDecimal), both `@NotNull`.

- `dto/PixResponseDTO.java`: Output DTO with `confirmationUrl` (the URL encoded in the QR code), `copyPaste` (static mock PIX code), `transactionId`, `expiresAt`, and `amount`.

- `model/Payment.java`: JPA entity with BigDecimal amount, status string, transaction ID, `expiresAt` (LocalDateTime), and `@OneToOne` relationship to `Order`.

- `repository/PaymentRepository.java`: Spring Data repository with `findByTransactionId` and `findByOrderId`.

- `exception/PaymentExpiredException.java`: Custom exception mapped to HTTP 410 (Gone) for expired payment links.

## Design Decisions

- **Simulated payment flow.** Since this is a portfolio project, no real payment gateway is integrated. The flow works as follows:
  1. The backend generates a unique `transactionId` and builds a `confirmationUrl` pointing to the frontend (e.g. `http://localhost:5173/payment/confirm/{transactionId}`).
  2. The frontend renders this URL as a QR Code using the `qrcode` library and displays a "Simular pagamento" button that opens the URL in a new tab.
  3. When the URL is accessed, the frontend's `PaymentConfirm.vue` calls `POST /api/payments/confirm/{transactionId}` on the public backend endpoint.
  4. The backend validates the payment is `PENDING` and not expired, then transitions it to `CONFIRMED` and the order to `PAID`.
  5. The original checkout page polls `GET /api/payments/{orderId}/status` until it sees `CONFIRMED`.
  
  To replace with a real gateway, swap only `PaymentService`: call the external API, receive a real `confirmationUrl`, and remove the simulated confirm endpoint.

- **Idempotent payment generation.** If the user reloads the checkout page, `createPixPayment` returns the same pending payment instead of creating duplicates.

- **Idempotent confirmation.** Re-opening a confirmed link returns success without error.

- **Expiration.** `expiresAt` is persisted and validated at confirmation time. Expired links receive HTTP 410.

- Monetary values use `BigDecimal` to avoid floating-point precision issues.
- The amount submitted by the client is validated against `order.getTotalValue()` before creating the payment.
- Payment ownership is verified in both `createPixPayment` and `getPaymentStatusByOrderId`.

## PIX Payment Flow

```mermaid
sequenceDiagram
    participant Client
    participant Frontend
    participant Backend
    participant Link as Link de confirmacao

    Client->>Frontend: finaliza pedido
    Frontend->>Backend: POST /payments/pix/generate
    Backend->>Backend: cria Payment PENDING, gera transactionId e expiresAt
    Backend-->>Frontend: confirmationUrl, copyPaste, expiresAt
    Frontend->>Frontend: gera QR Code a partir da confirmationUrl
    Frontend-->>Client: mostra QR Code e botao Simular pagamento
    Client->>Link: abre o link (escaneia o QR ou clica no botao)
    Link->>Backend: POST /payments/confirm/{transactionId}
    Backend->>Backend: valida PENDING e nao expirado
    Backend->>Backend: Payment CONFIRMED, Order PAID
    Backend-->>Link: sucesso
    Link-->>Client: pagina Pagamento confirmado
    Frontend->>Backend: polling GET /payments/{orderId}/status
    Backend-->>Frontend: CONFIRMED
    Frontend-->>Client: redireciona para pedidos
```
