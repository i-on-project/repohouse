# Controlo de acessos do GitHub

> Para garantir a segurança e a privacidade dos dados do utilizador, a API do Github usa vários modelos de acesso que controlam quem pode aceder, quais dados e como os mesmos podem ser acedidos.

---

## Formas de autenticação

### [Autenticação básica](https://docs.github.com/en/rest/overview/other-authentication-methods?apiVersion=2022-11-28#basic-authentication)

#### Pontos relevantes sobre a autenticação básica no github:

- A autenticação através de _password_ foi descontinuada por parte da API do GitHub.
- A resposta da API do gitHub será **404 Not Found**, invés **401 Unauthorized**, como está definido no [RFC2617](https://www.ietf.org/rfc/rfc2617.txt).
- Como algumas bibliotecas HTTP podem não estar preparadas para receber uma resposta **404 Not Found**, a solução passa por usar o header **Authorization**.

![GitHub Basic Authentication Example](./img/github-basic-auth-scheme.png)

### [Autenticação OAuth](https://docs.github.com/en/rest/overview/other-authentication-methods?apiVersion=2022-11-28#basic-authentication)

#### Pontos relevantes sobre a autenticação OAuth no github:

- Para se poder ser usada, a aplicação terá de registada com o GitHub para receber e obter um client_id e um client_secret.
- O gitHub OAuth suporta o [authorization code grant type](https://tools.ietf.org/html/rfc6749#section-4.1) e [device authorization grant](https://www.rfc-editor.org/rfc/rfc8628).
- Para testar a aplicação existe o [non-web application flow](https://docs.github.com/en/apps/oauth-apps/building-oauth-apps/authorizing-oauth-apps#non-web-application-flow), que salta a autorização da aplicação.
- O fluxo do [web application](https://docs.github.com/en/apps/oauth-apps/building-oauth-apps/authorizing-oauth-apps#web-application-flow) é composto por três passos:
  1. Os utilizadores são redirecionados para pedir a sua identidade do GitHub.
  2. Os utilizadores são redirecionados de volta para o site pelo GitHub.
  3. A aplicação acede a API do GitHub em nome do utilizador, usando o token de acesso do mesmo.

![GitHub Flow Example](./img/github-oauth-scheme.png)

#### [Scope](https://docs.github.com/en/apps/oauth-apps/building-oauth-apps/scopes-for-oauth-apps)

Os _scopes_ que irão ser utilizados no projeto:

- **user** — Permite ler informações de perfil do utilizador, como o nome, endereço de email e foto de perfil.
- **user:email** — Permite ler os endereços de email associados à conta do utilizador.
- **admin:org** — Fornece acesso total à organização, incluindo a capacidade de criar e excluir organizações.
- **repo** — Permite a criação, leitura, atualização e exclusão de repositórios. Isso inclui a adição e remoção de colaboradores e a definição de permissões de repositório
- **repo:status** — Permite a leitura e gravação do estado de commit para um repositório. Isso pode ser útil para integração contínua e outras ferramentas de automação.
- **delete_repo** — Permite ler informações de uma organização.
- **write:org** — Permite criar e editar informações de uma organização, como a descrição e a imagem.

### [Criação de mutiplos tokens para aplicações OAuth](https://docs.github.com/en/apps/oauth-apps/building-oauth-apps/authorizing-oauth-apps#creating-multiple-tokens-for-oauth-apps)

- É possível criar multiplos tokens para diferentes combinações utilizador/aplicação/scope.
- Poderá haver combinações que permitem o acesso a repositórios privados, como poderá haver combinções que simplesmente fazem leitura duma organização.
- Há um limite de 10 tokens para o mesmo utilizador e mesmo scope.
