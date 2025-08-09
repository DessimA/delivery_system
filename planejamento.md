# 📋 Planejamento Detalhado - Sistema de Delivery

## 🎯 Visão Geral do Projeto

### Objetivo
Desenvolver um sistema completo de delivery que conecte restaurantes/estabelecimentos, clientes e entregadores em uma plataforma integrada, oferecendo uma experiência fluida de compra e entrega.

### Escopo
Sistema web responsivo com arquitetura de microsserviços, composto por:
- **Backend API RESTful** (Spring Boot + PostgreSQL)
- **Frontend Web** (Vue.js + Bootstrap)
- **Sistema de Autenticação** baseado em roles
- **Gestão completa de pedidos** e entregas
- **Interface administrativa** para gerenciamento

---

## 👥 Definição de Roles e Responsabilidades

### 🔧 **ADMIN (Administrador)**
**Responsabilidades:**
- Gerenciamento completo do sistema
- Supervisão de todas as operações
- Controle de usuários e estabelecimentos
- Análise de relatórios e métricas

**Permissões:**
- ✅ Acesso total à API
- ✅ CRUD completo de todos os recursos
- ✅ Visualização de todos os pedidos
- ✅ Gerenciamento de usuários e roles
- ✅ Configurações do sistema
- ✅ Relatórios e dashboards

### 🛍️ **USER (Cliente)**
**Responsabilidades:**
- Realizar pedidos
- Gerenciar perfil pessoal
- Acompanhar histórico de pedidos

**Permissões:**
- ✅ Visualizar produtos disponíveis
- ✅ Criar e gerenciar pedidos próprios
- ✅ Atualizar dados pessoais
- ✅ Visualizar histórico de pedidos próprios
- ❌ Acesso a dados de outros usuários
- ❌ Funções administrativas

### 🏪 **RESTAURANT (Estabelecimento)** *[Planejado]*
**Responsabilidades:**
- Gerenciar cardápio próprio
- Processar pedidos recebidos
- Controlar disponibilidade de produtos
- Definir horários de funcionamento

**Permissões:**
- ✅ CRUD dos próprios produtos
- ✅ Visualizar pedidos do estabelecimento
- ✅ Atualizar status dos pedidos
- ✅ Gerenciar horários e disponibilidade
- ❌ Acesso a dados de outros estabelecimentos

### 🚚 **DELIVERY (Entregador)** *[Planejado]*
**Responsabilidades:**
- Aceitar entregas disponíveis
- Atualizar status das entregas
- Reportar problemas na entrega

**Permissões:**
- ✅ Visualizar pedidos disponíveis para entrega
- ✅ Aceitar/recusar entregas
- ✅ Atualizar status de entrega
- ✅ Reportar ocorrências
- ❌ Acesso a dados de pagamento

---

## 🖥️ Detalhamento das Telas por Usuário

### 📱 **Telas do ADMIN**

#### 1. **Dashboard Principal**
- **Descrição:** Painel centralizado com métricas e indicadores
- **Funcionalidades:**
  - Resumo de pedidos (hoje, semana, mês)
  - Gráficos de vendas e performance
  - Alertas e notificações importantes
  - Ações rápidas (novos usuários, pedidos pendentes)
- **Componentes:** Cards de métricas, gráficos, tabelas resumo
- **Estado:** 🔄 Planejado

#### 2. **Gerenciamento de Usuários**
- **Descrição:** CRUD completo de todos os usuários do sistema
- **Funcionalidades:**
  - Listar todos os usuários com filtros
  - Criar/editar/desativar usuários
  - Alterar roles e permissões
  - Visualizar histórico de atividades
- **Componentes:** Tabela paginada, modais de edição, filtros
- **Estado:** 🔄 Planejado

#### 3. **Gerenciamento de Produtos**
- **Descrição:** Controle global do catálogo de produtos
- **Funcionalidades:**
  - Visualizar produtos de todos os estabelecimentos
  - Aprovar/reprovar produtos
  - Moderar descrições e imagens
  - Configurar categorias globais
- **Componentes:** Grid de produtos, formulários, upload de imagens
- **Estado:** ✅ Implementado (básico)

#### 4. **Gerenciamento de Pedidos**
- **Descrição:** Monitoramento e controle de todos os pedidos
- **Funcionalidades:**
  - Visualizar todos os pedidos do sistema
  - Filtrar por status, data, estabelecimento
  - Intervir em casos de problema
  - Gerar relatórios detalhados
