
## **KARTERIA** 

Plataforma de Conexão de Serviços Informais 

## **PLANO DE IMPLANTAÇÃO E DOCUMENTAÇÃO TÉCNICA COMPLETA** 

Disciplina: Práticas de Laboratório 2  |  Data: 20 de maio de 2026 

Karteria – Plataforma de Conexão de Serviços Informais  |  Práticas de Laboratório 2 

**KARTERIA  |  Plano de Implantação e Documentação Técnica** 

20/05/2026 

## **1. Plano de Implantação** 

## **1.1 Introdução** 

## **Nome do Sistema** 

Karteria 

## **Objetivo do Sistema** 

O Karteria é uma plataforma web desenvolvida para conectar prestadores de serviços informais (colaboradores) a clientes (empregadores), facilitando a busca, contratação e avaliação de profissionais de serviços como limpeza, jardinagem, marcenaria, elétrica, entre outros. O sistema oferece uma interface segura e intuitiva para publicação de anúncios de vagas, candidatura a oportunidades, sistema de avaliação mútua e notificações em tempo real via WebSocket. 

## **Descrição da Organização Cliente** 

O projeto foi desenvolvido no âmbito acadêmico da disciplina Práticas de Laboratório 2. A organização fictícia cliente é uma startup de tecnologia denominada Karteria Soluções Digitais Ltda., sediada em Goiânia – GO, cujo objetivo é digitalizar e formalizar o mercado de trabalho informal, proporcionando segurança tanto para quem contrata quanto para quem presta o serviço. 

Segmento: Marketplace de serviços. 

Público-alvo: Autônomos, prestadores informais e pequenas empresas que contratam serviços eventuais. 

## **1.2 Estratégia de Implantação** 

A estratégia escolhida para o Karteria é a Implantação Piloto. 

## **Justificativa** 

A implantação piloto foi selecionada pelos seguintes motivos: 

- Permite validar o sistema em um ambiente controlado antes do lançamento total, reduzindo riscos. 

- O sistema possui dois perfis de usuário (Empregador e Colaborador), e um piloto com grupo reduzido permitirá identificar falhas de usabilidade específicas para cada perfil. 

- Possibilita ajustes de banco de dados, regras de segurança e fluxos de notificação sem impactar todos os usuários. 

- A equipe de desenvolvimento consegue coletar feedback real sem comprometer a reputação do produto. 

Fase piloto sugerida: 35 dias com 20 empregadores e 50 colaboradores voluntários, monitorando logs, candidaturas, avaliações e notificações. 


## **1.3 Cronograma de Implantação** 

| Atividade | Responsável | Data de Início | Tempo Estimado |
| :--- | :--- | :--- | :--- |
| Análise e revisão do código | Dev Backend | 20/05/2026 | 4 dias |
| Preparação do ambiente de produção (Docker/VPS) | DevOps | 22/05/2026 | 2 dias |
| Configuração do banco PostgreSQL | DBA | 24/05/2026 | 1 dia |
| Testes funcionais integrados | QA / Equipe | 25/05/2026 | 2 dias |
| Início da fase piloto | Toda a equipe | 27/05/2026 | 4 dias |
| Coleta de feedback e correções | Dev Full-stack | 31/05/2026 | 3 dias |
| Lançamento oficial (Go-Live) | Toda a equipe | 03/06/2026 | – |


## **1.4 Recursos Necessários** 

## **Hardware** 

- Servidor VPS com mínimo de 2 vCPUs e 4 GB de RAM (ex.: Oracle Cloud Always Free ou AWS EC2 t3.medium) 

- Armazenamento mínimo de 30 GB SSD para banco de dados e arquivos de log 

- Computadores dos desenvolvedores: Windows/Linux/macOS com 8 GB RAM e Java 21 instalado 

## **Software** 

- Java 21 LTS (OpenJDK ou Amazon Corretto) 

- Spring Boot 3.5.7 

- Maven 3.9+ 

- Docker Engine + Docker Compose 

- PostgreSQL 15+ (container Docker) 

- pgAdmin 4 (gerenciamento do banco) 

- Git + GitHub (controle de versão) 

