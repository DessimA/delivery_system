# Guia de Testes

## 1. A Piramide de Testes

```mermaid
pie title Distribuicao de Testes
    "Testes de Unidade (Services/Stores)" : 70
    "Testes de Integracao (API/Componentes)" : 20
    "Testes E2E (Smoke)" : 10
```

## 2. Testes Backend (JUnit 5 + Mockito)

- **Services:** Isolar logica de negocio com mocks.
- **Controllers:** `@WebMvcTest` com `MockMvc`.
- **Nomeacao:** `should[ExpectedBehavior]When[Condition]`.

## 3. Testes Frontend (Vitest + Vue Test Utils)

- **Estrutura:** Testes em `src/tests/` com `*.test.js`.
- **Testes de Unidade:** Testar stores Pinia e composables isoladamente. Usar `setActivePinia(createPinia())` no `beforeEach`.
- **Testes de Componente:** Montar componentes e simular interacoes.
- **Mocks:** `vi.mock` para isolar dependencias (Axios, Router).
- **Comandos:**
  - `npm run test`: Executa todos os testes.
  - `npm run test -- --coverage`: Gera relatorio de cobertura.

## 4. Quality Gates

- **Null Safety:** Uso rigoroso de anotacoes de validacao.
- **Contexto de portfolio:** O projeto simula servicos externos (pagamento via link QR Code, mapa estatico para rastreio) sem integrar APIs reais. Os testes refletem esses fluxos simulados.