- **Componentes:** Tabela avançada, filtros, modais de detalhes
- **Estado:** ✅ Implementado (básico)

#### 5. **Relatórios e Análises**
- **Descrição:** Geração de relatórios e análises de negócio
- **Funcionalidades:**
  - Relatórios de vendas por período
  - Performance de estabelecimentos
  - Análise de produtos mais vendidos
  - Exportação de dados (PDF, Excel)
- **Componentes:** Formulários de filtro, gráficos, botões de export
- **Estado:** 🔄 Planejado

### 🛒 **Telas do USER (Cliente)**

#### 1. **Página Inicial/Catálogo**
- **Descrição:** Listagem de produtos disponíveis para pedido
- **Funcionalidades:**
  - Visualizar produtos com imagens e preços
  - Filtrar por categoria, preço, estabelecimento
  - Pesquisar produtos específicos
  - Adicionar produtos ao carrinho
- **Componentes:** Grid de produtos, filtros, barra de pesquisa
- **Estado:** ✅ Implementado

#### 2. **Carrinho de Compras**
- **Descrição:** Gerenciamento dos itens selecionados
- **Funcionalidades:**
  - Visualizar itens no carrinho
  - Alterar quantidades
  - Remover itens
  - Calcular total com taxas
  - Finalizar pedido
- **Componentes:** Lista de itens, controles de quantidade, resumo
- **Estado:** ✅ Implementado

#### 3. **Finalização de Pedido**
- **Descrição:** Processo de checkout e confirmação
- **Funcionalidades:**
  - Confirmar endereço de entrega
  - Selecionar forma de pagamento
  - Adicionar observações
  - Confirmar pedido final
- **Componentes:** Formulários, resumo do pedido, botões de ação
- **Estado:** ✅ Implementado (básico)

#### 4. **Histórico de Pedidos**
- **Descrição:** Visualização de pedidos anteriores
- **Funcionalidades:**
  - Listar pedidos realizados
  - Filtrar por status e data
  - Visualizar detalhes de cada pedido
  - Repetir pedidos anteriores
- **Componentes:** Lista/cards de pedidos, filtros, detalhes
- **Estado:** ✅ Implementado

#### 5. **Perfil do Usuário**
- **Descrição:** Gerenciamento de dados pessoais
- **Funcionalidades:**
  - Editar informações pessoais
  - Gerenciar endereços
  - Alterar senha
  - Configurar preferências
- **Componentes:** Formulários de edição, validações
- **Estado:** ✅ Implementado (básico)

#### 6. **Acompanhamento de Pedido** *[Planejado]*
- **Descrição:** Tracking em tempo real do pedido
- **Funcionalidades:**
  - Status atual do pedido
  - Tempo estimado de entrega
  - Localização do entregador (se aplicável)
  - Notificações de mudança de status
- **Componentes:** Timeline de status, mapa, notificações
- **Estado:** 🔄 Planejado

### 🏪 **Telas do RESTAURANT** *[Planejadas]*

#### 1. **Dashboard do Estabelecimento**
- **Descrição:** Painel de controle do restaurante
- **Funcionalidades:**
  - Resumo de vendas do dia
  - Pedidos pendentes e em preparo
  - Performance de produtos
  - Alertas importantes
- **Estado:** 🔄 Planejado

#### 2. **Gerenciamento de Cardápio**
- **Descrição:** CRUD do menu do estabelecimento
- **Funcionalidades:**
  - Adicionar/editar/remover produtos
  - Upload de imagens dos pratos
  - Definir preços e promoções
  - Controlar disponibilidade
- **Estado:** 🔄 Planejado

#### 3. **Gestão de Pedidos**
- **Descrição:** Processamento de pedidos recebidos
- **Funcionalidades:**
  - Visualizar novos pedidos
  - Aceitar/recusar pedidos
  - Atualizar status de preparo
  - Definir tempo de preparo
- **Estado:** 🔄 Planejado

### 🚚 **Telas do DELIVERY** *[Planejadas]*

#### 1. **Entregas Disponíveis**
- **Descrição:** Lista de pedidos disponíveis para entrega
- **Funcionalidades:**
  - Visualizar pedidos prontos
  - Ver detalhes da entrega (endereço, valor)
  - Aceitar entregas
  - Filtrar por proximidade
