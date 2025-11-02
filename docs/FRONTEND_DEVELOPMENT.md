# Guia de Desenvolvimento do Frontend

Este documento fornece instruções para executar e trabalhar com o projeto frontend de forma isolada.

## Configuração do Projeto

Para instalar as dependências, execute o seguinte comando no diretório `frontend-delivery`:

```bash
npm install
```

## Comandos Principais

### Servidor de Desenvolvimento

Para iniciar o servidor de desenvolvimento com hot-reload, execute:

```bash
npm run dev
```

O servidor estará disponível em `http://localhost:5173`.

### Build de Produção

Para compilar e minificar os arquivos para produção, execute:

```bash
npm run build
```

Os arquivos otimizados serão gerados no diretório `dist`.

### Linter

Para verificar e corrigir erros de linting no código, execute:

```bash
npm run lint
```

## Solução de Problemas Comuns

### `Uncaught ReferenceError: global is not defined`

Este erro ocorre porque a biblioteca `sockjs-client` (utilizada para WebSockets) tenta acessar a variável `global`, que é específica do ambiente Node.js e não está presente diretamente nos navegadores. Para resolver isso, um polyfill é necessário.

**Solução:**

Adicione a seguinte configuração ao seu `vite.config.js`:

```javascript
define: {
  global: 'globalThis',
},
```

Isso instrui o Vite a substituir todas as ocorrências de `global` por `globalThis`, que é o objeto global universalmente disponível em todos os ambientes JavaScript (incluindo navegadores).

## Status Atual

Todas as funcionalidades principais do frontend foram implementadas conforme o `PLANO_ACAO_MVP.md`. Atualmente, estamos na fase de testes e refinamento para garantir a estabilidade e a melhor experiência do usuário.