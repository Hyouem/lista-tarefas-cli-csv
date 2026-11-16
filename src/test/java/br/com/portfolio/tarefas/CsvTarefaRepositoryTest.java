package br.com.portfolio.tarefas;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CsvTarefaRepositoryTest {

    @TempDir
    Path diretorio;

    @Test
    void deveRetornarListaVaziaSemArquivo() throws IOException {
        CsvTarefaRepository repository =
                new CsvTarefaRepository(diretorio.resolve("tarefas.csv"));

        assertTrue(repository.listar().isEmpty());
    }

    @Test
    void devePersistirERecuperarTarefas() throws IOException {
        Path arquivo = diretorio.resolve("tarefas.csv");

        CsvTarefaRepository primeiro = new CsvTarefaRepository(arquivo);
        primeiro.salvar(List.of(
                new Tarefa(1, "Java", "JUnit"),
                new Tarefa(2, "CSV", "Persistencia", StatusTarefa.CONCLUIDA)
        ));

        CsvTarefaRepository segundo = new CsvTarefaRepository(arquivo);
        List<Tarefa> recuperadas = segundo.listar();

        assertEquals(2, recuperadas.size());
        assertEquals("Java", recuperadas.get(0).getTitulo());
        assertEquals(StatusTarefa.CONCLUIDA, recuperadas.get(1).getStatus());
    }

    @Test
    void devePreservarCaracteresEspeciais() throws IOException {
        Path arquivo = diretorio.resolve("tarefas.csv");
        CsvTarefaRepository repository = new CsvTarefaRepository(arquivo);

        String descricao = "Texto, com aspas \"duplas\"\ne nova linha";

        repository.salvar(List.of(
                new Tarefa(1, "Estudar, Java", descricao)
        ));

        Tarefa recuperada = repository.listar().get(0);

        assertEquals("Estudar, Java", recuperada.getTitulo());
        assertEquals(descricao, recuperada.getDescricao());
    }

    @Test
    void deveRejeitarCabecalhoInvalido() throws IOException {
        Path arquivo = diretorio.resolve("tarefas.csv");
        Files.writeString(arquivo, "coluna,incorreta\n", StandardCharsets.UTF_8);

        CsvTarefaRepository repository = new CsvTarefaRepository(arquivo);

        assertThrows(IOException.class, repository::listar);
    }

    @Test
    void deveRejeitarIdsDuplicados() throws IOException {
        Path arquivo = diretorio.resolve("tarefas.csv");

        Files.writeString(arquivo,
                "id,titulo,descricao,status\n"
                        + "1,Primeira,,PENDENTE\n"
                        + "1,Segunda,,CONCLUIDA\n",
                StandardCharsets.UTF_8);

        CsvTarefaRepository repository = new CsvTarefaRepository(arquivo);

        assertThrows(IOException.class, repository::listar);
    }

    @Test
    void deveRejeitarStatusInvalido() throws IOException {
        Path arquivo = diretorio.resolve("tarefas.csv");

        Files.writeString(arquivo,
                "id,titulo,descricao,status\n1,Teste,,DESCONHECIDO\n",
                StandardCharsets.UTF_8);

        CsvTarefaRepository repository = new CsvTarefaRepository(arquivo);

        assertThrows(IOException.class, repository::listar);
    }

    @Test
    void deveSubstituirConteudoAnterior() throws IOException {
        Path arquivo = diretorio.resolve("tarefas.csv");
        CsvTarefaRepository repository = new CsvTarefaRepository(arquivo);

        repository.salvar(List.of(new Tarefa(1, "Antiga", "")));
        repository.salvar(List.of(new Tarefa(2, "Nova", "")));

        List<Tarefa> tarefas = repository.listar();

        assertEquals(1, tarefas.size());
        assertEquals(2, tarefas.get(0).getId());
    }

    @Test
    void deveCriarDiretoriosNecessarios() throws IOException {
        Path arquivo = diretorio.resolve("dados").resolve("tarefas.csv");
        CsvTarefaRepository repository = new CsvTarefaRepository(arquivo);

        repository.salvar(List.of(new Tarefa(1, "Teste", "")));

        assertTrue(Files.exists(arquivo));
    }
}