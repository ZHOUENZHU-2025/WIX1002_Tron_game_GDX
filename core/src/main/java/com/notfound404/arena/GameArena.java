package com.notfound404.arena;

import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.notfound404.character.*;

import com.notfound404.fileReader.ImageHandler;

import com.badlogic.gdx.math.GridPoint2;
import java.util.Random;

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

    //MAP PROPERTIES
    private char borderType = 'B'; // 'B' = Bordered (Wall), 'U' = Unbordered (Cliff)

    // Optimization Lists: Do the list rather than 40*40 = 1600 map
    public ArrayList<GridPoint2> wallList;
    public ArrayList<GridPoint2> acceleratorList;

    //Defining directions for bike movement
    //Use W, A, S, D for UP, LEFT, DOWN, RIGHT respectively
    public enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }

    //Entities
    private ArrayList<Bike> bikes;
    private ArrayList<Explosion> explosions;
    private ArrayList<Disco> discos;
    private ArrayList<Trail> trails;
    private Player playerBike;


    //This is for player: Disco shooting span
    private final static int SHOOTSPAN = 3; //The span required between 2 times we check disco shoot. (unit: second)
    private float shootTimer;

    private boolean isGameOver;
    //Win!
    private boolean isTriumph;


    public GameArena() {
        //Lists (int[][] also a list, isn't it?)
        grid = new int[ARENA_WIDTH + 2*BORDER_WIDTH][ARENA_HEIGHT + 2*BORDER_WIDTH];
        bikes = new ArrayList<Bike>();
        explosions = new ArrayList<Explosion>();
        discos = new ArrayList<Disco>();
        trails = new ArrayList<Trail>();
        wallList = new ArrayList<>();
        acceleratorList = new ArrayList<>();

        clearMap();

        shootTimer = 0f;

        isGameOver = false;
    }

    public void setBorderType(char type){
        this.borderType = (type == 'U' ? 'U' : 'B' );
    }

    public char getBoardType(){return borderType; }

    //reset Everything
    public void clearMap() {
        int borderVal = (borderType == 'U') ? -1 : 4;

        for(int i = 0; i < ARENA_SIZE; i++){
            for(int j = 0; j < ARENA_SIZE; j++){
                // If in border region
                if(i < BORDER_WIDTH || j < BORDER_WIDTH || i >= ARENA_WIDTH + BORDER_WIDTH || j >= ARENA_HEIGHT + BORDER_WIDTH) {
                    grid[i][j] = borderVal;
                } else {
                    grid[i][j] = 0;
                }
            }
        }
    }
    
    //Load Map from the txt file(done by the fileReader)
    public void scanAndInitLists() {
        wallList.clear();
        acceleratorList.clear();

        for(int i = 0; i < ARENA_SIZE; i++){
            for(int j = 0; j < ARENA_SIZE; j++){
                int val = grid[i][j];
                if (val == 4) {
                    wallList.add(new GridPoint2(i, j));
                } else if (val == 3) {
                    acceleratorList.add(new GridPoint2(i, j));
                }
            }
        }
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
        
        //Here we check is we win
        isTriumph = isBossSpawned && Enemy.enemyCount==0;
        if(isTriumph){
            return;
        }
        //每帧生成敌人
        addNewEnemy();
            
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

        // Update & Remove Bike
        Iterator<Bike> bikeIt = bikes.iterator();
        while (bikeIt.hasNext()) {
            Bike bike = bikeIt.next();
            if (bike.isDisposed()) {
                bikeIt.remove();
            } else {
                bike.update(deltaTime);
            }
        }

        // Update & Remove Explosion
        Iterator<Explosion> expIt = explosions.iterator();
        while (expIt.hasNext()) {
            Explosion epl = expIt.next();
            if (epl.update(deltaTime)) { // update return true -> Explosion Play done 真表示播放完毕
                expIt.remove();
            }
        }

        for(GridPoint2 accelerator: acceleratorList){
            if(grid[accelerator.x][accelerator.y]==0){
                grid[accelerator.x][accelerator.y] = 3;
            }
        }


    }

    // For AI to get the play's info
    public Bike getPlayer() {
        // Assuming the first bike added is the player.
        if(!bikes.isEmpty()) return bikes.get(0);
        return null;
    }


    public void draw(ShapeRenderer shaper, ImageHandler painter){
        
        //Quickly draw the Border(No need to do grid by grid)
        drawBorder(shaper);

        //Here we draw the whole map
        for(Trail trail : trails)
            trail.drawTrail(shaper);

        for(Disco disco : discos)
            painter.drawDisco(disco);

        for(Bike bike : bikes)
            painter.drawBike(bike);

        for(GridPoint2 wall: wallList){
            shaper.setColor(Color.WHITE);
            shaper.rect(wall.x * CELL_SIZE, wall.y * CELL_SIZE, CELL_SIZE,CELL_SIZE);
        }

        for(GridPoint2 accelerator: acceleratorList){
            if(grid[accelerator.x][accelerator.y]==3){
                shaper.setColor(Color.YELLOW);
                shaper.rect(accelerator.x * CELL_SIZE + CELL_SIZE /2f, accelerator.y * CELL_SIZE + CELL_SIZE/2f, CELL_SIZE/3f,CELL_SIZE/3f);
            }
            
        }

        for(Explosion explosion: explosions)
            explosion.draw(shaper);
        
    }

    private void drawBorder(ShapeRenderer shaper) {
        float fullSize = ARENA_SIZE * CELL_SIZE;
        float borderSize = BORDER_WIDTH * CELL_SIZE;

        if (borderType == 'B') {
            // 'B': White Walls for border
            shaper.setColor(Color.WHITE);
            // Draw 4 rectangles for the border
            shaper.rect(0, 0, fullSize, borderSize); // Bottom
            shaper.rect(0, fullSize - borderSize, fullSize, borderSize); // Top
            shaper.rect(0, borderSize, borderSize, fullSize - 2*borderSize); // Left
            shaper.rect(fullSize - borderSize, borderSize, borderSize, fullSize - 2*borderSize); // Right

        } else if (borderType == 'U') {
            // 'U': Thin Red Line (Waring for Cliff)
            shaper.setColor(Color.RED);
            // line width
            float lineThickness = 2f;
            
            // Draw inner edge of the border area
            float start = borderSize;
            float end = fullSize - borderSize;

            shaper.rectLine(start, start, end, start, lineThickness); // Bottom Line
            shaper.rectLine(start, end, end, end, lineThickness);     // Top Line
            shaper.rectLine(start, start, start, end, lineThickness); // Left Line
            shaper.rectLine(end, start, end, end, lineThickness);     // Right Line
        }
    }

    //Initialization Method
    public void initPlayerAndEnemies(String heroType) {
        // Reset lists
        bikes.clear();
        
        // Add Player
        GridPoint2 safeCoordinate = getSafePosition();
        Player p = new Player(heroType, safeCoordinate.x, safeCoordinate.y, this);
        addBike(p);
        playerBike = p;

        // Add Enemy
        safeCoordinate = getSafePosition();
        addBike(new Enemy(this, safeCoordinate.x, safeCoordinate.y, 1));

    }

    //Randomly Generate an enemy
    private int lastTrackedLevel = 1; // 记录上一次处理生成逻辑时的玩家等级
    private boolean isBossSpawned = false; // 确保难度4的BOSS只生成一次
    //每帧检查（即生成，已在update中调用）



