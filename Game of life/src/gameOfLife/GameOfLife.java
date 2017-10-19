//Helio Fernandez Abad
//Sergio Alonso Pascual
package gameOfLife;

import java.util.*;
import java.io.*;

public class GameOfLife {

	public static void main(String[] args) {
		Scanner teclado = new Scanner(System.in);
		System.out.println("Introduzca el nombre del fichero txt a usar: ");
		String fichero = teclado.nextLine();
		System.out.println("Introduzca el número de iteraciones deseadas: ");
		int iteraciones = teclado.nextInt();
		ArrayList<ArrayList<Boolean>> grid = leerFichero(fichero);
		ArrayList<int[]> updates = new ArrayList<int[]>();
		long inicio = System.currentTimeMillis(); // Esta variable contiene el tiempo que ha pasado, en milisegundos, desde el 1 de Enero de el año actual hasta el momento en el que se ejecuta esta misma línea.
		for (int x = 0; x < iteraciones; x++) {
			testBorders(grid);
			for (int i = 0; i < grid.size(); i++) {
				for (int j = 0; j < grid.get(i).size(); j++) {
					test(grid, updates, i, j);
				}
			}
			for (int i = 0; i < updates.size(); i++) {
				grid.get(updates.get(i)[0]).set(updates.get(i)[1], !grid.get(updates.get(i)[0]).get(updates.get(i)[1]));
			}
			updates.clear();
			reducirMatriz(grid);
		}
		long acaba = System.currentTimeMillis(); // Esta variable hace lo mismo que la variable inicio, solo que se ejecuta una vez acabamos las iteraciones.
		int nCeldas = contarCeldas(grid); // El objetivo de esta variable nCeldas es contar el número de celdas vivas en el "tablero" al final del programa.
		double tiempo = (acaba-inicio)/1000.0;
		System.out.println("\n1.Número de iteraciones: " + iteraciones + "\n2.Número de celdas vivas: " + nCeldas+ "\n3.Dimensiones: " + grid.size() + "x" + grid.get(0).size() + "\n4.Tiempo: " + tiempo+ " segundos.");
		System.out.println("Introduzca el nombre del fichero en el que desea almacenar el patrón: ");
		String nombre = teclado.nextLine();
		nombre = teclado.nextLine();
		teclado.close();
		EscribirFichero(grid, nombre);
	}

	public static ArrayList<ArrayList<Boolean>> leerFichero(String nombreFichero) {
		// Lee el fichero (el patrón) y crea el tablero.
		Scanner f = null;
		try {
			f = new Scanner(new FileReader(nombreFichero));
			int numfilas = f.nextInt();
			int numcols = f.nextInt();
			ArrayList<ArrayList<Boolean>> grid = new ArrayList<ArrayList<Boolean>>(numfilas);
			for (int i = 0; i < numfilas; i++) {
				grid.add(new ArrayList<Boolean>(numcols));
				String line = f.next();
				for (int j = 0; j < numcols; j++) {
					if (line.charAt(j) == 'X')
						grid.get(i).add(true);
					else
						grid.get(i).add(false);
				}
			}
			f.close();
			return grid;
		} catch (FileNotFoundException e) {
			throw new RuntimeException("No se ha encontrado el fichero");
		}
	}

	public static void test(ArrayList<ArrayList<Boolean>> grid, ArrayList<int[]> updates, int i, int j) {
		// Comprueba las celdas alrededor de la actual para determinar si debe cambiar
		// de estado en la siguiente iteración.
		int surrounding = 0;
		int i_init = i - 1;
		int j_init = j - 1;
		int i_end = i + 1;
		int j_end = j + 1;
		if (i == 0)
			i_init++;
		if (j == 0)
			j_init++;
		if (i == grid.size() - 1)
			i_end--;
		if (j == grid.get(i).size() - 1)
			j_end--;
		for (int x = i_init; x <= i_end; x++) {
			for (int y = j_init; y <= j_end; y++) {
				if (x != i || y != j) {
					if (grid.get(x).get(y))
						surrounding++;
				}
			}
		}
		if (grid.get(i).get(j)) {
			if (surrounding < 2 || surrounding > 3) {
				updates.add(new int[] { i, j });
			}
		} else {
			if (surrounding == 3) {
				updates.add(new int[] { i, j });
			}
		}
	}

