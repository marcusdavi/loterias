package br.com.loterias;

import java.util.Arrays;

/**
 * Canal onde a aposta foi feita. O código é o valor usado na primeira coluna do arquivo de apostas.
 */
public enum Channel {

	LOTERICA(0),
	INTERNET(1);

	private final int code;

	Channel(int code) {
		this.code = code;
	}

	public int code() {
		return code;
	}

	public static Channel fromCode(int code) {
		return Arrays.stream(values())
				.filter(c -> c.code == code)
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException(
						"canal inválido: " + code + " (use 0 para lotérica ou 1 para internet)"));
	}
}
