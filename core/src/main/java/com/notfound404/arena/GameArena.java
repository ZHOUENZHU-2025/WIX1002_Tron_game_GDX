package com.notfound404.arena;

import java.util.ArrayList;
import com.notfound404.character.*;

public class GameArena {
    
    //The cells of the arena
    //竞技场的单元格
    private final static int ARENA_WIDTH = 40;
    private final static int ARENA_HEIGHT = 40;
    //Border: Wall 4 / Cliff -1 (As the following) 
    //边界：围墙/空(用4/-1，如下)
    private final static int BORDER_WIDTH = 2;
    private final static int ARENA_SIZE = 44;

    //The size of each cell in pixels
    //像素尺寸
    public final static int CELL_SIZE = 8;

    /*The grid representing the arena
    * Each cell can have the following values:
    * 0 = empty cell
    * 1 = trail cell
    * 2 = bike cell
    * 3 = accelerator cell
    * 4 = wall cell
    * 5 = disco cell
    * 6 = dead disco cell
    * 
    * -1 = out of bounds/cliff
    * */
    private int[][] grid;

    //Defining directions for bike movement
    //Use W, A, S, D for UP, LEFT, DOWN, RIGHT respectively
    public enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }

    private ArrayList<Bike> bikes;
    private ArrayList<Explosion> explosions;
    private ArrayList<Disco> discos;


    public GameArena() {
        grid = new int[ARENA_WIDTH + 2*BORDER_WIDTH][ARENA_HEIGHT + + 2*BORDER_WIDTH];
        bikes = new ArrayList<Bike>();
        explosions = new ArrayList<Explosion>();
        discos = new ArrayList<Disco>();
    }


    public int getCellValue(int x, int y) {
        if (x < 0 || x >= ARENA_SIZE || y < 0 || y >= ARENA_SIZE) {
            return -1; // Out of bounds
        }
        return grid[x][y];
    }

    public void setCellValue(int x, int y, int value) {
        if (x < 0 || x >= ARENA_SIZE || y < 0 || y >= ARENA_SIZE) {
            return; // Out of bounds
        }
        grid[x][y] = value;
    }

    public void addBike(Bike bike) {
        bikes.add(bike);
    }

    public void addExplosion(int x, int y) {
        explosions.add(new Explosion(x, y));
    }

    public void addDisco(Disco disco){
        discos.add(disco);
    }
}