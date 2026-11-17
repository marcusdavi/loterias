package br.com.loterias;

import java.util.List;
import java.util.Objects;

/**
 * Conferência de todas as apostas contra um concurso, já ordenada por quantidade de acertos.
 */
public record ContestReport(Draw draw, List<BetCheck> checks) {

	public ContestReport {
		Objects.requireNonNull(draw, "draw");
		checks = List.copyOf(checks);
	}

	public long prizedCount() {
		return checks.stream().filter(BetCheck::prized).count();
	}
}
