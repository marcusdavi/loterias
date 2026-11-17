package br.com.loterias;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * Resultado de um concurso: o número do concurso e as dezenas sorteadas (em ordem crescente).
 */
public record Draw(int contest, Set<Integer> numbers) {

	public Draw {
		numbers = Collections.unmodifiableSortedSet(new TreeSet<>(Objects.requireNonNull(numbers, "numbers")));
	}
}
