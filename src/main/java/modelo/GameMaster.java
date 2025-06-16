/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.io.Serializable;
import java.util.Comparator;

/**
 *
 * @author euclasio
 */
public class GameMaster implements Serializable{

    private static boolean crossTurn;

    public static int checkBoard(Board board) {
    Comparator<Symbol> cmp = board.getCmp();
    Symbol[][] cells = board.getCells();

    // Usar una lista de combinaciones para iterar en lugar de tener múltiples if's
    int[][] positions = {
        {0, 0, 1, 0, 2, 0}, // Primera columna
        {0, 1, 1, 1, 2, 1}, // Segunda columna
        {0, 2, 1, 2, 2, 2}, // Tercera columna
        {0, 0, 0, 1, 0, 2}, // Fila superior
        {1, 0, 1, 1, 1, 2}, // Fila del medio
        {2, 0, 2, 1, 2, 2}, // Fila inferior
        {0, 0, 1, 1, 2, 2}, // Diagonal de arriba a abajo
        {2, 0, 1, 1, 0, 2}  // Diagonal de abajo a arriba
    };

    for (int[] pos : positions) {
        Symbol s1 = cells[pos[0]][pos[1]];
        Symbol s2 = cells[pos[2]][pos[3]];
        Symbol s3 = cells[pos[4]][pos[5]];

        if (cmp.compare(s1, s2) != -1 && cmp.compare(s2, s3) != -1 && cmp.compare(s1, s2) == cmp.compare(s2, s3)) {
            return cmp.compare(s1, s2);
        }
    }

    return -1;
}


    public static boolean isCrossTurn() {
        return crossTurn;
    }

    public static void setCrossTurn(boolean b) {
        crossTurn = b;
    }
}
