# Fluxo de experiência

> O fluxo de experiência é fundamental para o sucesso de uma aplicação, pois uma boa usabilidade pode garantir a satisfação e fidelidade dos usuários.
> Permite ajudar a identificar possíveis problemas na experiência do usuário, permitindo que sejam feitas melhorias na interface ou no design da aplicação.

## Fluxo

O fluxo da aplicação é iniciada por ambos os tipos de utilizadores _'Docente'_ e _'Aluno'_, onde os mesmos, através da página principal, se deslocam para o local de autenticação para ser feita a destinação entre qual o papel do utilizador na aplicação, sendo feito no momento de registo do utilizador.

<p align="center"><img src="..\docs\img\user_flow_login.png" align="center" alt="flowLogin"> </p>

Todo o processo de registo e autorização é realizado por **OAuth 2.0** conectada à API do GitHub.
Este troço de fluxo pode ser saltado após haver a informação em **cache** sobre o utilizador em questão.

### Docente

Entrando na aplicação como _'Docente'_, a experiência de utilização passa por um menu inicial onde o docente poderá ter acesso a todas as turmas que leciona, onde é permitido a navegação para o conteúdo de cada uma.

Em cada turma tem-se acesso aos enunciados já publicados, por publicar e até mesmo criar, para os alunos terem acesso à sua publicação; também terá acesso à lista de grupos, criados por parte dos alunos, obtendo depois tanto as informações dos alunos como o conteúdo enviado por parte dos mesmos para ir ao encontro do proposto nos enunciados.

<p align="center"><img src="..\docs\img\user_flow_teacher.png" align="center" alt="flowTeacher"> </p>

### Aluno

Um utilizador identificado como _'Aluno'_, poderá ter 2(dois) tipos de experiência de utilização, uma mais comum e usual:

<p align="center"><img src="..\docs\img\user_flow_student.png" align="center" alt="flowTeacher"> </p>

Onde o aluno ao iniciar a aplicação, terá acesso a todas as disciplinas às quais se encontra inscrito, onde poderá depois em cada uma delas verificar poderá ter acesso à uma secção com todos os trabalhos já expostos pelo docente, podendo depois ir ao detalhe de cada um; como pode ter acesso ao repositório de GitHub, sobre o qual onde devem ser realizados os trabalhos.

Outra maneira de experiência de utilização que um aluno poderá presenciar, é através de um convite para a integração de um grupo para uma determinada disciplina, onde o mesmo poderá criar ou integrar-se num grupo e depois irá ser encaminhado para o mesmo, onde irá então ter acesso ao seu repositório.

<p align="center"><img src="..\docs\img\user_flow_invite.png" align="center" alt="flowTeacher"> </p>

Se o mesmo já se encontrar no grupo e pretender recuar no fluxo de utilização, o mesmo será reencaminhado para o início do fluxo anterior.
