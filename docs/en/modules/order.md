# Order Module

## Files

- `model/Order.java`: JPA entity. Replaced `@ManyToMany products` with `@OneToMany orderItems` to support quantities. `calculateTotal()` now sums `unitPrice * quantity` per item.

- `model/OrderItem.java`: New JPA entity representing a single product line in an order. Stores `product` (ManyToOne), `quantity`, and `unitPrice` (snapshot of the price at order time).

- `dto/OrderRequestDTO.java`: Accepts `List<OrderItemRequestDTO>`. Each item carries `productId` and `quantity`.

- `dto/OrderItemRequestDTO.java`: New record with `productId` (NotNull) and `quantity` (Min 1).

- `dto/OrderResponseDTO.java`: Changed `List<ProductResponseDTO> products` to `List<OrderItemResponseDTO> items`.

- `dto/OrderItemResponseDTO.java`: New record with `productId`, `productName`, `productImageUrl`, `quantity`, `unitPrice`.

- `mapper/OrderMapper.java`: Maps `orderItems` to `items`. Uses `OrderItemMapper`.

- `mapper/OrderItemMapper.java`: New MapStruct mapper converting `OrderItem` to `OrderItemResponseDTO`, resolving product name and image through the `product` relationship.

- `service/OrderService.java`: `createOrder` builds `OrderItem` list from the request, snapshots `unitPrice` from `Product.getPrice()`, validates all products belong to the same establishment, and persists the order with items.

- `repository/OrderRepository.java`: Uses `@EntityGraph` to eagerly fetch `orderItems`, `orderItems.product`, and `delivery`.

- `repository/OrderItemRepository.java`: Standard Spring Data repository.

## Design Decisions

- **Quantity support.** The old design lost quantity information entirely. The `order_items` table captures `quantity` and `unitPrice` as a snapshot, so price changes after ordering don't affect existing orders.

- **Snapshot pricing.** `unitPrice` is copied from `Product.price` at order time. This is the correct ecommerce pattern: the receipt reflects what the customer agreed to pay.

- **Product relationship.** `OrderItem` has a `@ManyToOne` to `Product`, enabling the mapper to resolve product name and image without an extra query.

- **Cascade.** `Order.orderItems` uses `CascadeType.ALL` and `orphanRemoval=true`, so items are persisted and removed with the order.
