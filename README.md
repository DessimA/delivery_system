# Delivery System 🍔

<p align="center">
  <img src="https://img.shields.io/badge/Java-17-blue?logo=java&logoColor=white" alt="Java 17">
  <img src="https://img.shields.io/badge/Spring_Boot-3.x-green?logo=spring&logoColor=white" alt="Spring Boot 3.x">
  <img src="https://img.shields.io/badge/Vue.js-3.x-brightgreen?logo=vue.js&logoColor=white" alt="Vue.js 3.x">
  <img src="https://img.shields.io/badge/Docker-blue?logo=docker&logoColor=white" alt="Docker">
  <img src="https://img.shields.io/badge/PostgreSQL-purple?logo=postgresql&logoColor=white" alt="PostgreSQL">
</p>

<p align="center">
  Um sistema de delivery de comida completo, construído com tecnologias modernas e uma infraestrutura robusta para facilitar o desenvolvimento e a implantação.
</p>

---

## 🌟 Visão Geral

O Delivery System é uma plataforma que conecta restaurantes, clientes e entregadores. Ele oferece uma experiência de usuário fluida, desde a escolha do prato até a entrega, com um backend poderoso e um frontend reativo e moderno.

### 💡 Recursos Principais ✨

- **Sistema Multi-Restaurante**: Gerenciamento de múltiplos estabelecimentos, cada um com seu próprio cardápio.
- **Gerenciamento de Produtos**: Restaurantes podem adicionar, editar e remover produtos de seus cardápios, incluindo upload de imagens via Cloudinary.
- **Pagamento PIX Simulado**: Um fluxo de pagamento PIX completo, com geração de QR Code e simulação de confirmação em tempo real. **(Problema: 401 Unauthorized na finalização do pedido)**
- **Sistema de Entregas Automatizado**: Gerenciamento do ciclo de vida da entrega, desde a criação do pedido até a entrega final, com atribuição automática a entregadores disponíveis.
- **Tracking de Entrega em Tempo Real**: Clientes e entregadores podem acompanhar o status da entrega via WebSockets, com atualizações em tempo real.
- **Dashboards por Papel**: Interfaces dedicadas para Administradores, Restaurantes e Entregadores.
- **Autenticação JWT**: Segurança robusta com autenticação e autorização baseada em tokens.
- **Frontend Reativo e Responsivo**: Construído com Vue.js 3, oferecendo uma experiência de usuário moderna, responsiva e com animações.
- **Infraestrutura Dockerizada**: Ambiente de desenvolvimento e produção empacotados em contêineres para fácil configuração e escalabilidade.
- **Validações de Entrada**: Validações básicas implementadas no backend para garantir a integridade dos dados.

### 📸 Screenshots e GIFs

_Em breve: Imagens e GIFs demonstrativos da aplicação em funcionamento. Atualmente, estamos focados na implementação e testes das funcionalidades principais._


## 🛠️ Tecnologias Utilizadas

| Categoria   | Tecnologia                                                                                                  |
|-------------|-------------------------------------------------------------------------------------------------------------|
| **Backend** | Java 17, Spring Boot, Spring Security, JPA/Hibernate, Maven, Cloudinary, WebSockets (STOMP), Lombok, MapStruct, ZXing (QR Code)                   |
| **Frontend**| Vue.js 3, Vite, Pinia (para gerenciamento de estado), Vue Router, Sass, Axios, SockJS, StompJS                                 |
| **Banco de Dados** | PostgreSQL                                                                                                  |
| **Infraestrutura** | Docker, Docker Compose                                                                                      |
| **Outros** | JWT (JSON Web Tokens), Swagger/OpenAPI                                                              |

## 🚀 Começando

Este guia irá ajudá-lo a configurar e executar o projeto em seu ambiente de desenvolvimento local. É mais fácil do que você imagina!

### 1. Pré-requisitos

Antes de começar, certifique-se de que você tem os seguintes softwares instalados. Eles são essenciais para que a mágica do Docker funcione.

- [**Docker Desktop**](https://www.docker.com/products/docker-desktop/): A ferramenta que gerencia nossos contêineres. Baixe a versão para o seu sistema operacional (Windows, macOS ou Linux).

> **Nota para usuários Windows**: Certifique-se de que o WSL 2 (Windows Subsystem for Linux) está ativado, pois o Docker Desktop depende dele para uma melhor performance.

### 2. Clonando o Repositório

Primeiro, você precisa de uma cópia do projeto em sua máquina. Abra seu terminal e execute o comando abaixo:

```bash
git clone https://github.com/seu-usuario/delivery_system.git
cd delivery_system
```

### 3. Iniciando o Ambiente Mágico

Nós criamos um script que automatiza todo o processo para você. Basta dar um duplo clique no arquivo:

```bash
./start-dev.bat
```

**O que este script faz?**

- 🎩 **Limpa a casa**: Para e remove quaisquer contêineres de execuções anteriores para garantir um começo limpo.
- 🏗️ **Constrói tudo**: Cria as imagens Docker para o frontend e o backend. Da primeira vez, isso pode levar alguns minutos, pois ele baixa todas as dependências. Nas próximas, será muito mais rápido!
- 🚀 **Lança os foguetes**: Inicia os três contêineres (frontend, backend e banco de dados) em modo de desenvolvimento.

> **Atenção**: Atualmente, há um problema conhecido onde a finalização de pedidos pode retornar um erro '401 Unauthorized'. Estamos investigando a causa, que parece estar relacionada à configuração CORS no backend. A funcionalidade de adicionar/remover itens do carrinho está funcionando, mas o fluxo completo de pagamento via PIX ainda está em depuração.

### 4. Acessando a Aplicação

Após a conclusão do script, a aplicação estará viva! Abra seu navegador e acesse os seguintes endereços:

- **Frontend (Sua Loja Virtual)**: ➡️ [**http://localhost:5173**](http://localhost:5173) ⬅️
- **Backend API (O Cérebro da Operação)**: [http://localhost:8080](http://localhost:8080)
- **Banco de Dados (Onde os Dados Moram)**: Acessível em `localhost:5432` por um cliente de banco de dados de sua preferência (como DBeaver ou pgAdmin).

> 🎉 **Parabéns!** Você está com o ambiente de desenvolvimento completo rodando na sua máquina!

### 5. Parando o Ambiente

Quando terminar de trabalhar, você pode parar tudo com um único comando no terminal:

```bash
docker-compose -f docker-compose.dev.yml down -v
```

Isso irá parar os contêineres e remover os volumes do banco de dados, garantindo que na próxima vez que você iniciar, tudo estará limpo novamente.

## 📚 Documentação Adicional

Para uma exploração mais profunda do projeto, incluindo guias de desenvolvimento detalhados, arquitetura e decisões de design, mergulhe em nossa pasta de documentação:

- ➡️ [**Guia de Desenvolvimento](./docs/DEVELOPMENT_GUIDE.md)**: Dicas e truques para o dia a dia do desenvolvimento, incluindo como o hot-reload funciona.
- ➡️ [**Planejamento do Projeto](./docs/planejamento.md)**: Entenda a visão completa do projeto, funcionalidades planejadas e arquitetura.
- ➡️ [**Changelog de Design](./docs/DESIGN_CHANGELOG.md)**: Veja o histórico de melhorias de design que foram implementadas.
- ➡️ [**Tarefas Concluídas](./docs/TASKS_COMPLETES.md)**: Um registro detalhado das funcionalidades e melhorias implementadas até o momento.