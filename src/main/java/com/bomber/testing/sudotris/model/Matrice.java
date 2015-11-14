package com.bomber.testing.sudotris.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Matrice {
	private static final int DEFAULT_SIZE = 9;

	private int[][] grid;
	private int size;

	public static Matrice Random() {
		return Matrice.Random(DEFAULT_SIZE);
	}

	public static Matrice Random(int size) {
		Matrice matrice = new Matrice(size);
		matrice = generateSolution(matrice);
		matrice = generateGame(matrice);

		return matrice;
	}

	private static Matrice generateSolution(Matrice matrice) {
		return generateSolution(matrice, 0);
	}

	private static Matrice generateSolution(Matrice game, int index) {
		if (index > game.size * game.size - 1)
			return game;

		int x = index % game.size;
		int y = index / game.size;

		List<Integer> numbers = new ArrayList<Integer>();
		for (int i = 1; i <= game.size; i++)
			numbers.add(i);
		Collections.shuffle(numbers);

		while (numbers.size() > 0) {
			int number = getNextPossibleNumber(game, x, y, numbers);
			if (number == -1)
				return null;

			game.grid[y][x] = number;
			Matrice tmpGame = generateSolution(game, index + 1);
			if (tmpGame != null)
				return tmpGame;
			game.grid[y][x] = 0;
		}

		return null;
	}

	private static int getNextPossibleNumber(Matrice game, int column, int line, List<Integer> numbers) {
		while (numbers.size() > 0) {
			int number = numbers.remove(0);
			if (game.checkRow(line, number) && game.checkCol(column, number) && game.checkBox(line, column, number))
				return number;
		}
		return -1;
	}

	private static Matrice generateGame(Matrice game) {
		List<Integer> positions = new ArrayList<Integer>();
		for (int i = 0; i < game.size * game.size; i++)
			positions.add(i);
		Collections.shuffle(positions);
		positions = positions.subList(0, (int) (Math.random() * 1000 % (game.size * game.size) + game.size));
		return generateGame(game, positions);
	}

	private static Matrice generateGame(Matrice game, List<Integer> positions) {
		while (positions.size() > 0) {
			int position = positions.remove(0);
			int column = position % game.size;
			int line = position / game.size;
			int temp = game.grid[line][column];
			game.grid[line][column] = 0;

			if (!isValid(game))
				game.grid[line][column] = temp;
		}

		return game;
	}

	private static boolean isValid(Matrice game) {
		return isValid(game, 0, new int[] { 0 });
	}

	private static boolean isValid(Matrice game, int index, int[] numberOfSolutions) {
		if (index > game.size * game.size - 1)
			return ++numberOfSolutions[0] == 1;

		int column = index % game.size;
		int line = index / game.size;

		if (game.grid[line][column] == 0) {
			List<Integer> numbers = new ArrayList<Integer>();
			for (int i = 1; i <= 9; i++)
				numbers.add(i);

			while (numbers.size() > 0) {
				int number = getNextPossibleNumber(game, column, line, numbers);
				if (number == -1)
					break;
				game.grid[line][column] = number;

				if (!isValid(game, index + 1, numberOfSolutions)) {
					game.grid[line][column] = 0;
					return false;
				}
				game.grid[line][column] = 0;
			}
		} else if (!isValid(game, index + 1, numberOfSolutions))
			return false;

		return true;
	}

	public Matrice() {
		this(DEFAULT_SIZE);
	}

	public Matrice(int size) {
		this.size = size;
		this.grid = new int[size][size];

		for (int i = 0; i < grid.length; i++) {
			int[] line = grid[i];
			for (int j = 0; j < line.length; j++) {
				grid[i][j] = 0;
			}
		}
	}

	public Matrice(Matrice original) {
		this.size = original.size;
		this.grid = new int[size][size];

		for (int i = 0; i < grid.length; i++) {
			int[] line = grid[i];
			for (int j = 0; j < line.length; j++) {
				this.grid[i][j] = original.getCell(i, j);
			}
		}
	}

	public int getCell(int line, int column) {
		if (line < 0 || line >= grid.length) {
			throw new IndexOutOfBoundsException(
					"La ligne '" + line + "' est en dehors des limites de la grille (0 - " + (grid.length - 1) + ")");
		}

		if (column < 0 || column >= grid[line].length) {
			throw new IndexOutOfBoundsException("La colone '" + column
					+ "' est en dehors des limites de la grille (0 - " + (grid[line].length - 1) + ")");
		}

		return grid[line][column];
	}

	public void setCell(int line, int column, int value) {
		if (line < 0 || line >= grid.length) {
			throw new IndexOutOfBoundsException(
					"La ligne '" + line + "' est en dehors des limites de la grille (0 - " + (grid.length - 1) + ")");
		}

		if (column < 0 || column >= grid[line].length) {
			throw new IndexOutOfBoundsException("La colone '" + column
					+ "' est en dehors des limites de la grille (0 - " + (grid[line].length - 1) + ")");
		}

		grid[line][column] = value;
	}

	public int[] getLine(int line) {
		if (line < 0 || line >= grid.length) {
			throw new IndexOutOfBoundsException(
					"La ligne '" + line + "' est en dehors des limites de la grille (0 - " + (grid.length - 1) + ")");
		}

		return grid[line];
	}

	public void setLine(int line, int[] values) {
		if (line < 0 || line >= grid.length) {
			throw new IndexOutOfBoundsException(
					"La ligne '" + line + "' est en dehors des limites de la grille (0 - " + (grid.length - 1) + ")");
		}

		if (values == null) {
			throw new NullPointerException("Les valeurs à définir pour la ligne '" + line + "' sont null");
		}

		if (values.length != grid[line].length) {
			throw new ArrayStoreException("Les valeurs à définir pour la ligne '" + line
					+ "' ne sont pas de la même taille que la grille (" + (grid[line].length - 1) + ")");
		}

		grid[line] = values;
	}

	public int getSize() {
		return size;
	}

	public void solve() {
		solve(0);
	}

	public void solve(long speed) {
		try {
			solve(0, 0, speed);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	private void solve(int row, int col, long speed) throws Exception {
		// Throw an exception to stop the process if the puzzle is solved
		if (row > size - 1)
			throw new Exception("Solution found");

		// If the cell is not empty, continue with the next cell
		if (this.getCell(row, col) != 0)
			next(row, col, speed);
		else {
			// Find a valid number for the empty cell
			for (int num = 1; num < 10; num++) {
				if (checkRow(row, num) && checkCol(col, num) && checkBox(row, col, num)) {
					this.setCell(row, col, num);
					// updateView();

					// Let the observer see it
					Thread.sleep(speed);

					// Delegate work on the next cell to a recursive call
					next(row, col, speed);
				}
			}

			// No valid number was found, clean up and return to caller
			this.setCell(row, col, 0);
			// updateView();
		}
	}

	private void next(int row, int col, long speed) throws Exception {
		if (col < size - 1)
			solve(row, col + 1, speed);
		else
			solve(row + 1, 0, speed);
	}

	protected boolean checkRow(int row, int num) {
		for (int col = 0; col < size; col++)
			if (this.getCell(row, col) == num)
				return false;

		return true;
	}

	/** Checks if num is an acceptable value for the given column */
	protected boolean checkCol(int col, int num) {
		for (int row = 0; row < size; row++)
			if (this.getCell(row, col) == num)
				return false;

		return true;
	}

	/** Checks if num is an acceptable value for the box around row and col */
	protected boolean checkBox(int row, int col, int num) {
		row = (row / 3) * 3;
		col = (col / 3) * 3;

		for (int r = 0; r < 3; r++)
			for (int c = 0; c < 3; c++)
				if (this.getCell(row + r, col + c) == num)
					return false;

		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.deepHashCode(grid);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Matrice other = (Matrice) obj;
		if (!Arrays.deepEquals(grid, other.grid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		String string = "";

		for (int i = 0; i < grid.length; i++) {
			int[] line = grid[i];
			for (int j = 0; j < line.length; j++) {
				string += grid[i][j] + " ";
			}
			string += i < grid.length - 1 ? "\n" : "";
		}

		return string;
	}

}
