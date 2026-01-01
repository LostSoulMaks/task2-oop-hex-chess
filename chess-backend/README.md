# Chess Backend

Backend модуль для игры в шестиугольные шахматы Глинского.

## Структура проекта

- `model/` - Модели данных (Board, Hex, Move, Piece, PieceType)
- `utils/` - Утилиты (HexDirection, MoveValidator)
- `controller/` - Контроллеры (AIController)
- `GameConstants.java` - Константы игры (GameMode, PlayerColor)

## Сборка

```bash
mvn clean install
```

Это установит артефакт в локальный Maven репозиторий, чтобы UI проект мог его использовать.

## Зависимости

Проект не имеет внешних зависимостей, только стандартная Java библиотека.

