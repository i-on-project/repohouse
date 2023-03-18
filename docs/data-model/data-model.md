# Modelo de dados

> O modelo de dados é uma representação abstrata de um conjunto de informações que descreve entidades, atributos e relações.
> É um componente fundamental em sistemas de informação usado para organizar, armazenar e recuperar dados de forma eficiente.


___

``User`` 
> Entidade que representa um utilizador do sistema.
> Pode ser um docente ou um aluno.

- id - identificador do utilizador
- name - nome do utilizador
- email - email do utilizador
- github_username - username do utilizador no github

``Teacher`` - especialização de ``User``
> Entidade que representa um docente do sistema.
- is_Created - flag que indica se o docente já foi criado

``Student`` - especialização de ``User``
> Entidade que representa um aluno do sistema.
- school_id - identificador da escola do aluno

``Course``
> Entidade que representa uma disciplina/cadeira do sistema.
- course_id - identificador da disciplina/cadeira
- name - nome da disciplina/cadeira
- org_url - url da organização no github

``Classroom``
> Entidade que representa uma turma, pertencente a uma disciplina/cadeira do sistema.
- class_id - identificador da turma
- name - nome da turma
- invite_link - link de convite para a turma
- last_sync - data da última sincronização

``Assignment``
> Entidade que representa uma tarefa, atribuída a uma turma do sistema.
- assignment_id - identificador da tarefa
- title - título da tarefa
- description - descrição da tarefa
- release_date - data de lançamento da tarefa
- max_groups - número máximo de grupos
- max_elems_per_group - número máximo de elementos por grupo

``Delivery`` - entidade fraca de ``Assignment``
> Entidade que representa as entregas de uma tarefa do sistema.
- delivery_id - identificador da entrega
- tag_control - tag de controlo da entrega
- due_date - data limite de entrega

``Team`` - entidade fraca de ``Assignment``
> Entidade que representa um grupo de uma turma do sistema.
- team_id - identificador do grupo
- name - nome do grupo
- dirty_flag - flag que indica se o grupo sofreu alterações

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

``Tag`` - entidade fraca de ``Repo``
> Entidade que representa as _tag´s_ de um repositório Github do sistema.
- tag_id - identificador da tag
- name - nome da tag
- tag_date - data da tag
- is_delivered - flag que indica se a tag foi entregue

``Request``
> Entidade que representa um pedido de escrita externa.
- id - identificador do pedido

``Create`` - especialização de ``Request``
> Entidade que representa um pedido de criação de uma turma.
- team_id - identificador do grupo

``Join`` - especialização de ``Request``
> Entidade que representa um pedido de adesão a uma turma.
- team_id - identificador do grupo

``Leave`` - especialização de ``Request``
> Entidade que representa um pedido de saída de uma turma.
- team_id - identificador do grupo

``Apply`` - especialização de ``Request``
> Entidade que representa um pedido de aplicação como docente no sistema.