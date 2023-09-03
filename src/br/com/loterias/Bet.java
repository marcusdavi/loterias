package br.com.loterias;

import java.util.List;
import java.util.stream.Collectors;

public class Bet {
	private ChannelEnum channel;
	private List<Integer> numbers;
	private int matcheds;

	public Bet(ChannelEnum channel, List<Integer> numbers) {
		this.channel = channel;
		this.numbers = numbers;
		this.matcheds = 0;
	}

	public ChannelEnum getChannel() {
		return channel;
	}

	public void setChannel(ChannelEnum channel) {
		this.channel = channel;
	}

	public List<Integer> getNumbers() {
		return numbers;
	}

	public void setNumbers(List<Integer> numbers) {
		this.numbers = numbers;
	}

	public int getMatcheds() {
		return matcheds;
	}

	public void setMatcheds(int matcheds) {
		this.matcheds = matcheds;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("%02d",matcheds));
		sb.append(" acertos");
		sb.append(" --> ");
		sb.append(channel.name());
		sb.append(" --> ");
		sb.append(numbers.stream()
				.map(n -> String.format("%02d", n))
	            .collect(Collectors.joining(" - ")));
		return sb.toString();
	}

}
