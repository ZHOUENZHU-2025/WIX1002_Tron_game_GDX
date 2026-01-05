package com.notfound404.arena;

public class GameArena {
    
    //The cells of the arena
    //竞技场的单元格
    private final static int ARENA_WIDTH = 40;
    private final static int ARENA_HEIGHT = 40;

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

    //Max enemy count

}