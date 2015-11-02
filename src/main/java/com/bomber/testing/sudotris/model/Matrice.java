package com.bomber.testing.sudotris.model;

import java.util.Arrays;

public class Matrice {
	private int[][] grid;
	private int size;

	public Matrice() {
		this(9);
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
