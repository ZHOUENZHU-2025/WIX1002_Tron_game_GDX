package com.notfound404.tron;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.notfound404.arena.GameArena;
import com.notfound404.arena.GameArena.Direction;
import com.notfound404.character.Player;
import com.notfound404.fileReader.ArchiveManager;
import com.notfound404.fileReader.ImageHandler;
import com.notfound404.fileReader.MapLoader;
import com.notfound404.fileReader.StoryManager;
import com.notfound404.fileReader.StoryManager.Dialogue;



/** Game screen where the main gameplay occurs. */
public class GameScreen implements Screen, InputProcessor {
    //UI_Story
    private StoryManager storyManager = new StoryManager();
    private Texture currentPortrait;
    private String lastPortraitPath = "";
    private int lastStoryLevel = 1; // 追踪等级变化

    //Store player's ID input
    private String playerIDInput = "";

    public final Main game;
    //private static final int FONT_SCALE = 5?;
    private static final float UI_START_X = 400f;
    private static final float UI_START_Y = 375f;
    private static final float UI_LINE_SPACING = 15f;
    private static final float ICON_SIZE = 20f;

    private Texture discoTexture;
    private Texture fullLPTexture;
    private Texture halfLPTexture;
    private Array<Music> playList;
    private Music currentMusic;
    private int currentMusicIndex = 0;
    //Here need to declare objects will be used in the Game.
    //这里需要声明一些游戏内要用到的对象，例如车，地图，背景音乐等等
    private GameArena arena;
    private ImageHandler painter;
    private String mapName;
    private String heroType;
    private boolean overHandling = false;
    private boolean callSave = false;
    private boolean saveHandling = false;
    private String gameOverPrompt = "";
    Vector2 touchPos;//Mouse position

    GameScreen(Main game, String mapName, String heroType) {
        this.game = game;
        this.mapName = mapName;
        this.heroType = heroType;

        //Initialization Objects declared above
        //接下来初始化上面声明的对象，加载文件就在这里，可以写一些method或者class来增强可读性
        discoTexture = new Texture("./image/disco.png");
        fullLPTexture = new Texture("./image/fullHeart.png");
        halfLPTexture = new Texture("./image/halfHeart.png");

        //music
        playList = new Array<Music>();
        playList.add(Gdx.audio.newMusic(Gdx.files.internal("music/Quirky Runner.mp3")));
        playList.add(Gdx.audio.newMusic(Gdx.files.internal("music/tron-pop-instrumental.mp3")));
        
        //Disable the loop setting
        for(Music m : playList) {
            m.setLooping(false);
        }

        arena = new GameArena();
        touchPos = new Vector2();
        painter = new ImageHandler(game.shapeRenderer);

        //Initialize map
        loadMap();
        storyManager.trigger("START");
    }

    //This constructor is for Loaded startmusic: level and score are set at the beginning.
    //这个是给读取存档构建新游戏而准备的构造器
    GameScreen(Main game, String mapName, String heroType, int level, int score) {
        this.game = game;
        this.mapName = mapName;
        this.heroType = heroType;
        

        //Initialization Objects declared above
        //接下来初始化上面声明的对象，加载文件就在这里，可以写一些method或者class来增强可读性
        discoTexture = new Texture("./image/disco.png");
        fullLPTexture = new Texture("./image/fullHeart.png");
        halfLPTexture = new Texture("./image/halfHeart.png");

        // 必须初始化音乐播放列表，否则 startMusic 会崩
        playList = new Array<Music>();
        playList.add(Gdx.audio.newMusic(Gdx.files.internal("music/Quirky Runner.mp3")));
        playList.add(Gdx.audio.newMusic(Gdx.files.internal("music/tron-pop-instrumental.mp3")));
        
        for(Music m : playList) {
            m.setLooping(false);
        }
        
        arena = new GameArena();
        touchPos = new Vector2();
        painter = new ImageHandler(game.shapeRenderer);

        /**这里或者后续需要加入初始化玩家经验和等级的操作
         * 需要同步经验到经验系统
         */

        //Initialize map
        loadMap();

        //level loader
        if (arena.getPlayerBike() != null) {
        Player player = arena.getPlayerBike();
        
        // 灌入等级和存档里的“分数”（在此逻辑下作为经验值处理）
        player.getLevelSystem().loadFromSave(level, score);
        
        // 同步剧情追踪等级，防止重复弹出剧情对话
        this.lastStoryLevel = level;
    }

        storyManager.trigger("START");
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
        // Play the first Music
        startMusic(0);
    }

    //Music play methods
    //===========================================
    private void startMusic(int index){
        
        currentMusic = playList.get(index);
        currentMusic.play();
    }

