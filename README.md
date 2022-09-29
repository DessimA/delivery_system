# :clipboard: Projeto Final - Sistema de Delivery

### :one: Descrição

##### Título do Trabalho

Restaurante Delivery

Objetivos do trabalho

1. Demonstrar habilidade para desenvolvimento de aplicação Web utilizando
Spring
2. Acesso ao SGBD PostgreSQL/MySQL
1. Demonstrar conhecimento dos seguintes aspectos de programação:
2. Linguagem Java
3. Uso adequado de OO

Trabalho proposto

Escreva uma aplicação WEB para gerenciar seu restaurante delivery.

Gerente do Restaurante:
- Cadastrar Pratos
- Remover Pratos

Cliente:
- Fazer pedidos
- Verificar os pedidos que estão sendo feitos, podendo remover e adicionar pratos ao
pedido
- Visualizar todos os pedidos que já foram feitos no restaurante pelo cliente

### :one: Primeira Parte: entrega até 20/08

Cadastrar Pratos (Gerente do Restaurante)

  O gerente do restaurante poderá adicionar os pratos que serão vendidos e que aparecerão na
página de galeria de Pratos. No momento do cadastro o gerente deverá informar:
- Nome do Prato
- Imagem do prato

Remover Pratos (Gerente do Restaurante)

  O gerente do restaurante poderá remover pratos que já foram adicionados. Para o gerente
aparecerá uma página de listagem com todos os pratos do restaurante e um botão de remover
em cada prato. Ao clicar no botão de remover, o prato some da galeria de pratos do restaurante.
Visualizar todos os Pratos do Restaurante (Todos os usuários)

  No sistema existirá uma página que mostrará todos os pratos do restaurante com Nome do
Prato, Imagem do Prato e Preço do Prato. O botão de “Selecionar prato para pedido” em cada
um dos pratos será visível ao Usuário Visitante que no momento de clique será redirecionado
para um formulário de cadastro, esse botão também será visível para o cliente que no momento
do clique colocará o prato na página de “pratos adicionados para o pedido atual”.
  
  Se cadastrar no sistema através de formulário para virar cliente
O usuário visitante poderá se cadastrar através de formulário de cadastro e virar cliente. O
sistema deverá ter no menu uma página que redireciona o usuário visitante para o formulário de
cadastro. No momento do cadastro o usuário deverá informar:

- Nome
- Cpf
- Data de Nascimento
- Endereço
- Senha
- Email
### :two: Segunda Parte: entrega até 09/09

Fazer Pedido(Cliente)

  Na galeria de pratos deverá existir um botão de “Selecionar prato para pedido” para que o cliente,
ao clicar nesse botão, colocará o prato no pedido atual, como se fosse uma sacola de compras.
Verificar os prato que estão sendo comprados, podendo remover qualquer prato da
página de pratos para pedido atual (Cliente)

  O cliente poderá verificar através de uma página de pratos selecionados para pedido, os pratos
que estão sendo comprados naquela sessão e o valor total da compra. Se o cliente ficar
insatisfeito com algum prato ele poderá remover algum prato que ele estiver comprando e o valor
total da compra deverá ser atualizado. Quando o cliente selecionar todos os pratos, existirá um
campo para digitar o endereço de entrega.

  Ao clicar no botão “Confirmar Pedido” o pedido é de fato realizado colocando os pratos
selecionados no pedido. (Parecido com o Ifood)
Visualizar todos os pedidos que já foram comprados no restaurante pelo cliente (Cliente)
O cliente poderá visualizar através de uma página, o histórico de todos os pedidos que já foram
feitos no restaurante.

  Enviar e-mail quando o mesmo confirmar um pedido (Cliente) (OPCIONAL) (EXTRA DE
0.6 décimos na nota do trabalho)
  Toda vez que um Cliente confirmar um pedido, uma mensagem será enviada para o e-mail do
cliente contendo os pratos daquele pedido e o valor gasto.
Atraso

  Os trabalhos só poderão ser entregues no dia da apresentação. Não serão aceitos trabalhos
entregues em datas posteriores.
Serão cobrados no trabalho:

Implementação:
- Funcionamento adequado segundo descrição do sistema
- Utilização correta do Spring
- Acesso ao SGBD PostgreSQL/MySQL.
- Utilização correta de orientação a objetos
- Clareza do código (boa estruturação)
- Entendimento do seu próprio código na hora da explicação (Importante)

  Este foi o trabalho final da disciplina de WEB, nele o temos os papeis que o cliente pode exercer, sendo ele ADMIN(administrador)
ou USER(Cliente usuário).

  * O USER, pode ver o cardápio, selecionar pedido, vizualizar o carrinho de compras e seu histórico de compras no sistema.
  * O ADMIN, pode cadastrar, remover e alterar pratos.
  
 ## :key: **Informações necessárias para testes atuais:**
 
 :one: Criar um banco no Postgres, depois alterar o nome do banco, o nome de usuário e senha no _application.properts_ que fica no caminho
      src/main/resource/application.properties
      
 :two: Para testar o projeto, após sua importação como "Existing Maven Projects", seŕá preciso clicar com o botão direito em cima do projeto escolhendo a opção "Run As" e depois _Spring Boot App_
  
  :three: No postgres, crie na tabela Role, os seguintes dados: ROLE_ADMIN e ROLE_USER, como o exemplo abaixo:
  
      sql
        INSERT INTO ROLE(papel) VALUES('ROLE_ADMIN'),('ROLE_USER);
      
  :four: OBS: No sistema, todos que se cadastrarem serão usuário, então crie um usuário admin da seguinte forma:
          
Cadastre-se normalmente no sistema. No banco de dados, liste as pessoas cadastradas, veja qual o 
Código da pessoa, e então atribua o papel de ADMIN à ela da seguinte forma:
          
      sql
         INSERT INTO PESSOAS_ROLES VALUES(1,'ROLE_ADMIN')
      
          
O primeiro campo passado no _values_ é o código da pessoa, e o seguindo campo é a role(o papel dela no sistema)
