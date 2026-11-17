package br.com.loterias;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * Uma aposta: o canal onde foi feita e os números escolhidos (guardados em ordem crescente, sem repetição).
 */
public record Bet(Channel channel, Set<Integer> numbers) {

	public Bet {
		Objects.requireNonNull(channel, "channel");
		numbers = Collections.unmodifiableSortedSet(new TreeSet<>(Objects.requireNonNull(numbers, "numbers")));
	}
}