	public static void testBorders(ArrayList<ArrayList<Boolean>> grid) {
		// Comprueba "los bordes del tablero" para ver si es necesario ampliar la matriz
		// y, si fuese necesario, también la ampliaría.
		int cont = 0;
		// test up
		for (int i = 0; i < grid.get(0).size(); i++) {
			if (grid.get(0).get(i))
				cont++;
			else
				cont = 0;
			
			if (cont >= 3) {
				grid.add(0, new ArrayList<Boolean>(grid.get(0).size()));
				for (int j = 0; j < grid.get(1).size(); j++) {
					grid.get(0).add(false);
				}
				break;
			}
		}
		// test down
		cont = 0;
		for (int i = 0; i < grid.get(0).size(); i++) {
			if (grid.get(grid.size() - 1).get(i))
				cont++;

			else
				cont = 0;

			if (cont >= 3) {
				grid.add(new ArrayList<Boolean>(grid.get(0).size()));
				for (int j = 0; j < grid.get(0).size(); j++) {
					grid.get(grid.size() - 1).add(false);
				}
				break;
			}
		}
		// test left
		cont = 0;
		for (int i = 0; i < grid.size(); i++) {
			if (grid.get(i).get(0))
				cont++;

			else
				cont = 0;

			if (cont >= 3) {
				for (int j = 0; j < grid.size(); j++) {
					grid.get(j).add(0, false);
				}
				break;
			}
		}
		// test right
		cont = 0;
		for (int i = 0; i < grid.size(); i++) {
			if (grid.get(i).get(grid.get(0).size() - 1))
				cont++;

			else
				cont = 0;

			if (cont >= 3) {
				for (int j = 0; j < grid.size(); j++) {
					grid.get(j).add(false);
				}
				break;
			}
		}
	}
	
	public static void reducirMatriz(ArrayList<ArrayList<Boolean>> grid) {// TO DO
		// Método encargado de que el tablero mostrado al finalizar la simulación sea el
		// "rectángulo mínimo", es decir, que no presente filas ni columnas en los
		// bordes compuestas por únicamente celdas muertas.
		boolean reduce = true;
		// test up
		while (reduce) {
			for (int i = 0; i < grid.get(0).size(); i++) {
				if (grid.get(0).get(i)) {
					reduce = false;
					break;
				}
			}
			if (reduce) {
				grid.remove(0);
			}
		}
		// test down
		reduce = true;
		while (reduce) {
			for (int i = 0; i < grid.get(0).size(); i++) {
				if (grid.get(grid.size() - 1).get(i)) {
					reduce = false;
					break;
				}
			}
			if (reduce) {
				grid.remove(grid.size() - 1);
			}
		}
		// test left
		reduce = true;
		while (reduce) {
			for (int i = 0; i < grid.size(); i++) {
				if (grid.get(i).get(0)) {
					reduce = false;
					break;
				}
			}
			if (reduce) {
				for (int i = 0; i < grid.size(); i++) {
					grid.get(i).remove(0);
				}
			}
		}

		// test right
		reduce = true;
		while (reduce) {
			for (int i = 0; i < grid.size(); i++) {
				if (grid.get(i).get(grid.get(0).size() - 1)) {
					reduce = false;
					break;
				}
			}
			if (reduce) {
				for (int i = 0; i < grid.size(); i++) {
					grid.get(i).remove(grid.get(i).size() - 1);
				}
			}
		}

	}

	public static void EscribirFichero(ArrayList<ArrayList<Boolean>> grid, String nombreFichero) {
		// Este método es el encargado de almacenar el patrón final (en el formato
		// indicado) en un fichero cuyo nombre se pide al usuario anteriormente.
		if (nombreFichero == null || nombreFichero.equals(""))
			mostrarMatriz(grid);
		else
			try {
				PrintWriter fichero = new PrintWriter(nombreFichero);
				fichero.println("Filas: " + grid.size());
				fichero.print("Columnas: " + grid.get(0).size());
				for (int i = 0; i < grid.size(); i++) {
					fichero.println();
					for (int j = 0; j < grid.get(i).size(); j++) {
						if (grid.get(i).get(j))
							fichero.print("X");
						else
							fichero.print(".");
					}
				}
				fichero.close();
			} catch (IOException e) {
				System.out.println(e);
				return;
			}
	}

	public static int contarCeldas(ArrayList<ArrayList<Boolean>> grid) {
		int nCeldas=0;
		for (int i = 0; i < grid.size(); i++) {
			for (int j = 0; j < grid.get(i).size(); j++) {
				if (grid.get(i).get(j))
					nCeldas++;
			}
		}
		return nCeldas;
	}

	public static void mostrarMatriz(ArrayList<ArrayList<Boolean>> grid) {
		for (int i = 0; i < grid.size(); i++) {
			System.out.println();
			for (int j = 0; j < grid.get(i).size(); j++) {
				if (grid.get(i).get(j))
					System.out.print("X");
				else
					System.out.print(".");
			}
		}
	}
}













