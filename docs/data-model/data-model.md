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

``PendingStudent`` 
> Entidade que representa um aluno que ainda não se registou no sistema.
- id - identificador do utilizador.
- school_id - identificador da escola do aluno.
- email - email do utilizador
- github_username - username do utilizador no github
- github_id - username do utilizador no github
- token - token de autenticação do utilizador
- is_created - flag que indica se o utilizador já foi criado
- created_at - data de criação do utilizador

Restrições

- id - valor numérico inteiro não negativo. Chave primária.
- school_id - valor numérico inteiro não negativo. 
- email - não pode ser vazio ou null. Deve ser um email válido.
- github_username - não pode ser vazio ou null.
- github_id - não pode ser vazio ou null.
- token - não pode ser vazio ou null. Valor único.
- is_created - não pode ser null.
- created_at - não pode ser null. formato de data.

``PendingTeacher``
> Entidade que representa um docente que ainda não se registou no sistema.
- id - identificador do utilizador.
- email - email do utilizador
- github_username - username do utilizador no github
- github_id - username do utilizador no github
- token - token de autenticação do utilizador
- is_created - flag que indica se o utilizador já foi criado
- created_at - data de criação do utilizador
- github_token - token de autenticação do docente no github.

Restrições

- id - valor numérico inteiro não negativo. Chave primária.
- email - não pode ser vazio ou null. Deve ser um email válido.
- github_username - não pode ser vazio ou null.
- github_id - não pode ser vazio ou null.
- token - não pode ser vazio ou null. Valor único.
- is_created - não pode ser null.
- created_at - não pode ser null. formato de data.
- github_token - não pode ser vazio ou null. 

``Course``
> Entidade que representa uma disciplina/cadeira do sistema.
- id - identificador da disciplina/cadeira.
- name - nome da disciplina/cadeira.
- org_url - url da organização no github.
- org_id - identificador da organização no github.
- teacher_id - identificador do professor criador da cadeira.

Restrições

- id - valor numérico inteiro não negativo. Chave primária.
- name - não pode ser vazio ou null. Valor único.
- org_url - não pode ser vazio ou null. Valor único.
- org_id - não pode ser vazio ou null. Valor único.
- teacher_id - valor numérico inteiro não negativo. Chave estrangeira para ``Teacher``.

``Classroom``
> Entidade que representa uma turma, pertencente a uma disciplina/cadeira do sistema.
- id - identificador da turma.
- name - nome da turma.
- invite_code - código de convite para a turma
- last_sync - data da última sincronização.
- is_archived - flag que indica se a turma está arquivada.
- course_id - identificador da disciplina/cadeira associada.

Restrições

- id - valor numérico inteiro não negativo. Chave primária.
- name - não pode ser vazio ou null.
- invite_code - não pode ser vazio ou null. Valor único.
- last_sync - não pode ser null. formato de data. 
- is_archived - não pode ser null. Valor por omissão é falso.
- course_id - valor numérico inteiro não negativo. Chave estrangeira para ``Course``.

``Assignment``
> Entidade que representa uma tarefa, atribuída a uma turma do sistema.
- id - identificador da tarefa.
- title - título da tarefa.
- description - descrição da tarefa.
- release_date - data de lançamento da tarefa.
- max_number_groups - número máximo de grupos
- min_elems_per_group - número mínimo de elementos por grupo.
- max_elems_per_group - número máximo de elementos por grupo.
- classroom_id - identificador da classroom associada.

Restrições

- id - valor numérico inteiro não negativo. Chave primária.
- title - não pode ser vazio ou null. Valor único.
- description - não pode ser vazio ou null. Valor único.
- release_date - não pode ser null. formato de data.
- min_elems_per_group - não pode ser null. valor numérico inteiro não negativo.
- max_number_groups - não pode ser null. valor numérico inteiro não negativo.
- max_elems_per_group - não pode ser null. valor numérico inteiro não negativo.
- classroom_id - valor numérico inteiro não negativo. Chave estrangeira para ``Classroom``.

``Delivery`` - entidade fraca de ``Assignment``
> Entidade que representa as entregas de uma tarefa do sistema.
- id - identificador da entrega.
- tag_control - tag de controlo da entrega.
- due_date - data limite de entrega.
- last_sync - data da última sincronização.
- assignment_id - identificador da tarefa associada a esta entrega.

Restrições

