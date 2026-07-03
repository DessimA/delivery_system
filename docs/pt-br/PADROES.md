# Padroes de Desenvolvimento

## 1. Convencoes de Nomenclatura

- **Idioma:** 100% Ingles para codigo (Backend e Frontend), banco de dados e comentarios.
- **Classes Backend:** PascalCase (ex: `OrderService`).
- **Metodos/Variaveis:** camelCase (ex: `createOrder`).
- **Tabelas do Banco:** snake_case plural (ex: `order_items`).
- **Componentes Frontend:** PascalCase (ex: `ProductCard.vue`).
- **Arquivos Frontend:** camelCase para arquivos nao-componente (ex: `auth.service.js`).

## 2. Padroes de Codigo Backend

- **Injecao de Dependencia:** Construtor com `@RequiredArgsConstructor`.
- **Estrategia de DTO:** Java Records. Contrato da API em Ingles.
- **Tratamento de Erros:** `@RestControllerAdvice` com excecoes de dominio.

## 3. Padroes de Codigo Frontend (Vue 3)

- **Composition API:** `<script setup>` e composables.
- **Camada de Services:** Todas as chamadas de API em `/src/services/`.
- **Persistencia de Estado:** `storage.js` (sessionStorage).

## 4. Biblioteca de Componentes UI

| Componente | Props Principais | Descricao |
| :--- | :--- | :--- |
| **BaseButton** | `label`, `variant`, `loading`, `icon` | Variantes primary, secondary, danger, light |
| **BaseInput** | `id` (obrig), `label`, `modelValue`, `error`, `readonly` | Campo de formulario com validacao |
| **BaseModal** | `modelValue`, `title` | Dialogo overlay com slots `default` e `footer` |
| **LoadingSpinner** | `size`, `color` | Indicador de processamento assincrono |

## 5. Boas Praticas de Seguranca

- **Prevencao de XSS:** Vue escapa `{{ }}` automaticamente. Evitar `v-html` sem sanitizacao.
- **Dados Sensiveis:** Mascarar CPF na UI. Nunca logar informacoes sensiveis.

## 6. Comunicacao em Tempo Real

- **Padrao:** STOMP sobre WebSocket para atualizacoes ao vivo.
- **Topicos:** Nomenclatura em Ingles (ex: `/topic/orders/{id}`).
