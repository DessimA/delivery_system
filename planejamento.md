# Planejamento Detalhado do Sistema de Delivery de Lanches

## 1. Visão Geral do Projeto
Este projeto implementa um sistema de delivery de lanches, composto por um backend em Spring Boot (Java) e um frontend em Vue.js. O sistema permite o gerenciamento de produtos (lanches), pedidos e usuários, com diferentes níveis de acesso baseados em roles (ADMIN e USER). O objetivo é fornecer uma plataforma completa para que clientes possam navegar, selecionar e pedir lanches, e para que administradores possam gerenciar o catálogo e os pedidos.

## 2. Modelos de Dados Essenciais (Implícitos/Explícitos)

*   **Produto:** Representa um lanche ou item do cardápio.
    *   `id`: Identificador único.
    *   `nome`: Nome do lanche (ex: X-Bacon, Refrigerante).
    *   `descricao`: Descrição detalhada do lanche (ingredientes, etc.).
    *   `preco`: Preço unitário do lanche.
    *   `imagemUrl`: URL para a imagem do lanche.
    *   `categoria`: Categoria do lanche (ex: Lanches, Bebidas, Sobremesas, Porções).
    *   `disponibilidade`: Booleano indicando se o lanche está disponível para venda.

*   **Usuário (Pessoa):** Representa um cliente ou administrador do sistema.
    *   `id`: Identificador único.
    *   `nome`: Nome completo do usuário.
    *   `email`: Email do usuário (usado para login).
    *   `senha`: Senha criptografada do usuário.
    *   `enderecos`: Lista de endereços de entrega associados ao usuário.
    *   `roles`: Lista de roles do usuário (ex: `ROLE_USER`, `ROLE_ADMIN`).

*   **Endereço:** Informações de um endereço de entrega.
    *   `id`: Identificador único.
    *   `rua`: Nome da rua.
    *   `numero`: Número do imóvel.
    *   `complemento`: Complemento (apto, bloco, etc.).
    *   `bairro`: Bairro.
    *   `cidade`: Cidade.
    *   `estado`: Estado.
    *   `cep`: Código de Endereçamento Postal.

*   **Pedido:** Representa uma ordem de compra de lanches.
    *   `id`: Identificador único.
    *   `dataHora`: Data e hora em que o pedido foi realizado.
    *   `status`: Status atual do pedido (ex: `PENDENTE`, `EM_PREPARACAO`, `EM_ENTREGA`, `ENTREGUE`, `CANCELADO`).
    *   `total`: Valor total do pedido.
    *   `itensPedido`: Lista de itens que compõem o pedido.
    *   `enderecoEntrega`: Endereço para onde o pedido deve ser entregue.
    *   `usuario`: O usuário que realizou o pedido.

*   **ItemPedido:** Representa um produto dentro de um pedido.
    *   `id`: Identificador único.
    *   `produto`: O produto que foi pedido.
    *   `quantidade`: Quantidade do produto pedido.
    *   `precoUnitario`: Preço do produto no momento da compra (para histórico).

*   **Carrinho (Frontend - Modelo Lógico):** Representação temporária dos itens selecionados pelo usuário antes de finalizar o pedido. Geralmente armazenado no `localStorage` ou `sessionStorage` do navegador.
    *   `itens`: Lista de objetos contendo `produtoId`, `quantidade`, `nome`, `preco`, `imagemUrl`.
    *   `total`: Soma dos preços dos itens no carrinho.

## 3. Fluxos de Usuário Detalhados e Comportamento do Frontend

### 3.1. Usuário Não Autenticado (Visitante)

**Objetivo:** Navegar pelo cardápio, visualizar lanches e adicioná-los a um carrinho temporário.