- id - valor numérico inteiro não negativo. Chave primária.
- tag_control - não pode ser vazio ou null.
- due_date - não pode ser null. formato de data.
- last_sync - não pode ser null. formato de data.
- assignment_id - valor numérico inteiro não negativo. Chave estrangeira para ``Assignment``.

``Team`` - entidade fraca de ``Assignment``
> Entidade que representa um grupo de uma turma do sistema.
- id - identificador do grupo.
- name - nome do grupo.
- is_created - flag que indica se o grupo já foi criado.
- is_closed - flag que indica se o grupo está fechado.
- assignment_id - identificador da tarefa associada a esta entrega.

Restrições

- id - valor numérico inteiro não negativo. Chave primária.
- name - não pode ser vazio ou null.
- is_created - não pode ser null.
- is_closed - não pode ser null.
- assignment_id - valor numérico inteiro não negativo. Chave estrangeira para ``Assignment``.

``Feedback`` - entidade fraca de ``Team``
> Entidade que representa os _feedback´s_ dados por um docente a um grupo do sistema.
- id - identificador do feedback.
- description - descrição do feedback.
- Label - categoria do feedback.
- team_id - identificador do grupo associado.

Restrições

- id - valor numérico inteiro não negativo. Chave primária.
- description - não pode ser vazio ou null.
- Label - não pode ser vazio ou null.
- team_id - valor numérico inteiro não negativo. Chave estrangeira para ``Team``.

``Repo`` - entidade fraca de ``Team``
> Entidade que representa um repositório Github de um grupo do sistema.
- id - identificador do repositório.
- url - url do repositório.
- name - nome do repositório.
- is_created - flag que indica se o repositório já foi criado.
- team_id - identificador do grupo associado.

Restrições

- id - valor numérico inteiro não negativo. Chave primária.
- url - não pode ser vazio ou null. Valor único.
- name - não pode ser vazio ou null.
- is_created - não pode ser null.
- team_id - valor numérico inteiro não negativo. Chave estrangeira para ``Team``.

``Tags`` - entidade fraca de ``Repo``
> Entidade que representa as _tag´s_ de um repositório Github do sistema.
- id - identificador da tag.
- name - nome da tag.
- tag_date - data da tag.
- is_delivered - flag que indica se a tag foi entregue.
- delivery_id - identificador da tarefa para a qual a tag se destina.
- repo_id - identificador do repositório a qual esta tag pertence.

Restrições

- id - valor numérico inteiro não negativo. Chave primária.
- name - não pode ser vazio ou null.
- tag_date - não pode ser null. formato de data.
- is_delivered - não pode ser null.
- delivery_id - valor numérico inteiro não negativo. Chave estrangeira para ``Delivery``.
- repo_id - valor numérico inteiro não negativo. Chave estrangeira para ``Repo``.

``Student_Classroom``
> Entidade que representa a associação entre um aluno e uma turma.
- student_id - identificador do aluno.
- classroom_id - identificador da turma.

Restrições

- student_id - valor numérico inteiro não negativo. Chave estrangeira para ``Users``.
- course_id - valor numérico inteiro não negativo. Chave estrangeira para ``Course``.

``Student_Team``
> Entidade que representa a associação entre um aluno e um grupo.
- student_id - identificador do aluno.
- team_id - identificador do grupo.

Restrições

- student_id - valor numérico inteiro não negativo. Chave estrangeira para ``Users``.
- team_id - valor numérico inteiro não negativo. Chave estrangeira para ``Team``.


``Request``
> Entidade que representa um pedido de escrita externa.
- id - identificador do pedido.
- creator - identificador do criador do pedido.
- composite - identificador de um possível pedido composto caso este faça parte desse pedido.
- state - estado atual do pedido.

Restrições

- id - valor numérico inteiro não negativo. Chave primária.
- creator - valor numérico inteiro não negativo. Chave estrangeira para ``Users``.
- composite - valor numérico inteiro não negativo. Chave estrangeira para ``Request``. Pode ser null.
- state - não pode ser null. Toma os valores 'pending', 'accepted', 'rejected' ou 'Not_Concluded'.

``CreateRepo`` - especialização de ``Request``
> Entidade que representa um pedido de criação de um repositório.
- id - identificador do pedido.
- repo_id - identificador do repositório.

Restrições

- id - valor numérico inteiro não negativo. Chave primária. Chave estrangeira para ``Request``.
- repo_id - não pode ser null. valor numérico inteiro não negativo.

``ArchieveRepo`` - especialização de ``Request``
> Entidade que representa um pedido de arquivamento de um repositório.
- id - identificador do pedido.
- repo_id - identificador do repositório.

