# 📋 Planejamento Detalhado - Sistema de Delivery

---

## 📜 Índice

1. [Visão Geral do Projeto](#-visão-geral-do-projeto)
2. [Definição de Roles e Responsabilidades](#-definição-de-roles-e-responsabilidades)
3. [Detalhamento das Telas por Usuário](#-detalhamento-das-telas-por-usuário)
   - [Telas do ADMIN](#-telas-do-admin)
   - [Telas do USER (Cliente)](#-telas-do-user-cliente)
   - [Telas do RESTAURANT](#-telas-do-restaurant)
   - [Telas do DELIVERY](#-telas-do-delivery)
4. [Arquitetura Técnica Detalhada](#-arquitetura-técnica-detalhada)
   - [Backend (Spring Boot)](#backend-spring-boot)
   - [Frontend (Vue.js)](#frontend-vuejs)
   - [Banco de Dados](#banco-de-dados)
5. [Funcionalidades Detalhadas](#-funcionalidades-detalhadas)
6. [Cronograma de Desenvolvimento](#-cronograma-de-desenvolvimento)
7. [Configuração de Desenvolvimento](#-configuração-de-desenvolvimento)
8. [Métricas e KPIs](#-métricas-e-kpis)
9. [Próximos Passos Imediatos](#-próximos-passos-imediatos)
10. [Considerações Importantes](#-considerações-importantes)

---

## 🎯 Visão Geral do Projeto

### Objetivo
Desenvolver um sistema completo de delivery que conecte restaurantes/estabelecimentos, clientes e entregadores em uma plataforma integrada, oferecendo uma experiência fluida de compra e entrega, com foco em um MVP robusto para portfólio.

### Escopo
Sistema web responsivo com arquitetura de microsserviços, composto por:
- **Backend API RESTful** (Spring Boot + PostgreSQL) com funcionalidades de autenticação JWT, gerenciamento de estabelecimentos, produtos, pedidos, entregas e pagamentos PIX simulados.
- **Frontend Web** (Vue.js + Vite) com interfaces para clientes, restaurantes, entregadores e administradores, incluindo tracking de entregas em tempo real via WebSockets.
- **Sistema de Autenticação** baseado em roles (ADMIN, USER, RESTAURANT, DELIVERY).
- **Gestão completa de pedidos** e entregas com atualizações em tempo real.
- **Interfaces dedicadas** para cada perfil de usuário.

---

## 👥 Definição de Roles e Responsabilidades

| Role | Descrição | Responsabilidades Principais | Status |
| :--- | :--- | :--- | :--- |
| 🔧 **ADMIN** | Administrador do Sistema | Gerenciamento completo, supervisão de operações, controle de usuários. | ✅ Implementado |
| 🛍️ **USER** | Cliente Final | Realizar pedidos, gerenciar perfil, acompanhar histórico. | ✅ Implementado |
| 🏪 **RESTAURANT** | Dono de Estabelecimento | Gerenciar cardápio, processar pedidos, controlar disponibilidade. | ✅ Implementado |
| 🚚 **DELIVERY** | Entregador | Aceitar e realizar entregas, atualizar status. | ✅ Implementado |

---

## 🖥️ Detalhamento das Telas por Usuário

### 📱 **Telas do ADMIN**

- **Dashboard Principal**: Painel com métricas, gráficos de vendas e alertas. `(Implementado - Básico)`
- **Gerenciamento de Usuários**: CRUD completo de todos os usuários e suas roles. `(Implementado - Básico)`
- **Gerenciamento de Produtos**: Controle global do catálogo, aprovação e moderação. `(Implementado - Básico)`
- **Gerenciamento de Pedidos**: Monitoramento de todos os pedidos do sistema. `(Implementado - Básico)`
- **Relatórios e Análises**: Geração de relatórios de negócio. `(Planejado)`

### 🛒 **Telas do USER (Cliente)**

- **Página Inicial/Catálogo**: Listagem de produtos, filtros e pesquisa. `(Implementado)`
- **Carrinho de Compras**: Gerenciamento dos itens selecionados. `(Implementado)`
- **Finalização de Pedido**: Checkout, endereço e pagamento PIX. `(Implementado)`
- **Histórico de Pedidos**: Visualização de pedidos anteriores. `(Implementado)`
- **Perfil do Usuário**: Gerenciamento de dados pessoais e senha. `(Implementado)`
- **Acompanhamento de Pedido**: Tracking em tempo real do pedido via WebSocket. `(Implementado)`

### 🏪 **Telas do RESTAURANT**

- **Dashboard do Estabelecimento**: Painel de controle com resumo de vendas e pedidos. `(Implementado)`
- **Gerenciamento de Cardápio**: CRUD do menu do próprio estabelecimento. `(Implementado)`
- **Gestão de Pedidos**: Aceitar/recusar e processar pedidos recebidos. `(Implementado - Básico)`

### 🚚 **Telas do DELIVERY**

- **Entregas Disponíveis**: Lista de pedidos prontos para entrega. `(Implementado)`
- **Entrega em Andamento**: Controle e atualização de status da entrega atual. `(Implementado)`

---

## 🛠️ Arquitetura Técnica Detalhada

### Backend (Spring Boot)

Estrutura de camadas clara e desacoplada para facilitar a manutenção, utilizando:
- **Spring Security** para autenticação JWT e autorização baseada em roles.
- **Spring Data JPA** com Hibernate para persistência de dados no PostgreSQL.
- **WebSockets (STOMP)** para comunicação em tempo real.
- **Cloudinary** para gerenciamento de upload de imagens.
- **ZXing** para geração de QR Codes PIX.
- **Lombok e MapStruct** para reduzir boilerplate e facilitar mapeamento de DTOs.

```
backend-delivery/
├── config/         # Configurações (Security, CORS, WebSocket, etc.)
├── controller/     # Endpoints REST
├── dto/            # Data Transfer Objects (Request/Response)
├── exception/      # Classes de exceção customizadas
├── mapper/         # Conversores Entity <-> DTO
├── model/          # Entidades JPA e Enums
├── repository/     # Interfaces Spring Data
├── security/       # Implementações de segurança (JWT)
└── service/        # Lógica de negócio
```

### Frontend (Vue.js)

Arquitetura baseada em componentes para máxima reutilização e clareza, utilizando:
- **Vite** como ferramenta de build e servidor de desenvolvimento.
- **Pinia** para gerenciamento de estado global.
- **Vue Router** para navegação e rotas protegidas.
- **Sass** para estilização.
- **Axios** para requisições HTTP.
- **SockJS e StompJS** para comunicação WebSocket.

```
frontend-delivery/
├── src/
│   ├── api/          # Configuração do Axios
│   ├── assets/       # Imagens, fontes, etc.
│   ├── components/   # Componentes reutilizáveis (Base, Cart, Layout, Order, Product, Profile, etc.)
│   ├── composables/  # Funções reutilizáveis (useApi, useNotifications, useWebSocket)
│   ├── plugins/      # Plugins Vue (e.g., Axios)
│   ├── router/       # Configuração de rotas (Vue Router)
│   ├── stores/       # Gerenciamento de estado global (Pinia)
│   ├── styles/       # Estilos globais e variáveis SCSS
│   ├── tests/        # Testes de unidade/componente
│   └── views/        # Páginas principais da aplicação
```

### Banco de Dados

PostgreSQL, com modelo de dados que inclui `Usuario`, `Role`, `Estabelecimento`, `Produto`, `Pedido`, `Entrega` e `Pagamento` para suportar todas as funcionalidades do MVP.

---

## 📋 Funcionalidades Detalhadas

- **Implementadas**: Todas as funcionalidades do MVP, incluindo sistema de autenticação, gerenciamento completo de estabelecimentos e produtos, fluxo de pedidos com pagamento PIX simulado, sistema de entregas com tracking em tempo real via WebSockets, e dashboards dedicados para cada perfil de usuário.
- **Planejadas (Pós-MVP)**: Sistema de avaliações, gestão de categorias de produtos, configuração de taxa de entrega dinâmica, funcionalidades avançadas de relatórios.

---

## 📅 Cronograma de Desenvolvimento

O projeto foi desenvolvido em 3 fases, com todas as funcionalidades do MVP já implementadas:

- **Fase 1**: Sistema de Estabelecimentos (Concluída)
- **Fase 2**: Sistema de Entregas e Pagamento PIX Simulado (Concluída)
- **Fase 3**: Polish (UX/UI, Validações, Responsividade, Animações) e Preparação para Deploy (Concluída)

Atualmente, o projeto está na fase de testes finais e refinamento pós-deploy.

---

## 🔧 Configuração de Desenvolvimento

As instruções detalhadas para configurar e executar o ambiente de desenvolvimento estão no [**README.md principal**](../README.md).

---

## 📊 Métricas e KPIs

O sucesso do projeto será medido através de métricas de negócio e técnicas, como:
- **Negócio**: Número de pedidos, valor médio, taxa de conversão (após lançamento).
- **Técnicas**: Uptime da aplicação, tempo de resposta da API, cobertura de testes (em progresso).

---

## 🚀 Próximos Passos Imediatos

As prioridades atuais para o desenvolvimento são:

1.  **Finalizar Testes**: Concluir testes unitários e de integração no backend, e realizar testes E2E no frontend.
2.  **Documentação Técnica**: Criar diagramas de arquitetura e modelo de dados, e detalhar decisões técnicas.
3.  **Refinamento Pós-Deploy**: Ajustes finos e correções identificadas durante os testes em ambiente de produção.

---

## 📝 Considerações Importantes

- **Segurança**: Implementação de JWT para autenticação e autorização, validação de inputs e HTTPS (em produção).
- **Performance**: Paginação, otimização de imagens (Cloudinary), e otimizações de build no frontend.
- **Escalabilidade**: A arquitetura de microsserviços e o uso de Docker facilitam a escalabilidade futura.
- **Monitoramento**: Logs detalhados e relatórios de erro para facilitar a depuração e monitoramento.

---

Este planejamento serve como um guia para o desenvolvimento contínuo do sistema, devendo ser atualizado conforme o projeto evolui e novos requisitos surgem.