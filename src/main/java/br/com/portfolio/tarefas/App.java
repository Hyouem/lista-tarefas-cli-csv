package br.com.portfolio.tarefas;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class App {

    private final TarefaService service;
    private final Scanner scanner;

    public App(TarefaService service, Scanner scanner) {
        this.service = service;
        this.scanner = scanner;
    }

    public static void main(String[] args) {
        if (args.length > 1) {
            System.err.println("Uso: App [caminho-do-csv]");
            System.exit(1);
        }

        Path arquivo = args.length == 1
                ? Path.of(args[0])
                : Path.of("dados", "tarefas.csv");

        TarefaService service = new TarefaService(
                new CsvTarefaRepository(arquivo)
        );

        new App(service, new Scanner(System.in)).executar();
    }

    public void executar() {
        while (scanner.hasNextLine()) {
            mostrarMenu();
            String opcao = scanner.nextLine().trim();

            try {
                switch (opcao) {
                    case "1" -> criar();
                    case "2" -> listar(service.listar());
                    case "3" -> consultar();
                    case "4" -> atualizar();
                    case "5" -> concluir();
                    case "6" -> reabrir();
                    case "7" -> excluir();
                    case "8" -> listar(service.listarPorStatus(
                            StatusTarefa.PENDENTE));
                    case "9" -> listar(service.listarPorStatus(
                            StatusTarefa.CONCLUIDA));
                    case "0" -> {
                        System.out.println("Encerrado.");
                        return;
                    }
                    default -> System.out.println("Opcao invalida.");
                }
            } catch (IOException exception) {
                System.out.println(
                        "Erro de persistencia: " + exception.getMessage()
                );
            } catch (IllegalArgumentException
                     | IllegalStateException
                     | NoSuchElementException exception) {
                System.out.println("Erro: " + exception.getMessage());
            }
        }
    }

    private void mostrarMenu() {
        System.out.println();
        System.out.println("=== LISTA DE TAREFAS ===");
        System.out.println("1 - Criar tarefa");
        System.out.println("2 - Listar todas");
        System.out.println("3 - Consultar por ID");
        System.out.println("4 - Atualizar tarefa");
        System.out.println("5 - Concluir tarefa");
        System.out.println("6 - Reabrir tarefa");
        System.out.println("7 - Excluir tarefa");
        System.out.println("8 - Listar pendentes");
        System.out.println("9 - Listar concluidas");
        System.out.println("0 - Sair");
        System.out.print("Opcao: ");
    }

    private void criar() {
        String titulo = ler("Titulo: ");
        String descricao = ler("Descricao: ");

        try {
            Tarefa tarefa = service.criar(titulo, descricao);
            System.out.println("Tarefa criada. ID: " + tarefa.getId());
        } catch (IOException exception) {
            System.out.println(
                    "Erro de persistencia: " + exception.getMessage()
            );
        }
    }

    private void listar(List<Tarefa> tarefas) {
        if (tarefas.isEmpty()) {
            System.out.println("Nenhuma tarefa encontrada.");
            return;
        }

        tarefas.forEach(this::exibir);
    }

    private void consultar() throws IOException {
        exibir(service.consultar(lerId()));
    }

    private void atualizar() throws IOException {
        long id = lerId();
        String titulo = ler("Novo titulo: ");
        String descricao = ler("Nova descricao: ");

        service.atualizar(id, titulo, descricao);
        System.out.println("Tarefa atualizada.");
    }

    private void concluir() throws IOException {
        service.concluir(lerId());
        System.out.println("Tarefa concluida.");
    }

    private void reabrir() throws IOException {
        service.reabrir(lerId());
        System.out.println("Tarefa reaberta.");
    }

    private void excluir() throws IOException {
        service.excluir(lerId());
        System.out.println("Tarefa excluida.");
    }

    private long lerId() {
        String entrada = ler("ID: ");

        try {
            long id = Long.parseLong(entrada);

            if (id <= 0) {
                throw new NumberFormatException();
            }

            return id;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("ID invalido.");
        }
    }

    private String ler(String mensagem) {
        System.out.print(mensagem);

        if (!scanner.hasNextLine()) {
            throw new IllegalStateException("Entrada encerrada.");
        }

        return scanner.nextLine();
    }

    private void exibir(Tarefa tarefa) {
        System.out.printf(
                "ID: %d | [%s] %s | %s%n",
                tarefa.getId(),
                tarefa.getStatus(),
                tarefa.getTitulo(),
                tarefa.getDescricao()
        );
    }
}