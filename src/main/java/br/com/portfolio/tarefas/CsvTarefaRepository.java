package br.com.portfolio.tarefas;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CsvTarefaRepository implements TarefaRepository {

    private static final List<String> CABECALHO =
            List.of("id", "titulo", "descricao", "status");

    private final Path arquivo;

    public CsvTarefaRepository(Path arquivo) {
        this.arquivo = arquivo.toAbsolutePath().normalize();
    }

    @Override
    public List<Tarefa> listar() throws IOException {
        if (Files.notExists(arquivo)) {
            return List.of();
        }

        List<Tarefa> tarefas = new ArrayList<>();
        Set<Long> ids = new HashSet<>();

        try (Reader reader = Files.newBufferedReader(
                arquivo, StandardCharsets.UTF_8);
             CSVParser parser = CSVFormat.DEFAULT.parse(reader)) {

            boolean primeiro = true;

            for (CSVRecord registro : parser) {
                if (primeiro) {
                    primeiro = false;

                    if (registro.size() != CABECALHO.size()
                            || !CABECALHO.equals(registro.toList())) {
                        throw new IOException("Cabecalho CSV invalido.");
                    }

                    continue;
                }

                if (registro.size() != 4) {
                    throw new IOException("Registro CSV invalido: "
                            + registro.getRecordNumber());
                }

                try {
                    long id = Long.parseLong(registro.get(0));

                    if (!ids.add(id)) {
                        throw new IOException("ID duplicado no CSV: " + id);
                    }

                    StatusTarefa status =
                            StatusTarefa.valueOf(registro.get(3));

                    tarefas.add(new Tarefa(
                            id,
                            registro.get(1),
                            registro.get(2),
                            status
                    ));
                } catch (IllegalArgumentException exception) {
                    throw new IOException(
                            "Dados invalidos no registro "
                                    + registro.getRecordNumber(),
                            exception
                    );
                }
            }

            if (primeiro) {
                throw new IOException("Arquivo CSV vazio ou sem cabecalho.");
            }
        }

        return List.copyOf(tarefas);
    }

    @Override
    public void salvar(List<Tarefa> tarefas) throws IOException {
        Path diretorio = arquivo.getParent();

        Files.createDirectories(diretorio);

        Path temporario = Files.createTempFile(
                diretorio, ".tarefas-", ".tmp"
        );

        try {
            try (Writer writer = Files.newBufferedWriter(
                    temporario, StandardCharsets.UTF_8);
                 CSVPrinter printer = new CSVPrinter(
                         writer, CSVFormat.DEFAULT)) {

                printer.printRecord(CABECALHO);

                for (Tarefa tarefa : tarefas) {
                    printer.printRecord(
                            tarefa.getId(),
                            tarefa.getTitulo(),
                            tarefa.getDescricao(),
                            tarefa.getStatus().name()
                    );
                }
            }

            try {
                Files.move(
                        temporario,
                        arquivo,
                        StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING
                );
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(
                        temporario,
                        arquivo,
                        StandardCopyOption.REPLACE_EXISTING
                );
            }
        } finally {
            Files.deleteIfExists(temporario);
        }
    }
}