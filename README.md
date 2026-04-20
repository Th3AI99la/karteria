# Karteria

Uma plataforma robusta para conectar prestadores de serviços informais a clientes, focada em escalabilidade e segurança. Projeto desenvolvido para a disciplina de Práticas de Laboratório 2.

## 💻 Tecnologias Utilizadas

* Java 21 (LTS)
* Spring Boot 3.x
* Maven
* Docker (Gerenciamento de containers)
* PostgreSQL (Banco de dados relacional oficial)
* JPA / Hibernate (Persistência de dados)
* Thymeleaf (Renderização server-side)

## 🚀 Como Executar o Projeto

### 1. Preparando o Banco de Dados (Docker)

Antes de rodar a aplicação, é necessário subir o container do PostgreSQL. Certifique-se de que o Docker está em execução e rode o comando abaixo no terminal:

```bash
docker run --name karteria-db -e POSTGRES_PASSWORD=kart_2001 -p 5432:5432 -v karteria_data:/var/lib/postgresql -d postgres
```

### 2. Visualização com pgAdmin4

Para gerenciar e visualizar as tabelas do sistema, configure uma nova conexão no pgAdmin4 com as seguintes credenciais:

| Campo                | Valor        |
| :------------------- | :----------- |
| Host name/address    | `localhost`  |
| Port                 | `5432`       |
| Maintenance database | `postgres`   |
| Username             | `postgres`   |
| Password             | `kart_2001`  |

### 3. Executando a Aplicação

Com o banco de dados ativo, navegue até a pasta raiz do projeto e execute o comando:

```bash
./mvnw spring-boot:run
```

A aplicação estará disponível em: `http://localhost:8080`.

## 🛠️ Implementações de Laboratório

Atualmente, o projeto foca nos seguintes pilares técnicos:

* **Arquitetura Cliente-Servidor**: Desacoplamento de lógica e persistência.
* **Persistência Real**: Migração completa do H2 (em memória) para PostgreSQL (persistente via Docker).
* **Segurança**: Implementação planejada de Hashing BCrypt para proteção de dados sensíveis.
* **Real-time**: Planejamento de notificações via WebSockets.
