package br.com.loterias;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

class ReportFormatterTest {

	private final ReportFormatter formatter = new ReportFormatter(Modality.QUINA);

	@Test
	void marksMissedNumbersAndPrize() {
		Bet bet = new Bet(Channel.LOTERICA, Set.of(5, 10, 20, 30, 40));
		BetCheck check = new BetCheck(bet, Set.of(5, 30), true);

		assertEquals("02 acertos --> LOTERICA --> 05 - (10) - (20) - 30 - (40)  ★ PREMIADA", formatter.format(check));
	}

	@Test
	void formatsContest() {
		Draw draw = new Draw(6000, Set.of(1, 2, 3, 4, 5));
		Bet bet = new Bet(Channel.INTERNET, Set.of(1, 2, 3, 4, 5));
		ContestReport report = new ContestReport(draw, List.of(new BetCheck(bet, bet.numbers(), true)));

		assertEquals(List.of(
				"RESULTADO",
				"Concurso: 6000 --> 01 - 02 - 03 - 04 - 05",
				"CONFERÊNCIA",
				"05 acertos --> INTERNET --> 01 - 02 - 03 - 04 - 05  ★ PREMIADA",
				"Apostas premiadas: 1 de 1"), formatter.format(report));
	}
}
