package com.minesweeper;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class MinesweeperGame extends Application {
    private static final int SIDE = 15;
    private static final int TILE_SIZE = 40;
    private static final String MINE = "\uD83D\uDCA3";
    private static final String FLAG = "\uD83D\uDEA9";

    private GameObject[][] gameField = new GameObject[SIDE][SIDE];
    private StackPane[][] tileGrid = new StackPane[SIDE][SIDE]; // Add this to store StackPanes
    private int countClosedTiles = SIDE * SIDE;
    private int countFlags;
    private int score;
    private int countMinesOnField;
    private boolean isGameStopped;

    @Override
    public void start(Stage primaryStage) {
        GridPane grid = new GridPane();
        createGame(grid);

        double sceneWidth = SIDE * TILE_SIZE;
        double sceneHeight = SIDE * TILE_SIZE;

        Scene scene = new Scene(grid, sceneWidth*1.04, sceneHeight*1.04); //make the screen equal to game size
        primaryStage.setScene(scene);
        primaryStage.setResizable(false); // make the screen nonresizable
        primaryStage.setTitle("Minesweeper");
        primaryStage.show();
    }

    private void createGame(GridPane grid) {
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                boolean isMine = Math.random() < 0.2;
                if (isMine) {
                    countMinesOnField++;
                }
                gameField[y][x] = new GameObject(x, y, isMine);

                StackPane tile = createTile(x, y);
                grid.add(tile, x, y);

                // Store the StackPane in the tileGrid array
                tileGrid[y][x] = tile;
            }
        }

        countMineNeighbors();
        countFlags = countMinesOnField;
    }

    private StackPane createTile(int x, int y) {
        Rectangle rectangle = new Rectangle(TILE_SIZE, TILE_SIZE);
        rectangle.setFill(Color.ORANGE);
        rectangle.setStroke(Color.BLACK);

        Text text = new Text();
        text.setFont(Font.font(20));
        text.setVisible(false); // Initially hidden

        StackPane stack = new StackPane(rectangle, text);

        stack.setOnMouseClicked(event -> {
            if (isGameStopped) {
                restart();
                return;
            }

            if (event.getButton() == MouseButton.PRIMARY) {
                openTile(x, y, rectangle, text);
            } else if (event.getButton() == MouseButton.SECONDARY) {
                markTile(x, y, rectangle, text);
            }
        });

        return stack;
    }

    private void openTile(int x, int y, Rectangle tile, Text text) {
        GameObject gameObject = gameField[y][x];

        if (gameObject.isOpen || gameObject.isFlag || isGameStopped) {
            return;
        }

        countClosedTiles--;
        gameObject.isOpen = true;
        tile.setFill(Color.GREEN);

        if (gameObject.isMine) {
            tile.setFill(Color.RED);
            text.setText(MINE);
            text.setVisible(true);
            gameOver();
            return;
        }

        this.score += 5;

        if (countClosedTiles == countMinesOnField) {
            win();
            return;
        }

        if (gameObject.countMineNeighbors > 0) {
            text.setText(String.valueOf(gameObject.countMineNeighbors));
            text.setVisible(true);
        } else {
            text.setVisible(false); // No number displayed for 0 neighbors
            openNeighbors(gameObject);
        }
    }

    private void openNeighbors(GameObject gameObject) {
        List<GameObject> neighbors = getNeighbors(gameObject);
        for (GameObject neighbor : neighbors) {
            if (!neighbor.isOpen && !neighbor.isMine && !neighbor.isFlag) {
                // Get the corresponding StackPane from the tileGrid array
                StackPane neighborTile = tileGrid[neighbor.y][neighbor.x];

                // Retrieve the Rectangle and Text from the StackPane's children
                Rectangle neighborRect = (Rectangle) neighborTile.getChildren().get(0);
                Text neighborText = (Text) neighborTile.getChildren().get(1);

                openTile(neighbor.x, neighbor.y, neighborRect, neighborText);
            }
        }
    }

    private void markTile(int x, int y, Rectangle tile, Text text) {
        GameObject gameObject = gameField[y][x];
    
        // Prevent flagging if the tile is open, the game is stopped, or no flags are left
        if (gameObject.isOpen || isGameStopped || (countFlags <= 0 && !gameObject.isFlag)) {
            return;
        }
    
        if (gameObject.isFlag) {
            // Unmark the flag if it's already set
            countFlags++;
            gameObject.isFlag = false;
            tile.setFill(Color.ORANGE);  // Reset the tile color
            text.setText("");            // Clear the flag symbol
            text.setVisible(false);      // Hide the text
        } else {
            // Mark the flag
            countFlags--;
            gameObject.isFlag = true;
            tile.setFill(Color.YELLOW);  // Change the tile color to indicate flag
            text.setText(FLAG);          // Display the flag symbol
            text.setVisible(true);       // Make the flag visible
        }
    }

    private void countMineNeighbors() {
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                GameObject gameObject = gameField[y][x];
                if (!gameObject.isMine) {
                    gameObject.countMineNeighbors = (int) getNeighbors(gameObject).stream()
                            .filter(neighbor -> neighbor.isMine).count();
                }
            }
        }
    }

    private List<GameObject> getNeighbors(GameObject gameObject) {
        List<GameObject> result = new ArrayList<>();
        for (int y = gameObject.y - 1; y <= gameObject.y + 1; y++) {
            for (int x = gameObject.x - 1; x <= gameObject.x + 1; x++) {
                if (x >= 0 && x < SIDE && y >= 0 && y < SIDE && !(x == gameObject.x && y == gameObject.y)) {
                    result.add(gameField[y][x]);
                }
            }
        }
        return result;
    }

    private void gameOver() {
        isGameStopped = true;
        showMessage("Game Over", "You Lost!");
    }

    private void win() {
        isGameStopped = true;
        showMessage("You Win", "Congratulations!");
    }

    private void showMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void restart() {

        countClosedTiles = SIDE * SIDE;
        score = 0;
        countMinesOnField = 0;
        countFlags = 0;
        isGameStopped = false;

        // Reset the game field and tile grid
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                // Reset the GameObject for the field
                gameField[y][x] = new GameObject(x, y, Math.random() < 0.1); // Randomly assign mines
                if (gameField[y][x].isMine) {
                    countMinesOnField++;
                }

                // Reset the tile's UI (Rectangle and Text)
                StackPane tile = tileGrid[y][x];
                Rectangle rectangle = (Rectangle) tile.getChildren().get(0);
                Text text = (Text) tile.getChildren().get(1);

                // Reset tile appearance
                rectangle.setFill(Color.ORANGE);
                text.setText("");
                text.setVisible(false);

                // Reset flags and open status
                gameField[y][x].isFlag = false;
                gameField[y][x].isOpen = false;
            }
        }

        countMineNeighbors(); // Recalculate mine neighbors
    }

    public static void main(String[] args) {
        launch(args);
    }
}