- **Estado:** 🔄 Planejado

#### 2. **Entrega em Andamento**
- **Descrição:** Controle da entrega atual
- **Funcionalidades:**
  - Detalhes do pedido e cliente
  - Navegação para o endereço
  - Atualizar status da entrega
  - Reportar problemas
- **Estado:** 🔄 Planejado

---

## 🛠️ Arquitetura Técnica Detalhada

### **Backend (Spring Boot)**

#### Estrutura de Camadas
```
backend-delivery/
├── config/              # Configurações (Security, CORS, etc.)
├── controller/          # Endpoints REST
│   ├── ProdutoController
│   ├── UsuarioController
│   ├── PedidoController
│   └── AdminController (planejado)
├── dto/                 # Data Transfer Objects
│   ├── request/         # DTOs de entrada
│   └── response/        # DTOs de saída
├── mapper/              # Conversores Entity <-> DTO
├── model/               # Entidades JPA
├── repository/          # Interfaces Spring Data
├── security/            # Implementações de segurança
└── service/             # Lógica de negócio
```

#### Endpoints Principais

##### **Produtos**
- `GET /api/produtos` - Listar produtos (público)
- `POST /api/produtos` - Criar produto (ADMIN/RESTAURANT)
- `PUT /api/produtos/{id}` - Atualizar produto (ADMIN/RESTAURANT)
- `DELETE /api/produtos/{id}` - Remover produto (ADMIN)

##### **Usuários**
- `POST /api/usuarios/registro` - Registrar usuário
- `GET /api/usuarios/perfil` - Obter perfil atual
- `PUT /api/usuarios/perfil` - Atualizar perfil
- `GET /api/admin/usuarios` - Listar usuários (ADMIN)

##### **Pedidos**
- `POST /api/pedidos` - Criar pedido (USER)
- `GET /api/pedidos/meus` - Histórico pessoal (USER)
- `GET /api/pedidos` - Todos os pedidos (ADMIN)
- `PUT /api/pedidos/{id}/status` - Atualizar status (ADMIN/RESTAURANT)

#### Melhorias Planejadas
- ✅ Implementar WebSockets para notificações em tempo real
- ✅ Adicionar sistema de cache (Redis)
- ✅ Implementar rate limiting
- ✅ Adicionar logs estruturados
- ✅ Implementar testes automatizados (JUnit + MockMVC)

### **Frontend (Vue.js)**

#### Estrutura de Componentes
```
frontend-delivery/
├── public/              # Arquivos estáticos
├── src/
│   ├── api/            # Configuração Axios
│   ├── assets/         # Imagens, CSS
│   ├── components/     # Componentes reutilizáveis
│   │   ├── common/     # Componentes gerais
│   │   ├── admin/      # Componentes administrativos
│   │   └── user/       # Componentes do cliente
│   ├── router/         # Configuração de rotas
│   ├── store/          # Vuex/Pinia (estado global)
│   ├── views/          # Páginas principais
│   └── utils/          # Utilitários e helpers
```

#### Melhorias Planejadas
- ✅ Implementar estado global (Pinia)
- ✅ Adicionar PWA (Progressive Web App)
- ✅ Implementar lazy loading de rotas
- ✅ Adicionar testes unitários (Vitest)
- ✅ Melhorar acessibilidade (a11y)

### **Banco de Dados**

#### Modelo Atual
- **Pessoa** (usuários)
- **Role** (papéis/permissões)
- **Produto** (itens do cardápio)
- **Pedido** (orders)

#### Extensões Planejadas
```sql
-- Tabelas adicionais planejadas
CREATE TABLE Estabelecimento (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    cnpj VARCHAR(14) UNIQUE,
    endereco TEXT,
    telefone VARCHAR(20),
    horario_funcionamento JSONB,
    ativo BOOLEAN DEFAULT TRUE,
    usuario_id BIGINT REFERENCES Pessoa(codigo)
);

CREATE TABLE Categoria (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    descricao TEXT,
    ativo BOOLEAN DEFAULT TRUE
);

CREATE TABLE Entrega (
    id BIGSERIAL PRIMARY KEY,
    pedido_id BIGINT REFERENCES Pedido(codigoPedido),
    entregador_id BIGINT REFERENCES Pessoa(codigo),
    endereco_origem TEXT,
    endereco_destino TEXT,
    status VARCHAR(50),
    tempo_estimado INTEGER, -- em minutos
    valor_entrega DECIMAL(10,2),
    criado_em TIMESTAMP DEFAULT NOW(),
    entregue_em TIMESTAMP
);

CREATE TABLE Avaliacao (
    id BIGSERIAL PRIMARY KEY,
    pedido_id BIGINT REFERENCES Pedido(codigoPedido),
    cliente_id BIGINT REFERENCES Pessoa(codigo),
    nota INTEGER CHECK (nota >= 1 AND nota <= 5),
    comentario TEXT,
    criado_em TIMESTAMP DEFAULT NOW()
);
```

