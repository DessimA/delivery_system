# Padrões de Desenvolvimento

## 1. Convenções de Nomenclatura
- **Idioma:** 100% Inglês para código (Backend e Frontend), banco de dados e comentários.
- **Classes Backend:** PascalCase (ex: `OrderService`).
- **Métodos/Variáveis:** camelCase (ex: `createOrder`).
- **Tabelas do Banco:** snake_case no plural (ex: `order_products`).
- **Componentes Frontend:** PascalCase (ex: `ProductCard.vue`).
- **Arquivos Frontend:** camelCase para arquivos não-componentes (ex: `auth.service.js`).

## 2. Padrões de Código Backend
- **Injeção de Dependência:** Sempre usar **Injeção via Construtor** com `@RequiredArgsConstructor`.
- **Estratégia de DTO:** Usar **Java Records**. O contrato da API deve ser nativamente em Inglês.
- **Tratamento de Erros:** Use `@RestControllerAdvice` e exceções de domínio.

## 3. Padrões de Código Frontend (Vue 3)
- **Composition API:** Usar `<script setup>` e Composables.
- **Camada de Services:** Todas as chamadas de API devem passar pela pasta `/src/services/`.
- **Persistência de Estado:** Usar a utilidade `storage.js` (sessionStorage).

## 4. Biblioteca de Componentes UI
Componentes base padronizados para UX consistente:

| Componente | Props Principais | Descrição |
| :--- | :--- | :--- |
| **BaseButton** | `label`, `variant`, `loading`, `icon` | Suporta variantes primary, secondary, danger e light. |
| **BaseInput** | `id` (obrig), `label`, `modelValue`, `error` | Campo de formulário padrão com suporte a erros. |
| **BaseModal** | `modelValue`, `title` | Overlay para diálogos com slots `default` e `footer`. |
| **LoadingSpinner**| `size`, `color` | Indicador visual para processamento assíncrono. |

## 5. Melhores Práticas de Segurança
- **Prevenção de XSS**: Vue escapa automaticamente `{{ }}`. Evite `v-html` sem sanitização.
- **CSRF**: Axios inclui tokens CSRF se fornecidos pelo backend.
- **Dados Sensíveis**: Mascarar CPF/Cartões na UI. Nunca logar informações sensíveis.

## 6. Comunicação Real-time
- **Padrão:** Usar STOMP sobre WebSocket para atualizações ao vivo.
- **Tópicos:** Nomenclatura em Inglês (ex: `/topic/orders/{id}`).

## 7. Histórico de Evolução e Refatoração
O sistema passou por uma grande refatoração para resolver os seguintes problemas identificados em auditoria:
- **Segurança:** Migração do armazenamento de JWT do `localStorage` para o `sessionStorage` para mitigar riscos de XSS.
- **Arquitetura:** Eliminação de chamadas diretas ao Axios nos componentes através da implementação obrigatória da **Camada de Services**.
- **Consistência:** Remoção de nomenclatura mista (Português/Inglês), estabelecendo código e contrato de API 100% em Inglês.
- **Modularidade:** Decomposição de componentes gigantes em funcionalidades especializadas em `/src/components/features/`.