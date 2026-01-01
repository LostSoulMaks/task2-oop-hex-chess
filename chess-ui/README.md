# Chess UI

UI модуль для игры в шестиугольные шахматы Глинского.

## Структура проекта

- `view/` - UI компоненты (GameUI, HexBoardView, PieceView)
- `controller/` - UI контроллер (GameController)
- `utils/` - UI утилиты (GameTimer)
- `Main.java` - Точка входа приложения

## Зависимости

- `chess-backend` - Backend модуль (через Maven)
- `javafx-controls` - JavaFX библиотека

## Сборка и запуск

### Сначала установите backend:

```bash
cd ../chess-backend
mvn clean install
```

### Затем соберите и запустите UI:

```bash
cd ../chess-ui
mvn clean compile javafx:run
```

Или просто:

```bash
mvn javafx:run
```

## Запуск

```bash
mvn javafx:run
```