- Navegador moderno (Chrome, Firefox, Edge) para acesso ao sistema 

## **Banco de Dados** 

- PostgreSQL rodando em container Docker 

- Porta padrão: 5432 

- Volume persistente: karteria_data 

## **Servidor** 

- Servidor de aplicação embutido: Tomcat (via Spring Boot) 

- Porta da aplicação: 8080 

- Endereço de escuta: 0.0.0.0 (todas as interfaces) 

## **Internet e Acesso** 

- Conexão mínima de 10 Mbps para o servidor 

- Domínio ou IP público para acesso externo 

- Serviço SMTP configurado (Gmail com STARTTLS na porta 587) para envio de e- mails de recuperação de senha 

## **Permissões de Acesso** 

- Acesso SSH ao servidor para deploy e manutenção 

- Usuário de banco de dados com permissões de leitura e escrita no schema public 

- Variáveis de ambiente configuradas: MAIL_USERNAME e MAIL_PASSWORD 

- Chave JWT configurada no application.properties (256 bits hexadecimal) 


## **2. Configuração do Ambiente** 

## **2.1 Instalação do Sistema** 

Pré-requisitos: Java 21, Maven, Docker e Git instalados na máquina ou servidor. 

## **Passo 1 – Clonar o repositório** 

```
git clone https://github.com/Th3AI99la/karteria.git
```

```
cd karteria
```

## **Passo 2 – Subir o banco de dados PostgreSQL via Docker** 

```
docker run --name karteria-db \
```

- `-e POSTGRES_PASSWORD=kart_2001 \` 

- `-p 5432:5432 \` 

- `-v karteria_data:/var/lib/postgresql/data \` 

- `-d postgres` 

## **Passo 3 – Configurar variáveis de ambiente** 

Criar um arquivo .env na raiz do projeto (ou exportar no terminal): 

```
MAIL_USERNAME=seuemail@gmail.com
```

```
MAIL_PASSWORD=sua_senha_de_app_gmail
```

## **Passo 4 – Executar a aplicação** 

```
./mvnw spring-boot:run
```

A aplicação estará disponível em: http://localhost:8080 

## **2.2 Configuração do Banco de Dados** 

O Hibernate está configurado com spring.jpa.hibernate.ddl-auto=update, o que significa que as tabelas são criadas ou atualizadas automaticamente na primeira execução com base nas entidades JPA mapeadas. As tabelas geradas são: 

- usuarios – dados de cadastro, tipo de perfil, tokens de reset 

- anuncios – vagas publicadas pelos empregadores 

- candidaturas – candidaturas de colaboradores às vagas 

- avaliacoes – avaliações pós-serviço entre empregador e colaborador 

- notificacoes – notificações do sistema 

- password_reset_tokens – tokens temporários de recuperação de senha 

## **2.3 Configuração do Servidor** 

O servidor embutido Tomcat é iniciado automaticamente pelo Spring Boot. As configurações em application.properties são: 

|**Propriedade**|Valor|
|---|---|
|||
|**server.port**|8080|
|||
|**server.address**|0.0.0.0|
|||
|**spring.datasource.url**|jdbc:postgresql://localhost:5432/postgres|


**spring.datasource.driver** org.postgresql.Driver **spring.jpa.ddl-auto** update **jwt.expiration** 86400000 ms (1 dia) **smtp.host** smtp.gmail.com : 587 (STARTTLS) 

## **2.4 Criação de Usuários e Perfis** 

O sistema possui dois tipos de usuário definidos pelo enum TipoUsuario: 

- EMPREGADOR – pode publicar anúncios, visualizar candidatos, aceitar candidaturas e avaliar colaboradores. 

- COLABORADOR – pode se candidatar a vagas, gerenciar seu perfil e receber avaliações. 

O cadastro é realizado pela tela /register, onde o usuário escolhe seu perfil. Após o cadastro básico, é redirecionado para /completar-cadastro para preencher dados complementares (CPF, telefone, endereço). Permissões são gerenciadas pelo Spring Security com base no tipo de usuário. 

## **2.5 Configuração de Permissões, Portas e APIs** 

A segurança é implementada com dupla camada: 

- API REST (/api/**): autenticada via JWT (Bearer Token). O filtro JwtAuthenticationFilter intercepta as requisições e valida o token antes do processamento. 

- Interface Web (/**: autenticada via sessão HTTP. O Spring Security gerencia login/logout com formulário. 

Rotas públicas liberadas: /, /login, /register, /esqueci-senha, /resetar-senha, /completarcadastro, /css/**, /js/**, /images/**, /ws/**. 

WebSocket configurado no endpoint /ws para notificações em tempo real (STOMP sobre SockJS). 

## **3. Script de Banco de Dados** 

## **3.1 Script de Criação das Tabelas** 

O Hibernate cria as tabelas automaticamente. Abaixo o script SQL equivalente para criação manual ou restauração: 

```
-- Tabela de usuários


