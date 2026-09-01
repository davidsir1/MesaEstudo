package com.example.mesaestudo.models;

import java.util.Date;

public class Trabalho {
    private int id;
    private int idDisciplina;
    private String titulo;
    private String tipo;
    private Date dataEntrega;
    private String notas;

    public Trabalho(int id, int idDisciplina, String titulo, String tipo, Date dataEntrega, String notas) {
        this.id = id;
        this.idDisciplina = idDisciplina;
        this.titulo = titulo;
        this.tipo = tipo;
        this.dataEntrega = dataEntrega;
        this.notas = notas;
    }

    public int getId() { return id; }
    public int getIdDisciplina() { return idDisciplina; }
    public String getTitulo() { return titulo; }
    public String getTipo() { return tipo; }
    public Date getDataEntrega() { return dataEntrega; }
    public String getNotas() { return notas; }

    @Override
    public String toString() { return titulo; }
}