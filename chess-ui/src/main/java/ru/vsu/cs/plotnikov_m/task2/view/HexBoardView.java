package ru.vsu.cs.plotnikov_m.task2.view;

import ru.vsu.cs.plotnikov_m.task2.model.*;
import ru.vsu.cs.plotnikov_m.task2.controller.GameController;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import javafx.scene.Cursor;
import javafx.scene.effect.Glow;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * Класс для графического представления шестиугольной шахматной доски.
 */
public class HexBoardView {
    private static final double HEX_SIZE = 35.0;
    private static final double HEX_WIDTH = Math.sqrt(3) * HEX_SIZE;
    private static final double HEX_HEIGHT = 2 * HEX_SIZE;

    private final Pane boardPane;
    private final Map<Hex, Polygon> hexMap;
    private final Map<Hex, PieceView> pieceViews;
    private final List<Circle> currentMoveIndicators;
    private GameController controller;
    private Hex selectedHex;

    public HexBoardView() {
        this.boardPane = new Pane();
        this.hexMap = new HashMap<>();
        this.pieceViews = new HashMap<>();
        this.currentMoveIndicators = new ArrayList<>();
        this.selectedHex = null;
        boardPane.setStyle("-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #667eea 0%, #764ba2 100%);");
    }

    public void setController(GameController controller) {
        this.controller = controller;
    }

    /**
     * Создает графическое представление доски.
     * Очищает панель и рисует все шестиугольные поля в соответствии с координатами.
     */
    public void createBoard() {
        boardPane.getChildren().clear();
        hexMap.clear();
        pieceViews.clear();

        for (int q = -5; q <= 5; q++) {
            for (int r = -5; r <= 5; r++) {
                int s = -q - r;
                if (Math.abs(s) <= 5) {
                    Hex hex = new Hex(q, r, s);
                    Polygon hexagon = createHexagon(hex);
                    hexMap.put(hex, hexagon);
                    boardPane.getChildren().add(hexagon);
                }
            }
        }

        centerBoard();
    }

    /**
     * Создает шестиугольник для указанного поля.
     */
    private Polygon createHexagon(Hex hex) {
        double centerX = calculateCenterX(hex);
        double centerY = calculateCenterY(hex);

        Polygon hexagon = new Polygon();

        for (int i = 0; i < 6; i++) {
            double angle = 2.0 * Math.PI / 6 * i + Math.PI / 6;
            double x = centerX + HEX_SIZE * Math.cos(angle);
            double y = centerY + HEX_SIZE * Math.sin(angle);
            hexagon.getPoints().addAll(x, y);
        }

        Color baseColor = switch (hex.getColor()) {
            case LIGHT -> Color.web("#E8E6E3");
            case MEDIUM -> Color.web("#B8B5B0");
            case DARK -> Color.web("#7D7A75");
        };

        hexagon.setFill(baseColor);
        hexagon.setStroke(Color.web("#5A5A5A"));
        hexagon.setStrokeWidth(1.0);

        hexagon.setOnMouseClicked(event -> {
            event.consume();
            if (controller != null) {
                controller.handleHexClick(hex);
            }
        });
        hexagon.setOnMouseEntered(event -> highlightHex(hexagon, true));
        hexagon.setOnMouseExited(event -> highlightHex(hexagon, false));
        hexagon.setCursor(Cursor.HAND);

        return hexagon;
    }

    /**
     * Вычисляет координату X центра шестиугольника.
     */
    private double calculateCenterX(Hex hex) {
        return boardPane.getWidth() / 2 + HEX_WIDTH * (hex.getQ() + hex.getR() / 2.0);
    }

    /**
     * Вычисляет координату Y центра шестиугольника.
     */
    private double calculateCenterY(Hex hex) {
        return boardPane.getHeight() / 2 + HEX_HEIGHT * 0.75 * hex.getR();
    }

    /**
     * Центрирует доску на панели.
     * Выравнивает все шестиугольные поля относительно центра панели.
     */
    private void centerBoard() {
        double boardWidth = 11 * HEX_WIDTH;
        double boardHeight = 11 * HEX_HEIGHT * 0.75;

        double offsetX = (boardPane.getWidth() - boardWidth) / 2;
        double offsetY = (boardPane.getHeight() - boardHeight) / 2;

        for (Map.Entry<Hex, Polygon> entry : hexMap.entrySet()) {
            Hex hex = entry.getKey();
            Polygon polygon = entry.getValue();

            double centerX = offsetX + HEX_WIDTH * (hex.getQ() + hex.getR() / 2.0) + boardWidth / 2;
            double centerY = offsetY + HEX_HEIGHT * 0.75 * hex.getR() + boardHeight / 2;

            polygon.getPoints().clear();
            for (int i = 0; i < 6; i++) {
                double angle = 2.0 * Math.PI / 6 * i + Math.PI / 6;
                double x = centerX + HEX_SIZE * Math.cos(angle);
                double y = centerY + HEX_SIZE * Math.sin(angle);
                polygon.getPoints().addAll(x, y);
            }
        }
    }