    private void playNextSong() {
        currentMusicIndex++;
        if (currentMusicIndex >= playList.size) {
            currentMusicIndex = 0;
        }
        startMusic(currentMusicIndex);
    }
    //=============================================

    @Override
    public void render(float delta) {
        // Draw your screen here. "delta" is the time since last render in seconds.
        //If we are in the handling process draw only, no logic processing.
        if(overHandling||saveHandling){
            drawGameScene();
            return;
        }
        if (storyManager.isActive()) {
            if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                storyManager.next();
            }
        } else {
            input(delta); // 只有剧情结束才响应赛车控制
        }
        
        // 2. 逻辑更新：剧情模式下暂停游戏逻辑
        if (!storyManager.isActive()) {
            if (arena.gameOver()||arena.userWin()) {
                //Handle the over of our game
                if (!overHandling)
                    gameOverPrompt = arena.userWin() ? "MISSION COMPLETE!":"YOU FAILED";
                    handleGameOver();
            } else if(callSave){
                callSave = false;
                if(!saveHandling)
                    handleSave();

            } else {
                logic(delta); // Continue performing logic
                //Check again after logic performed
                checkStoryEvents();
            } 
             
        }

        drawGameScene();
        //music handling
        if (!currentMusic.isPlaying()) {
            playNextSong();
        }

    }

    // 4. 检查剧情触发 (根据玩家等级)
    private void checkStoryEvents() {
        int currentLevel = arena.getPlayerBike().getPlayerLevel();
    
        if (currentLevel != lastStoryLevel) {
            // 每当等级提升，判断是否符合特定的对话触发点
            if (currentLevel == 20 || currentLevel == 40 || currentLevel == 60 || currentLevel == 80) {
                storyManager.trigger("REINFORCE");
            } else if (currentLevel == 10 || currentLevel == 41 || currentLevel == 71) {
                // 对应你 addNewEnemy 里的难度跳跃点
                storyManager.trigger("STRONGER");
            } else if (currentLevel == 99) {
                storyManager.trigger("FINAL");
            }
            lastStoryLevel = currentLevel;
        }
    }

    //Seal the game scene drawing process
    private void drawGameScene(){
        ScreenUtils.clear(Color.BLACK);
        game.viewport.apply(); 

        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);
        game.shapeRenderer.setProjectionMatrix(game.viewport.getCamera().combined);

        draw(); 
        
        if (storyManager.isActive()) {
            renderStoryUI(); 
        }

        drawPrompt();
    }
    //Draw prompt if the game is over
    private void drawPrompt(){
        if (overHandling||saveHandling) {
            //Veil to hide the game arena
            Gdx.gl.glEnable(GL20.GL_BLEND);
            game.shapeRenderer.begin(ShapeType.Filled);
            game.shapeRenderer.setColor(0, 0, 0, 0.7f);
            game.shapeRenderer.rect(0, 0, game.viewport.getWorldWidth(), game.viewport.getWorldHeight());
            game.shapeRenderer.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);

            float promptX = game.viewport.getWorldWidth()/2f;
            float promptY = game.viewport.getWorldHeight()/2f + 45f;
            game.batch.begin();
            String promptTitle = overHandling ? gameOverPrompt :"Archive";
            game.font.setColor(Color.YELLOW);
            GlyphLayout layout = new GlyphLayout(game.font, promptTitle);
            game.font.draw(game.batch, promptTitle, promptX - layout.width/2, promptY);
            promptY -= 50;
            game.font.setColor(Color.WHITE);
            layout = new GlyphLayout(game.font, "Enter Name: " + playerIDInput + "_");
            game.font.draw(game.batch, "Enter Name: " + playerIDInput + "_", promptX - layout.width/2, promptY);
            promptY -= 50;
            game.font.setColor(Color.GRAY);
            layout = new GlyphLayout(game.font, "[Press ENTER]");
            game.font.draw(game.batch, "[Press ENTER]", promptX - layout.width/2, promptY);
            game.batch.end();
        }
    }

    // 5. 渲染剧情 UI
