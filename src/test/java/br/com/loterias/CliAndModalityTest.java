package br.com.loterias;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class CliAndModalityTest {

	@TempDir
	Path dir;

	@Test
	void modalityAcceptsFlexibleNames() {
		assertEquals(Modality.LOTOFACIL, Modality.fromName("Lotofácil"));
		assertEquals(Modality.MEGA_SENA, Modality.fromName("mega-sena"));
		assertEquals(Modality.MEGA_SENA, Modality.fromName("MEGASENA"));
		assertEquals(Modality.QUINA, Modality.fromName("quina"));
		assertThrows(IllegalArgumentException.class, () -> Modality.fromName("timemania"));
	}

	@Test
	void parsesOptions() {
		CliOptions options = CliOptions.parse(new String[] { "-m", "quina", "--apostas", "a.txt", "-r", "r.txt" });

		assertEquals(Modality.QUINA, options.modality());
		assertEquals(Path.of("a.txt"), options.betsFile());
		assertEquals(Path.of("r.txt"), options.resultFile());
		assertEquals(Path.of("data", "result-processed.txt"), options.outputFile());
	}

	@Test
	void rejectsUnknownOrIncompleteOptions() {
		assertThrows(IllegalArgumentException.class, () -> CliOptions.parse(new String[] { "--x" }));
		assertThrows(IllegalArgumentException.class, () -> CliOptions.parse(new String[] { "-a" }));
	}

	@Test
	void runsEndToEnd() throws Exception {
		Path bets = dir.resolve("bets.txt");
		Path result = dir.resolve("result.txt");
		Path output = dir.resolve("out/report.txt");
		Files.writeString(bets, "1,01,02,03,04,05\n0,10,20,30,40,50\n");
		Files.writeString(result, "6000,01,02,03,40,50\n");

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayOutputStream err = new ByteArrayOutputStream();
		int code = LoteriaApp.run(
				new String[] { "-m", "quina", "-a", bets.toString(), "-r", result.toString(), "-s", output.toString() },
				new PrintStream(out, true, StandardCharsets.UTF_8), new PrintStream(err, true, StandardCharsets.UTF_8));

		assertEquals(LoteriaApp.EXIT_OK, code, err.toString(StandardCharsets.UTF_8));
		String report = Files.readString(output, StandardCharsets.UTF_8);
		assertTrue(report.contains("03 acertos --> INTERNET --> 01 - 02 - 03 - (04) - (05)  ★ PREMIADA"), report);
		assertTrue(report.contains("02 acertos --> LOTERICA"), report);
		assertTrue(report.contains("CONFERÊNCIA"), report);
	}

	@Test
	void reportsMissingFile() {
		ByteArrayOutputStream err = new ByteArrayOutputStream();
		int code = LoteriaApp.run(new String[] { "-a", dir.resolve("nao-existe.txt").toString() },
				new PrintStream(new ByteArrayOutputStream()), new PrintStream(err, true, StandardCharsets.UTF_8));

		assertEquals(LoteriaApp.EXIT_FILE_ERROR, code);
		assertTrue(err.toString(StandardCharsets.UTF_8).contains("Arquivo não encontrado"));
	}
}
