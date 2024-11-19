# Minesweeper Game

A simple implementation of the classic Minesweeper game using JavaFX. The game features a grid where you must uncover tiles while avoiding mines. You can flag suspected mines with right-clicks and uncover tiles with left-clicks.

## Features
- **Grid Layout:** A 9x9 grid (adjustable size) where each cell can either be a mine or a safe tile.
- **Mines:** Randomly placed mines on the grid. You need to avoid clicking them to win.
- **Flagging:** Right-click to flag a tile if you suspect it contains a mine. You can only flag up to the number of mines on the field.
- **Winning & Losing Conditions:** The game ends when all non-mine tiles are uncovered (win) or when a mine is uncovered (lose).
- **Game Reset:** The game can be reset after either winning or losing to start a new game.
- **Score:** Points are awarded for uncovering safe tiles, and you can track your score throughout the game.

## How to Play
1. **Left-click** to uncover a tile. If the tile contains a mine, the game ends in a loss.
2. **Right-click** to flag a tile if you suspect it contains a mine. If you're wrong, you'll lose points, but if you're right, the flag stays.
3. The goal is to uncover all non-mine tiles without triggering a mine.
4. If all non-mine tiles are uncovered, you win!

## Getting Started

### Prerequisites
- Java 8 or higher
- JavaFX (included in the JDK if you are using JDK 8, or you may need to download it separately for newer JDKs)

### How to Run
1. Clone the repository to your local machine:
   ```bash
   git clone https://github.com/chanonpu/MineSweeperGame.git
