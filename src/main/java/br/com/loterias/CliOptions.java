package br.com.loterias;

import java.nio.file.Path;

/**
 * Opções de linha de comando.
 */
public record CliOptions(Modality modality, Path betsFile, Path resultFile, Path outputFile, boolean help) {

	public static final String USAGE = String.join(System.lineSeparator(),
			"Uso: java -jar loterias.jar [opções]",
			"",
			"Opções:",
			"  -m, --modalidade <nome>   " + Modality.availableNames() + " (padrão: lotofacil)",
			"  -a, --apostas <arquivo>   arquivo de apostas (padrão: data/bets.txt)",
			"  -r, --resultado <arquivo> arquivo de resultados (padrão: data/result.txt)",
			"  -s, --saida <arquivo>     arquivo de saída (padrão: data/result-processed.txt)",
			"  -h, --ajuda               mostra esta ajuda");

	public static CliOptions parse(String[] args) {
		Modality modality = Modality.LOTOFACIL;
		Path bets = Path.of("data", "bets.txt");
		Path result = Path.of("data", "result.txt");
		Path output = Path.of("data", "result-processed.txt");

		for (int i = 0; i < args.length; i++) {
			String option = args[i];
			switch (option) {
			case "-h", "--ajuda", "--help" -> {
				return new CliOptions(modality, bets, result, output, true);
			}
			case "-m", "--modalidade" -> modality = Modality.fromName(value(args, ++i, option));
			case "-a", "--apostas" -> bets = Path.of(value(args, ++i, option));
			case "-r", "--resultado" -> result = Path.of(value(args, ++i, option));
			case "-s", "--saida" -> output = Path.of(value(args, ++i, option));
			default -> throw new IllegalArgumentException("Opção desconhecida: " + option);
			}
		}
		return new CliOptions(modality, bets, result, output, false);
	}

	private static String value(String[] args, int index, String option) {
		if (index >= args.length) {
			throw new IllegalArgumentException("A opção " + option + " exige um valor");
		}
		return args[index];
	}
}
