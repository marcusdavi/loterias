package br.com.loterias;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Confere apostas contra o resultado de um concurso.
 */
public final class ResultChecker {

	private final Modality modality;

	public ResultChecker(Modality modality) {
		this.modality = Objects.requireNonNull(modality, "modality");
	}

	public BetCheck check(Bet bet, Draw draw) {
		SortedSet<Integer> hits = new TreeSet<>(bet.numbers());
		hits.retainAll(draw.numbers());
		return new BetCheck(bet, hits, modality.isPrized(hits.size()));
	}

	/** Confere todas as apostas e as ordena da maior para a menor quantidade de acertos. */
	public ContestReport check(List<Bet> bets, Draw draw) {
		List<BetCheck> checks = bets.stream()
				.map(bet -> check(bet, draw))
				.sorted(Comparator.comparingInt(BetCheck::hitCount).reversed())
				.toList();
		return new ContestReport(draw, checks);
	}
}
