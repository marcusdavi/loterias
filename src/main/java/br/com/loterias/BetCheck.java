package br.com.loterias;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * Resultado da conferência de uma aposta contra um concurso.
 *
 * @param bet    a aposta conferida
 * @param hits   os números da aposta que foram sorteados
 * @param prized se a quantidade de acertos dá direito a prêmio na modalidade
 */
public record BetCheck(Bet bet, Set<Integer> hits, boolean prized) {

	public BetCheck {
		Objects.requireNonNull(bet, "bet");
		hits = Collections.unmodifiableSortedSet(new TreeSet<>(Objects.requireNonNull(hits, "hits")));
	}

	public int hitCount() {
		return hits.size();
	}
}
