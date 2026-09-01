package com.example.mesaestudo.models;

import java.util.Date;

public class Nota {
    private int id;
    private int idDisciplina;
    private int idUsuario;
    private float valor;
    private Date data;
    private float peso;

    public Nota(int id, int idDisciplina, int idUsuario, float valor, Date data, float peso) {
        this.id = id;
        this.idDisciplina = idDisciplina;
        this.idUsuario = idUsuario;
        this.valor = valor;
        this.data = data;
        this.peso = peso;
    }

    public int getId() { return id; }
    public int getIdDisciplina() { return idDisciplina; }
    public int getIdUsuario() { return idUsuario; }
    public float getValor() { return valor; }
    public Date getData() { return data; }
    public float getPeso() { return peso; }
}