## ESPECIFICAÇÃO DE CASOS DE USO 

## ‒ [UC001] Cadastro de Usuário 

- Nome: Cadastro de Usuário. 

- Ator: Usuário (Visitante). 

- Descrição: O sistema permite que um novo usuário se registre para acessar as funcionalidades da plataforma. 

#### Fluxo Principal: 

1. O usuário acessa a opção “Criar Conta”. 

2. O sistema exibe o formulário de dados (Nome, E-mail, Senha, CPF/CNPJ). 

3. O usuário preenche os campos e confirma. 

4. O sistema valida o formato dos dados e a unicidade do e-mail/documento. 

5. O sistema registra os dados e solicita a escolha do perfil inicial. 

#### Fluxos Alternativos: 

   - Dados Inválidos: O sistema sinaliza o erro nos campos específicos e impede o envio. 

   - E-mail/Documento já Cadastrado: O sistema informa que os dados já constam na base e sugere login. 

- Pré-condições: O usuário não deve estar autenticado. 

- Pós-condições: Registro criado no banco de dados e usuário redirecionado para seleção de perfil. 

## ‒ [UC002] Login 

- Nome: Login. 

- Ator: Usuário. 

- Descrição: Autenticação do usuário para acesso ao dashboard. 

#### Fluxo Principal: 

   1. O usuário insere e-mail e senha. 

   2. O sistema verifica as credenciais. 

   3. O sistema libera o acesso ao dashboard conforme o último perfil utilizado. 

#### Fluxos Alternativos: 

   - Credenciais Inválidas: O sistema exibe erro e retorna ao início do formulário. 

- Pré-condições: Usuário possuir conta ativa. 

- Pós-condições: Sessão iniciada com token de autenticação ativo. 

## ‒ [UC003] Recuperação de Login 

- Nome: Recuperação de Login. 

- Ator: Usuário. 

- Descrição: Recuperação de acesso através de código de segurança. 

#### Fluxo Principal: 

   1. Usuário seleciona “Esqueci minha senha”. 

   2. Escolhe o método (E-mail ou SMS). 

   3. O sistema envia o código. 

   4. Usuário valida o código e define nova senha. 

 - Pré-condições: Usuário ter e-mail ou telefone cadastrado. 

- Pós-condições: Senha atualizada e acesso permitido. 

## ‒ [UC004] Seleção de Perfil 

- Nome: Seleção de Perfil. 

- Ator: Usuário. 

- Descrição: Define se o usuário atuará como Empregador ou Colaborador na sessão. 

#### Fluxo Principal: 

   1. O sistema apresenta os cards de “Empregador” e “Colaborador”. 

   2. O usuário seleciona a opção desejada. 

   3. O sistema carrega as permissões e telas específicas do papel escolhido. 

- Pré-condições: Usuário autenticado. 

- Pós-condições: Interface ajustada ao escopo do perfil. 

## ‒ [UC005] Dashboard (Empregador) 

- Nome: Visualizar Dashboard de Empregador. 

- Ator: Empregador. 

- Descrição: Tela principal com visão geral das vagas e menu de navegação. Fluxo Principal: 

   1. O sistema carrega o resumo de vagas ativas. 

   2. Exibe o menu lateral (Início, Vagas, Notificações, Perfil). 

   3. Exibe o botão de criação de novas oportunidades. 

- Pré-condições: Perfil “Empregador” selecionado. 

- Pós-condições: Exibição dos dados analíticos e de gestão. 

## ‒ [UC006] Gerenciar Histórico de Vagas 

- Nome: Gerenciar Histórico de Vagas. 

- Ator: Empregador. 

- Descrição: Permite visualizar e alterar o status de vagas passadas e presentes. 

#### Fluxo Principal: 

   1. O usuário acessa a aba “Minhas Vagas”. 

   2. O sistema lista as vagas ordenadas por data. 

   3. O usuário pode clicar para ver detalhes ou editar. 

- Pré-condições: Ter ao menos uma vaga criada. 

## ‒ [UC007] Criar Vaga 

- Nome: Criar Vaga. 

- Ator: Empregador. 

- Descrição: Cadastro de nova oportunidade de serviço. 

#### Fluxo Principal: 

1. O empregador clica em “+ Criar Vaga”. 

2. O sistema verifica se o perfil está completo (contato). 

3. Usuário preenche Título, Descrição, Valor e Local. 

4. O sistema valida e publica a vaga. 

#### Fluxos Alternativos: 

   - Perfil Incompleto: O sistema bloqueia a criação e exige preenchimento dos dados de contato. 

- Pré-condições: Perfil Empregador ativo. 

- Pós-condições: Vaga disponível para busca. 

## ‒ [UC008] Listagem de Candidatos 

