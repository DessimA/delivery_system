# 📋 Planejamento Detalhado - Sistema de Delivery

---

## 📜 Índice

1. [Visão Geral do Projeto](#-visão-geral-do-projeto)
2. [Definição de Roles e Responsabilidades](#-definição-de-roles-e-responsabilidades)
3. [Detalhamento das Telas por Usuário](#-detalhamento-das-telas-por-usuário)
   - [Telas do ADMIN](#-telas-do-admin)
   - [Telas do USER (Cliente)](#-telas-do-user-cliente)
   - [Telas do RESTAURANT](#-telas-do-restaurant-planejadas)
   - [Telas do DELIVERY](#-telas-do-delivery-planejadas)
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
Desenvolver um sistema completo de delivery que conecte restaurantes/estabelecimentos, clientes e entregadores em uma plataforma integrada, oferecendo uma experiência fluida de compra e entrega.

### Escopo
Sistema web responsivo com arquitetura de microsserviços, composto por:
- **Backend API RESTful** (Spring Boot + PostgreSQL)
- **Frontend Web** (Vue.js + Vite)
- **Sistema de Autenticação** baseado em roles
- **Gestão completa de pedidos** e entregas
- **Interface administrativa** para gerenciamento

---

## 👥 Definição de Roles e Responsabilidades

| Role | Descrição | Responsabilidades Principais |
| :--- | :--- | :--- |
| 🔧 **ADMIN** | Administrador do Sistema | Gerenciamento completo, supervisão de operações, controle de usuários. |
| 🛍️ **USER** | Cliente Final | Realizar pedidos, gerenciar perfil, acompanhar histórico. |
| 🏪 **RESTAURANT** | Dono de Estabelecimento | Gerenciar cardápio, processar pedidos, controlar disponibilidade. | 
| 🚚 **DELIVERY** | Entregador | Aceitar e realizar entregas, atualizar status. |

---

## 🖥️ Detalhamento das Telas por Usuário

### 📱 **Telas do ADMIN**

- **Dashboard Principal**: Painel com métricas, gráficos de vendas e alertas. `(Planejado)`
- **Gerenciamento de Usuários**: CRUD completo de todos os usuários e suas roles. `(Planejado)`
- **Gerenciamento de Produtos**: Controle global do catálogo, aprovação e moderação. `(Implementado - Básico)`
- **Gerenciamento de Pedidos**: Monitoramento de todos os pedidos do sistema. `(Implementado - Básico)`
- **Relatórios e Análises**: Geração de relatórios de negócio. `(Planejado)`

### 🛒 **Telas do USER (Cliente)**

- **Página Inicial/Catálogo**: Listagem de produtos, filtros e pesquisa. `(Implementado)`
- **Carrinho de Compras**: Gerenciamento dos itens selecionados. `(Implementado)`
- **Finalização de Pedido**: Checkout, endereço e pagamento. `(Implementado - Básico)`
- **Histórico de Pedidos**: Visualização de pedidos anteriores. `(Implementado)`
- **Perfil do Usuário**: Gerenciamento de dados pessoais e senha. `(Implementado - Básico)`
- **Acompanhamento de Pedido**: Tracking em tempo real do pedido. `(Planejado)`

### 🏪 **Telas do RESTAURANT** *[Planejadas]*

- **Dashboard do Estabelecimento**: Painel de controle com resumo de vendas e pedidos.
- **Gerenciamento de Cardápio**: CRUD do menu do próprio estabelecimento.
- **Gestão de Pedidos**: Aceitar/recusar e processar pedidos recebidos.

### 🚚 **Telas do DELIVERY** *[Planejadas]*

- **Entregas Disponíveis**: Lista de pedidos prontos para entrega.
- **Entrega em Andamento**: Controle e atualização de status da entrega atual.

---

## 🛠️ Arquitetura Técnica Detalhada

### Backend (Spring Boot)

Estrutura de camadas clara e desacoplada para facilitar a manutenção.

```
backend-delivery/
├── config/         # Configurações (Security, CORS, etc.)
├── controller/     # Endpoints REST
├── dto/            # Data Transfer Objects (Request/Response)
├── mapper/         # Conversores Entity <-> DTO
├── model/          # Entidades JPA
├── repository/     # Interfaces Spring Data
├── security/       # Implementações de segurança (JWT)
└── service/        # Lógica de negócio
```

### Frontend (Vue.js)

Arquitetura baseada em componentes para máxima reutilização e clareza.

```
frontend-delivery/
├── src/
│   ├── api/          # Configuração do Axios
│   ├── assets/       # Imagens, fontes, etc.
│   ├── components/   # Componentes reutilizáveis (Base, Layout, etc.)
│   ├── composables/  # Funções reutilizáveis (useApi, etc.)
│   ├── router/       # Configuração de rotas (Vue Router)
│   ├── stores/       # Gerenciamento de estado global (Pinia)
│   └── views/        # Páginas principais da aplicação
```

### Banco de Dados

O modelo de dados atual inclui `Pessoa`, `Role`, `Produto` e `Pedido`. O plano de expansão inclui tabelas para `Estabelecimento`, `Categoria`, `Entrega` e `Avaliacao` para suportar as novas funcionalidades.

---

## 📋 Funcionalidades Detalhadas

- **Implementadas**: Sistema de autenticação, gestão básica de produtos e pedidos, e fluxo completo do cliente (catálogo, carrinho, checkout, histórico).
- **Planejadas**: Sistema de estabelecimentos, sistema de entregas, notificações, integração com gateways de pagamento e sistema de avaliações.

---

## 📅 Cronograma de Desenvolvimento

O projeto está dividido em fases, começando com a solidificação da base atual e evoluindo para funcionalidades mais complexas.

- **Fase 1**: Melhorias da Base (Testes, Validações, Cache)
- **Fase 2**: Sistema de Estabelecimentos
- **Fase 3**: Sistema de Entregas
- **Fase 4**: Sistema de Pagamentos
- **Fase 5**: Funcionalidades Avançadas (Notificações, Avaliações)
- **Fase 6**: Otimizações e Lançamento

---

## 🔧 Configuração de Desenvolvimento

As instruções detalhadas para configurar e executar o ambiente de desenvolvimento estão no [**README.md principal**](../README.md).

---

## 📊 Métricas e KPIs

O sucesso do projeto será medido através de métricas de negócio e técnicas, como:
- **Negócio**: Número de pedidos, valor médio, taxa de conversão.
- **Técnicas**: Uptime da aplicação, tempo de resposta da API, cobertura de testes.

---

## 🚀 Próximos Passos Imediatos

As prioridades atuais para o desenvolvimento são:

1.  **Alta Prioridade**: Implementar testes automatizados, sistema de estabelecimentos e sistema de pagamentos.
2.  **Média Prioridade**: Sistema de entregas, notificações em tempo real e sistema de avaliações.
3.  **Baixa Prioridade**: Dashboard de analytics, sistema de promoções e internacionalização.

---

## 📝 Considerações Importantes

- **Segurança**: Migrar de HTTP Basic para JWT, implementar validação de inputs e HTTPS.
- **Performance**: Paginação, cache, otimização de imagens e CDN.
- **Escalabilidade**: A arquitetura de microsserviços foi escolhida para suportar o crescimento.
- **Monitoramento**: Planejamento para APM, logs centralizados e alertas.

---

Este planejamento serve como um guia para o desenvolvimento contínuo do sistema, devendo ser atualizado conforme o projeto evolui e novos requisitos surgem.