    /**
     * Подсвечивает шестиугольник при наведении курсора.
     */
    private void highlightHex(Polygon hexagon, boolean highlight) {
        if (highlight) {
            hexagon.setStroke(Color.web("#FFD700"));
            hexagon.setStrokeWidth(3);
        } else {
            hexagon.setStroke(Color.web("#5A5A5A"));
            hexagon.setStrokeWidth(1);
        }
    }

    /**
     * Отображает фигуру на указанном поле.
     */
    public void drawPiece(Piece piece, Hex hex) {
        removePiece(hex);

        PieceView pieceView = new PieceView(piece);

        Polygon hexagon = hexMap.get(hex);
        if (hexagon != null) {
            double centerX = getHexagonCenterX(hexagon);
            double centerY = getHexagonCenterY(hexagon);

            pieceView.setPosition(centerX, centerY);
            
            pieceView.getView().setOnMouseClicked(event -> {
                event.consume();
                if (controller != null) {
                    controller.handleHexClick(hex);
                }
            });
            pieceView.getView().setCursor(Cursor.HAND);
            pieceView.getView().setMouseTransparent(false);
            pieceView.getView().setPickOnBounds(true);
        }

        pieceViews.put(hex, pieceView);

        int insertIndex = boardPane.getChildren().size();
        boardPane.getChildren().add(insertIndex, pieceView.getView());
        
        pieceView.getView().setMouseTransparent(false);
        pieceView.getView().setPickOnBounds(true);
    }

    /**
     * Вычисляет координату X геометрического центра шестиугольника.
     */
    private double getHexagonCenterX(Polygon hexagon) {
        if (hexagon.getPoints().size() < 6) return 0;

        double minX = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;

        for (int i = 0; i < hexagon.getPoints().size(); i += 2) {
            double x = hexagon.getPoints().get(i);
            minX = Math.min(minX, x);
            maxX = Math.max(maxX, x);
        }

        return (minX + maxX) / 2;
    }

    /**
     * Вычисляет координату Y геометрического центра шестиугольника.
     */
    private double getHexagonCenterY(Polygon hexagon) {
        if (hexagon.getPoints().size() < 6) return 0;

        double minY = Double.MAX_VALUE;
        double maxY = Double.MIN_VALUE;

        for (int i = 1; i < hexagon.getPoints().size(); i += 2) {
            double y = hexagon.getPoints().get(i);
            minY = Math.min(minY, y);
            maxY = Math.max(maxY, y);
        }

        return (minY + maxY) / 2;
    }

    /**
     * Удаляет фигуру с указанного поля.
     */
    public void removePiece(Hex hex) {
        PieceView pieceView = pieceViews.remove(hex);
        if (pieceView != null && pieceView.getView() != null) {
            boardPane.getChildren().remove(pieceView.getView());
        }
    }

    /**
     * Очищает все фигуры с доски.
     */
    public void clearAllPieces() {
        for (PieceView pieceView : pieceViews.values()) {
            if (pieceView != null && pieceView.getView() != null) {
                boardPane.getChildren().remove(pieceView.getView());
            }
        }
        pieceViews.clear();
    }

    /**
     * Перемещает фигуру с одного поля на другое.
     */
    public void movePiece(Hex from, Hex to) {
        PieceView pieceView = pieceViews.get(from);
        if (pieceView == null) {
            return;
        }

        pieceViews.remove(from);
        pieceView.getPiece().setPosition(to);
        pieceViews.put(to, pieceView);

        Polygon targetHex = hexMap.get(to);
        if (targetHex != null) {
            double centerX = getHexagonCenterX(targetHex);
            double centerY = getHexagonCenterY(targetHex);

            pieceView.setPosition(centerX, centerY);
            
            pieceView.getView().setOnMouseClicked(event -> {
                if (controller != null) {
                    controller.handleHexClick(to);
                }
                event.consume();
            });
        }

        boardPane.requestLayout();
    }

    /**
     * Подсвечивает выбранное поле.
     */
    public void highlightSelectedHex(Hex hex, boolean selected) {
        if (selectedHex != null && !selectedHex.equals(hex)) {
            Polygon oldHex = hexMap.get(selectedHex);
            if (oldHex != null) {
                resetHexColor(oldHex, selectedHex);
                oldHex.setEffect(null);
            }
        }

        if (selected && hex != null) {
            Polygon newHex = hexMap.get(hex);
            if (newHex != null) {
                Color highlightColor = Color.rgb(100, 150, 255, 0.5);
                newHex.setFill(highlightColor);
                newHex.setStroke(Color.rgb(50, 100, 255));
                newHex.setStrokeWidth(4);
                
                Glow glow = new Glow(0.8);
                newHex.setEffect(glow);
                
                selectedHex = hex;
            }
        } else {
            if (selectedHex != null) {
                Polygon oldHex = hexMap.get(selectedHex);
                if (oldHex != null) {
                    resetHexColor(oldHex, selectedHex);
                    oldHex.setEffect(null);
                }
            }
            selectedHex = null;
        }
    }

