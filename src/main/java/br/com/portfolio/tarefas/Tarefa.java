package br.com.portfolio.tarefas;

import java.util.Objects;

public class Tarefa {

    private final long id;
    private String titulo;
    private String descricao;
    private StatusTarefa status;

    public Tarefa(long id, String titulo, String descricao) {
        this(id, titulo, descricao, StatusTarefa.PENDENTE);
    }

    public Tarefa(long id, String titulo, String descricao,
                  StatusTarefa status) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID invalido.");
        }

        this.id = id;
        this.status = Objects.requireNonNull(
                status, "Status obrigatorio."
        );

        atualizar(titulo, descricao);
    }

    public void atualizar(String titulo, String descricao) {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("Titulo obrigatorio.");
        }

        this.titulo = titulo.trim();
        this.descricao = descricao == null ? "" : descricao.trim();
    }

    public void concluir() {
        if (status == StatusTarefa.CONCLUIDA) {
            throw new IllegalStateException("Tarefa ja concluida.");
        }

        status = StatusTarefa.CONCLUIDA;
    }

    public void reabrir() {
        if (status == StatusTarefa.PENDENTE) {
            throw new IllegalStateException("Tarefa ja pendente.");
        }

        status = StatusTarefa.PENDENTE;
    }

    public long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public StatusTarefa getStatus() {
        return status;
    }
}