CREATE TABLE IF NOT EXISTS usuarios (

    id                BIGSERIAL PRIMARY KEY,
    nome              VARCHAR(255),
    sobrenome         VARCHAR(255),
    email             VARCHAR(255) UNIQUE NOT NULL,
    senha             VARCHAR(255),
    telefone          VARCHAR(20),
    telefone2         VARCHAR(20),
    cpf               VARCHAR(14),
    endereco          VARCHAR(500),
    tipo              VARCHAR(20),
    cadastro_completo BOOLEAN DEFAULT FALSE,
    data_cadastro     TIMESTAMP,
    codigo_validacao  VARCHAR(255) UNIQUE,
    reset_token       VARCHAR(255),
    reset_token_expiry TIMESTAMP);
```

```
-- Tabela de anúncios
CREATE TABLE IF NOT EXISTS anuncios (
    id               BIGSERIAL PRIMARY KEY,
    titulo           VARCHAR(255),
    descricao        VARCHAR(2000),
    localizacao      VARCHAR(255),
    data_postagem    TIMESTAMP,
    valor_min        DOUBLE PRECISION,
    valor_max        DOUBLE PRECISION,
    tipo_pagamento   VARCHAR(50),
    status           VARCHAR(30),
    visualizacoes    INTEGER DEFAULT 0,
    exibir_telefone  BOOLEAN DEFAULT FALSE,
    permitir_contato BOOLEAN DEFAULT TRUE,
    anunciante_id    BIGINT REFERENCES usuarios(id)
);
```

```
-- Tabela de candidaturas
CREATE TABLE IF NOT EXISTS candidaturas (
    id               BIGSERIAL PRIMARY KEY,
    colaborador_id   BIGINT REFERENCES usuarios(id),
    anuncio_id       BIGINT REFERENCES anuncios(id),
    data_candidatura TIMESTAMP
);
```
```
-- Tabela de avaliações
CREATE TABLE IF NOT EXISTS avaliacoes (
    id              BIGSERIAL PRIMARY KEY,
    avaliador_id    BIGINT REFERENCES usuarios(id),
    avaliado_id     BIGINT REFERENCES usuarios(id),
    anuncio_id      BIGINT REFERENCES anuncios(id),
    nota            INTEGER,
    comentario      TEXT,
    data_avaliacao  TIMESTAMP);
```

## **3.2 Inserts Iniciais (Dados de Teste)** 

```
-- Usuário administrador / empregador de teste
INSERT INTO usuarios (nome, sobrenome, email, senha, tipo, cadastro_completo,
data_cadastro)
VALUES ('Admin', 'Karteria', 'admin@karteria.com',
        '$2a$10$HASH_BCRYPT_DA_SENHA', 'EMPREGADOR', TRUE, NOW());
-- Colaborador de teste
INSERT INTO usuarios (nome, sobrenome, email, senha, tipo, cadastro_completo,
data_cadastro)
VALUES ('João', 'Silva', 'joao@email.com',
        '$2a$10$HASH_BCRYPT_DA_SENHA', 'COLABORADOR', TRUE, NOW());
