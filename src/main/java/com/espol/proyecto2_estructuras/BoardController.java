/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.espol.proyecto2_estructuras;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Stack;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import modelo.Board;
import modelo.Circle;
import modelo.Cross;
import modelo.GameMaster;
import modelo.MinMaxer;
import modelo.Symbol;
import util.Memory;

/**
 * FXML Controller class
 *
 * @author euclasio
 */
public class BoardController implements Initializable {

    @FXML
    private Button button1;

    @FXML
    private Button button2;

    @FXML
    private Button button3;

    @FXML
    private Button button4;

    @FXML
    private Button button5;

    @FXML
    private Button button6;

    @FXML
    private Button button7;

    @FXML
    private Button button8;

    @FXML
    private Button button9;

    @FXML
    private FlowPane tableroVisible;

    @FXML
    private GridPane intermediateBoards;

    private static Board tableroActual;

    private Button[][] grid;

    private Symbol cross;

    private Symbol circle;

    private Class computer;

    private Class human;

    private static boolean hardMode;

    private static boolean player1First;

    private static boolean startedAsCross;

    private static boolean againstComputer;

    private static boolean autoplay;

    private static boolean loaded;

    private String STYLE = "-fx-background-color: #3498db; -fx-text-fill: white;";

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (tableroActual == null) {
            tableroActual = new Board();
        } else {
            loaded = true;
            GameMaster.setCrossTurn(tableroActual.isCrossTurnWhenSaved());
        }
        if (GameMaster.isCrossTurn()) {
            human = Cross.class;
            computer = Circle.class;
        } else {
            human = Circle.class;
            computer = Cross.class;
        }
        grid = new Button[3][3];
        cross = new Cross();
        circle = new Circle();

