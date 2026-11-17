package br.com.loterias;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Monta o texto do relatório de conferência, usado tanto no console quanto no arquivo de saída.
 */
public final class ReportFormatter {

	private static final String SEPARATOR = " - ";

	private final Modality modality;

	public ReportFormatter(Modality modality) {
		this.modality = Objects.requireNonNull(modality, "modality");
	}

	public List<String> format(List<ContestReport> reports) {
		List<String> lines = new ArrayList<>();
		lines.add(modality.displayName().toUpperCase(Locale.ROOT));
		lines.add("Números entre parênteses não foram sorteados. Prêmio a partir de "
				+ modality.minPrizeHits() + " acertos.");
		for (ContestReport report : reports) {
			lines.add("");
			lines.addAll(format(report));
		}
		return lines;
	}

	public List<String> format(ContestReport report) {
		List<String> lines = new ArrayList<>();
		lines.add("RESULTADO");
		lines.add("Concurso: " + report.draw().contest() + " --> " + formatNumbers(report.draw().numbers()));
		lines.add("CONFERÊNCIA");
		report.checks().forEach(check -> lines.add(format(check)));
		lines.add(String.format("Apostas premiadas: %d de %d", report.prizedCount(), report.checks().size()));
		return lines;
	}

	public String format(BetCheck check) {
		StringBuilder sb = new StringBuilder()
				.append(String.format("%02d acertos", check.hitCount()))
				.append(" --> ")
				.append(check.bet().channel().name())
				.append(" --> ")
				.append(formatNumbers(check.bet().numbers(), check.hits()));
		if (check.prized()) {
			sb.append("  ★ PREMIADA");
		}
		return sb.toString();
	}

	static String formatNumbers(Collection<Integer> numbers) {
		return numbers.stream()
				.map(n -> String.format("%02d", n))
				.collect(Collectors.joining(SEPARATOR));
	}

	/** Formata os números, colocando entre parênteses os que não estão em {@code hits}. */
	static String formatNumbers(Collection<Integer> numbers, Set<Integer> hits) {
		return numbers.stream()
				.map(n -> hits.contains(n) ? String.format("%02d", n) : String.format("(%02d)", n))
				.collect(Collectors.joining(SEPARATOR));
	}
}