private void renderStoryUI() {
    Dialogue d = storyManager.cur();
    if (d == null) return;

    // 重新同步矩阵，防止偏移
    game.shapeRenderer.setProjectionMatrix(game.viewport.getCamera().combined);
    game.batch.setProjectionMatrix(game.viewport.getCamera().combined);

    // A. 变暗遮罩 (Shape)
    Gdx.gl.glEnable(GL20.GL_BLEND);
    game.shapeRenderer.begin(ShapeType.Filled);
    game.shapeRenderer.setColor(0, 0, 0, 0.7f);
    game.shapeRenderer.rect(0, 0, game.viewport.getWorldWidth(), game.viewport.getWorldHeight());
    game.shapeRenderer.end();
    Gdx.gl.glDisable(GL20.GL_BLEND);

    // B. 加载立绘
    String path = "uistory/" + d.portrait;
    if (!path.equals(lastPortraitPath)) {
        if (currentPortrait != null) currentPortrait.dispose();
        currentPortrait = new com.badlogic.gdx.graphics.Texture(Gdx.files.internal(path));
        lastPortraitPath = path;
    }

    game.batch.begin();
    // 画人物（左侧）
    game.batch.draw(currentPortrait, 10, 80, 150, 220);
    
    // 画对话框背景
    game.batch.end();
    game.shapeRenderer.begin(com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled);
    game.shapeRenderer.setColor(0.1f, 0.1f, 0.2f, 0.9f);
    game.shapeRenderer.rect(10, 10, game.viewport.getWorldWidth()-20, 70);
    game.shapeRenderer.end();
    
    game.batch.begin();
    // 画名字和文本
    game.font.setColor(com.badlogic.gdx.graphics.Color.CYAN);
    game.font.draw(game.batch, d.name, 30, 70);
    game.font.setColor(com.badlogic.gdx.graphics.Color.WHITE);
    game.font.draw(game.batch, d.text, 30, 45);
    
    // 提示
    game.font.setColor(com.badlogic.gdx.graphics.Color.YELLOW);
    game.font.draw(game.batch, "NEXT [L-Click]", game.viewport.getWorldWidth()-120, 25);
    game.batch.end();
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
        } else if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)){
            callSave = true;
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
        //render中，清过屏了
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
        overHandling = true;
        // Move the control to self(inputProcessor part)
        Gdx.input.setInputProcessor(this);
    }

    private void handleSave(){
        saveHandling = true;
        // Move the control to self(inputProcessor part)
        Gdx.input.setInputProcessor(this);
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

    float textY = lpBarY + barHeight + 25f;
    
    //==========================================================================
    //血条和等级数值改为右侧显示
    //The old version: display the LP and Level under the screen

    // // 左侧：等级
    // game.font.draw(game.batch, "LV: " + player.getPlayerLevel(), padding, textY);
    
    // // 中间：血量数值
    // String lpText = "HP: " + (int)player.getLP() + " / " + (int)player.getMaxLP();
    // game.font.draw(game.batch, lpText, screenWidth / 2f - 45, textY);
    //==========================================================================
    
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
        if (currentPortrait != null) currentPortrait.dispose();
        if (discoTexture != null) discoTexture.dispose();
        if (fullLPTexture != null) fullLPTexture.dispose();
        if (halfLPTexture != null) halfLPTexture.dispose();
         // 如果 painter 或其他对象有 texture，也要 dispose
        for(Music music : playList)
            music.dispose();

    }

    //Implement input-processor
    @Override
    public boolean keyTyped(char character){
        //Here we simulating a real-time keyboard input process
        if(overHandling||saveHandling){
            // Enter means confirming (End input)
            if (character == '\r' || character == '\n') {
                confirmName();
            }
            // BackSpace
            else if (character == 8) {
                if (playerIDInput.length() > 0) {
                    // Delete the last char
                    playerIDInput = playerIDInput.substring(0, playerIDInput.length() - 1);
                }
            }
            // concatenate
            else if (playerIDInput.length() < 12) {
                // visible/normal char(letters and numbers etc.) only
                if (character >= 32 && character <= 126) {
                    playerIDInput += character;
                }
            }
            return true;
        }
        return false;
    }

    private void confirmName() {
        //Default Value
        if (playerIDInput.length() == 0) {
            playerIDInput = "Unknown";
        }
        
        //Release the input control (or we can't use it on the main menu)
        Gdx.input.setInputProcessor(null);

        if(overHandling){
            // Store ur grade into the leaderboard
            ArchiveManager.saveScoreLB(
                new ArchiveManager.ArchiveEntry(
                    playerIDInput, 
                    heroType,
                    mapName,
                    arena.getPlayerBike().getPlayerLevel(), 
                    arena.getPlayerBike().getExp()
                )
            );
        }else if(saveHandling){
            //Save the data to archive library
            ArchiveManager.saveScoreAch(
                new ArchiveManager.ArchiveEntry(
                    playerIDInput, 
                    heroType,
                    mapName,
                    arena.getPlayerBike().getPlayerLevel(), 
                    arena.getPlayerBike().getExp()
                )
            );
        }

        //LB screen
        game.setScreen(new LeaderBoard(game));
        this.dispose();
        
    }
    
    //No use
    @Override public boolean keyDown(int keycode) { return false; }
    @Override public boolean keyUp(int keycode) { return false; }
    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }
    @Override public boolean scrolled(float amountX, float amountY) { return false; }
    @Override public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {return false;}
}