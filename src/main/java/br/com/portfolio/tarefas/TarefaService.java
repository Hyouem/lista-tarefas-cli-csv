package br.com.portfolio.tarefas;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

public class TarefaService {

    private final TarefaRepository repository;

    public TarefaService(TarefaRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    public Tarefa criar(String titulo, String descricao) throws IOException {
        List<Tarefa> tarefas = carregar();

        long id = tarefas.stream()
                .mapToLong(Tarefa::getId)
                .max()
                .orElse(0);

        if (id == Long.MAX_VALUE) {
            throw new IllegalStateException("Limite de IDs atingido.");
        }

        Tarefa nova = new Tarefa(id + 1, titulo, descricao);
        tarefas.add(nova);
        repository.salvar(tarefas);

        return nova;
    }

    public List<Tarefa> listar() throws IOException {
        return List.copyOf(carregar());
    }

    public List<Tarefa> listarPorStatus(StatusTarefa status)
            throws IOException {

        Objects.requireNonNull(status);

        return carregar().stream()
                .filter(tarefa -> tarefa.getStatus() == status)
                .sorted(Comparator.comparingLong(Tarefa::getId))
                .toList();
    }

    public Tarefa consultar(long id) throws IOException {
        return encontrar(carregar(), id);
    }

    public Tarefa atualizar(long id, String titulo, String descricao)
            throws IOException {

        List<Tarefa> tarefas = carregar();
        Tarefa tarefa = encontrar(tarefas, id);

        tarefa.atualizar(titulo, descricao);
        repository.salvar(tarefas);

        return tarefa;
    }

    public Tarefa concluir(long id) throws IOException {
        List<Tarefa> tarefas = carregar();
        Tarefa tarefa = encontrar(tarefas, id);

        tarefa.concluir();
        repository.salvar(tarefas);

        return tarefa;
    }

    public Tarefa reabrir(long id) throws IOException {
        List<Tarefa> tarefas = carregar();
        Tarefa tarefa = encontrar(tarefas, id);

        tarefa.reabrir();
        repository.salvar(tarefas);

        return tarefa;
    }

    public void excluir(long id) throws IOException {
        List<Tarefa> tarefas = carregar();
        Tarefa tarefa = encontrar(tarefas, id);

        tarefas.remove(tarefa);
        repository.salvar(tarefas);
    }

    private List<Tarefa> carregar() throws IOException {
        return new ArrayList<>(repository.listar());
    }

    private Tarefa encontrar(List<Tarefa> tarefas, long id) {
        return tarefas.stream()
                .filter(tarefa -> tarefa.getId() == id)
                .findFirst()
                .orElseThrow(() ->
                        new NoSuchElementException("Tarefa nao encontrada.")
                );
    }
}