- Nome: Visualizar Lista de Candidatos. 

- Ator: Empregador. 

- Descrição: Visualização de quem se candidatou a uma vaga específica. 

#### Fluxo Principal: 

1. Empregador seleciona uma vaga ativa. 

2. O sistema lista os colaboradores inscritos com seus respectivos IDs. 

3. O sistema permite acesso ao botão de avaliação do colaborador. 

Pré-condições: Vaga possuir ao menos uma candidatura. 

## ‒ [UC009] Notificações (Mobile/Web) 

- Nome: Receber Notificações. 

- Ator: Usuário. 

- Descrição: Alertas sobre novas candidaturas, vagas ou avaliações. 

#### Fluxo Principal: 

   1. Ocorre um evento no sistema (ex: nova candidatura). 

   2. O sistema verifica dispositivos vinculados (Classe DispositivoMobile). 

   3. O sistema dispara o alerta Push ou badge no sistema web. 

- Pré-condições: Evento disparador ocorrido. 

## ‒ [UC010] Rastreamento de Visualizações 

- Nome: Rastrear Visualizações da Vaga. 

- Ator: Sistema. 

- Descrição: Conta quantos usuários únicos visualizaram a vaga. 

#### Fluxo Principal: 

   1. Colaborador acessa detalhes de uma vaga. 

   2. Sistema verifica se já houve acesso desse ID nas últimas 24h. 

   3. Se não, incrementa o contador. 

- Pós-condições: Métricas de visualização atualizadas. 

## ‒ [UC011] Contagem de Candidaturas 

- Nome: Contagem de Candidaturas. 

- Ator: Sistema. 

- Descrição: Exibe o total de interessados no card da vaga. 

#### Fluxo Principal: 

1. Sistema monitora a tabela de candidaturas vinculada à vaga. 

2. Atualiza o dashboard do empregador em tempo real. 

## ‒ [UC012] Ciclo de Vida da Vaga 

- Nome: Alterar Status da Vaga. 

- Ator: Empregador. 

- Descrição: Pausar, cancelar ou concluir uma vaga. 

#### Fluxo Principal: 

1. Usuário seleciona ação (ex: “Concluir”). 

2. Sistema altera o status e remove da busca pública. 

## ‒ [UC013] Buscar Vagas 

- Nome: Buscar Vagas. 

- Ator: Colaborador. 

- Descrição: Filtragem de oportunidades por critérios. 

#### Fluxo Principal: 

1. Colaborador insere termos na busca ou usa filtros. 

2. Sistema retorna lista de vagas compatíveis. 

- Pré-condições: Perfil “Colaborador” ativo. 

## ‒ [UC014] Candidatar-se a Vaga 

- Nome: Candidatar-se a Vaga. 

- Ator: Colaborador. 

- Descrição: Registro de interesse em um trabalho. 

#### Fluxo Principal: 

1. Colaborador clica em “Candidatar-se”. 

2. O sistema valida as regras (não ser vaga própria/não ser duplicada). 

3. Sistema gera o código único de candidatura e notifica o empregador. 

#### Fluxos Alternativos: 

- Vaga Própria/Duplicada: O sistema bloqueia a inscrição e informa o motivo. 

- Pós-condições: Candidatura registrada e visível para o empregador. 

## ‒ [UC015] Receber Avaliações 

- Nome: Receber Avaliações. 

- Ator: Colaborador. 

- Descrição: Feedback do empregador após o serviço. 

#### Fluxo Principal: 

1. Empregador avalia o candidato. 

2. Nota e comentário são vinculados ao perfil do colaborador. 

## ‒ [UC016] Histórico de Trabalhos 

- Nome: Visualizar Histórico de Trabalhos. 

- Ator: Colaborador. 

- Descrição: Listagem de todas as vagas em que o colaborador atuou. 

#### Fluxo Principal: 

1. Usuário acessa aba “Meus Trabalhos”. 

2. Sistema lista por data e status de conclusão. 

## ‒ [UC017] Dashboard (Colaborador) 

- Nome: Visualizar Dashboard de Colaborador. 

- Ator: Colaborador. 

- Descrição: Tela principal com visão geral das candidaturas, vagas salvas e notificações. 

#### Fluxo Principal: 

   1. O sistema carrega o resumo das candidaturas ativas e o status de cada uma. 

   2. Exibe vagas salvas ou recomendadas. 

   3. Apresenta um feed de notificações relevantes. 

- Pré-condições: Perfil “Colaborador” selecionado e autenticado. 

- Pós-condições: Exibição dos dados relevantes para o colaborador. 

## ‒ [UC018] Gerenciar Status da Candidatura 

- Nome: Gerenciar Status da Candidatura. 

- Ator: Empregador. 