---

## 📋 Funcionalidades Detalhadas

### ✅ **Implementadas**

#### Sistema de Autenticação
- Login/logout com HTTP Basic
- Registro de novos usuários
- Controle de acesso baseado em roles
- Usuários padrão (admin/user) criados automaticamente

#### Gestão de Produtos
- CRUD completo de produtos
- Upload de imagens
- Listagem pública para clientes
- Controle de acesso para modificações

#### Gestão de Pedidos
- Criação de pedidos com múltiplos produtos
- Histórico personalizado por usuário
- Visualização administrativa de todos os pedidos
- Cálculo automático de valores

#### Interface do Cliente
- Catálogo de produtos responsivo
- Carrinho de compras funcional
- Processo de checkout
- Histórico de pedidos

### 🔄 **Em Desenvolvimento/Planejadas**

#### Sistema de Estabelecimentos
- **Prioridade:** Alta
- **Descrição:** Permitir que estabelecimentos gerenciem seus próprios cardápios
- **Funcionalidades:**
  - Registro de restaurantes/estabelecimentos
  - Dashboard específico para cada estabelecimento
  - Gestão independente de produtos
  - Controle de horário de funcionamento
  - Sistema de aprovação de novos estabelecimentos

#### Sistema de Entregas
- **Prioridade:** Alta
- **Descrição:** Gerenciamento completo do processo de entrega
- **Funcionalidades:**
  - Cadastro de entregadores
  - Sistema de distribuição de entregas
  - Tracking em tempo real
  - Cálculo automático de taxas de entrega
  - Integração com APIs de geolocalização

#### Sistema de Notificações
- **Prioridade:** Média
- **Descrição:** Notificações em tempo real para todos os usuários
- **Funcionalidades:**
  - WebSockets para comunicação em tempo real
  - Notificações push (PWA)
  - Email notifications
  - SMS notifications (integração externa)

#### Sistema de Pagamento
- **Prioridade:** Alta
- **Descrição:** Integração com gateways de pagamento
- **Funcionalidades:**
  - Múltiplas formas de pagamento
  - Integração com Stripe/PayPal/PagSeguro
  - Controle de transações
  - Sistema de reembolsos
  - Relatórios financeiros

#### Sistema de Avaliações
- **Prioridade:** Média
- **Descrição:** Feedback de clientes sobre pedidos e estabelecimentos
- **Funcionalidades:**
  - Avaliação de pedidos (1-5 estrelas)
  - Comentários e reviews
  - Sistema de moderação
  - Ranking de estabelecimentos
  - Métricas de satisfação

#### Dashboard Analytics
- **Prioridade:** Baixa
- **Descrição:** Painéis avançados com métricas e KPIs
- **Funcionalidades:**
  - Gráficos interativos (Chart.js/D3.js)
  - Métricas de vendas e performance
  - Relatórios automatizados
  - Exportação de dados
  - Alertas baseados em thresholds

---

## 📅 Cronograma de Desenvolvimento

### **Fase 1: Melhorias da Base (1-2 meses)**
- [ ] Implementar testes automatizados (backend e frontend)
- [ ] Melhorar validações e tratamento de erros
- [ ] Adicionar logs estruturados
- [ ] Implementar cache (Redis)
- [ ] Melhorar documentação da API

### **Fase 2: Sistema de Estabelecimentos (2-3 meses)**
- [ ] Criar modelo de dados para estabelecimentos
- [ ] Implementar role RESTAURANT
- [ ] Desenvolver dashboard do estabelecimento
- [ ] Implementar gestão de cardápio por estabelecimento
- [ ] Criar processo de aprovação de estabelecimentos

