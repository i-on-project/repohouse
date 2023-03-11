# Controlo de acessos do GitHub

> Para garantir a segurança e a privacidade dods dados do utilizador, a API do Github usa vários modelos de acesso que controlam que pode aceder, quais dados e como esses dados podem ser acedidos.

---

## Tipos de autenticação

### Autenticação básica

Autenticação básica ́e a maneira mais simples de autenticar solicitações
da API do Github. ́E feito o envio do nome e do token pessoal do utilizador
junto com a solicitação da API. A autenticação através de password foi descontinuada por parte da API do GitHub. A autenticação básica permite a
leitura de dados públicos, como consultas de repositório ou visualização de
perfis. Para a usar, deve ser enviado um cabeçalho Authorization em cada
pedido à API, com o valor de ’Bearer personal token’.

O GitHub segue modelo que está defenido no RFC2617, com algumas alterações. A principal é que invês de enviar uma resposta 401 Unauthorized a um pedido não autenticado, envia uma resposta 404 Not Found. Isto deve-se a possível revelação que existia data de um utilizador,

### Autenticação OAuth

A autenticação OAuth é um modelo de autorização mais seguro e flexível que permite que aplicativos terceiros acedam recursos da API do Github em nome dos utilizadores. Com a autenticação OAuth, os utilizadores concedem autorizações específicas para uma aplicação aceder às suas contas do Github.

Para usar a autenticação OAuth, é necessário registar a aplicação desenvolvida com o Github e obter um client_id e um client_secret. A aplicação poderá usar o fluxo de autorização OAuth do Github para solicitar um access_token que permita que a aplicação aceder a API em nome do utilizador.

Depois de obter um access_token, este será colocado em cada pedido da API como um cabeçalho Authorization com um valor no formato **'Bearer access_token'**.

É importante lembrar que a autenticação OAuth é necessária para aceder a recursos protegidos da API do Github, como criação de repositórios ou a sua manipulação.

O gitHub OAuth suporta o authorization code grant type (web application flow), para autorizar utilizadores de aplicações OAuth padrão, e o OAuth 2.0 Device Authorization Grant (device flow), para aplicações que não tem acesso a um
web browser(CLI tools). Ainda existe, para casos como testar a aplicação, non-web application flow que permite saltar a autorização padrão da aplicação.

#### Scope

Scopes são uma maneira de controlar quais recursos e operações da API um _token_ de acesso tem permissão para aceder. Quando um utilizador autoriza uma aplicação a aceder à API do Github em seu nome, o utilizador pode conceder diferentes níveis de acesso, dependendo do que a aplicação necessitar.

Os scopes que irão ser usados no acesso serão:
| Scope | Descrição |
| --- | --- |
| "admin:org" | Fornece acesso total à organização, incluindo a capacidade de criar e excluir organizações. |
| "repo" | Permite a criação, leitura, atualização e exclusão de repositórios. Isso inclui a adição e remoção de colaboradores e a definição de permissões de repositório |
| "repo:status" | Permite a leitura e gravação de status de commit para um repositório. Isso pode ser útil para integração contínua e outras ferramentas de automação. |
| "delete_repo" | Permite ler informações de uma organização. |
| "write:org" | Permite criar e editar informações de uma organização, como a descrição e a imagem. |

### Autenticação OAuth GitHub

O modelo OAuth do Github está envolvido em três partes principais: o utilizador, a aplicação e o Github. O utilizador concede permissão à aplicação para aceder aos seus recursos no Github. A aplicação então solicita um token de acesso ao Github, que é usado para aceder à API do Github em nome do utilizador. O token de acesso pode ser limitado por scopes, que definem as permissões que o aplicativo tem para aceder aos recursos do utilizador. Isto é posível de ser realizado, após o registro da aplicação para o uso de OAuth com o GitHub.

1.  **Pedido da identidade do utilizador**

    GET https://github.com/login/oauth/authorize

    - **client_id** - Recebido pelo GitHub ao fazer registo da aplicação OAuth.
    - **redirect_uri** - Redirecionamento após autorização do cliente.
    - **scopes** - Já retratados em cima.
    - **state** - Razões de segurança, caso o state recebido na resposta for diferente, o processo deve ser abortado

2.  **Redirecionamento do utilizador à aplicação**

    POST https://github.com/login/oauth/access_token

    - **client_id** - Recebido pelo GitHub para a aplicação OAuth.
    - **client_secret** - Recebido pelo GitHub para a aplicação OAuth.
    - **code** - Recebido como resposta do primeiro passo (possuí um timeout de 10 minutos).
    - **redirect_uri** - Redirecionamento após autorização do cliente seja realizada

3.  **Uso do token de acesso**

    Authorization: Bearer _OAuth-Token_

    O token é recebido como resposta do passo anterior, permitindo depois então efetuar operações e pedidos à api do GitHub, conforme o scope enviado.

    É possível criar até dez tokens para uma combinação utilizador/aplicação/scope. Se uma aplicação criar mais dez tokens para o mesmo utilizador e os mesmos scopes, o mais antigo será revogado.

<p align="center"><img src="..\docs\img\github_oauth_example.png" align="center" alt="GithubExample"> </p>
