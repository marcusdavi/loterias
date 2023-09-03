package br.com.loterias;

import java.util.List;
import java.util.stream.Collectors;

public class Result {
	private int id;
	private List<Integer> numbers;

	public Result(Integer id, List<Integer> numbers) {
		this.id = id;
		this.numbers = numbers;
	}

	public Result() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<Integer> getNumbers() {
		return numbers;
	}

	public void setNumbers(List<Integer> numbers) {
		this.numbers = numbers;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Concurso: ");
		sb.append(id);
		sb.append(" --> ");
		sb.append(numbers.stream()
				.map(n -> String.format("%02d", n))
	            .collect(Collectors.joining(" - ")));
		return sb.toString();
	}

}
