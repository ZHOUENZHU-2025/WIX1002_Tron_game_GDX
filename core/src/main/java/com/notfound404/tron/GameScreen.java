package com.notfound404.tron;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.notfound404.arena.GameArena;
import com.notfound404.arena.GameArena.Direction;
import com.notfound404.character.Player;
import com.notfound404.fileReader.ImageHandler;
import com.notfound404.fileReader.MapLoader;


/** Game screen where the main gameplay occurs. */
public class GameScreen implements Screen {

    public final Main game;
    //private static final int FONT_SCALE = 5?;
    private static final float UI_START_X = 400f;
    private static final float UI_START_Y = 375f;
    private static final float UI_LINE_SPACING = 15f;
    private static final float ICON_SIZE = 20f;

    Texture discoTexture;
    Texture fullLPTexture;
    Texture halfLPTexture;
    //Here need to declare objects will be used in the Game.
    //这里需要声明一些游戏内要用到的对象，例如车，地图，背景音乐等等
    private GameArena arena;
    private ImageHandler painter;
    private String mapName;
    private String heroType;
    private boolean hasPrompt;
    Vector2 touchPos;//Mouse position

    GameScreen(Main game, String mapName, String heroType) {
        this.game = game;
        this.mapName = mapName;
        this.heroType = heroType;
        this.hasPrompt = false;
        //Initialization Objects declared above
        //接下来初始化上面声明的对象，加载文件就在这里，可以写一些method或者class来增强可读性
        discoTexture = new Texture("./image/disco.png");
        fullLPTexture = new Texture("./image/fullHeart.png");
        halfLPTexture = new Texture("./image/halfHeart.png");

        arena = new GameArena();
        touchPos = new Vector2();
        painter = new ImageHandler(game.shapeRenderer);

        //Initialize map
        loadMap();
    }

    //Load the map into Arena 
    private void loadMap(){
        MapLoader loader = new MapLoader();
        loader.loadMap(arena, mapName);
        
        // Initialize Players/Enemies after map is loaded
        arena.initPlayerAndEnemies(heroType);
    }

    @Override
    public void show() {
        // Prepare your screen here.
    }

    @Override
    public void render(float delta) {
        // Draw your screen here. "delta" is the time since last render in seconds.
        input(delta);
        if (arena.gameOver()||arena.userWin()) {
            //Display the prompt for only one time
            //Avoid give out a number of prompts in seconds and result in crashing.
            if(!hasPrompt){
                hasPrompt = true;
                handleGameOver();//Game is over here
            }else{
                draw();
                return;
            }
        } else {
            logic(delta); // Continue performing logic
        }
        draw();
    }

