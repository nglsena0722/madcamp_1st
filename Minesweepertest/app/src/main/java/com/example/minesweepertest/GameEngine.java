package com.example.minesweepertest;

import android.content.Context;
import android.view.View;
import android.view.WindowId;
import android.widget.Toast;

import com.example.minesweepertest.util.Generator;
import com.example.minesweepertest.util.PrintGrid;
import com.example.minesweepertest.views.grid.Cell;

public class GameEngine {
    private static GameEngine instance;

    public static final int BOMB_NUMBER = 1;
    public static final int WIDTH = 10;
    public static final int HEIGHT = 16;

    private Context context;

    private Cell[][] MinesweeperGrid = new Cell[WIDTH][HEIGHT];

    public static GameEngine getInstance() {
        if(instance == null) {
            instance = new GameEngine();
        }
        return instance;
    }

    private GameEngine() { }

    public void createGrid(Context context) {
        this.context = context;

        int [][] GeneratedGrid = Generator.generate(BOMB_NUMBER, WIDTH,HEIGHT);
        PrintGrid.print(GeneratedGrid, WIDTH, HEIGHT);
        setGrid(context, GeneratedGrid);
    }

    private void setGrid(final Context context , final int[][] grid) {
        for (int x = 0; x < WIDTH; x++){
            for (int y = 0; y < HEIGHT; y++){
                if (MinesweeperGrid[x][y] == null) {
                    MinesweeperGrid[x][y] = new Cell(context, x, y);
                }
                MinesweeperGrid[x][y].setValue(grid[x][y]);
                MinesweeperGrid[x][y].invalidate();
            }
        }
    }

    public Cell getCellAt(int position) {
        int x = position % WIDTH;
        int y = position / WIDTH;

        return MinesweeperGrid[x][y];
    }

    public Cell getCellAt(int x, int y) {
        return MinesweeperGrid[x][y];
    }

    public void click (int x, int y) {
        if (x >= 0 && y >= 0 && x < WIDTH && y < HEIGHT && !getCellAt(x, y).isClicked()) {
            getCellAt(x,y).setClicked();

            if (getCellAt(x,y).getValue() == 0) {
                for (int xt = -1; xt <= 1; xt++) {
                    for (int yt = -1; yt <= 1; yt++) {
                        if (xt != 0 || yt != 0) {
                            click(x + xt, y + yt);
                        }
                    }
                }
            }
            if (getCellAt(x, y).isBomb()) {
                onGameLost();
            }
        }

        checkEnd();
    }

    private boolean checkEnd(){
        int bombNotFound = BOMB_NUMBER;
        int notRevealed = WIDTH * HEIGHT;
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (getCellAt(x,y).isRevealed() || getCellAt(x,y).isFlagged()) {
                    notRevealed--;
                }

                if (getCellAt(x,y).isFlagged() && getCellAt(x,y).isBomb()) {
                    bombNotFound--;
                }
            }
        }
        if (bombNotFound == 0 && notRevealed == 0) {
            Toast.makeText(context, "Game won", Toast.LENGTH_SHORT).show();
        }
        return false;

    }

    public void flag(int x, int y) {
            boolean isFlagged = getCellAt(x, y).isFlagged();
            getCellAt(x, y).setFlagged(!isFlagged);
            getCellAt(x, y).invalidate();
            checkEnd();
    }

    private void onGameLost() {
        Toast.makeText(context, "Game lost", Toast.LENGTH_SHORT).show();

        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                getCellAt(x,y).setRevealed();

            }
        }

    }
}
