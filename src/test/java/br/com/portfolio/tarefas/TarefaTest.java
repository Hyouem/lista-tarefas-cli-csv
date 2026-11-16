package br.com.portfolio.tarefas;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TarefaTest {

    @Test
    void deveCriarTarefaPendente() {
        Tarefa tarefa = new Tarefa(1, "Estudar Java", "JUnit");

        assertEquals(1, tarefa.getId());
        assertEquals("Estudar Java", tarefa.getTitulo());
        assertEquals("JUnit", tarefa.getDescricao());
        assertEquals(StatusTarefa.PENDENTE, tarefa.getStatus());
    }

    @Test
    void deveConcluirTarefa() {
        Tarefa tarefa = new Tarefa(1, "Estudar", "");

        tarefa.concluir();

        assertEquals(StatusTarefa.CONCLUIDA, tarefa.getStatus());
    }

    @Test
    void deveReabrirTarefa() {
        Tarefa tarefa = new Tarefa(1, "Estudar", "");

        tarefa.concluir();
        tarefa.reabrir();

        assertEquals(StatusTarefa.PENDENTE, tarefa.getStatus());
    }

    @Test
    void deveRejeitarConclusaoDuplicada() {
        Tarefa tarefa = new Tarefa(1, "Estudar", "");
        tarefa.concluir();

        assertThrows(IllegalStateException.class, tarefa::concluir);
    }

    @Test
    void deveRejeitarReaberturaDePendente() {
        Tarefa tarefa = new Tarefa(1, "Estudar", "");

        assertThrows(IllegalStateException.class, tarefa::reabrir);
    }

    @Test
    void deveRejeitarTituloVazio() {
        assertThrows(IllegalArgumentException.class,
                () -> new Tarefa(1, " ", ""));
    }

    @Test
    void deveRejeitarIdInvalido() {
        assertThrows(IllegalArgumentException.class,
                () -> new Tarefa(0, "Estudar", ""));
    }

    @Test
    void deveAceitarDescricaoNula() {
        Tarefa tarefa = new Tarefa(1, "Estudar", null);

        assertEquals("", tarefa.getDescricao());
    }

    @Test
    void deveAtualizarDados() {
        Tarefa tarefa = new Tarefa(1, "Antigo", "");

        tarefa.atualizar("Novo titulo", "Nova descricao");

        assertEquals("Novo titulo", tarefa.getTitulo());
        assertEquals("Nova descricao", tarefa.getDescricao());
    }

    @Test
    void atualizacaoInvalidaNaoDeveAlterarDados() {
        Tarefa tarefa = new Tarefa(1, "Original", "Descricao");

        assertThrows(IllegalArgumentException.class,
                () -> tarefa.atualizar("", "Alterada"));

        assertEquals("Original", tarefa.getTitulo());
        assertEquals("Descricao", tarefa.getDescricao());
    }

    @Test
    void deveRestaurarTarefaConcluida() {
        Tarefa tarefa = new Tarefa(
                5, "Persistida", "CSV", StatusTarefa.CONCLUIDA
        );

        assertEquals(StatusTarefa.CONCLUIDA, tarefa.getStatus());
    }

    @Test
    void deveRejeitarStatusNulo() {
        assertThrows(NullPointerException.class,
                () -> new Tarefa(1, "Estudar", "", null));
    }
}