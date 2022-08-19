# Sistema de Delivery

  Este foi o trabalho final da disciplina de WEB, nele o temos os papeis que o cliente pode exercer, sendo ele ADMIN(administrador)
ou USER(Cliente usuário).

  * O USER, pode ver o cardápio, selecionar pedido, vizualizar o carrinho de compras e seu histórico de compras no sistema.
  * O ADMIN, pode cadastrar, remover e alterar pratos.
  
 ## Caso queira baixar o projeto, será preciso:
 
  1. Criar um banco no Postgres, depois alterar o nome do banco, o nome de usuário e senha no _application.properts_ que fica no caminho
      src/main/resource/application.properties
  2. Para testar o projeto, após sua importação como "Existing Maven Projects", seŕá preciso clicar com o botão direito em cima do projeto escolhendo a opção "Run As" e depois _Spring Boot App_
  3. No postgres, crie na tabela Role, os seguintes dados: ROLE_ADMIN e ROLE_USER, como o exemplo abaixo:
  
      ```sql
        INSERT INTO ROLE(papel) VALUES('ROLE_ADMIN'),('ROLE_USER);
      ```
  4. OBS: No sistema, todos que se cadastrarem serão usuário, então crie um usuário admin da seguinte forma:
          
      Cadastre-se normalmente no sistema. No banco de dados, liste as pessoas cadastradas, veja qual o 
      Código da pessoa, e então atribua o papel de AMIN à ela da seguinte forma:
          
      ```sql
         INSERT INTO PESSOAS_ROLES VALUES(1,'ROLE_ADMIN')
      ```
          
     O primeiro campo passado no _values_ é o código da pessoa, e o seguindo campo é a role(o papel dela no sistema)