        setButtonPos(button1, 0, 0);
        setButtonPos(button2, 1, 0);
        setButtonPos(button3, 2, 0);
        setButtonPos(button4, 0, 1);
        setButtonPos(button5, 1, 1);
        setButtonPos(button6, 2, 1);
        setButtonPos(button7, 0, 2);
        setButtonPos(button8, 1, 2);
        setButtonPos(button9, 2, 2);
        if (loaded) {
            showBoard();
            if (!startedAsCross && GameMaster.isCrossTurn()) {
                startedAsCross = !startedAsCross;
            }
        }
        if (againstComputer && !loaded && !player1First) {
            Class temp = human;
            human = computer;
            computer = temp;
            computerMove();
        }
        if (autoplay) {
            computer2Move();
        }
    }

    @FXML
    private void switchToMenu() throws IOException {
        hardMode = false;
        autoplay = false;
        againstComputer = false;
        loaded = false;
        startedAsCross = false;
        tableroActual = null;
        App.setRoot("menu");
    }

    public void showBoard() {
        Symbol[][] cells = tableroActual.getCells();
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells.length; j++) {
                if (cells[i][j] != null) {
                    Symbol s = cells[i][j];
                    if (s instanceof Circle) {
                        Circle c = (Circle) s;
                        setSymbol(c, i, j);
                    } else if (s instanceof Cross) {
                        Cross c = (Cross) s;
                        setSymbol(c, i, j);
                    }
                }
            }
        }
    }

    public void setButtonPos(Button b, int x, int y) {
        grid[x][y] = b;
        b.setOnAction(ev -> {
            tableroActual.nextTurn();
            if (tableroActual.getCells()[x][y] != null) {
                return;
            }
            Symbol symbol = cross;
            if (!GameMaster.isCrossTurn()) {
                symbol = circle;
            }
            tableroActual.getCells()[x][y] = symbol;
            GameMaster.setCrossTurn(!GameMaster.isCrossTurn());
            showBoard();
            int i = checkGame();
            if (againstComputer && i == -1) {
                computerMove();
            }
        });
    }

    private int checkGame() {
        int winner = GameMaster.checkBoard(tableroActual);
        switch (winner) {
            case 1:
                endGame("Ganador: CIRCULO");
                return 1;
            case 2:
                endGame("Ganador: CRUZ");
                return 2;
            default:
                break;
        }
        if (tableroActual.getTurnsLeft() == 0) {
            endGame("Empate");
            return 0;
        }
        return -1;
    }

    public void endGame(String outcome) {
        Alert informationAlert = new Alert(AlertType.INFORMATION);
        informationAlert.setTitle("Fin del juego");
        informationAlert.setHeaderText(outcome);
        informationAlert.showAndWait();
        try {
            switchToMenu();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void setSymbol(Symbol c, int i, int j) {
        try {
            //dependiendo la version, el "src/" es necesario o no
            //si no compila, eliminarlo
            FileInputStream input = new FileInputStream("src/" + c.getPath());
            Image imagen = new Image(input);
            ImageView imageView = new ImageView(imagen);
            imageView.setFitWidth(80);
            imageView.setFitHeight(80);
            grid[i][j].setGraphic(imageView);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    public void saveGame() {
        TextInputDialog dialog = new TextInputDialog("Guardar tablero actual");
        dialog.setTitle("Guardar");
        dialog.setHeaderText("Ingrese el nombre de la partida a guardar:");
        Optional<String> result = dialog.showAndWait();
        String text = result.get();
        Board copy = tableroActual.getCopy();
        copy.setDescription(text);
        copy.setCrossTurnWhenSaved(GameMaster.isCrossTurn());
        Memory.addBoard(copy);
        Memory.save();
    }

    public static void setTableroActual(Board b) {
        tableroActual = b;
    }

    private void computerMove() {
        showBoard();
        Symbol symbol = cross;
        if (!GameMaster.isCrossTurn()) {
            symbol = circle;
        }

        int[] play = MinMaxer.minmax(tableroActual, human, computer, hardMode);
        intermediateBoards.getChildren().clear();
        Stack<Board> intermediates = MinMaxer.getIntermediateBoards();
        for (int i = 0; i < intermediateBoards.getColumnCount(); i++) {
            for (int j = 0; j < intermediateBoards.getColumnCount(); j++) {
                if (!intermediates.isEmpty()) {
                    Board b = intermediates.pop();
                    intermediateBoards.add(new Label(b.toGrid()), i, j);
                }
            }
        }
        tableroActual.getCells()[play[0]][play[1]] = symbol;
        tableroActual.nextTurn();
        GameMaster.setCrossTurn(!GameMaster.isCrossTurn());
        showBoard();
        int i = checkGame();
        if (autoplay && i == -1) {
            computer2Move();
        }
    }

    private void computer2Move() {
        Symbol symbol = cross;
        if (!GameMaster.isCrossTurn()) {
            symbol = circle;
        }
        int[] play = MinMaxer.minmax(tableroActual, computer, human, hardMode);
        tableroActual.getCells()[play[0]][play[1]] = symbol;
        tableroActual.nextTurn();
        GameMaster.setCrossTurn(!GameMaster.isCrossTurn());
        showBoard();
        int i = checkGame();
        if (autoplay && i == -1) {
            computerMove();
        }
    }

    @FXML
    private void giveTip() {
        if (againstComputer) {
            int[] play = MinMaxer.minmax(tableroActual, computer, human, hardMode);
            grid[play[0]][play[1]].setStyle(STYLE);
        } else {
            boolean current = GameMaster.isCrossTurn();
            if (startedAsCross) {
                if (current) {
                    int[] reply = MinMaxer.minmax(tableroActual, computer, human, hardMode);
                    grid[reply[0]][reply[1]].setStyle(STYLE);
                    current = !current;
                } else {
                    int[] reply = MinMaxer.minmax(tableroActual, human, computer, hardMode);
                    grid[reply[0]][reply[1]].setStyle(STYLE);
                    current = !current;
                }
            } else {
                if (!current) {
                    int[] reply = MinMaxer.minmax(tableroActual, computer, human, hardMode);
                    grid[reply[0]][reply[1]].setStyle(STYLE);
                    current = !current;
                } else {
                    int[] reply = MinMaxer.minmax(tableroActual, human, computer, hardMode);
                    grid[reply[0]][reply[1]].setStyle(STYLE);
                    current = !current;
                }
            }
        }
    }

    public static void setHardMode(boolean hardMode) {
        BoardController.hardMode = hardMode;
    }

    static boolean isAgainstComputer() {
        return againstComputer;
    }

    public static void setPlayer1First(boolean player1First) {
        BoardController.player1First = player1First;
    }

    public static void setStartedAsCross(boolean started) {
        BoardController.startedAsCross = started;
    }

    public static void setAgainstComputer(boolean againstComputer) {
        BoardController.againstComputer = againstComputer;
    }

    public static void setAutoplay(boolean autoplay) {
        BoardController.autoplay = autoplay;
    }
}
