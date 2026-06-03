# 1. Identificação do Projeto

```
● Nome do Projeto: Karteria
● Data de Abertura: 11 de Fevereiro de 2026
● Patrocinador: Linkedin
● Gerente do Projeto: Italo Gabriel
```
# 2. Justificativa

A continuidade do projeto Karteria é necessária para evoluir a plataforma de um sistema primário de conexões para uma ferramenta segura e madura de gestão de recrutamento. O desenvolvimento desta nova fase justifica-se por três necessidades centrais:

● **Controle do Processo Seletivo:** Acompanhamento prático do funil de contratação através da gestão do status das candidaturas (Em análise,
Aprovado, Rejeitado), superando o modelo atual que registra apenas a aplicação e a avaliação.

● **Autonomia e Conformidade:** Fornecimento de ferramentas essenciais para a gestão de informações pessoais, permitindo aos usuários a alteração de senhas , a edição de dados cadastrais e a exclusão permanente da conta , alinhando a plataforma às exigências de privacidade.

● **Segurança e Rastreabilidade:** Elevação da proteção sistêmica mediante controles rigorosos de acesso por perfil , validação robusta de dados e o registro estruturado de logs para ações críticas do sistema.

# 3. Objetivos do Projeto

**Objetivo Geral:** Implementar a segunda fase de desenvolvimento da plataforma Karteria, com foco em elevar a maturidade do sistema através da introdução de ferramentas para a gestão do funil de candidaturas, ampliação da autonomia de dados dos usuários e fortalecimento da segurança e rastreabilidade técnica da aplicação.

**Objetivos Específicos:**

● **Gestão de Processo Seletivo:** Capacitar o sistema para que o empregador possa alterar e gerenciar o status das candidaturas nas etapas "Em análise", "Aprovado" e "Rejeitado".

● **Autonomia do Usuário:** Desenvolver e integrar funcionalidades no perfil do usuário que permitam a alteração voluntária de senha , a edição de dados cadastrais pós-registro (ex: telefone e endereço) e a exclusão permanente da conta e de seus dados associados.

●**Controle e Autorização**: Garantir que o acesso às funcionalidades da plataforma seja estritamente controlado pelo perfil logado (Empregador ou Colaborador), evitando falhas de autorização.

● **Integridade de Dados**: Estabelecer validações obrigatórias para dados sensíveis, como CPF, formato de e-mail e exigência de senhas fortes, garantindo que as informações só sejam persistidas no banco de dados após verificação.

● **Auditoria e Rastreabilidade**: Desenvolver um sistema de registro de logs para capturar ações críticas operacionais, incluindo a exclusão de contas, exclusão de vagas e as avaliações realizadas.

# 4. Escopo Inicial

**Incluído:**
● **Gestão de Candidaturas:** Implementação de funcionalidade para que o perfil Empregador possa alterar o status das aplicações recebidas para as opções "Em análise", "Aprovado" e "Rejeitado".
● **Gestão de Conta do Usuário:** Desenvolvimento de interfaces e lógicas para permitir ao usuário logado realizar a alteração voluntária de senha , a edição de seus dados cadastrais (como telefone e endereço) pós-registro e a exclusão permanente de sua conta e dados do sistema.
● **Segurança e Validação:** Implementação de travas de controle de acesso rigorosas para garantir que as funcionalidades sejam exclusivas de seus respectivos perfis (Empregador ou Colaborador) , além de validações sistêmicas obrigatórias (CPF, formato de e-mail, exigência de senha forte) antes da gravação no banco de dados.
● **Rastreabilidade (Logs):** Criação de um sistema de registros (logs) no banco de dados para mapear ações críticas, especificamente: exclusão de contas, exclusão de vagas e avaliações realizadas na plataforma.

**Excluído:**
● Alterações estruturais nos módulos de busca de vagas ou na funcionalidade principal de criação de anúncios (o foco será apenas adicionar validações a esses processos).
● Desenvolvimento de sistemas de chat ou mensageria interna nativa (a comunicação continua sendo feita de forma direta entre as partes, sem intermediários, conforme o escopo original).
● Integração com sistemas externos de gestão de recursos humanos, contabilidade ou departamento pessoal (a plataforma Karteria mantém-se delimitada à mediação e intermediação de vagas).


# 5. Premissas

**Infraestrutura e Arquitetura Estáveis:** Assume-se que a arquitetura atual do sistema está funcional, estável e suportará a inclusão das novas tabelas de logs e rotinas de validação sem a necessidade de uma refatoração completa.
**Confiabilidade da Documentação:** A documentação técnica da versão 1.7 reflete fielmente o estado atual do software, servindo como base confiável e atualizada para a equipe técnica de desenvolvimento.
**Acesso ao Código-Fonte e Banco de Dados:** A equipe de desenvolvimento e QA terá acesso irrestrito ao código-fonte atual e aos ambientes de banco de dados para implementar e testar as restrições de acesso e o novo fluxo de status de candidaturas.
**Compatibilidade do Modelo de Dados:** O modelo Entidade-Relacionamento (DER) existente permite a expansão e adição de novos atributos (como os status "Em análise", "Aprovado" e "Rejeitado" para as candidaturas) de forma escalável.
**Adesão dos Usuários:** Presume-se que tanto os Empregadores quanto os Colaboradores adotarão as novas funcionalidades de gestão de perfil e acompanharão as atualizações no ciclo de vida da candidatura através da plataforma


# 6. Restrições

**Tecnológicas:** O desenvolvimento deve ser obrigatoriamente mantido na arquitetura.
**Desempenho:** A implementação das validações de dados e gravação de logs não pode comprometer a restrição de desempenho já estabelecida, onde as
páginas devem carregar em até 3 segundos e as consultas serem respondidas em menos de 1 segundo.
**Disponibilidade:** A implantação das novas funcionalidades de controle de acesso não deve violar a meta de 99,9% de disponibilidade da plataforma.
**Prazo e Orçamento:** O desenvolvimento, testes e implantação desta fase devem ser concluídos em **6 meses** , respeitando o teto orçamentário de
**[Preencher Orçamento]**

# 7. Riscos Iniciais

**Risco de Integridade de Dados:** A funcionalidade de exclusão permanente de conta pode gerar conflitos no banco de dados caso o usuário excluído possua
histórico de avaliações emitidas ou recebidas, podendo quebrar a integridade das métricas de outros usuários.
**Risco Técnico de Legado:** A aplicação de novas validações rigorosas (ex: formato de CPF e e-mail) pode causar travamentos no sistema caso a base de usuários antiga possua dados cadastrados fora desse novo padrão.
**Risco Operacional/Adoção:** A alteração do fluxo de candidatura, que agora exigirá que o Empregador mude manualmente o status para "Em análise",
"Aprovado" ou "Rejeitado" , pode gerar atrito caso os usuários não sejam devidamente instruídos sobre a nova jornada.
**Risco de Segurança:** A implementação do controle de acesso restrito por perfil pode gerar bugs temporários de sessão (ex: um perfil acessar indevidamente o dashboard do outro), exigindo uma rigorosa fase de testes (QA)

# 8. Principais Entregas


● Documento de requisitos.
● Protótipo funcional.
● Sistema em ambiente de testes.
● Sistema implantado em produção.
● Treinamento dos usuários.


# 9. Partes Interessadas (Stakeholders)

● Internos: Diretoria, equipe de TI, equipe de logística/almoxarifado, setor
financeiro.
● Externos: Fornecedores de software e consultoria.