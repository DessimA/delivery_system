# Padrões de Desenvolvimento

## 1. Convenções de Nomenclatura
- **Idioma:** 100% Inglês para código, banco de dados e comentários.
- **Classes:** PascalCase (ex: `OrderService`).
- **Métodos/Variáveis:** camelCase (ex: `createOrder`).
- **Tabelas do Banco:** snake_case no plural (ex: `order_products`).

## 2. Padrões de Código

### 2.1. Injeção de Dependência
- **Regra:** Sempre usar **Injeção via Construtor**.
- **Ferramenta:** Use `@RequiredArgsConstructor` do Lombok e campos `private final`.

### 2.2. Estratégia de DTO
- **Regra:** Usar **Java Records** para todos os DTOs.
- **Isolamento:** Jamais expor Entidades diretamente na API.

### 2.3. Tratamento de Erros
- **Global Advice:** Use `@RestControllerAdvice` para capturar exceções.
- **Exceções Customizadas:** Crie exceções de domínio (ex: `ResourceNotFoundException`).

## 3. Validação de Entrada
- Use **Jakarta Bean Validation** (`@NotNull`, `@NotBlank`).
- **Fail-Fast:** Valide no nível do Controller usando `@Valid`.