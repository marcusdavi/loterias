package br.com.loterias;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Lê e valida os arquivos de apostas e de resultados.
 *
 * <p>Formato (valores separados por vírgula, espaços são ignorados):
 * <ul>
 * <li>Apostas: {@code canal,n1,n2,...} onde canal é 0 (lotérica) ou 1 (internet).</li>
 * <li>Resultados: {@code concurso,n1,n2,...}, uma linha por concurso.</li>
 * </ul>
 * Linhas em branco e linhas iniciadas por {@code #} são ignoradas.
 */
public final class LotteryFileParser {

	private static final char BOM = '﻿';

	private final Modality modality;

	public LotteryFileParser(Modality modality) {
		this.modality = Objects.requireNonNull(modality, "modality");
	}

	public List<Bet> parseBets(Path file) throws IOException, InvalidFileException {
		List<Bet> bets = new ArrayList<>();
		for (Line line : readLines(file)) {
			List<String> fields = line.fields();
			Channel channel;
			try {
				channel = Channel.fromCode(parseInt(fields.get(0), file, line.number()));
			} catch (IllegalArgumentException e) {
				throw new InvalidFileException(file, line.number(), e.getMessage());
			}
			SortedSet<Integer> numbers = parseNumbers(fields, file, line.number());
			validateSize(numbers.size(), modality.minBetSize(), modality.maxBetSize(), "aposta", file, line.number());
			bets.add(new Bet(channel, numbers));
		}
		if (bets.isEmpty()) {
			throw new InvalidFileException(file, "nenhuma aposta encontrada");
		}
		return bets;
	}

	public List<Draw> parseDraws(Path file) throws IOException, InvalidFileException {
		List<Draw> draws = new ArrayList<>();
		Set<Integer> contests = new HashSet<>();
		for (Line line : readLines(file)) {
			List<String> fields = line.fields();
			int contest = parseInt(fields.get(0), file, line.number());
			if (contest <= 0) {
				throw new InvalidFileException(file, line.number(), "número do concurso inválido: " + contest);
			}
			if (!contests.add(contest)) {
				throw new InvalidFileException(file, line.number(), "concurso " + contest + " informado mais de uma vez");
			}
			SortedSet<Integer> numbers = parseNumbers(fields, file, line.number());
			validateSize(numbers.size(), modality.drawSize(), modality.drawSize(), "resultado", file, line.number());
			draws.add(new Draw(contest, numbers));
		}
		if (draws.isEmpty()) {
			throw new InvalidFileException(file, "nenhum resultado encontrado");
		}
		return draws;
	}

	/** Converte os campos a partir do segundo em números, validando repetição e intervalo. */
	private SortedSet<Integer> parseNumbers(List<String> fields, Path file, int lineNumber)
			throws InvalidFileException {
		SortedSet<Integer> numbers = new TreeSet<>();
		for (String field : fields.subList(1, fields.size())) {
			int number = parseInt(field, file, lineNumber);
			if (number < modality.minNumber() || number > modality.maxNumber()) {
				throw new InvalidFileException(file, lineNumber, String.format(
						"número %d fora do intervalo da %s (%d a %d)",
						number, modality.displayName(), modality.minNumber(), modality.maxNumber()));
			}
			if (!numbers.add(number)) {
				throw new InvalidFileException(file, lineNumber, "número " + number + " repetido");
			}
		}
		return numbers;
	}

	private void validateSize(int size, int min, int max, String kind, Path file, int lineNumber)
			throws InvalidFileException {
		if (size < min || size > max) {
			String expected = min == max ? String.valueOf(min) : "de " + min + " a " + max;
			throw new InvalidFileException(file, lineNumber, String.format(
					"%s com %d números; a %s exige %s", kind, size, modality.displayName(), expected));
		}
	}

	private static int parseInt(String value, Path file, int lineNumber) throws InvalidFileException {
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			throw new InvalidFileException(file, lineNumber, "valor '" + value + "' não é um número");
		}
	}

	private static List<Line> readLines(Path file) throws IOException, InvalidFileException {
		List<String> rawLines = Files.readAllLines(file, StandardCharsets.UTF_8);
		List<Line> lines = new ArrayList<>();
		for (int i = 0; i < rawLines.size(); i++) {
			String content = rawLines.get(i);
			if (i == 0 && !content.isEmpty() && content.charAt(0) == BOM) {
				content = content.substring(1);
			}
			content = content.strip();
			if (content.isEmpty() || content.startsWith("#")) {
				continue;
			}
			int lineNumber = i + 1;
			List<String> fields = new ArrayList<>();
			for (String field : content.split(",", -1)) {
				String trimmed = field.strip();
				if (trimmed.isEmpty()) {
					throw new InvalidFileException(file, lineNumber, "valor vazio entre vírgulas");
				}
				fields.add(trimmed);
			}
			if (fields.size() < 2) {
				throw new InvalidFileException(file, lineNumber, "linha sem números");
			}
			lines.add(new Line(lineNumber, fields));
		}
		return lines;
	}

	private record Line(int number, List<String> fields) {
	}
}
