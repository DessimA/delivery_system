# Delivery Module

## Files

- `service/DeliveryService.java`: `updateStatus` now syncs the parent `Order.status` for each delivery transition:
  - `ACEITA` -> `OrderStatus.PREPARING`
  - `EM_ROTA` -> `OrderStatus.IN_TRANSIT`
  - `ENTREGUE` -> `OrderStatus.DELIVERED`

- `repository/DeliveryRepository.java`: Added `@EntityGraph(attributePaths = {"order", "courier"})` to `findByStatusAndCourierIsNull` and `findByCourier` to prevent N+1 queries.

## Design Decisions

- **Status synchronization.** `Order.status` is automatically updated alongside `Delivery.status` transitions: `ACEITA` sets `PREPARING`, `EM_ROTA` sets `IN_TRANSIT`, `ENTREGUE` sets `DELIVERED`. This keeps both entities consistent.

- **EntityGraph for N+1.** Without eager fetching, each delivery query would trigger additional queries for the related order and courier. The `@EntityGraph` ensures they are fetched in a single query.
