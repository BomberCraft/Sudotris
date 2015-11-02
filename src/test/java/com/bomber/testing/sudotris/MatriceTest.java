package com.bomber.testing.sudotris;

import org.junit.Test;

import com.bomber.testing.sudotris.model.Matrice;

public class MatriceTest {

	@Test
	public void test() {
		Matrice matrice1 = new Matrice();
		System.out.println("----- matrice1 -----");
		System.out.println(matrice1.toString());

		System.out.println();
		Matrice matrice2 = new Matrice();
		System.out.println("----- matrice2 -----");
		System.out.println(matrice2.toString());

		System.out.println();
		System.out.print("matrice1 == matrice2: ");
		System.out.println(matrice1.equals(matrice2));

		matrice1.setLine(0, new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 });
		matrice1.setLine(1, new int[] { 2, 1, 2, 3, 4, 5, 6, 7, 8 });
		matrice1.setLine(2, new int[] { 3, 2, 1, 2, 3, 4, 5, 6, 7 });
		matrice1.setLine(3, new int[] { 4, 3, 2, 1, 2, 3, 4, 5, 6 });
		matrice1.setLine(4, new int[] { 5, 4, 3, 2, 1, 2, 3, 4, 5 });
		matrice1.setLine(5, new int[] { 6, 5, 4, 3, 2, 1, 2, 3, 4 });
		matrice1.setLine(6, new int[] { 7, 6, 5, 4, 3, 5, 1, 2, 3 });
		matrice1.setLine(7, new int[] { 8, 7, 6, 5, 4, 3, 6, 1, 2 });
		matrice1.setLine(8, new int[] { 9, 8, 7, 6, 5, 4, 3, 2, 1 });

		System.out.println();
		System.out.println("----- matrice1 -----");
		System.out.println(matrice1.toString());
		System.out.print("matrice1 == matrice2: ");
		System.out.println(matrice1.equals(matrice2));

		matrice2.setLine(0, new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 });
		matrice2.setLine(1, new int[] { 2, 1, 2, 3, 4, 5, 6, 7, 8 });
		matrice2.setLine(2, new int[] { 3, 2, 1, 2, 3, 4, 5, 6, 7 });
		matrice2.setLine(3, new int[] { 4, 3, 2, 1, 2, 3, 4, 5, 6 });
		matrice2.setLine(4, new int[] { 5, 4, 3, 2, 1, 2, 3, 4, 5 });
		matrice2.setLine(5, new int[] { 6, 5, 4, 3, 2, 1, 2, 3, 4 });
		matrice2.setLine(6, new int[] { 7, 6, 5, 4, 3, 5, 1, 2, 3 });
		matrice2.setLine(7, new int[] { 8, 7, 6, 5, 4, 3, 6, 1, 2 });
		matrice2.setLine(8, new int[] { 9, 8, 7, 6, 5, 4, 3, 2, 1 });

		System.out.println();
		System.out.println("----- matrice2 -----");
		System.out.println(matrice2.toString());
		System.out.print("matrice1 == matrice2: ");
		System.out.println(matrice1.equals(matrice2));
	}

}
