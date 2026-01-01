package ru.vsu.cs.plotnikov_m.task2.view;

import ru.vsu.cs.plotnikov_m.task2.GameConstants.GameMode;
import ru.vsu.cs.plotnikov_m.task2.GameConstants.PlayerColor;
import ru.vsu.cs.plotnikov_m.task2.controller.GameController;
import ru.vsu.cs.plotnikov_m.task2.utils.GameTimer;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

/**
 * Класс для интерфейса игры.
 */
public class GameUI {
    private final Stage primaryStage;
    private GameController gameController;
    private HexBoardView boardView;
    private GameTimer gameTimer;

    private Label statusLabel;
    private Label timerLabel;
    private Label playerLabel;

    public GameUI(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * Инициализирует графический интерфейс игры.
     */
    public void initialize(GameMode gameMode, PlayerColor playerColor) {

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f5f5;");

        boardView = new HexBoardView();
        root.setCenter(boardView.getBoardPane());

        VBox statusPanel = createStatusPanel();
        root.setTop(statusPanel);

        VBox controlPanel = createControlPanel();
        root.setLeft(controlPanel);

        gameController = new GameController(boardView, this);
        boardView.setController(gameController);

        gameController.initializeGame(gameMode, playerColor);

        Scene scene = new Scene(root, 1200, 800);

        String title = "Шестиугольные шахматы Глинского";
        if (gameMode == GameMode.NON_INTERACTIVE) {
            title += " [РЕЖИМ НАБЛЮДАТЕЛЯ]";
        }

        primaryStage.setTitle(title);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.show();

        boardView.updateSize(scene.getWidth() - 240, scene.getHeight() - 120);

        startGameTimer();
    }

    /**
     * Создает верхнюю панель статуса игры.
     */
    private VBox createStatusPanel() {
        VBox statusPanel = new VBox(15);
        statusPanel.setPadding(new Insets(15, 20, 15, 20));
        statusPanel.setStyle("-fx-background-color: linear-gradient(to right, #667eea 0%, #764ba2 100%); " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 2);");

        Label titleLabel = new Label("♟ ШЕСТИУГОЛЬНЫЕ ШАХМАТЫ ГЛИНСКОГО ♟");
        titleLabel.setFont(Font.font("Arial", 22));
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 3, 0, 0, 1);");

        HBox infoPanel = new HBox(30);
        infoPanel.setAlignment(Pos.CENTER_LEFT);
        infoPanel.setPadding(new Insets(5, 0, 0, 0));

        statusLabel = new Label("Статус: Ожидание начала игры");
        statusLabel.setFont(Font.font("Arial", 13));
        statusLabel.setStyle("-fx-text-fill: white; -fx-background-color: rgba(255,255,255,0.2); " +
                "-fx-background-radius: 5; -fx-padding: 5 10 5 10;");

        timerLabel = new Label("⏱ Время: 05:00");
        timerLabel.setFont(Font.font("Arial", 13));
        timerLabel.setStyle("-fx-text-fill: white; -fx-background-color: rgba(255,255,255,0.2); " +
                "-fx-background-radius: 5; -fx-padding: 5 10 5 10;");

        playerLabel = new Label("👤 Игрок: Белые");
        playerLabel.setFont(Font.font("Arial", 13));
        playerLabel.setStyle("-fx-text-fill: white; -fx-background-color: rgba(255,255,255,0.2); " +
                "-fx-background-radius: 5; -fx-padding: 5 10 5 10;");

        infoPanel.getChildren().addAll(statusLabel, timerLabel, playerLabel);
        statusPanel.getChildren().addAll(titleLabel, infoPanel);
        return statusPanel;
    }

