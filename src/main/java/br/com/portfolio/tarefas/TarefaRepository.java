package br.com.portfolio.tarefas;

import java.io.IOException;
import java.util.List;

public interface TarefaRepository {

    List<Tarefa> listar() throws IOException;

    void salvar(List<Tarefa> tarefas) throws IOException;
}