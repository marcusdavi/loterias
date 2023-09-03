package br.com.loterias;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class LoteriaApp {

	private static final String PATH = System.getProperty("user.dir") + "\\src\\";

	public static void main(String[] args) {
		try {
			String betsFile = PATH + "bets.txt";
			String resultFile = PATH + "result.txt";
			String resultProcessed = PATH + "result-processed.txt";

			List<Bet> bets = buildBets(betsFile);
			Result result = buildResult(resultFile);
			
			processResult(result, bets);
			showResults(result, bets);
			createfileResults(result, bets, resultProcessed);
			
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Error Processing File.");
		}
	}

	private static void createfileResults(Result result, List<Bet> bets, String resultProcessedPath)
			throws IOException {

		try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(resultProcessedPath))) {
			writeLine(bufferedWriter, "RESULTADO");
			writeLine(bufferedWriter, result.toString());
			writeLine(bufferedWriter, "CONFERÊNCIA");
			bets.forEach(b -> {
				writeLine(bufferedWriter, b.toString());
			});
		}
	}

	private static void writeLine(BufferedWriter bufferedWriter, String content) {

		try {
			bufferedWriter.write(content);
			bufferedWriter.newLine();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	private static void processResult(Result result, List<Bet> bets) {
		bets.forEach(b -> {
			b.setMatcheds((int) b.getNumbers().stream().filter(n -> result.getNumbers().contains(n)).count());
		});
		bets.sort(Comparator.comparing(Bet::getMatcheds).reversed());
	}

	private static void showResults(Result result, List<Bet> bets) {
		System.out.println("RESULTADO");
		System.out.println(result);
		System.out.println("\nCONFERÊNCIA");
		bets.forEach(System.out::println);
	}

	private static Result buildResult(String filePath) throws FileNotFoundException, IOException {
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
			Result result = null;
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				String[] partsLine = line.split(",");

				List<Integer> numbers = getNumbersFile(partsLine);
				result = new Result(Integer.valueOf(partsLine[0]), numbers);
			}
			return result;
		}

	}

	private static List<Bet> buildBets(String filePath) throws FileNotFoundException, IOException {

		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
			List<Bet> bets = new ArrayList<>();
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				String[] partsLine = line.split(",");

				ChannelEnum channel = ChannelEnum.values()[Integer.valueOf(partsLine[0])];
				List<Integer> numbers = getNumbersFile(partsLine);
				bets.add(new Bet(channel, numbers));
			}
			return bets;

		}

	}

	private static List<Integer> getNumbersFile(String[] partsLine) {
		List<Integer> numbers = new ArrayList<>();

		for (int i = 1; i < partsLine.length; i++) {
			int number = Integer.parseInt(partsLine[i]);
			numbers.add(number);
		}

		return numbers;
	}

}
