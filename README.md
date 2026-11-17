# Processador para conferir Loterias

Confere suas apostas contra um ou mais concursos e gera um relatório com os acertos
de cada aposta, destacando as premiadas. Funciona com **Lotofácil**, **Mega-Sena** e **Quina**.

## Requisitos

- Java 17 ou superior
- Maven 3.8 ou superior

## Preparar os arquivos

Os arquivos ficam na pasta `data/`. Copie os exemplos e edite com suas apostas:

```bash
cp data/example-bets.txt data/bets.txt
cp data/example-result.txt data/result.txt
```

Os arquivos `bets.txt`, `result.txt` e `result-processed.txt` estão no `.gitignore`.

### `bets.txt` (apostas)

Uma aposta por linha, com os valores separados por vírgula:

1. O primeiro valor é o canal: `0` para aposta na lotérica e `1` para aposta na internet.
2. Do segundo valor em diante vêm os números apostados.

```
1,01,02,03,04,05,06,07,08,09,10,11,12,13,14,15
0,03,05,07,09,11,13,15,17,19,21,22,23,24,25,02,04
```

### `result.txt` (resultados)

Um concurso por linha: primeiro o número do concurso, depois os números sorteados.

```
2900,01,02,03,04,05,06,07,08,09,10,11,12,13,14,15
```

Nos dois arquivos, linhas em branco, espaços e linhas que começam com `#` (comentários)
são ignorados. O programa recusa números repetidos, números fora do intervalo da modalidade
e quantidades de números inválidas, e informa o arquivo e a linha do erro.

## Executar

```bash
mvn package
```

```bash
java -jar target/loterias.jar
```

Opções:

| Opção | Descrição | Padrão |
|---|---|---|
| `-m`, `--modalidade` | `lotofacil`, `megasena` ou `quina` | `lotofacil` |
| `-a`, `--apostas` | arquivo de apostas | `data/bets.txt` |
| `-r`, `--resultado` | arquivo de resultados | `data/result.txt` |
| `-s`, `--saida` | arquivo do relatório | `data/result-processed.txt` |
| `-h`, `--ajuda` | mostra a ajuda | |

Exemplo:

```bash
java -jar target/loterias.jar -m megasena -a data/mega-bets.txt -r data/mega-result.txt
```

No Eclipse ou no IntelliJ, importe o projeto como projeto Maven e rode a classe
`br.com.loterias.LoteriaApp`.

## Relatório

O relatório aparece no console e é gravado em `data/result-processed.txt`. Em cada concurso,
as apostas vêm ordenadas da maior para a menor quantidade de acertos. Os números entre
parênteses são os que não foram sorteados:

```
RESULTADO
Concurso: 2900 --> 01 - 02 - 03 - 04 - 05 - 06 - 07 - 08 - 09 - 10 - 11 - 12 - 13 - 14 - 15
CONFERÊNCIA
15 acertos --> INTERNET --> 01 - 02 - 03 - 04 - 05 - 06 - 07 - 08 - 09 - 10 - 11 - 12 - 13 - 14 - 15  ★ PREMIADA
14 acertos --> INTERNET --> 01 - 02 - 03 - 04 - 05 - 06 - 07 - 08 - 09 - 10 - 11 - 12 - 13 - 14 - (16)  ★ PREMIADA
Apostas premiadas: 2 de 2
```

## Testes

```bash
mvn test
```
