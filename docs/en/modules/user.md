# User Module

## Files

- `controller/UserController.java`: `POST /api/users/register` for registration (uses `UserRequestDTO`), `PUT /api/users/me` for profile updates (uses `UserUpdateRequestDTO`, which does not require password).

- `dto/UserRequestDTO.java`: Used only for registration. Requires `@Size(min = 8)` on password.

- `dto/UserUpdateRequestDTO.java`: New DTO for profile updates. All fields except `password` are required. The `password` field has no validation annotation, so it can be omitted or empty.

- `service/UserService.java`: `createUser` now checks both email and CPF uniqueness. `updateProfile` accepts `UserUpdateRequestDTO` and only re-encodes password if a non-blank value is provided.

- `exception/CpfAlreadyExistsException.java`: New exception, mapped to HTTP 409 via `RestExceptionHandler`.

- `repository/UserRepository.java`: Added `findByCpf(Cpf)` and `findByCpfValue(String)` for CPF uniqueness checking. `@EntityGraph` now includes `establishment` to prevent lazy loading issues.

## Design Decisions

- **Separate DTOs for registration and update.** Registration must enforce password strength. Profile update should not require a password, allowing users to change name/address without re-entering their password. The frontend strips the `password` key when empty before sending.

- **Role seeding via Flyway migration.** Roles are seeded by a database migration, guaranteeing they exist in every environment regardless of profile.

- **CPF uniqueness.** A `UNIQUE` constraint at the database level plus an explicit check in the service prevents duplicate CPFs. The `CpfAlreadyExistsException` follows the same pattern as `EmailAlreadyExistsException`.
