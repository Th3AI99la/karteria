# Karteria

Uma plataforma para conectar prestadores de serviços informais a clientes. O projeto está sendo desenvolvido com Java e Spring Boot.

---

## 💻 Tecnologias Utilizadas

* **Java 21**
* **Spring Boot**
* **Maven**
* **JPA / Hibernate**
* **Thymeleaf** (para renderização de HTML no servidor)
* **H2 Database** (utilizado para o ambiente de desenvolvimento)
* **PostgreSQL** (planejado para o ambiente de produção)

---

## ▶️ Como Executar o Projeto

1.  **Pré-requisitos:**
    * Ter o **Java JDK 21** ou superior instalado.
    * Ter o **Maven** instalado.

2.  **Passos para execução:**
    * Clone este repositório.
    * Abra o projeto em sua IDE de preferência (IntelliJ, VSCode, Eclipse).
    * Aguarde o Maven baixar todas as dependências listadas no `pom.xml`.
    * Execute a classe principal `KarteriaApplication.java`.
    * A aplicação estará disponível em **`http://localhost:8080`**.

---

### Banco de Dados de Desenvolvimento (H2)

Para visualizar e interagir com o banco de dados em memória durante o desenvolvimento:

1.  Acesse o console do H2 em **`http://localhost:8080/h2-console`**.
2.  Insira as seguintes informações para conectar:
    * **JDBC URL:** `jdbc:h2:mem:karteria_db`
    * **User Name:** `sa`
    * **Password:** `password`

---

## 🚧 Status do Projeto

O projeto está em sua fase inicial de desenvolvimento. A estrutura base e a configuração do ambiente foram concluídas.
