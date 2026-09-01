package com.example.mesaestudo.models;

import java.util.Date;

public class Sessao {
    private int id;
    private int idDisciplina;
    private int idUsuario;
    private String tempo;
    private Date dataSessao;
    private String nomeDisciplina;

    public Sessao(int id, int idDisciplina, int idUsuario, String tempo, Date dataSessao, String nomeDisciplina) {
        this.id = id;
        this.idDisciplina = idDisciplina;
        this.idUsuario = idUsuario;
        this.tempo = tempo;
        this.dataSessao = dataSessao;
        this.nomeDisciplina = nomeDisciplina;
    }

    public int getId() { return id; }
    public String getTempo() { return tempo; }
    public Date getDataSessao() { return dataSessao; }
    public String getNomeDisciplina() { return nomeDisciplina; }
}