*   **Páginas Acessíveis:**
    *   **Página Inicial (`/`):** Exibe uma visão geral do restaurante, promoções, ou lanches em destaque. Pode ter um carrossel de imagens ou seções de categorias.
    *   **Página de Produtos (`/products`):**
        *   Exibição em grade ou lista de todos os lanches disponíveis, com imagem, nome e preço.
        *   **Funcionalidade de Busca:** Campo de texto para pesquisar lanches por nome.
        *   **Funcionalidade de Filtro:** Opções para filtrar lanches por categoria (ex: Lanches, Bebidas, Sobremesas).
        *   **Botão "Adicionar ao Carrinho":** Presente em cada item do produto. Ao clicar, o item é adicionado ao carrinho local (sem necessidade de login).
        *   **Botão "Ver Detalhes":** Ao clicar em um produto, redireciona para a página de detalhes do produto.
    *   **Página de Detalhes do Produto (`/products/:id`):** Exibe informações mais detalhadas do lanche, incluindo descrição completa, ingredientes (se aplicável), preço, imagem maior e um botão "Adicionar ao Carrinho" com seletor de quantidade.
    *   **Página de Login (`/login`):** Formulário para que usuários existentes possam fazer login.
    *   **Página de Registro (`/register`):** Formulário para que novos usuários possam criar uma conta.
    *   **Carrinho de Compras (`/cart`):**
        *   Acessível via ícone persistente na barra de navegação.
        *   Exibe a lista de itens adicionados, suas quantidades e o subtotal.
        *   Permite ajustar a quantidade de cada item ou remover itens do carrinho.
        *   **Botão "Finalizar Pedido":** Ao ser clicado, se o usuário não estiver autenticado, exibe uma mensagem clara (ex: "Você precisa estar logado para finalizar o pedido") e redireciona para a página de login/registro.

*   **Elementos de UI Visíveis/Ocultos:**
    *   **Navegação:** Links para "Home", "Cardápio" (ou "Produtos"), "Login", "Registrar", e um ícone de "Carrinho" com um contador de itens.
    *   **Ocultos:** Links para "Meu Perfil", "Meus Pedidos", "Painel Administrativo" ou qualquer funcionalidade de gerenciamento.

*   **Interações:**
    *   Pode navegar livremente pelo cardápio e visualizar detalhes dos lanches.
    *   Pode adicionar lanches ao carrinho (armazenado localmente no navegador).
    *   Não pode persistir o carrinho entre sessões ou finalizar um pedido sem antes se autenticar.

### 3.2. Usuário Autenticado (Role: USER - Cliente)

**Objetivo:** Gerenciar perfil, fazer pedidos, acompanhar o status e visualizar histórico.

*   **Páginas Acessíveis:**
    *   Todas as páginas acessíveis ao visitante.
    *   **Página de Perfil (`/profile` ou `/me`):** Exibe informações do usuário (nome, email). Permite editar dados pessoais e gerenciar múltiplos endereços de entrega (adicionar, editar, remover, definir como padrão).
    *   **Página de Meus Pedidos (`/my-orders`):** Lista todos os pedidos feitos pelo usuário, com informações resumidas (ID, data, total, status). Ao clicar em um pedido, exibe uma página de detalhes do pedido com todos os itens, endereço de entrega e histórico de status.
    *   **Página de Checkout (`/checkout`):**
        *   Exibe um resumo detalhado do carrinho.
        *   Permite selecionar um endereço de entrega existente ou adicionar um novo endereço.
        *   Seleção de método de pagamento (ex: Cartão de Crédito, Dinheiro na Entrega, Pix - inicialmente pode ser um placeholder).
        *   **Botão "Confirmar Pedido":** Envia o pedido para o backend e redireciona para uma página de confirmação ou para a lista de "Meus Pedidos".

*   **Elementos de UI Visíveis/Ocultos:**
    *   **Navegação:** Links para "Home", "Cardápio", "Meu Perfil", "Meus Pedidos", "Carrinho", e um botão "Sair" (Logout).
    *   **Ocultos:** Links para "Login", "Registrar", "Painel Administrativo".

*   **Interações:**
    *   Pode realizar todas as ações do usuário não autenticado.
    *   Pode gerenciar suas informações de perfil e endereços de entrega.
    *   Pode finalizar pedidos, utilizando seus endereços cadastrados e selecionando um método de pagamento.
    *   Pode visualizar o histórico completo de seus pedidos e acompanhar o status atual de cada um.
    *   Não tem acesso a funcionalidades de gerenciamento de produtos ou visualização de todos os pedidos do sistema.

### 3.3. Usuário Autenticado (Role: ADMIN)

**Objetivo:** Gerenciar o catálogo de lanches e todos os pedidos do sistema.