    /**
     * Восстанавливает исходный цвет шестиугольника.
     */
    private void resetHexColor(Polygon hexagon, Hex hex) {
        Color baseColor = switch (hex.getColor()) {
            case LIGHT -> Color.web("#E8E6E3");
            case MEDIUM -> Color.web("#B8B5B0");
            case DARK -> Color.web("#7D7A75");
        };
        hexagon.setFill(baseColor);
        hexagon.setStroke(Color.web("#5A5A5A"));
        hexagon.setStrokeWidth(1);
    }

    /**
     * Подсвечивает доступные ходы.
     */
    public void highlightValidMoves(List<Hex> validMoves) {
        clearMoveIndicators();
        
        for (Map.Entry<Hex, Polygon> entry : hexMap.entrySet()) {
            Hex hex = entry.getKey();
            Polygon hexagon = entry.getValue();
            
            if (selectedHex != null && hex.equals(selectedHex)) {
                continue;
            }
            
            if (validMoves != null && validMoves.contains(hex)) {
                continue;
            }
            
            resetHexColor(hexagon, hex);
            hexagon.setEffect(null);
        }
        
        if (validMoves != null) {
            for (Hex move : validMoves) {
                Polygon hexagon = hexMap.get(move);
                if (hexagon != null) {
                    double centerX = getHexagonCenterX(hexagon);
                    double centerY = getHexagonCenterY(hexagon);
                    
                    Color baseColor = switch (move.getColor()) {
                        case LIGHT -> Color.web("#E8E6E3");
                        case MEDIUM -> Color.web("#B8B5B0");
                        case DARK -> Color.web("#7D7A75");
                    };
                    
                    if (controller != null && controller.isSquareOccupiedByOpponent(move)) {
                        hexagon.setFill(baseColor);
                        hexagon.setStroke(Color.rgb(255, 0, 0));
                        hexagon.setStrokeWidth(4);
                        
                        Glow glow = new Glow(0.7);
                        hexagon.setEffect(glow);
                        
                        Circle attackIndicator = new Circle(centerX, centerY, 15);
                        attackIndicator.setFill(Color.rgb(255, 0, 0, 0.95));
                        attackIndicator.setStroke(Color.WHITE);
                        attackIndicator.setStrokeWidth(3);
                        attackIndicator.setEffect(new Glow(0.6));
                        attackIndicator.setMouseTransparent(true);
                        boardPane.getChildren().add(attackIndicator);
                        attackIndicator.toFront();
                        currentMoveIndicators.add(attackIndicator);
                    } else {
                        hexagon.setFill(baseColor);
                        hexagon.setStroke(Color.rgb(0, 200, 0));
                        hexagon.setStrokeWidth(3);
                        
                        Circle moveIndicator = new Circle(centerX, centerY, 10);
                        moveIndicator.setFill(Color.rgb(0, 200, 0, 0.9));
                        moveIndicator.setStroke(Color.WHITE);
                        moveIndicator.setStrokeWidth(2);
                        moveIndicator.setEffect(new Glow(0.5));
                        moveIndicator.setMouseTransparent(true);
                        boardPane.getChildren().add(moveIndicator);
                        moveIndicator.toFront();
                        currentMoveIndicators.add(moveIndicator);
                    }
                }
            }
        }
    }
    
    /**
     * Убирает индикаторы возможных ходов.
     */
    private void clearMoveIndicators() {
        for (Circle indicator : currentMoveIndicators) {
            boardPane.getChildren().remove(indicator);
        }
        currentMoveIndicators.clear();
    }

    /**
     * Очищает все подсветки на доске.
     * Восстанавливает исходные цвета всех полей.
     */
    public void clearHighlights() {
        clearMoveIndicators();
        for (Map.Entry<Hex, Polygon> entry : hexMap.entrySet()) {
            Hex hex = entry.getKey();
            Polygon hexagon = entry.getValue();
            resetHexColor(hexagon, hex);
            hexagon.setEffect(null);
        }
        selectedHex = null;
    }

    /**
     * Очищает доску.
     * Удаляет все графические элементы с панели.
     */
    public void clearBoard() {
        boardPane.getChildren().clear();
        hexMap.clear();
        pieceViews.clear();
    }

    /**
     * Обновляет размеры доски.
     * Пересчитывает позиции всех элементов при изменении размера окна.
     */
    public void updateSize(double width, double height) {
        boardPane.setPrefSize(width, height);
        centerBoard();

        for (Map.Entry<Hex, PieceView> entry : pieceViews.entrySet()) {
            Hex hex = entry.getKey();
            PieceView pieceView = entry.getValue();

            Polygon hexagon = hexMap.get(hex);
            if (hexagon != null && pieceView != null) {
                double centerX = getHexagonCenterX(hexagon);
                double centerY = getHexagonCenterY(hexagon);
                pieceView.setPosition(centerX, centerY);
            }
        }

        boardPane.requestLayout();
    }

    public Pane getBoardPane() {
        return boardPane;
    }

}