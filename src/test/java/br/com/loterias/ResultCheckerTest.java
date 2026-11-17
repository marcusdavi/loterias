package br.com.loterias;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

class ResultCheckerTest {

	private final ResultChecker checker = new ResultChecker(Modality.LOTOFACIL);
	private final Draw draw = new Draw(2900, range(1, 15));

	@Test
	void countsHitsAndFlagsPrize() {
		Bet bet = new Bet(Channel.INTERNET, union(range(1, 11), range(21, 24)));

		BetCheck check = checker.check(bet, draw);

		assertEquals(11, check.hitCount());
		assertEquals(range(1, 11), check.hits());
		assertTrue(check.prized());
	}

	@Test
	void belowMinimumIsNotPrized() {
		Bet bet = new Bet(Channel.LOTERICA, union(range(1, 10), range(16, 20)));

		BetCheck check = checker.check(bet, draw);

		assertEquals(10, check.hitCount());
		assertFalse(check.prized());
	}

	@Test
	void sortsByHitsDescendingKeepingOriginalOrderOnTies() {
		Bet low = new Bet(Channel.INTERNET, union(range(1, 10), range(16, 20)));
		Bet highA = new Bet(Channel.INTERNET, range(1, 15));
		Bet highB = new Bet(Channel.LOTERICA, range(1, 15));

		ContestReport report = checker.check(List.of(low, highA, highB), draw);

		assertEquals(List.of(highA, highB, low), report.checks().stream().map(BetCheck::bet).toList());
		assertEquals(2, report.prizedCount());
	}

	private static Set<Integer> range(int from, int to) {
		return IntStream.rangeClosed(from, to).boxed().collect(Collectors.toCollection(TreeSet::new));
	}

	private static TreeSet<Integer> union(Set<Integer> a, Set<Integer> b) {
		TreeSet<Integer> result = new TreeSet<>(a);
		result.addAll(b);
		return result;
	}
}
