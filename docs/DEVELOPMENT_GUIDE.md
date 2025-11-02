# 🐋 Guia de Desenvolvimento com Docker

Bem-vindo ao guia de desenvolvimento do Delivery System! Este documento é seu melhor amigo para entender como nosso ambiente Docker funciona, como tirar o máximo proveito do hot-reload e como resolver problemas comuns. Vamos mergulhar!

---

## 🔥 A Mágica do Hot-Reload

Hot-reload é o que nos permite desenvolver em alta velocidade. Em vez de reiniciar tudo a cada pequena mudança, o sistema se atualiza automaticamente. Veja como isso funciona para cada parte do projeto.

### Frontend (Vue.js + Vite) ⚡

- **O que é?**: Pense no Vite como um garçom ultrarrápido. Ele fica observando seus arquivos de frontend (`.vue`, `.js`).
- **Como funciona**: Assim que você salva uma alteração, o Vite a pega e a entrega diretamente para o seu navegador em um piscar de olhos, na maioria das vezes sem nem precisar recarregar a página. É por isso que as mudanças na interface são quase instantâneas!
- **Status**: Já vem ativado por padrão quando você executa o `start-dev.bat`.

### Backend (Spring Boot) ☕

- **O que é?**: O Spring Boot DevTools é como um engenheiro atento monitorando a construção do motor do nosso sistema.
- **Como funciona**: 
  1. Você altera um arquivo `.java` e sua IDE (IntelliJ, VS Code, etc.) o compila, criando um arquivo `.class`.
  2. Nosso `docker-compose.dev.yml` compartilha a pasta onde esses arquivos `.class` são criados (`target/classes`) com o contêiner do backend.
  3. O DevTools, que está dentro do contêiner, percebe o novo arquivo e, em vez de reconstruir tudo, ele reinicia o servidor de aplicação de forma inteligente e rápida. É muito mais veloz do que um rebuild completo!
- **Status**: Ativado por padrão. Você verá o log de reinicialização do Spring no terminal se estiver acompanhando os logs.

---

## 🚀 Seu Dia a Dia como Desenvolvedor

Este é o fluxo de trabalho que recomendamos para máxima produtividade.

| Passo | Ação | O que Acontece | 
| :--- | :--- | :--- |
| **1** | Execute `start-dev.bat` | Todo o ambiente (frontend, backend, DB) é construído e iniciado. | 
| **2** | Codifique no Frontend | Altere arquivos em `frontend-delivery/src`. As mudanças aparecem instantaneamente em `http://localhost:5173`. | 
| **3** | Codifique no Backend | Altere arquivos em `backend-delivery/src`. Sua IDE compila, e o Spring reinicia sozinho. | 
| **4** | Monitore os Logs | Abra um novo terminal e rode `docker-compose -f docker-compose.dev.yml logs -f` para ver tudo o que acontece em tempo real. | 
| **5** | Pare Tudo | Quando terminar, rode `docker-compose -f docker-compose.dev.yml down -v` para desligar os contêineres e limpar os dados. | 

---

## 🐛 Solução de Problemas (Não Entre em Pânico!)

De vez em quando, as coisas podem não sair como o esperado. Aqui estão algumas soluções para os problemas mais comuns.

### Problema: Hot-reload do backend não funciona!

- **Causa Provável**: Sua IDE não está compilando os arquivos automaticamente.
- **Solução**: 
  - **IntelliJ IDEA**: Vá em `File` > `Settings` > `Build, Execution, Deployment` > `Compiler` e marque a opção `Build project automatically`.
  - **VS Code**: Se você usa a extensão da Spring, ela geralmente cuida disso. Verifique se não há erros de compilação no seu terminal.
  - **Verificação Manual**: Confira se a pasta `backend-delivery/target/classes` está sendo atualizada após você salvar um arquivo Java.

### Problema: Erro "Cannot find module" ou similar no frontend.

- **Causa Provável**: Uma nova dependência foi adicionada ao `package.json` mas a imagem Docker não foi atualizada para incluí-la.
- **Solução**: Simplesmente rode o `start-dev.bat` novamente. O script foi projetado para reconstruir a imagem do frontend se o `package.json` mudou.

### Problema: A porta `5173` ou `8080` já está em uso.

- **Causa Provável**: Outro processo em sua máquina está usando a mesma porta.
- **Solução**: Edite o arquivo `docker-compose.dev.yml` e mude a porta no mapeamento `HOST:CONTAINER`.
  ```yaml
  # Exemplo: Mudar a porta do frontend de 5173 para 3000
  services:
    frontend-app-dev:
      ports:
        - "3000:5173" # Agora acesse em http://localhost:3000
  ```

### Problema: `Uncaught ReferenceError: global is not defined` no frontend.

- **Causa Provável**: A biblioteca `sockjs-client` tenta acessar a variável `global`, que não existe em ambientes de navegador.
- **Solução**: Adicione um polyfill para `global` no `vite.config.js`. Certifique-se de que seu `vite.config.js` contenha:
  ```javascript
  define: {
    global: 'globalThis',
  },
  ```
  Após a alteração, reinicie os containers Docker.

### Problema: Tudo parece quebrado!

- **Causa Provável**: Cache do Docker, volumes antigos ou alguma outra inconsistência.
- **Solução ("O Canivete Suíço")**: Execute os seguintes comandos para uma limpeza completa e um recomeço fresco.
  ```bash
  # 1. Pare tudo e remova os volumes
  docker-compose -f docker-compose.dev.yml down -v

  # 2. (Opcional, mas eficaz) Limpeza geral do Docker
  docker system prune -a -f

  # 3. Comece de novo
  start-dev.bat
  ```

> **Aviso**: O comando `docker system prune` é poderoso e irá remover todas as imagens, contêineres e volumes não utilizados em seu sistema. Use com sabedoria!