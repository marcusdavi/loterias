package br.com.loterias;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class LotteryFileParserTest {

	private static final String LOTOFACIL_15 = "01,02,03,04,05,06,07,08,09,10,11,12,13,14,15";

	@TempDir
	Path dir;

	private final LotteryFileParser parser = new LotteryFileParser(Modality.LOTOFACIL);

	private Path file(String content) throws IOException {
		Path file = dir.resolve("input.txt");
		Files.writeString(file, content, StandardCharsets.UTF_8);
		return file;
	}

	@Test
	void parsesBetsIgnoringBlankLinesCommentsSpacesAndCrlf() throws Exception {
		Path file = file("﻿# comentário\r\n1, " + LOTOFACIL_15.replace(",", " , ") + "\r\n\r\n0," + LOTOFACIL_15
				+ ",16\r\n\r\n");

		List<Bet> bets = parser.parseBets(file);

		assertEquals(2, bets.size());
		assertEquals(Channel.INTERNET, bets.get(0).channel());
		assertEquals(15, bets.get(0).numbers().size());
		assertEquals(Channel.LOTERICA, bets.get(1).channel());
		assertEquals(16, bets.get(1).numbers().size());
	}

	@Test
	void parsesMultipleDraws() throws Exception {
		Path file = file("2900," + LOTOFACIL_15 + "\n2901," + LOTOFACIL_15 + "\n");

		List<Draw> draws = parser.parseDraws(file);

		assertEquals(List.of(2900, 2901), draws.stream().map(Draw::contest).toList());
	}

	@Test
	void rejectsInvalidChannel() throws Exception {
		assertError("2," + LOTOFACIL_15, "linha 1: canal inválido: 2");
	}

	@Test
	void rejectsRepeatedNumber() throws Exception {
		assertError("1," + LOTOFACIL_15 + ",15", "número 15 repetido");
	}

	@Test
	void rejectsNumberOutOfRange() throws Exception {
		assertError("1,01,02,03,04,05,06,07,08,09,10,11,12,13,14,26", "número 26 fora do intervalo");
	}

	@Test
	void rejectsWrongBetSize() throws Exception {
		assertError("1,01,02,03", "aposta com 3 números");
	}

	@Test
	void rejectsNonNumericValueWithLineNumber() throws Exception {
		assertError("1," + LOTOFACIL_15 + "\n1,01,xx", "linha 2: valor 'xx' não é um número");
	}

	@Test
	void rejectsEmptyField() throws Exception {
		assertError("1,01,,02", "valor vazio");
	}

	@Test
	void rejectsEmptyBetsFile() throws Exception {
		Path file = file("\n# só comentário\n");
		InvalidFileException e = assertThrows(InvalidFileException.class, () -> parser.parseBets(file));
		assertTrue(e.getMessage().contains("nenhuma aposta"), e.getMessage());
	}

	@Test
	void rejectsDrawWithWrongSize() throws Exception {
		Path file = file("2900," + LOTOFACIL_15 + ",16");
		InvalidFileException e = assertThrows(InvalidFileException.class, () -> parser.parseDraws(file));
		assertTrue(e.getMessage().contains("resultado com 16 números"), e.getMessage());
	}

	@Test
	void rejectsDuplicatedContest() throws Exception {
		Path file = file("2900," + LOTOFACIL_15 + "\n2900," + LOTOFACIL_15);
		InvalidFileException e = assertThrows(InvalidFileException.class, () -> parser.parseDraws(file));
		assertTrue(e.getMessage().contains("concurso 2900 informado mais de uma vez"), e.getMessage());
	}

	@Test
	void validatesAccordingToModality() throws Exception {
		LotteryFileParser megaSena = new LotteryFileParser(Modality.MEGA_SENA);
		Path file = file("1,10,20,30,40,50,60");

		assertEquals(6, megaSena.parseBets(file).get(0).numbers().size());
	}

	private void assertError(String content, String expectedMessagePart) throws IOException {
		Path file = file(content);
		InvalidFileException e = assertThrows(InvalidFileException.class, () -> parser.parseBets(file));
		assertTrue(e.getMessage().contains(expectedMessagePart), e.getMessage());
	}
}
