package com.example.mesaestudo.models;

import java.util.Date;

public class Prova {
    private int id;
    private int idDisciplina;
    private String titulo;
    private Date dataProva;
    private String dificuldade;
    private int diasEstudo;
    private String notas;

    public Prova(int id, int idDisciplina, String titulo, Date dataProva, String dificuldade, int diasEstudo, String notas) {
        this.id = id;
        this.idDisciplina = idDisciplina;
        this.titulo = titulo;
        this.dataProva = dataProva;
        this.dificuldade = dificuldade;
        this.diasEstudo = diasEstudo;
        this.notas = notas;
    }

    public int getId() { return id; }
    public int getIdDisciplina() { return idDisciplina; }
    public String getTitulo() { return titulo; }
    public Date getDataProva() { return dataProva; }
    public String getDificuldade() { return dificuldade; }
    public int getDiasEstudo() { return diasEstudo; }
    public String getNotas() { return notas; }

    @Override
    public String toString() { return titulo; }
}