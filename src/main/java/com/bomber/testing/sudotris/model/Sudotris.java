package com.bomber.testing.sudotris.model;

import java.util.ArrayList;
import java.util.List;

public class Sudotris {

	private Matrice original;
	private Matrice current;
	private Matrice finished;

	public Sudotris(Matrice original, Matrice finished) {
		this.original = original;
		this.current = new Matrice(original);
		this.finished = finished;

		System.out.println("Original:\n" + original);
		System.out.println("Current:\n" + current);
		System.out.println("Finished:\n" + finished);
	}

	public Sudotris(int gridSize) {
		this.finished = new Matrice(gridSize);

		for (int i = 0; i < finished.getSize(); i++) {
			for (int j = 0; j < finished.getSize(); j++) {
				finished.setCell(i, j, (int) (Math.random() * 1000 % gridSize + 1));
			}
		}

		this.original = new Matrice(finished);

		int nbToClear = (int) (Math.random() * 1000 % (gridSize * gridSize - gridSize) + gridSize);
		for (int i = 0; i < nbToClear; i++) {
			int line;
			int column;
			do {
				line = (int) (Math.random() * 1000 % (gridSize));
				column = (int) (Math.random() * 1000 % (gridSize));
			} while (original.getCell(line, column) == 0);

			original.setCell(line, column, 0);
		}

		this.current = new Matrice(original);
	}

	public boolean isCellOriginal(int line, int column) {
		return original.getCell(line, column) != 0;
	}

	public boolean isCellCorrect(int line, int column) {
		return current.getCell(line, column) == finished.getCell(line, column);
	}

	public boolean isMoveValid(int line, int column, int value) {
		return !isCellOriginal(line, column) && current.getCell(line, column) != 0
				&& finished.getCell(line, column) == value;
	}

	public int getCell(int line, int column) {
		return current.getCell(line, column);
	}

	public void setCell(int line, int column, int value) {
		current.setCell(line, column, value);
	}

	public Integer[] getAllNumbersToPlace() {
		List<Integer> numbers = new ArrayList<>();

		for (int i = 0; i < current.getSize(); i++) {
			for (int j = 0; j < current.getSize(); j++) {
				if (current.getCell(i, j) == 0) {
					numbers.add(finished.getCell(i, j));
				}
			}
		}

		return numbers.toArray(new Integer[0]);
	}
}