```

## **3.3 Permissões de Usuário no Banco** 

```
-- Criar usuário da aplicação (recomendado em produção)
CREATE USER karteria_app WITH PASSWORD 'senha_segura_producao';
GRANT CONNECT ON DATABASE postgres TO karteria_app;
GRANT USAGE ON SCHEMA public TO karteria_app;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO
karteria_app;
```

```
GRANT USAGE ON ALL SEQUENCES IN SCHEMA public TO karteria_app;
```

## **3.4 Backup do Banco** 

Comando para realizar backup completo: 

```
docker exec karteria-db pg_dump -U postgres postgres > backup_karteria_$(date
+%Y%m%d).sql
```

## Restauração: 

```
docker exec -i karteria-db psql -U postgres postgres < backup_karteria_YYYYMMDD.sql
```

## **4. Manual do Usuário** 

## **4.1 Acesso ao Sistema** 

O Karteria é acessado pelo navegador web no endereço do servidor (ex.: http://localhost:8080 em ambiente local). Não é necessário instalar nenhum software adicional no computador do usuário. 

## **4.2 Tela de Login** 

Na página inicial (/) o usuário verá a tela de boas-vindas com botões para Entrar e Cadastrar-se. Clicando em Entrar, é redirecionado para /login. 

Campos obrigatórios na tela de login: 

- E-mail – o mesmo cadastrado no sistema 

- Senha – mínimo de 8 caracteres 

Após login bem-sucedido, o sistema redireciona automaticamente para o dashboard de escolha ao perfil do usuário (Empregador ou Colaborador). 

## **4.3 Cadastro de Novo Usuário** 

Acesse /register e preencha: 

- Nome e Sobrenome 

- E-mail (único no sistema) 

- Senha (mínimo 8 caracteres) 

Após o cadastro inicial, o sistema solicitará o preenchimento de dados complementares em /completar-cadastro: 

- CPF 

- Telefone principal e secundário 

- Endereço completo 

## **4.4 Funcionalidades – Perfil Empregador** 

## **Publicar Anúncio de Vaga** 

- No dashboard do empregador, clique em Publicar Vaga. 

- Preencha: título, descrição, localização, faixa de valor (mínimo e máximo), tipo de pagamento. 

- Configure visibilidade do telefone e permissão de contato. 

- Clique em Publicar para salvar o anúncio. 

## **Gerenciar Candidaturas** 

- Acesse o anúncio desejado para ver todos os candidatos que se inscreveram. 

- Visualize o perfil de cada candidato, incluindo média de avaliações. 

- Aceite ou ignore candidaturas conforme necessário. 

## **Avaliar Colaborador** 

- Após o serviço concluído, acesse o anúncio e clique em Avaliar. 

- Atribua uma nota de 1 a 5 estrelas e insira um comentário opcional. 


**KARTERIA  |  Plano de Implantação e Documentação Técnica** 20/05/2026 

## **4.5 Funcionalidades – Perfil Colaborador** 

## **Buscar e Candidatar-se a Vagas** 

- No dashboard do colaborador, visualize os anúncios disponíveis. 

- Clique em Ver Detalhes para visualizar a descrição completa da vaga. 

- Clique em Candidatar-se para enviar sua candidatura. 

## **Gerenciar Perfil** 

- Acesse /perfil para atualizar seus dados pessoais e de contato. 

- Visualize sua média de avaliações e comentários recebidos. 

## **4.6 Notificações** 

O sistema envia notificações em tempo real via WebSocket. O ícone de sino no menu exibe as notificações pendentes. Clique para marcar como lida ou visualizar detalhes. 

## **4.7 Recuperação de Senha** 

- Acesse /esqueci-senha e informe seu e-mail cadastrado. 

- Você receberá um e-mail com um link de redefinição de senha. 

- Clique no link do e-mail e insira a nova senha em /resetar-senha. 

- O link de recuperação expira após um período definido pelo sistema. 


## **5. Manual Técnico** 

## **5.1 Arquitetura do Sistema** 

O Karteria segue a arquitetura MVC (Model-View-Controller) com separação de responsabilidades em camadas: 

- Model – Entidades JPA (Usuario, Anuncio, Candidatura, Avaliacao, Notificacao, PasswordResetToken) 

- View – Templates Thymeleaf com HTML/CSS/JS renderizados server-side 

- Controller – Controladores Spring MVC para rotas web e REST Controllers para endpoints de API 

- Service – Camada de serviços com regras de negócio (UsuarioService, JwtService, ActiveProfileSecurityService) 

- Repository – Interfaces Spring Data JPA para acesso ao banco de dados 

- Config – Configurações de segurança (SecurityConfig, WebSocketConfig, WebConfig, AppConfig) 

## **5.2 Tecnologias Utilizadas** 

|**Tecnologia**|Descrição|
|---|---|
|||
|**Java 21 LTS**|Linguagem de programação principal|
|||
|**Spring Boot 3.5.7**|Framework principal da aplicação web|
|||
|**Spring Security**|Autenticação, autorização e segurança|
|||
|**Spring Data JPA**|Persistência e mapeamento objeto-relacional|
|||
|**Hibernate**|Implementação JPA, DDL automático|
|||
|**PostgreSQL 15+**|Banco de dados relacional|
|||
|**Docker**|Containerização do banco de dados|
|||
|**Thymeleaf**|Template engine server-side|
|||
|**JWT (jjwt 0.11.5)**|Autenticação stateless para a API REST|
|||
|**Spring WebSocket**|Notificações em tempo real (STOMP/SockJS)|
|||
|**Spring Mail**|Envio de e-mails via SMTP|
|||
|**BCrypt**|Hash de senhas|
|||
|**Maven**|Gerenciador de dependências e build|
|||
|**Lombok**|Redução de boilerplate no código|



## **5.3 Estrutura do Banco de Dados** 

Tabelas e relacionamentos principais: 

- usuarios (1) → (N) anuncios – Um empregador pode ter múltiplos anúncios 

- usuarios (1) → (N) candidaturas – Um colaborador pode se candidatar a múltiplas vagas 

- anuncios (1) → (N) candidaturas – Um anúncio pode ter múltiplos candidatos 

- avaliacoes (N) → (1) avaliador [usuario] e (N) → (1) avaliado [usuario] 

- anuncios (1) → (1) avaliacoes – Cada anúncio pode ter uma avaliação associada 

## **5.4 APIs REST Disponíveis** 

- POST /api/auth/login – Autenticação com retorno de JWT (AuthRequestDTO → AuthResponseDTO) 

- GET /api/anuncios – Listagem de anúncios (autenticado via JWT) 

- WebSocket /ws – Endpoint para conexão STOMP (notificações em tempo real) 

## **6. Plano de Backup e Recuperação** 

## **6.1 Política de Backup** 

|||
|---|---|
|**Critério**|Definição|
|||
|**Periodicidade**|Diário (às 02h00 AM, horário de Brasília)|
|||
|**Tipo de Backup**|Full (completo) – dump SQL do banco PostgreSQL|
|||
|**Retenção**|7 backups diários + 4 semanais + 12 mensais|
|||
|**Local de Armazenamento**|Servidor local + bucket S3 (AWS) ou Google Drive (para ambiente<br>acadêmico)|
|||
|**Formato do Arquivo**|backup_karteria_YYYYMMDD.sql (texto SQL)|
|||
|**Responsável**|DevOps / Administrador do sistema|
|||
|**Compressão**|gzip (backup_karteria_YYYYMMDD.sql.gz)|



## **6.2 Procedimento de Backup** 

Executar manualmente ou via cron job: 

```
# Script de backup
docker exec karteria-db pg_dump -U postgres postgres | gzip >
/backups/backup_karteria_$(date +%Y%m%d_%H%M).sql.gz
# Verificar integridade
gunzip -t /backups/backup_karteria_*.sql.gz && echo 'Backup OK'
```

## **6.3 Procedimento de Restauração** 

Em caso de falha, restaurar o backup mais recente: 

```
# Descompactar o backup
gunzip /backups/backup_karteria_YYYYMMDD_HHmm.sql.gz
# Restaurar no PostgreSQL
docker exec -i karteria-db psql -U postgres postgres <
/backups/backup_karteria_YYYYMMDD_HHmm.sql
# Reiniciar a aplicação
./mvnw spring-boot:run
```

## **6.4 Simulação de Restauração (Evidência)** 

Para simular uma restauração em ambiente de teste: 

- Criar banco de dados de teste: CREATE DATABASE karteria_restore_test; 

- Restaurar o backup no banco de teste: psql -U postgres karteria_restore_test < backup.sql 

- Validar tabelas: SELECT COUNT(*) FROM usuarios; SELECT COUNT(*) FROM anuncios; 

- Configurar temporariamente spring.datasource.url para o banco de teste e iniciar a aplicação para validação. 


## **7. Plano de Segurança** 

## **7.1 Autenticação** 

O Karteria implementa dupla camada de autenticação: 

- Interface Web: autenticação por formulário com sessão gerenciada pelo Spring Security. O login é realizado via /login com e-mail e senha. 

- API REST: autenticação Stateless com JWT (JSON Web Token). O token é gerado pelo JwtService e tem validade de 24 horas (86400000 ms). O JwtAuthenticationFilter intercepta todas as requisições /api/** e valida o token no cabeçalho Authorization: Bearer <token>. 

## **7.2 Autorização** 

As permissões são baseadas no tipo de usuário (EMPREGADOR ou COLABORADOR): 

- Rotas /empregador/** – acessíveis apenas por EMPREGADOR 

- Rotas /colaborador/** – acessíveis apenas por COLABORADOR 

- Rotas de perfil e configurações – acessíveis por ambos os perfis autenticados 

- O ActiveProfileSecurityService verifica se o perfil ativo do usuário corresponde ao tipo exigido pela rota, prevenindo escalada horizontal de privilégios. 

## **7.3 Criptografia** 

- Senhas: armazenadas com hash BCrypt (salt aleatório por senha, fator de custo padrão Spring = 10 rounds). A senha nunca é armazenada em texto plano. 

- JWT: assinado com chave HMAC-SHA256 de 256 bits (configurada em jwt.secret no application.properties). Tokens expiram em 24 horas. 

- Comunicação SMTP: conexão com servidor de e-mail utiliza STARTTLS na porta 587, garantindo criptografia na transmissão de e-mails. 

## **7.4 Controle de Acesso** 

- CSRF desabilitado para a API REST (stateless por design). Na interface web, Spring Security gerencia proteção de sessão. 

- Códigos de validação únicos (codigoValidacao) gerados no cadastro de usuário para evitar duplicidade e facilitar auditoria. 

- Tokens de reset de senha (PasswordResetToken) têm validade temporal e são invalidados após uso. 

## **7.5 Proteção Contra Falhas** 

- Bean Validation (spring-boot-starter-validation): validação de dados de entrada nas DTOs e entidades, rejeitando dados malformados antes do processamento. 

- GlobalControllerAdvice: tratamento centralizado de exceções, evitando exposição de stack traces ao usuário. 

- Spring Boot Actuator (/actuator/health): monitoramento de saúde da aplicação. 

## **7.6 Política de Senhas** 

- Mínimo de 8 caracteres (definido nas validações do frontend) 

- Senhas armazenadas exclusivamente com BCrypt 

- Recuperação de senha via link temporário enviado ao e-mail cadastrado 

- Tokens de recuperação invalidados após expiração ou uso 

## **7.7 Logs de Auditoria** 

- spring.jpa.show-sql=true: todas as queries SQL são registradas no log da aplicação em desenvolvimento. 

- spring.jpa.properties.hibernate.format_sql=true: queries formatadas para legibilidade. 

- • Em produção, recomenda-se configurar Logback ou Log4j2 para persistir logs em arquivo com rotação diária. 

- O Spring Boot Actuator pode ser integrado com ferramentas como Prometheus + Grafana para monitoramento em tempo real. 

## **8. Treinamento dos Usuários** 

## **8.1 Plano de Treinamento** 

O Karteria atua como um Marketplace Matchmaker de serviços informais - uma vitrine digital de acesso direto, no mesmo espírito de plataformas como 99freelas, eFreela, Workana e GetNinjas, porém voltado ao universo de trabalhos pontuais e informais. Isso significa que não há processo de implantação em empresas clientes, instalação em máquinas de usuários ou configuração de ambientes corporativos. 

O usuário simplesmente acessa a URL da plataforma pelo navegador, realiza seu próprio cadastro de forma autônoma, escolhe seu perfil (Empregador ou Colaborador) e já começa a utilizar todos os recursos disponíveis. Não é necessário suporte técnico presencial para que o usuário comum inicie o uso do sistema. 

Portanto, o treinamento aqui descrito não se trata de capacitação para implantação, mas sim de uma apresentação demonstrativa das funcionalidades da plataforma, voltada para divulgação e familiarização dos primeiros usuários com a interface e os fluxos do sistema deconexão. 

## **9. Implantação em Ambiente Real ou Simulado** 

## **9.1 Ambiente de Implantação Escolhido** 

O ambiente selecionado para implantação é Docker local + possibilidade de deploy em nuvem via Railway ou Render. A opção Docker permite reproduzir o ambiente de produção em qualquer máquina, garantindo consistência entre os ambientes de desenvolvimento e produção. 

## **9.2 Implantação com Docker (Ambiente Local/Simulado)** 

**Opção 1 – Execução manual (conforme README)** 

## 1. Subir o banco de dados: 

```
docker run --name karteria-db -e POSTGRES_PASSWORD=kart_2001 -p 5432:5432 -v
karteria_data:/var/lib/postgresql/data -d postgres
```

## 2. Executar a aplicação: 

```
./mvnw spring-boot:run
```

## 3. Acessar em: http://localhost:8080 

**Opção 2 – Docker Compose (recomendado para produção)** 

Criar arquivo docker-compose.yml na raiz do projeto: 

```
version: '3.8'
services:
  db:
    image: postgres:15
    environment:
      POSTGRES_PASSWORD: kart_2001
    volumes:
      - karteria_data:/var/lib/postgresql/data
    ports:
      - '5432:5432'
  app:
    build: .
    ports:
      - '8080:8080'
    depends_on:
      - db
    environment:
      MAIL_USERNAME: ${MAIL_USERNAME}
      MAIL_PASSWORD: ${MAIL_PASSWORD}
