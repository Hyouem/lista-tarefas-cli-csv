# Lista de Tarefas CLI com CSV

Aplicacao de linha de comando desenvolvida com Java 25,
Maven, JUnit 5 e Apache Commons CSV.

## Funcionalidades

- Criar tarefas
- Listar todas as tarefas
- Consultar por ID
- Atualizar titulo e descricao
- Concluir e reabrir tarefas
- Excluir tarefas
- Filtrar por status
- Persistir dados em CSV entre execucoes

## Tecnologias

- Java 25
- Maven
- JUnit Jupiter 5
- Apache Commons CSV 1.14.1
- GitHub Actions

## Arquitetura

- `Tarefa`: entidade de dominio
- `StatusTarefa`: enum de status
- `TarefaRepository`: contrato de persistencia
- `CsvTarefaRepository`: leitura e gravacao CSV
- `TarefaService`: regras de negocio
- `App`: menu interativo

## Compilar e testar

```powershell
mvn clean verify
```

## Executar

O projeto utiliza Apache Commons CSV em tempo de execucao.
Primeiro gere o classpath das dependencias:

```powershell
mvn dependency:build-classpath -Dmdep.outputFile=target/classpath.txt
```

Depois, no PowerShell:

```powershell
$deps = (Get-Content target/classpath.txt -Raw).Trim()
java -cp "target/classes;$deps" br.com.portfolio.tarefas.App
```

Por padrao, os dados sao armazenados em `dados/tarefas.csv`.

Para usar outro arquivo:

```powershell
java -cp "target/classes;$deps" br.com.portfolio.tarefas.App "C:\temp\tarefas.csv"
```

## Menu

1. Criar tarefa
2. Listar todas
3. Consultar por ID
4. Atualizar
5. Concluir
6. Reabrir
7. Excluir
8. Listar pendentes
9. Listar concluidas
0. Sair

## Formato CSV

```csv
id,titulo,descricao,status
1,Estudar Java,Revisar JUnit,PENDENTE
2,Estudar CSV,Praticar persistencia,CONCLUIDA
```

Campos contendo virgulas, aspas ou quebras de linha
sao tratados pela biblioteca Apache Commons CSV.

## Validacoes

- IDs positivos
- Titulos obrigatorios
- Status validos
- Rejeicao de IDs duplicados no CSV
- Rejeicao de cabecalhos e registros invalidos
- Rejeicao de conclusao ou reabertura redundante
- Preservacao do arquivo original durante a escrita temporaria

## Limitacoes conhecidas

- Os IDs sao calculados a partir do maior ID existente.
  A exclusao do maior ID permite sua reutilizacao.
- Nao existe controle de concorrencia entre processos.
- A substituicao atomica depende do sistema de arquivos.
  O fallback nao oferece a mesma garantia de atomicidade.
- Nao ha banco de dados, autenticacao ou interface grafica.
- O arquivo CSV deve ser mantido em local com permissoes
  adequadas para leitura e escrita.

## Testes automatizados

```powershell
mvn clean verify
```

Os testes abrangem dominio, servico, persistencia e CLI.