*   **Páginas Acessíveis:**
    *   Todas as páginas acessíveis por um usuário comum.
    *   **Painel Administrativo (`/admin` ou `/dashboard`):** Uma página central que serve como ponto de partida para as funcionalidades de gerenciamento, com links claros para "Gerenciar Produtos" e "Gerenciar Pedidos".
    *   **Gerenciamento de Produtos (`/admin/products`):**
        *   Lista todos os produtos do cardápio em uma tabela ou grade, com opções de "Editar" e "Excluir" para cada item.
        *   **Botão "Adicionar Novo Produto":** Leva a um formulário dedicado para cadastro de um novo lanche.
        *   **Formulário de Adição/Edição de Produto:** Campos para `nome`, `descricao`, `preco`, `categoria`, `disponibilidade` e um campo para `upload de imagem` (ou URL da imagem).
    *   **Gerenciamento de Pedidos (`/admin/orders`):**
        *   Lista *todos* os pedidos realizados no sistema (não apenas os seus), com informações resumidas (ID, cliente, data, total, status).
        *   **Filtros:** Opções para filtrar pedidos por status (ex: Pendente, Em Preparação), por cliente, ou por período.
        *   Ao clicar em um pedido, exibe uma página de detalhes completa, similar à do cliente, mas com a funcionalidade adicional de **atualizar o status do pedido** (ex: de "Pendente" para "Em Preparação", "Em Entrega", "Entregue").

*   **Elementos de UI Visíveis/Ocultos:**
    *   **Navegação:** Links para "Home", "Cardápio", "Meu Perfil", "Meus Pedidos", "Carrinho", "Painel Administrativo", e um botão "Sair" (Logout).
    *   **Ocultos:** Links para "Login", "Registrar".

*   **Interações:**
    *   Pode realizar todas as ações de um usuário comum.
    *   Pode criar, editar e excluir lanches do cardápio.
    *   Pode visualizar e gerenciar o status de *todos* os pedidos do sistema, acompanhando o fluxo de entrega.
    *   Pode ter acesso a outras ferramentas de administração (se implementadas no futuro, como gerenciamento de categorias, promoções, relatórios de vendas, etc.).

## 4. Considerações de UI/UX e Componentes Chave

*   **Barra de Navegação (AppNavbar.vue):**
    *   Responsiva e adaptável, exibindo links dinamicamente com base no status de autenticação e na role do usuário.
    *   Ícone de carrinho com um contador numérico de itens.
    *   Exibição do nome do usuário logado e uma opção clara de "Sair" (Logout).
*   **Listagem de Produtos (AppProducts.vue):**
    *   Layout visualmente atraente e responsivo para exibir os lanches (cards, grade).
    *   Botões "Adicionar ao Carrinho" claramente visíveis e interativos para todos os usuários.
    *   Para `ADMINs`, botões "Editar" e "Excluir" devem ser visíveis em cada item do produto, ou acessíveis através de um painel de gerenciamento.
*   **Formulários:**
    *   Todos os formulários (login, registro, perfil, produto) devem ter validação de entrada robusta e feedback claro ao usuário.
    *   Design intuitivo e fácil de usar.
*   **Feedback ao Usuário:**
    *   Mensagens de sucesso (ex: "Lanche adicionado ao carrinho!", "Pedido realizado com sucesso!").
    *   Mensagens de erro (ex: "Login falhou. Verifique suas credenciais.", "Erro ao processar o pedido.").
    *   Indicadores de carregamento para operações assíncronas.
*   **Estado Global (Vuex/Pinia):**
    *   Utilização de um gerenciador de estado para manter informações cruciais como o status de autenticação, dados do usuário logado (incluindo roles), e os itens do carrinho de compras.
    *   Isso garante que os dados estejam sincronizados entre os componentes e as rotas.

## 5. Próximos Passos (Implícitos para o Desenvolvimento)

*   **Backend:**
    *   Implementação completa dos endpoints de gerenciamento de produtos (CRUD) com upload de imagem.
    *   Implementação dos endpoints de gerenciamento de pedidos (visualização de todos, atualização de status).
    *   Refinamento da autenticação e autorização para garantir que as roles sejam corretamente aplicadas em todos os endpoints.
    *   Implementação de um endpoint para o frontend obter os dados do usuário logado, incluindo suas roles.
*   **Frontend:**
    *   Desenvolvimento completo do fluxo de carrinho de compras (adição, remoção, atualização de quantidade, persistência local e sincronização com o backend no checkout).
    *   Criação da página de Checkout, incluindo seleção de endereço e método de pagamento.
    *   Desenvolvimento das páginas/componentes de gerenciamento de produtos para o `ADMIN` (listagem, formulário de adição/edição).
    *   Desenvolvimento das páginas/componentes de gerenciamento de pedidos para o `ADMIN` (listagem de todos os pedidos, detalhes do pedido com atualização de status).
    *   Implementação da lógica de renderização condicional de elementos da UI com base na role do usuário (ex: exibir/ocultar links de administração).
    *   Integração com as APIs do backend para todas as funcionalidades.