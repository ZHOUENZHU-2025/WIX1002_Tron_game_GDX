package com.notfound404.arena;

import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.notfound404.character.*;
import com.notfound404.fileReader.ImageHandler;

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
    public final static int CELL_SIZE = 9;

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

    //All the Objects we need to draw(update) frame by frame
    private ArrayList<Bike> bikes;
    private ArrayList<Explosion> explosions;
    private ArrayList<Disco> discos;
    private ArrayList<Trail> trails;

    private Bike playerBike;
    private final static int SHOOTSPAN = 3; //The span required between 2 times we check disco shoot. (unit: second)
    private float shootTimer;

    private boolean isGameOver;
    //这里稍后再添加一个胜利判断布尔变量
    //private boolean isTriumph;
    //再来一个BOSS 是否上过场的布尔变量
    //private boolean isBossOn;


    public GameArena() {
        grid = new int[ARENA_WIDTH + 2*BORDER_WIDTH][ARENA_HEIGHT + 2*BORDER_WIDTH];
        //临时测试地图 全0
        //边界：墙
        for(int i = 0 ;i<ARENA_WIDTH + 2*BORDER_WIDTH;i++){
            for(int j = 0;j<ARENA_HEIGHT + 2*BORDER_WIDTH;j++){
                if(i<=1||j<=1||i>=ARENA_WIDTH + BORDER_WIDTH||j>=ARENA_HEIGHT + BORDER_WIDTH)
                    grid[i][j] = 4;
                else
                    grid[i][j] = 0;
            }
        }
        //以上包括注释正式版删除
        bikes = new ArrayList<Bike>();
        explosions = new ArrayList<Explosion>();
        discos = new ArrayList<Disco>();
        trails = new ArrayList<Trail>();

        //Create the first bike: Player
        //创建第一个玩家，类一定有玩家，固定列表第一个是玩家
        bikes.add(new Player("Tron","Tester", ARENA_SIZE/2, ARENA_SIZE/3, this));

        //正式版创建玩家也用其他方法：玩家文件在选择界面已经读取，这里调用读取类返回的参数即可
        //测试版：创建一个敌人
        //正式版用专门的随机方法来创建敌人
        bikes.add(new Enemy(this, ARENA_SIZE/2, 2*ARENA_SIZE/3, Color.RED, 1));

        playerBike = bikes.get(0);
        shootTimer = 0f;

        isGameOver = false;
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
        for(int i = x-1; i <= x + 1 ;i++){
            for(int j = y - 1; j <= y + 1; j++){
                if(i >= 0 && i < grid.length && j >= 0 && j < grid[0].length) {
                    if(grid[i][j] == 1){
                        grid[i][j] = 0;
                    }
                }
            }
        }
            
        explosions.add(new Explosion(x, y));
    }

    public void addDisco(Disco disco){
        discos.add(disco);
    }

    public void addTrail(Trail trail){
        trails.add(trail);
    }

    //Input WASD control COMMAND
    //输入方向
    public void inputDir(Direction dir){
        playerBike.setDirection(dir);
    }

    //Input where to shoot the disco
    //输入射击命令
    public void inputShoot(int tX, int tY){
        if(shootTimer<=0){
            playerBike.shootDisco(tX, tY);
            shootTimer = SHOOTSPAN;
        }
    }

    public void update(float deltaTime){

        if(shootTimer>0){
            shootTimer-=deltaTime;
        }
        if(playerBike.isDisposed()){
            //Call GameOver function
            isGameOver = true;
            return;
        }

        //这里写后续写检测敌人的功能
        /** 没有敌人就检测是否胜利（最终BOSS 'CLU'被干碎)
         * 胜利就直接设置胜利boolean变量并返回
         *有敌人就比较最大敌人数量和生成间隔，满足条件就用随机生成方法生成一个新敌人
        */
            
        //Check the state. Then Update the state. Leave disposal to the next time
        Iterator<Disco> discoIt = discos.iterator();
        while (discoIt.hasNext()) {
            Disco disco = discoIt.next();
            if (disco.isDisposed()) {
                discoIt.remove();
            } else {
                disco.update(deltaTime);
            }
        }

        // 2. 安全地更新和移除 Bike
        Iterator<Bike> bikeIt = bikes.iterator();
        while (bikeIt.hasNext()) {
            Bike bike = bikeIt.next();
            if (bike.isDisposed()) {
                bikeIt.remove();
            } else {
                bike.update(deltaTime);
            }
        }

        // 3. 安全地更新和移除 Explosion
        Iterator<Explosion> expIt = explosions.iterator();
        while (expIt.hasNext()) {
            Explosion epl = expIt.next();
            if (epl.update(deltaTime)) { // update 返回 true 表示播放完毕
                expIt.remove();
            }
        }
    }

    public void draw(ShapeRenderer shaper, ImageHandler painter){
        //Here we draw the whole map
        for(Trail trail : trails){
            trail.drawTrail(shaper);
        }

        for(Disco disco : discos){
            painter.drawDisco(disco);
        }

        for(Bike bike : bikes){
            painter.drawBike(bike);
        }

        for(Explosion explosion: explosions){
            explosion.draw(shaper);
        }
        
    }


    public boolean gameOver(){return isGameOver;}

}