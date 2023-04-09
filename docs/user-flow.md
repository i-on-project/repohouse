# Fluxo de experiência

> O fluxo de experiência é fundamental para o sucesso de uma aplicação, pois uma boa usabilidade pode garantir a satisfação e fidelidade dos utilizadores.
> Permite ajudar a identificar possíveis problemas na experiência do utilizador, permitindo que sejam feitas melhorias na interface ou no *design* da aplicação.

## Fluxo

O fluxo da aplicação é iniciada por ambas as categorias de utilizadores _'Docente'_ e _'Aluno'_, onde os mesmos, através da página principal, se deslocam para o local de autenticação para ser feita a destinação entre qual o papel do utilizador na aplicação, feito no momento de registo do utilizador.

Todo o processo de registo e autorização é realizado por **OAuth 2.0** conectada à API do GitHub.
Este troço de fluxo pode ser saltado após haver a informação em **cache** sobre o utilizador em questão.

A aplicação é dividida numa componente *web* e uma componente *mobile*, onde a componente *web* é possível de ser acedida por qualquer utilizador, enquanto a componente móvel só é possivel ser utilizada por _'Docentes'_.
![Web User Flow](../docs/img/web-user-flow.png)*Web User Flow*

![Mobile User Flow](../docs/img/mobile-user-flow.png)*Mobile User Flow*

### Docente
Entrando na aplicação como _'Docente'_, a experiência de utilização passa por um menu inicial onde o docente poderá ter acesso a todas as turmas que leciona, onde é permitido a navegação para o conteúdo de cada uma.

Em cada turma tem-se acesso aos enunciados já publicados, por publicar e até mesmo criar, para os alunos terem acesso à sua publicação e à lista de grupos, criados por parte dos alunos, obtendo depois tanto as informações dos mesmos como o conteúdo realizado através dos repositórios.

![Detailed Web Teacher Flow](../docs/img/web-teacher-flow.png)*Detailed Web Teacher Flow*

Para um docente realizar o seu registro na aplicação, após integração com o Github, o mesmo deve submeter o seu nome e email, para que outro docente o verifique como um professor, para permitir assim ter uma aplicação confiável em relação aos seus utilizadores.

Todas as operações que necessitam de operações de escrita, são realizadas através da componente *mobile*, onde o docente armazena o seu token que permite realizar os pedidos sobre a API do GitHub.

Qualquer pedido que necessite de autenticação realizada pela componente *web*, quer por parte do docente como pelos alunos, é adicionada a uma lista de tarefas onde só é depois realizadas pela componente móvel através do token do docente armazenado no dispositivo móvel.
Este componente serve também para a sincronização entre os dados da aplicação e o GitHub.

![Detailed Mobile Teacher Flow](../docs/img/mobile-flow.png)*Detailed Mobile Teacher Flow*

### Aluno

Um utilizador identificado como _'Aluno'_, poderá ter duas categorias de experiência de utilização, uma comum e usual:

Onde o aluno ao iniciar a aplicação, terá acesso a todas as disciplinas às quais se encontra inscrito, onde poderá depois em cada uma delas verificar poderá ter acesso à uma secção com todos os trabalhos já expostos pelo docente, podendo depois ir ao detalhe de cada um; como pode ter acesso ao repositório de GitHub, sobre o qual onde devem ser realizados os trabalhos.

![Detailed User Flow](../docs/img/web-student-flow.png)*Detailed User Flow*

E outra maneira de experiência de utilização que um aluno poderá presenciar, é através de um convite para a integração de uma disciplina, onde o mesmo poderá criar ou integrar-se num grupo e ser encaminhado para o mesmo, aonde irá então ter acesso ao seu repositório.

O aluno ao iniciar a aplicação através de um convite, terá acesso a uma página onde irá ser questionado sobre a intenção de intregrar a disciplina, caso o deseje e após a sua autenticação caso seja necessária, será reencaminhado para uma página onde poderá criar um grupo ou integrar-se a um, ao qual após a sua escolha, será reencaminhado para o repositório da disciplina.

![Detailed User Invite Flow](../docs/img/web-invite-student-flow.png)*Detailed User Invite Flow*

Se o mesmo já se tiver inscrito em um grupo e pretender recuar no fluxo de utilização, o mesmo será reencaminhado para o início do fluxo anterior, para a página das suas disciplinas.
