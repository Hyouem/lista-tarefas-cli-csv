package br.com.portfolio.tarefas;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class TarefaServiceTest {

    @TempDir
    Path diretorio;

    private TarefaService criarService() {
        return new TarefaService(
                new CsvTarefaRepository(diretorio.resolve("tarefas.csv"))
        );
    }

    @Test
    void deveCriarComIdsSequenciais() throws IOException {
        TarefaService service = criarService();

        assertEquals(1, service.criar("Primeira", "").getId());
        assertEquals(2, service.criar("Segunda", "").getId());
    }

    @Test
    void deveConsultarTarefa() throws IOException {
        TarefaService service = criarService();
        service.criar("Java", "JUnit");

        assertEquals("Java", service.consultar(1).getTitulo());
    }

    @Test
    void deveAtualizarTarefa() throws IOException {
        TarefaService service = criarService();
        service.criar("Antiga", "");

        service.atualizar(1, "Nova", "Descricao");

        assertEquals("Nova", service.consultar(1).getTitulo());
    }

    @Test
    void deveConcluirEReabrir() throws IOException {
        TarefaService service = criarService();
        service.criar("Estudar", "");

        service.concluir(1);
        assertEquals(StatusTarefa.CONCLUIDA, service.consultar(1).getStatus());

        service.reabrir(1);
        assertEquals(StatusTarefa.PENDENTE, service.consultar(1).getStatus());
    }

    @Test
    void deveFiltrarPorStatus() throws IOException {
        TarefaService service = criarService();
        service.criar("Primeira", "");
        service.criar("Segunda", "");
        service.concluir(2);

        assertEquals(1, service.listarPorStatus(StatusTarefa.PENDENTE).size());
        assertEquals(1, service.listarPorStatus(StatusTarefa.CONCLUIDA).size());
    }

    @Test
    void deveExcluirTarefa() throws IOException {
        TarefaService service = criarService();
        service.criar("Estudar", "");

        service.excluir(1);

        assertTrue(service.listar().isEmpty());
    }

    @Test
    void devePreservarDadosEntreInstancias() throws IOException {
        criarService().criar("Persistida", "CSV");

        TarefaService novoService = criarService();

        assertEquals("Persistida", novoService.consultar(1).getTitulo());
    }

    @Test
    void deveRejeitarTarefaInexistente() {
        TarefaService service = criarService();

        assertThrows(NoSuchElementException.class,
                () -> service.consultar(999));
    }

    @Test
    void naoDevePersistirAtualizacaoInvalida() throws IOException {
        TarefaService service = criarService();
        service.criar("Original", "");

        assertThrows(IllegalArgumentException.class,
                () -> service.atualizar(1, "", "Alterada"));

        assertEquals("Original", criarService().consultar(1).getTitulo());
    }

    @Test
    void deveRejeitarConclusaoDuplicada() throws IOException {
        TarefaService service = criarService();
        service.criar("Estudar", "");
        service.concluir(1);

        assertThrows(IllegalStateException.class,
                () -> service.concluir(1));
    }

    @Test
    void deveRejeitarExclusaoInexistente() {
        TarefaService service = criarService();

        assertThrows(NoSuchElementException.class,
                () -> service.excluir(999));
    }
}