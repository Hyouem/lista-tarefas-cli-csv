package br.com.portfolio.tarefas;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class AppTest {

    @TempDir
    Path diretorio;

    private String executar(String entrada, Path arquivo) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream original = System.out;

        try (PrintStream saida = new PrintStream(
                buffer, true, StandardCharsets.UTF_8);
             Scanner scanner = new Scanner(entrada)) {

            System.setOut(saida);

            TarefaService service = new TarefaService(
                    new CsvTarefaRepository(arquivo)
            );

            new App(service, scanner).executar();
        } finally {
            System.setOut(original);
        }

        return buffer.toString(StandardCharsets.UTF_8);
    }

    @Test
    void deveExecutarFluxoCompleto() {
        Path arquivo = diretorio.resolve("tarefas.csv");

        String entrada = String.join("\n",
                "1", "Estudar Java", "JUnit",
                "2",
                "3", "1",
                "4", "1", "Estudar CSV", "Persistencia",
                "5", "1",
                "9",
                "6", "1",
                "8",
                "7", "1",
                "2",
                "0"
        ) + "\n";

        String saida = executar(entrada, arquivo);

        assertTrue(saida.contains("Tarefa criada. ID: 1"));
        assertTrue(saida.contains("Tarefa atualizada."));
        assertTrue(saida.contains("Tarefa concluida."));
        assertTrue(saida.contains("[CONCLUIDA] Estudar CSV"));
        assertTrue(saida.contains("Tarefa reaberta."));
        assertTrue(saida.contains("[PENDENTE] Estudar CSV"));
        assertTrue(saida.contains("Tarefa excluida."));
        assertTrue(saida.contains("Nenhuma tarefa encontrada."));
        assertTrue(saida.contains("Encerrado."));
    }

    @Test
    void devePersistirEntreExecucoes() throws IOException {
        Path arquivo = diretorio.resolve("tarefas.csv");

        executar("1\nEstudar Java\nJUnit\n0\n", arquivo);

        String segundaExecucao = executar("2\n0\n", arquivo);

        assertTrue(segundaExecucao.contains("Estudar Java"));
        assertTrue(segundaExecucao.contains("[PENDENTE]"));
    }

    @Test
    void deveTratarOpcaoEIdInvalidos() {
        Path arquivo = diretorio.resolve("tarefas.csv");

        String saida = executar("99\n3\nabc\n0\n", arquivo);

        assertTrue(saida.contains("Opcao invalida."));
        assertTrue(saida.contains("Erro: ID invalido."));
        assertTrue(saida.contains("Encerrado."));
    }

    @Test
    void deveImpedirConclusaoDuplicada() {
        Path arquivo = diretorio.resolve("tarefas.csv");

        String saida = executar(
                "1\nEstudar\n\n5\n1\n5\n1\n0\n", arquivo
        );

        assertTrue(saida.contains("Tarefa concluida."));
        assertTrue(saida.contains("Erro: Tarefa ja concluida."));
    }

    @Test
    void deveTratarCsvInvalido() throws IOException {
        Path arquivo = diretorio.resolve("tarefas.csv");

        java.nio.file.Files.writeString(
                arquivo, "arquivo,invalido\n",
                StandardCharsets.UTF_8
        );

        String saida = executar("2\n0\n", arquivo);

        assertTrue(saida.contains("Erro de persistencia:"));
        assertTrue(saida.contains("Encerrado."));
    }
}