### **Fase 3: Sistema de Entregas (2-3 meses)**
- [ ] Implementar role DELIVERY
- [ ] Criar sistema de distribuição de entregas
- [ ] Desenvolver tracking em tempo real
- [ ] Integrar APIs de geolocalização
- [ ] Implementar cálculo de taxas de entrega

### **Fase 4: Sistema de Pagamentos (1-2 meses)**
- [ ] Integrar gateway de pagamento
- [ ] Implementar múltiplas formas de pagamento
- [ ] Criar sistema de controle de transações
- [ ] Desenvolver relatórios financeiros

### **Fase 5: Funcionalidades Avançadas (2-3 meses)**
- [ ] Sistema de notificações em tempo real
- [ ] Sistema de avaliações e reviews
- [ ] Dashboard analytics avançado
- [ ] PWA com notificações push
- [ ] Sistema de promoções e cupons

### **Fase 6: Otimizações e Lançamento (1 mês)**
- [ ] Testes de carga e performance
- [ ] Implementar CI/CD completo
- [ ] Configurar monitoramento (APM)
- [ ] Deploy em produção
- [ ] Documentação para usuários finais

---

## 🔧 Configuração de Desenvolvimento

### Pré-requisitos
- Java 22+
- Node.js 18+
- Docker & Docker Compose
- PostgreSQL (via Docker)
- Maven 3.9+

### Executar Localmente

#### Via Docker (Recomendado)
```bash
# Clone o repositório
git clone https://github.com/DessimA/delivery_system.git
cd delivery_system

# Switch para branch dev
git checkout dev

# Construir e executar
docker-compose build --no-cache
docker-compose up

# Acessar
# Frontend: http://localhost/
# Backend API: http://localhost:8080
# Swagger UI: http://localhost:8080/swagger-ui.html
```

#### Desenvolvimento Local
```bash
# Backend
cd backend-delivery
./mvnw spring-boot:run

# Frontend (em outro terminal)
cd frontend-delivery
npm install
npm run serve
```

### Usuários Padrão
- **Admin:** admin@example.com / admin123
- **Cliente:** user@example.com / user123

---

## 📊 Métricas e KPIs

### Métricas de Negócio
- Número de pedidos por período
- Valor médio dos pedidos
- Taxa de conversão (visitantes → pedidos)
- Tempo médio de entrega
- Satisfação do cliente (NPS)

### Métricas Técnicas
- Uptime da aplicação (>99.5%)
- Tempo de resposta da API (<200ms)
- Taxa de erro (<1%)
- Cobertura de testes (>80%)
- Performance do frontend (Lighthouse >90)

---

## 🚀 Próximos Passos Imediatos

### Alta Prioridade
1. **Implementar testes automatizados** - Garantir qualidade do código
2. **Sistema de estabelecimentos** - Permitir múltiplos restaurantes
3. **Melhorar UX/UI** - Interface mais intuitiva e atrativa
4. **Sistema de pagamentos** - Integração com gateway real

### Média Prioridade
5. **Sistema de entregas** - Gestão completa de delivery
6. **Notificações em tempo real** - Melhor comunicação
7. **Sistema de avaliações** - Feedback dos clientes
8. **PWA e mobile responsivo** - Experiência mobile

### Baixa Prioridade
9. **Dashboard analytics** - Métricas avançadas
10. **Sistema de promoções** - Marketing e vendas
11. **Multi-idioma** - Internacionalização
12. **App mobile nativo** - iOS/Android

---

## 📝 Considerações Importantes

### Segurança
- Implementar autenticação JWT (substituir HTTP Basic)
- Validação rigorosa de inputs
- Rate limiting para APIs
- HTTPS em produção
- Criptografia de dados sensíveis

### Performance
- Implementar paginação em todas as listagens
- Cache para dados frequentemente acessados
- Otimização de imagens
- CDN para assets estáticos
- Database indexing

### Escalabilidade
- Arquitetura de microsserviços
- Load balancing
- Database sharding (se necessário)
- Message queue para processamento assíncrono

### Monitoramento
- APM (Application Performance Monitoring)
- Logs centralizados
- Alertas automáticos
- Métricas de negócio em tempo real

---

Este planejamento serve como guia para o desenvolvimento contínuo do sistema, devendo ser atualizado conforme o projeto evolui e novos requisitos surgem.