- Descrição: Permite ao empregador alterar o status de uma candidatura (ex: Em Análise, Entrevista, Contratado, Rejeitado). 

#### Fluxo Principal: 

   1. Empregador acessa a lista de candidatos de uma vaga. 

   2. Seleciona um candidato específico. 

   3. Escolhe a opção para alterar o status da candidatura. 

   4. Seleciona o novo status na lista de opções. 

   5. O sistema atualiza o status e notifica o colaborador. 

- Pré-condições: Vaga ativa com candidaturas e perfil “Empregador” ativo. 

- Pós-condições: Status da candidatura atualizado e colaborador notificado. 

## ‒ [UC019] Avaliar Colaborador 

- Nome: Avaliar Colaborador. 

- Ator: Empregador. 

- Descrição: Permite ao empregador atribuir uma nota e um comentário a um colaborador após a conclusão de um serviço ou processo seletivo. 

#### Fluxo Principal: 

1. Empregador acessa o perfil do colaborador ou a candidatura finalizada. 

2. Clica na opção “Avaliar Colaborador”. 

3. Atribui uma nota (ex: de 1 a 5 estrelas). 

4. Escreve um comentário descritivo sobre o desempenho ou adequação do colaborador. 

 5. Confirma a avaliação. 

- Pré-condições: Serviço ou processo seletivo concluído com o colaborador. 

- Pós-condições: Avaliação registrada e visível no perfil do colaborador (se aplicável). 

## ‒ [UC020] Acessar Painel de Controle (Empregador) 

- Nome: Acessar Painel de Controle. 

- Ator: Empregador. 

- Descrição: Permite ao empregador acessar funcionalidades administrativas e de gestão avançada da plataforma. 

#### Fluxo Principal: 

   1. Empregador faz login e seleciona o perfil “Empregador”. 

   2. No dashboard, acessa a opção “Painel de Controle” no menu lateral. 

   3. O sistema exibe as opções de gestão (ex: relatórios, configurações da conta, gestão de usuários). 

- Pré-condições: Perfil “Empregador” ativo e autenticado. 

- Pós-condições: Exibição do painel de controle com as funcionalidades administrativas. 

## ‒ [UC021] Alterar Senha 

- Nome: Alterar Senha. 

- Ator: Usuário Autenticado. 

- Descrição: Permite ao usuário alterar sua senha de acesso à plataforma. 

#### Fluxo Principal: 

1. Usuário acessa as configurações de perfil. 

2. Clica na opção “Alterar Senha”. 

3. Informa a senha atual. 

4. Informa a nova senha e a confirma. 

5. O sistema valida a senha atual e os requisitos da nova senha. 

6. Confirma a alteração da senha. 

#### Fluxos Alternativos: 

   - Senha Atual Incorreta: O sistema informa que a senha atual está incorreta. 

   - Nova Senha Inválida: O sistema informa que a nova senha não atende aos requisitos de segurança. 

- Pré-condições: Usuário autenticado. 

- Pós-condições: Senha de acesso atualizada. 

#### ‒ [UC022] Editar Dados Pessoais 

- Nome: Editar Dados Pessoais. 

- Ator: Usuário Autenticado. 

- Descrição: Permite ao usuário visualizar e atualizar suas informações pessoais cadastradas na plataforma. 

#### Fluxo Principal: 

1. Usuário acessa as configurações de perfil. 

2. Clica na opção “Editar Dados Pessoais”. 

3. O sistema exibe um formulário pré-preenchido com os dados atuais. 

4. Usuário edita as informações desejadas (ex: nome, e-mail, telefone). 

5. Confirma as alterações. 

#### Fluxos Alternativos: 

- Dados Inválidos: O sistema sinaliza erros em campos específicos e impede o salvamento. 

- Pré-condições: Usuário autenticado. 

- Pós-condições: Dados pessoais atualizados no sistema. 

## ‒ [UC023] Solicitar Exclusão de Conta 

- Nome: Solicitar Exclusão de Conta. 

- Ator: Usuário Autenticado. 

- Descrição: Permite ao usuário solicitar a exclusão permanente de sua conta na plataforma. 

#### Fluxo Principal: 

1. Usuário acessa as configurações de perfil. 

2. Clica na opção “Solicitar Exclusão de Conta”. 

3. O sistema exibe um aviso sobre as consequências da exclusão (perda de dados, etc.). 

4. Usuário confirma a solicitação de exclusão (pode ser necessário inserir a senha para confirmação). 

5. O sistema inicia o processo de exclusão da conta e informa o usuário. 

#### Fluxos Alternativos: 

- Confirmação Negada: Usuário desiste da exclusão. 

- Pré-condições: Usuário autenticado. 

- Pós-condições: Conta marcada para exclusão ou excluída, conforme política do sistema. 

