## 23 Março - reunião com o professor
- Os alunos não irão precisar de tokens deles, mas sim tokens de um professor. Estes tokens serão só de leitura e não poderão ser editados. Estes tokens do professor poderão ser usados para atualizar a aplicação(só em termos de read) logo poderão estar guardados na db.
- Os tokens de escrita serão guardados no telemóvel do professor. A forma de os guardar poderá ser feita através dum sharedpreferences, mas a chave que cifrou o token estára num keystore onde a validação se é o professor a mexer no telemóvel é feita através da biometria.
- As ações que possivelmente são compremetodoras serão requisitadas atravês da ‘web’, e forma de validar-se será através do telemóvel do professor. No repo de cada grupo poderá ser visível que existe ações que estão pending e ações rejeitadas. As ações rejeitadas podem passar a pending pelo professor, através da web.
- Os requests na db serão necessitam estar explicitos se vão mexer em uma 'team' ou num repo. No telemóvel do professor as várias explicitas do que vai acontecer no github, apesar na nossa aplicação ser uma ação mais simples.
- As classrooms são arquivadas quando chega ao fim do semestre(será que o course também poderá ser? quando mudam a cadeira ou assim)

## 24 Março - João
- Começo no uso no JDBI
- Começo na criação das classes de domain
- Criação do diário

## 26 Março - Ricardo
- Progresso na utilização de JDBI
- Progresso na criação das rotas
- Descobrir a possibilidade de ter a mesma rota para duas funções no spring e fazer a distinção através do header, podendo ter então como exemplo a rota /home e depender se é um aluno ou professor, adicionando ao header correspondente.

## 30 Março - Ricardo
- Outbox pattern
- Schedule para o envio de emails

## 31 Março - 9 Abril - João
- Testes ao repositório
- Resolução de bugs presente no repositório
- Criação dum ficheiro de insert para testes. Para o uso correto deverá garantir-se que corre logo a seguir a criação das tabelas, devido ao serials

## Reunião com o professor - 4 Abril
- 

## 10 Abril - ? Abril - João
- Aprender como usar o mockito para testes
- Testes ao services

## 24 Abril - Ricardo & André
- Decisao dos templates todos no home
- Nenhum siren retorna links e actions sem ser o home
- O react trata dos expand e de arranjar os templates

## 30 Abril - Ricardo
- Remoção do 'Apply' dos requests, pois não existe um utilizador ainda para o fazer, mas sim um professor pendente
- Implementação de ‘triggers’
- localhost para 127.0.0.1 devido ao redirect da documetnação do github oauth

## 2 Maio - Ricardo
- Arranjar cookies com a utilização do ngrok
- Opçao de domain com '.ngrok-free.app' para os cookies
- Problema no logout, pois são feitos pedidos para o localhost e não para o ngrok