Restrições

- id - valor numérico inteiro não negativo. Chave primária. Chave estrangeira para ``Request``.
- repo_id - não pode ser null. valor numérico inteiro não negativo.

``LeaveCourse`` - especialização de ``Request``
> Entidade que representa um pedido de saída de uma disciplina/cadeira.
- id - identificador do pedido.
- course_id - identificador da disciplina/cadeira.

Restrições

- id - valor numérico inteiro não negativo. Chave primária. Chave estrangeira para ``Request``.
- course_id - não pode ser null. valor numérico inteiro não negativo.

``CreateTeam`` - especialização de ``Request``
> Entidade que representa um pedido de criação de uma turma.
- id - identificador do pedido.
- team_id - identificador do grupo.
- github_team_id - identificador do grupo no Github.

Restrições

- id - valor numérico inteiro não negativo. Chave primária. Chave estrangeira para ``Request``.
- team_id - não pode ser null. valor numérico inteiro não negativo.
- github_team_id - não pode ser null. valor numérico inteiro não negativo.

``JoinTeam`` - especialização de ``Request``
> Entidade que representa um pedido de adesão a uma turma.
- id - identificador do pedido.
- team_id - identificador do grupo.
- assignment_id - identificador da tarefa associada ao pedido.

Restrições

- id - valor numérico inteiro não negativo. Chave primária. Chave estrangeira para ``Request``.
- team_id - não pode ser null. valor numérico inteiro não negativo.
- assignment_id - não pode ser null. valor numérico inteiro não negativo.

``LeaveTeam`` - especialização de ``Request``
> Entidade que representa um pedido de saída de uma turma.
- id - identificador do pedido.
- team_id - identificador do grupo.

Restrições

- id - valor numérico inteiro não negativo. Chave primária. Chave estrangeira para ``Request``.
- team_id - não pode ser null. valor numérico inteiro não negativo.

``Apply`` - especialização de ``Request``
> Entidade que representa um pedido de aplicação como docente no sistema.
- id - identificador do pedido.
- teacher_id - identificador do professor associado ao pedido.
- state - estado atual do pedido.

Restrições

- id - valor numérico inteiro não negativo. Chave primária. Chave estrangeira para ``Request``.
- team_id -  não pode ser null. valor numérico inteiro não negativo. Chave estrangeira para ``Users``.
- state - não pode ser null. Toma os valores 'pending', 'accepted', 'rejected'.

``Composite`` - especialização de ``Request``
> Entidade que representa um conjunto de Request's.
- id - identificador do pedido.

Restrições

- id - valor numérico inteiro não negativo. Chave primária. Chave estrangeira para ``Request``.

``Outbox``
> Entidade que representa a caixa de saída de um utilizador.
- user_id - identificador do utilizador.
- status - estado da caixa de saída.
- sent_at - data de envio da caixa de saída.

Restrições

- user_id - valor numérico inteiro não negativo. Chave estrangeira para ``Users``.
- status - não pode ser null. Toma os valores 'pending' ou 'sent'.

``Cooldown``
> Entidade que representa o tempo de espera de um utilizador.
- id - identificador do tempo de espera.
- user_id - identificador do utilizador.
- end_date - data de fim do tempo de espera.

Restrições

- id - valor numérico inteiro não negativo. Chave primária.
- user_id - valor numérico inteiro não negativo. Chave estrangeira para ``Users``.
- end_date - não pode ser null. formato de data.

``OTP``
> Entidade que representa um código de autenticação de dois fatores.
- user_id - identificador do utilizador.
- otp - código de autenticação de dois fatores.
- expires_at - data de expiração do código de autenticação de dois fatores.
- tries - número de tentativas de autenticação de dois fatores.

Restrições  

- user_id - valor numérico inteiro não negativo. Chave estrangeira para ``Users``.
- otp - não pode ser null. valor numérico inteiro não negativo de 6 dígitos.
- expires_at - não pode ser null. formato de data.
- tries - valor numérico inteiro não negativo.

``ChallengeInfo``
> Entidade que representa a informação sobre um pedido de autenticação vindo do aplicativo móvel.
- state - valor de controlo do pedido de autenticação.
- challenge - código de autenticação de dois fatores.
- challenge_method - método de encriptação do challenge.

Restrições

- state - não pode ser null. Chave primária. 
- challenge - não pode ser null.
- challenge_method - não pode ser null. Toma os valores 'plain' ou 's256'.

