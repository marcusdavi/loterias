package br.com.loterias;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;

/**
 * Ponto de entrada: lê apostas e resultados, confere e grava o relatório.
 */
public class LoteriaApp {

	static final int EXIT_OK = 0;
	static final int EXIT_FILE_ERROR = 1;
	static final int EXIT_USAGE_ERROR = 2;

	public static void main(String[] args) {
		System.exit(run(args, new PrintStream(System.out, true, StandardCharsets.UTF_8),
				new PrintStream(System.err, true, StandardCharsets.UTF_8)));
	}

	static int run(String[] args, PrintStream out, PrintStream err) {
		CliOptions options;
		try {
			options = CliOptions.parse(args);
		} catch (IllegalArgumentException e) {
			err.println(e.getMessage());
			err.println(CliOptions.USAGE);
			return EXIT_USAGE_ERROR;
		}
		if (options.help()) {
			out.println(CliOptions.USAGE);
			return EXIT_OK;
		}

		try {
			List<String> report = process(options);
			report.forEach(out::println);
			out.println();
			out.println("Relatório gravado em " + options.outputFile());
			return EXIT_OK;
		} catch (NoSuchFileException e) {
			err.println("Arquivo não encontrado: " + e.getFile());
			return EXIT_FILE_ERROR;
		} catch (IOException e) {
			err.println("Erro de leitura/gravação: " + e.getMessage());
			return EXIT_FILE_ERROR;
		} catch (InvalidFileException e) {
			err.println("Arquivo inválido: " + e.getMessage());
			return EXIT_FILE_ERROR;
		}
	}

	static List<String> process(CliOptions options) throws IOException, InvalidFileException {
		Modality modality = options.modality();
		LotteryFileParser parser = new LotteryFileParser(modality);
		List<Bet> bets = parser.parseBets(options.betsFile());
		List<Draw> draws = parser.parseDraws(options.resultFile());

		ResultChecker checker = new ResultChecker(modality);
		List<ContestReport> reports = draws.stream()
				.map(draw -> checker.check(bets, draw))
				.toList();

		List<String> lines = new ReportFormatter(modality).format(reports);
		writeReport(options.outputFile(), lines);
		return lines;
	}

	private static void writeReport(Path file, List<String> lines) throws IOException {
		Path parent = file.toAbsolutePath().getParent();
		if (parent != null) {
			Files.createDirectories(parent);
		}
		Files.write(file, lines, StandardCharsets.UTF_8);
	}
}
