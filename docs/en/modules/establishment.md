# Establishment Module

## Files

- `controller/EstablishmentController.java`: Added `POST /api/establishments` (create) and `PUT /api/establishments/{id}` (update) endpoints. The create endpoint links the establishment to the authenticated restaurant owner.

- `service/EstablishmentService.java`: `findAll()` now filters by `active = true` so deactivated establishments are hidden from customers.

- `repository/EstablishmentRepository.java`: Added `findByActiveTrue()` method.

## Design Decisions

- **Restaurant owner creates their own establishment.** The `POST` endpoint is available to both `ROLE_ADMIN` and `ROLE_RESTAURANT`. A restaurant owner's establishment is automatically linked to their user.

- **Active filter.** The `active` flag already existed on the entity but was not used in the public listing. Returning only active establishments prevents customers from ordering from deactivated restaurants without requiring a full deletion.