    /**
     * Создает левую панель управления игрой.
     * Содержит кнопки для управления игровым процессом.
     */
    private VBox createControlPanel() {
        VBox controlPanel = new VBox(20);
        controlPanel.setPadding(new Insets(25));
        controlPanel.setPrefWidth(220);
        controlPanel.setStyle("-fx-background-color: #ffffff; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 0); " +
                "-fx-border-color: #e0e0e0; -fx-border-width: 0 2 0 0;");

        Label controlTitle = new Label("Управление");
        controlTitle.setFont(Font.font("Arial", 16));
        controlTitle.setStyle("-fx-text-fill: #333333; -fx-font-weight: bold;");
        controlTitle.setPadding(new Insets(0, 0, 10, 0));

        Button newGameButton = createStyledButton("🔄 Новая игра", "#667eea", "#5568d3");
        newGameButton.setOnAction(e -> gameController.startNewGame());

        Button resignButton = createStyledButton("🏳 Сдаться", "#e74c3c", "#c0392b");
        resignButton.setOnAction(e -> gameController.resign());

        controlPanel.getChildren().addAll(
                controlTitle,
                newGameButton,
                resignButton
        );

        return controlPanel;
    }

    /**
     * Создает стилизованную кнопку с единым оформлением.
     */
    private Button createStyledButton(String text, String normalColor, String hoverColor) {
        Button button = new Button(text);
        button.setPrefWidth(170);
        button.setPrefHeight(45);
        button.setFont(Font.font("Arial", 13));
        button.setStyle("-fx-background-color: " + normalColor + "; " +
                "-fx-text-fill: white; -fx-font-weight: bold; " +
                "-fx-background-radius: 8; -fx-border-radius: 8; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 2); " +
                "-fx-cursor: hand;");

        button.setOnMouseEntered(e -> {
            button.setStyle("-fx-background-color: " + hoverColor + "; " +
                    "-fx-text-fill: white; -fx-font-weight: bold; " +
                    "-fx-background-radius: 8; -fx-border-radius: 8; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0, 0, 3); " +
                    "-fx-cursor: hand;");
            button.setTranslateY(-2);
        });
        button.setOnMouseExited(e -> {
            button.setStyle("-fx-background-color: " + normalColor + "; " +
                    "-fx-text-fill: white; -fx-font-weight: bold; " +
                    "-fx-background-radius: 8; -fx-border-radius: 8; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 2); " +
                    "-fx-cursor: hand;");
            button.setTranslateY(0);
        });
        button.setOnMousePressed(e -> button.setTranslateY(0));
        button.setOnMouseReleased(e -> button.setTranslateY(-2));

        return button;
    }

    /**
     * Запускает игровой таймер.
     */
    private void startGameTimer() {
        gameTimer = new GameTimer(new GameTimer.TimerCallback() {
            @Override
            public void onTimeUpdate(int minutes, int seconds) {
                updateTimerDisplay(minutes, seconds);
            }

            @Override
            public void onTimeExpired() {
                handleTimeExpired();
            }
        });
        gameTimer.start();
    }

    /**
     * Обновляет отображение статуса игры.
     */
    public void updateStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText("Статус: " + message);
        }
    }

    /**
     * Обновляет отображение таймера.
     */
    public void updateTimerDisplay(int minutes, int seconds) {
        if (timerLabel != null) {
            timerLabel.setText(String.format("Время: %02d:%02d", minutes, seconds));
        }
    }

    /**
     * Обновляет отображение информации о текущем игроке.
     * Выделяет цветом и добавляет пометку, если это ход текущего игрока.
     */
    public void updatePlayerDisplay(String playerName, boolean isCurrentPlayerTurn) {
        if (playerLabel != null) {
            String text = "👤 Игрок: " + playerName;
            if (isCurrentPlayerTurn) {
                text += " ✨ (Ваш ход)";
                playerLabel.setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold; " +
                        "-fx-background-color: rgba(46, 204, 113, 0.3); " +
                        "-fx-background-radius: 5; -fx-padding: 5 10 5 10;");
            } else {
                playerLabel.setStyle("-fx-text-fill: white; " +
                        "-fx-background-color: rgba(255,255,255,0.2); " +
                        "-fx-background-radius: 5; -fx-padding: 5 10 5 10;");
            }
            playerLabel.setText(text);
        }
    }

    /**
     * Обрабатывает событие окончания времени.
     */
    private void handleTimeExpired() {
        if (gameController != null) {
            gameController.handleTimeExpired();
        }
    }

    /**
     * Показывает диалоговое окно с информацией о игре.
     */
    public void showGameDialog(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public GameTimer getGameTimer() {
        return gameTimer;
    }
}