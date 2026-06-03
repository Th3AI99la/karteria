CREATE TABLE Usuario ( Id_PK VARCHAR(255) PRIMARY KEY, Nome VARCHAR(255), Email VARCHAR(255), Telefone VARCHAR(255), Senha VARCHAR(255), Id_Empregador VARCHAR(255), Id_Colaborador VARCHAR(255) 

); 

CREATE TABLE Empregador ( Id_Usuario VARCHAR(255) PRIMARY KEY, Perfil VARCHAR(255), FOREIGN KEY (Id_Usuario) REFERENCES Usuario(Id_PK) ); 

CREATE TABLE Colaborador ( Id_Usuario VARCHAR(255) PRIMARY KEY, Perfil VARCHAR(255), FOREIGN KEY (Id_Usuario) REFERENCES Usuario(Id_PK) 

); 

CREATE TABLE Vaga ( Id_vaga_PK VARCHAR(255) PRIMARY KEY, Id_empregador VARCHAR(255), Titulo VARCHAR(255), Descricao VARCHAR(255), Status_visualizacao VARCHAR(255), FOREIGN KEY (Id_empregador) REFERENCES Empregador(Id_Usuario) ); 

CREATE TABLE Candidatura ( Id_candidatura_PK VARCHAR(255) PRIMARY KEY, Id_colaborador_FK VARCHAR(255), Id_vaga_FK VARCHAR(255), data_candidatura VARCHAR(255), FOREIGN KEY (Id_colaborador_FK) REFERENCES Colaborador(Id_Usuario), FOREIGN KEY (Id_vaga_FK) REFERENCES Vaga(Id_vaga_PK) ); 

CREATE TABLE Avaliacao ( Id_avaliacao_PK VARCHAR(255) PRIMARY KEY, Id_colaborador_FK VARCHAR(255), Id_vaga_FK VARCHAR(255), Nota VARCHAR(255), Conclusao VARCHAR(255), FOREIGN KEY (Id_colaborador_FK) REFERENCES Colaborador(Id_Usuario), FOREIGN KEY (Id_vaga_FK) REFERENCES Vaga(Id_vaga_PK) 

); 

CREATE TABLE Notificacao ( Id_notificacao_PK VARCHAR(255) PRIMARY KEY, Id_usuario_FK VARCHAR(255), Id_candidatura_FK VARCHAR(255), Tipo VARCHAR(255), Mensagem VARCHAR(255), Lida VARCHAR(255), Data_envio VARCHAR(255), FOREIGN KEY (Id_usuario_FK) REFERENCES Usuario(Id_PK), FOREIGN KEY (Id_candidatura_FK) REFERENCES Candidatura(Id_candidatura_PK) 

); 

