package br.com.loterias;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Modalidades de loteria suportadas, com as regras usadas para validar apostas e premiação.
 */
public enum Modality {

	LOTOFACIL("Lotofácil", 1, 25, 15, 15, 20, 11),
	MEGA_SENA("Mega-Sena", 1, 60, 6, 6, 20, 4),
	QUINA("Quina", 1, 80, 5, 5, 15, 2);

	private final String displayName;
	private final int minNumber;
	private final int maxNumber;
	private final int drawSize;
	private final int minBetSize;
	private final int maxBetSize;
	private final int minPrizeHits;

	Modality(String displayName, int minNumber, int maxNumber, int drawSize, int minBetSize, int maxBetSize,
			int minPrizeHits) {
		this.displayName = displayName;
		this.minNumber = minNumber;
		this.maxNumber = maxNumber;
		this.drawSize = drawSize;
		this.minBetSize = minBetSize;
		this.maxBetSize = maxBetSize;
		this.minPrizeHits = minPrizeHits;
	}

	public String displayName() {
		return displayName;
	}

	public int minNumber() {
		return minNumber;
	}

	public int maxNumber() {
		return maxNumber;
	}

	/** Quantidade de números sorteados em cada concurso. */
	public int drawSize() {
		return drawSize;
	}

	public int minBetSize() {
		return minBetSize;
	}

	public int maxBetSize() {
		return maxBetSize;
	}

	/** Quantidade mínima de acertos que dá direito a prêmio. */
	public int minPrizeHits() {
		return minPrizeHits;
	}

	public boolean isPrized(int hits) {
		return hits >= minPrizeHits;
	}

	/**
	 * Converte um nome digitado pelo usuário (ex.: "lotofacil", "Mega-Sena", "megasena", "quina") na modalidade.
	 */
	public static Modality fromName(String name) {
		String key = normalize(name);
		return Arrays.stream(values())
				.filter(m -> normalize(m.name()).equals(key) || normalize(m.displayName).equals(key))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException(
						"Modalidade desconhecida: '" + name + "'. Opções: " + availableNames()));
	}

	public static String availableNames() {
		return Arrays.stream(values())
				.map(m -> normalize(m.name()))
				.collect(Collectors.joining(", "));
	}

	private static String normalize(String value) {
		String withoutAccents = Normalizer.normalize(value, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
		return withoutAccents.toLowerCase(Locale.ROOT).replaceAll("[^a-z]", "");
	}
}