    private void input(float delta){

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            arena.inputDir(Direction.UP);
        } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            arena.inputDir(Direction.DOWN);
        } else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            arena.inputDir(Direction.LEFT);
        } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            arena.inputDir(Direction.RIGHT);
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {

            touchPos.set(Gdx.input.getX(), Gdx.input.getY());
            game.viewport.unproject(touchPos);

            int gridX = (int) (touchPos.x / GameArena.CELL_SIZE);
            int gridY = (int) (touchPos.y / GameArena.CELL_SIZE);

            if (gridX >= 0 && gridX < 44 && gridY >= 0 && gridY < 44) {
                arena.inputShoot(gridX, gridY);
            }
        }

    }

    private void logic(float delta){
        arena.update(delta);
    }

    private void draw(){
        //Clean the screen
        ScreenUtils.clear(Color.BLACK);
        game.shapeRenderer.setProjectionMatrix(game.viewport.getCamera().combined);
        game.shapeRenderer.begin(ShapeType.Filled);

        //Draw the Arena
        arena.draw(game.shapeRenderer,painter);

        game.shapeRenderer.end();

        drawUI();

        //Maybe something else will be drawn here
        //Like the text messages
        //Life point display...
    }

    private void handleGameOver(){
        NameInputListener listener = new NameInputListener(game, arena.getPlayerBike(), mapName);

        // A prompt displayed
        Gdx.input.getTextInput(listener, "MISSION ACCOMPLISHED", "User", "Enter ID");
        game.setScreen(null);
    }

    private void drawUI() {
    Player player = arena.getPlayerBike();
    if (player == null) return;

    float screenWidth = game.viewport.getWorldWidth();
    float padding = 20f;
    float uiBottomOffset = 15f; // UI 距离底部的基础偏移
    float barWidth = screenWidth - (padding * 2);
    float barHeight = 10f;

    // --- 使用 ShapeRenderer 绘制条状图 ---
    game.shapeRenderer.begin(ShapeType.Filled);
    
    // 1. 经验条 (XP Bar) - 位于最下方
    // 背景
    game.shapeRenderer.setColor(new Color(0.1f, 0.1f, 0.1f, 1f)); // 深灰背景
    game.shapeRenderer.rect(padding, uiBottomOffset, barWidth, barHeight);
    // 进度 (青色)
    game.shapeRenderer.setColor(new Color(0f, 0.85f, 1f, 1f));
    float xpProgress = (float) (player.getCurrentXP() / player.getXPCap());
    game.shapeRenderer.rect(padding, uiBottomOffset, barWidth * Math.min(xpProgress, 1.0f), barHeight);

    //血条和等级数值改为右侧显示
    //The old version: display the LP and Level under the screen

    // 2. 生命值条 (LP Bar) - 位于经验条上方
    float lpBarY = uiBottomOffset + barHeight + 5f; // 间隔5像素
    // // 背景
    // game.shapeRenderer.setColor(new Color(0.2f, 0f, 0f, 1f)); // 深红背景
    // game.shapeRenderer.rect(padding, lpBarY, barWidth, barHeight);
    // // 进度 (亮红色)
    // game.shapeRenderer.setColor(new Color(1f, 0.2f, 0.2f, 1f));
    // float lpProgress = player.getLP() / player.getMaxLP();
    // game.shapeRenderer.rect(padding, lpBarY, barWidth * Math.max(0, Math.min(lpProgress, 1.0f)), barHeight);
    
    game.shapeRenderer.end();

    // --- 使用 Batch 绘制文字标签 ---
    game.batch.setProjectionMatrix(game.viewport.getCamera().combined);
    game.batch.begin();
    
    
    float currentY = UI_START_Y;
    float currentX = UI_START_X;
    game.font.setColor(Color.CYAN);
    game.font.draw(game.batch,heroType,UI_START_X,currentY);

    game.font.setColor(Color.WHITE);

    currentY -= UI_LINE_SPACING;

    // Level
    game.font.draw(game.batch, "LV: " + player.getPlayerLevel(), UI_START_X, currentY);     
    currentY -= UI_LINE_SPACING;

    float lp = player.getLP();
    int fullHearts = (int)(lp/1);
    boolean hasHalfHeart = (lp%1 != 0);

    game.font.setColor(Color.RED);    
    game.font.draw(game.batch, "HP:", UI_START_X, currentY);
    currentY -= UI_LINE_SPACING + ICON_SIZE;
    float heartX = UI_START_X;
    for(int i = 0;i<fullHearts;i++){
        game.batch.draw(fullLPTexture, heartX, currentY,ICON_SIZE,ICON_SIZE);
        heartX += ICON_SIZE+2f;

        if (heartX > game.viewport.getWorldWidth() - ICON_SIZE) {
            heartX = UI_START_X;
            currentY -= ICON_SIZE + 2f;
        }
    }
    if(hasHalfHeart){
        game.batch.draw(halfLPTexture, heartX, currentY,ICON_SIZE, ICON_SIZE);
    }

    currentY -= UI_LINE_SPACING;

    game.font.setColor(Color.WHITE);
    int ammoCount = player.getDiscoSlots();
    game.font.draw(game.batch, "DISC:", UI_START_X, currentY);

    currentY -= (UI_LINE_SPACING + ICON_SIZE);
        
    float discX = UI_START_X;
    for (int i = 0; i < ammoCount; i++) {
        game.batch.draw(discoTexture, discX, currentY, ICON_SIZE, ICON_SIZE);
        discX += ICON_SIZE+5f;
    }
    // 调整字体缩放，让UI文字稍微小一点更精致 (可选)
    // game.font.getData().setScale(0.8f);

    float textY = lpBarY + barHeight + 25f; // 
    
    //血条和等级数值改为右侧显示
    //The old version: display the LP and Level under the screen

    // // 左侧：等级
    // game.font.draw(game.batch, "LV: " + player.getPlayerLevel(), padding, textY);
    
    // // 中间：血量数值
    // String lpText = "HP: " + (int)player.getLP() + " / " + (int)player.getMaxLP();
    // game.font.draw(game.batch, lpText, screenWidth / 2f - 45, textY);
    
    // 右侧：经验百分比
    String xpText = String.format("XP: %.1f%%", xpProgress * 100);
    game.font.draw(game.batch, xpText, screenWidth - padding - 70, textY);

    game.batch.end();
}


    @Override
    public void resize(int width, int height) {
        // If the window is minimized on a desktop (LWJGL3) platform, width and height are 0, which causes problems.
        // In that case, we don't resize anything, and wait for the window to be a normal size before updating.
        if(width <= 0 || height <= 0) return;

        // Resize your screen here. The parameters represent the new window size.
        game.viewport.update(width, height,true);
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void hide() {
        // This method is called when another screen replaces this one.
    }

    @Override
    public void dispose() {
        // Destroy screen's assets here.
        
    }
}