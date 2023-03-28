# Modelo de dados

> O modelo de dados é uma representação abstrata de um conjunto de informações que descreve entidades, atributos e relações.
> É um componente fundamental em sistemas de informação usado para organizar, armazenar e recuperar dados de forma eficiente.


___

``Users`` 
> Entidade que representa um utilizador do sistema.
> Pode ser um docente ou um aluno.

- id - identificador do utilizador
- name - nome do utilizador
- email - email do utilizador
- token - token de autenticação do utilizador
- github_username - username do utilizador no github
- github_id - username do utilizador no github
- is_created - flag que indica se o utilizador já foi criado

Restrições

- id - valor numérico inteiro não negativo. Chave primária.
- name - não pode ser vazio ou null.
- email - não pode ser vazio ou null. Deve ser um email válido.
- token - não pode ser vazio ou null. Valor único.
- github_username - não pode ser vazio ou null. Valor único.
- github_id - não pode ser vazio ou null. Valor único.
- is_created - não pode ser null.

``Teacher`` - especialização de ``User``
> Entidade que representa um docente do sistema.
- id - identificador do utilizador.
- github_token - token de autenticação do docente no github.

Restrições

- id - valor numérico inteiro não negativo. Chave primária. Chave estrangeira para ``Users``.
- github_token - não pode ser vazio ou null. Valor único.

``Student`` - especialização de ``Users``
> Entidade que representa um aluno do sistema.
- id - identificador do utilizador.
- school_id - identificador da escola do aluno.

Restrições

- id - valor numérico inteiro não negativo. Chave primária. Chave estrangeira para ``Users``.
- school_id - valor numérico inteiro não negativo. Valor único.

``Course``
> Entidade que representa uma disciplina/cadeira do sistema.
- course_id - identificador da disciplina/cadeira.
- name - nome da disciplina/cadeira.
- org_url - url da organização no github.
- teacher_id - identificador do professor criador da cadeira.

Restrições

- course_id - valor numérico inteiro não negativo. Chave primária.
- name - não pode ser vazio ou null. Valor único.
- org_url - não pode ser vazio ou null. Valor único.
- teacher_id - valor numérico inteiro não negativo. Chave estrangeira para ``Teacher``.

``Classroom``
> Entidade que representa uma turma, pertencente a uma disciplina/cadeira do sistema.
- class_id - identificador da turma.
- name - nome da turma.
- invite_link - link de convite para a turma
- last_sync - data da última sincronização.
- is_archived - flag que indica se a turma está arquivada.
- course_id - identificador da disciplina/cadeira associada.

Restrições

- class_id - valor numérico inteiro não negativo. Chave primária.
- name - não pode ser vazio ou null.
- invite_link - não pode ser vazio ou null. Valor único.
- last_sync - não pode ser null. formato de data. 
- is_archived - não pode ser null. Valor por omissão é falso.
- course_id - valor numérico inteiro não negativo. Chave estrangeira para ``Course``.

``Assignment``
> Entidade que representa uma tarefa, atribuída a uma turma do sistema.
- assignment_id - identificador da tarefa
- title - título da tarefa
- description - descrição da tarefa
- release_date - data de lançamento da tarefa.
- max_groups - número máximo de grupos
- max_elems_per_group - número máximo de elementos por grupo
- classroom_id - identificador da classroom associada.

Restrições

- assignment_id - valor numérico inteiro não negativo. Chave primária.
- title - não pode ser vazio ou null. Valor único.
- description - não pode ser vazio ou null. Valor único.
- release_date - não pode ser null. formato de data.
- max_groups - não pode ser null. valor numérico inteiro não negativo.
- max_elems_per_group - não pode ser null. valor numérico inteiro não negativo.
- classroom_id - valor numérico inteiro não negativo. Chave estrangeira para ``Classroom``.

``Delivery`` - entidade fraca de ``Assignment``
> Entidade que representa as entregas de uma tarefa do sistema.
- delivery_id - identificador da entrega
- tag_control - tag de controlo da entrega
- due_date - data limite de entrega

``Team`` - entidade fraca de ``Assignment``
> Entidade que representa um grupo de uma turma do sistema.
- team_id - identificador do grupo
- name - nome do grupo
- is_created - flag que indica se o grupo já foi criado

``Feedback`` - entidade fraca de ``Team``
> Entidade que representa os _feedback´s_ dados por um docente a um grupo do sistema.
- feedback_id - identificador do feedback
- description - descrição do feedback
- Label - categoria do feedback

``Repo`` - entidade fraca de ``Team``
> Entidade que representa um repositório Github de um grupo do sistema.
- repo_id - identificador do repositório
- url - url do repositório
- name - nome do repositório
- is_created - flag que indica se o repositório já foi criado

``Tag`` - entidade fraca de ``Repo``
> Entidade que representa as _tag´s_ de um repositório Github do sistema.
- tag_id - identificador da tag
- name - nome da tag
- tag_date - data da tag
- is_delivered - flag que indica se a tag foi entregue

``Request``
> Entidade que representa um pedido de escrita externa.
- id - identificador do pedido

``CreateRepo`` - especialização de ``Request``
> Entidade que representa um pedido de criação de um repositório.
- repo_id - identificador do repositório

``ArchieveRepo`` - especialização de ``Request``
> Entidade que representa um pedido de arquivamento de um repositório.
- repo_id - identificador do repositório

``LeaveCourse`` - especialização de ``Request``
> Entidade que representa um pedido de saída de uma disciplina/cadeira.
- course_id - identificador da disciplina/cadeira

``CreateTeam`` - especialização de ``Request``
> Entidade que representa um pedido de criação de uma turma.
- team_id - identificador do grupo

``JoinTeam`` - especialização de ``Request``
> Entidade que representa um pedido de adesão a uma turma.
- team_id - identificador do grupo

``LeaveTeam`` - especialização de ``Request``
> Entidade que representa um pedido de saída de uma turma.
- team_id - identificador do grupo

``Apply`` - especialização de ``Request``
> Entidade que representa um pedido de aplicação como docente no sistema.

``Composite`` - especialização de ``Request``
> Entidade que representa um conjunto de Request's.
