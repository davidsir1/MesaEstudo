package com.example.mesaestudo.models;

public class Disciplina {
    private int id;
    private String nome;
    private String cor;

    public Disciplina(int id, String nome, String cor) {
        this.id = id;
        this.nome = nome;
        this.cor = cor;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getCor() { return cor; }

    @Override
    public String toString() { return nome; }
}