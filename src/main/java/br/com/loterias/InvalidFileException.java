package br.com.loterias;

import java.nio.file.Path;

/**
 * Erro de conteúdo em um arquivo de entrada, com a indicação do arquivo e da linha.
 */
public class InvalidFileException extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidFileException(Path file, int lineNumber, String message) {
		super(file + ", linha " + lineNumber + ": " + message);
	}

	public InvalidFileException(Path file, String message) {
		super(file + ": " + message);
	}
}
