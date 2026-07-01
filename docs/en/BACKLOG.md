# Technical Debt Backlog

Items identified in the technical audit that were not addressed in this cleanup round.

## Deferred Items

### CODE-6: Bootstrap dependency cleanup
- **Priority**: Medium
- **Reason**: Bootstrap CSS/JS and `bootstrap-vue-next` are imported globally in `main.js` but no `b-*` components or Bootstrap utility classes are used by the custom components. Removing them could cause visual regressions if any CSS depends on Bootstrap's reset or base styles. Requires visual regression testing before removal.
- **Action**: Verify no component depends on Bootstrap styles, then remove imports from `main.js` and dependencies from `package.json`.

### BUG-15: Order line items migration
- **Priority**: Medium
- **Reason**: Currently `Order` references `Product` directly via `@ManyToMany`. This means historic orders break if a product is deleted or its price changes. A proper fix requires creating an `order_items` table that captures product name, price, and quantity at order time. This involves schema migration, new entity/DTO/mapper, and updating `OrderService`.
- **Action**: Create `order_items` table, `OrderItem` entity, update `Order` to use `@OneToMany orderItems`, capture snapshot data during order creation.

### CODE-6 (part 2): Confirm tree-shaking for Lucide icons
- **Priority**: Low
- **Reason**: `BaseIcon.vue` now uses named imports for all icons used by the application. However, the import statement still imports all named icons at module load time. If the bundler (Vite/Rollup) tree-shakes unused exports from `lucide-vue-next`, this is already optimal. If not, consider dynamic import per icon name.
- **Action**: Verify bundle size impact and optimize if needed.

### PERF-7: Frontend server-side search
- **Priority**: Medium
- **Reason**: Backend search endpoint (`GET /api/products?q=...`) is implemented. `AppHome.vue` still uses client-side filtering for the search input. Should be updated to use the server-side search endpoint with debounce.
- **Action**: Update `AppHome.vue` to call `productService.getAll({ params: { q: searchQuery } })` with debouncing instead of filtering cached results.

### Test coverage gaps
- **Priority**: Medium
- **Reason**: Controller tests for `UserController`, `ProductController`, `DeliveryController`, `AuthController`, `EstablishmentController`, and `RestauranteController` are not yet implemented as `@WebMvcTest`. Vue component tests for `DeliveryDashboard.vue`, `RestaurantDashboard.vue`, `AppCart.vue`, `AppOrders.vue`, `CheckoutPix.vue`, and `DeliveryTracker.vue` are also pending.
- **Action**: Implement the remaining `@WebMvcTest` controller tests and Vue Test Utils component tests following existing patterns.

### SEC-8: Magic byte content validation
- **Priority**: Low
- **Reason**: File upload type validation currently relies on the HTTP `Content-Type` header, which can be spoofed. Should add actual magic byte detection for uploaded images.
- **Action**: Implement file header inspection in `LocalFileStorageService.store()` before accepting the file.