// 随机生成敌人的逻辑
// 随机生成敌人的逻辑
public void addNewEnemy() {
    if (playerBike == null || playerBike.isDisposed()) return;

    // 1. 获取玩家当前等级
    int currentLevel = playerBike.getPlayerLevel();

    // 2. 检查是否升级（只有等级提升时才触发生成逻辑）
    if (currentLevel > lastTrackedLevel) {
        
        //剧情等级点
        java.util.List<Integer> storyLevels = java.util.Arrays.asList(10, 20, 40, 60, 71, 80);
        boolean isStoryPoint = storyLevels.contains(currentLevel);

        // 识别场上敌人数量
        int currentEnemyCount = 0;
        for (Bike b : bikes) {
            // 统计场上还没死（没有被 dispose）的敌人
            if (b instanceof Enemy && !b.isDisposed()) {
                currentEnemyCount++;
            }
        }

        //如果是剧情点，就不拦截上限
        if (!isStoryPoint && currentEnemyCount >= 3) {
            // 非剧情点且场上已有3个或更多敌人，则跳过本次生成
            lastTrackedLevel = currentLevel; 
            return;
        }

        int enemiesToSpawn = 0;
        int difficultyToSpawn = 1;

        // 根据逻辑分支判断生成数量和难度
        if (currentLevel < 10) {
            enemiesToSpawn = new Random().nextInt(1) + 2; 
            difficultyToSpawn = 1;
        } else if (currentLevel < 40) {
            enemiesToSpawn = new Random().nextInt(1) + 2;
            difficultyToSpawn = 2;
        } else if (currentLevel < 70) {
            enemiesToSpawn = new Random().nextInt(1) + 2;
            difficultyToSpawn = 3;
        } else if (currentLevel < 99) {
            enemiesToSpawn = new Random().nextInt(1) + 4; 
            difficultyToSpawn = 3;
        } else if (currentLevel >= 99 && !isBossSpawned) {
            enemiesToSpawn = 1;
            difficultyToSpawn = 4;
            isBossSpawned = true;
        }

        //剧情点强行生成 3 个
        int actualToSpawn;
        if (isStoryPoint) {
            // 如果到达剧情点，无视上限限制，直接强行生成 3 个
            actualToSpawn = 3;
        } else {
            // 普通等级：根据上限修正数量，场上最多维持 3 个
            int spaceLeft = 3 - currentEnemyCount;
            actualToSpawn = Math.min(enemiesToSpawn, spaceLeft);
        }
        // 3. 执行生成
        for (int i = 0; i < actualToSpawn; i++) {
            GridPoint2 safePos = getSafePosition();
            addBike(new Enemy(this, safePos.x, safePos.y, difficultyToSpawn));
        }

        // 更新追踪的等级
        lastTrackedLevel = currentLevel;
    }
}


    //Get a random safe position
    public GridPoint2 getSafePosition(){
        Random gen = new Random();
        int rx;
        int ry;

        do{
            rx = gen.nextInt(ARENA_WIDTH) + BORDER_WIDTH;
            ry = gen.nextInt(ARENA_HEIGHT) + BORDER_WIDTH;
        }while(grid[rx][ry]!=0);

        return new GridPoint2(rx, ry);
    }


    //ui显示调用
    public Player getPlayerBike() {
        return (Player) this.playerBike;
    }
    
    public boolean gameOver(){return isGameOver;}
    public boolean userWin(){return isTriumph; }

}