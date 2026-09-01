CREATE DATABASE mesa_estudo_db;

USE mesa_estudo_db;

-- Criação da tabela Usuário
CREATE TABLE tb_usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nome_usuario VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    senha VARCHAR(255) NOT NULL
);

-- Criação da tabela Sessão
CREATE TABLE tb_sessao (
    id_sessao INT AUTO_INCREMENT PRIMARY KEY,
    id_disciplina INT NOT NULL,
    id_usuario INT NOT NULL,
    tempo TIME,
    data_sessao DATE,
    -- tipo_sessao ENUM('Estudo', 'Revisão', 'Outro'), -- Defina os valores do ENUM conforme sua necessidade
    FOREIGN KEY (id_disciplina) REFERENCES tb_disciplina(id_disciplina) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (id_usuario) REFERENCES tb_usuario(id_usuario) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Tabela Disciplina (Adicionado id_usuario)
CREATE TABLE tb_disciplina (
    id_disciplina INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    nome_disciplina VARCHAR(255) NOT NULL,
    cor VARCHAR(50),
    FOREIGN KEY (id_usuario) REFERENCES tb_usuario(id_usuario) ON DELETE CASCADE
);

-- Tabela Trabalho (Adicionado id_usuario)
CREATE TABLE tb_trabalho (
    id_trabalho INT AUTO_INCREMENT PRIMARY KEY,
    id_disciplina INT NOT NULL,
    id_usuario INT NOT NULL,
    titulo_trabalho VARCHAR(255) NOT NULL,
    tipo ENUM('Redação', 'Lista de Exercícios', 'Laboratório', 'Leitura', 'Prova', 'Projeto', 'Teste', 'Outro'),
    data_de_entrega DATE,
    notas TEXT,
    FOREIGN KEY (id_disciplina) REFERENCES tb_disciplina(id_disciplina) ON DELETE CASCADE,
    FOREIGN KEY (id_usuario) REFERENCES tb_usuario(id_usuario) ON DELETE CASCADE
);

-- Tabela Prova (Adicionado id_usuario, dificuldade e dias_estudo)
CREATE TABLE tb_prova (
    id_prova INT AUTO_INCREMENT PRIMARY KEY,
    id_disciplina INT NOT NULL,
    id_usuario INT NOT NULL,
    titulo_prova VARCHAR(255) NOT NULL,
    data_prova DATE,
    dificuldade ENUM('Fácil', 'Médio', 'Difícil', 'Extremo'),
    dias_estudo INT,
    comentarios TEXT,
    nota_obtida FLOAT DEFAULT 0,
    FOREIGN KEY (id_disciplina) REFERENCES tb_disciplina(id_disciplina) ON DELETE CASCADE,
    FOREIGN KEY (id_usuario) REFERENCES tb_usuario(id_usuario) ON DELETE CASCADE
);

-- Criação da tabela Nota
CREATE TABLE tb_nota (
    id_nota INT AUTO_INCREMENT PRIMARY KEY,
    id_disciplina INT NOT NULL,
    id_usuario INT NOT NULL,
    nota FLOAT NOT NULL,
    data_da_nota DATE,
    peso FLOAT,
    FOREIGN KEY (id_disciplina) REFERENCES tb_disciplina(id_disciplina) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (id_usuario) REFERENCES tb_usuario(id_usuario) ON DELETE CASCADE ON UPDATE CASCADE
);

SELECT * FROM tb_disciplina;