volumes:
  karteria_data:
Executar: docker-compose up -d
```
## **10. Termo de Entrega e Encerramento** 

## **10.1 Identificação do Projeto** 

|||
|---|---|
|**Campo**|Informação|
|||
|**Nome do Projeto**|Karteria – Plataforma de Conexão de Serviços Informais|
|||
|**Repositório**|github.com/Th3AI99la/karteria|
|||
|**Disciplina**|Práticas de Laboratório 2|
|||
|**Data de Entrega**|20 de maio de 2026|
|||
|**Versão**|0.0.1-SNAPSHOT|
|||
|**Ambiente de Produção**|Docker local|



## **10.2 Nome da Equipe e Integrantes** 


- Ítalo Gabriel Stfaisk
-  Gustavo Abreu da Silva
-  Nathan Lopes D Souza
-   Thalles Henrique Alves de Souza. 

## **10.3 Funcionalidades Entregues** 

- Cadastro de usuários com dois perfis: Empregador e Colaborador 

- Login com autenticação via sessão (web) e JWT (API REST) 

- Completar cadastro com dados complementares (CPF, telefone, endereço) 

- Publicação e gerenciamento de anúncios de serviços pelo Empregador 

- Candidatura a vagas pelo Colaborador 

- Visualização e gerenciamento de candidatos pelo Empregador 

- Sistema de avaliação com nota (1-5 estrelas) e comentário 

- Notificações em tempo real via WebSocket (STOMP/SockJS) 

- Recuperação de senha via e-mail (link temporário) 

- API REST para autenticação e listagem de anúncios 

- Segurança com BCrypt, JWT e Spring Security 

- Dashboard personalizado por perfil de usuário 

## **10.4 Limitações Conhecidas** 

- O sistema não possui sistema de pagamentos integrado – as negociações financeiras ocorrem fora da plataforma. 

- Não há upload de fotos/portfólio para o perfil do colaborador na versão atual. 

- A plataforma não se responsabiliza por problemas interpessoais entre empregador e colaborador, se limitando apenas em matchmaker. 


## **10.5 Pendências** 

- Testes automatizados (JUnit / Mockito) para cobertura de serviços críticos 

- Configuração de HTTPS com certificado SSL para produção 

- Implementação de rate limiting na API REST 


