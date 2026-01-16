package com.notfound404.tron;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;

public class MenuScreen implements Screen {
    
    private final Main game;
    private final String[] menuItems = {
        "START GAME",
        "LOAD SYSTEM", 
        "OPTIONS",
        "RANKS",
        "EXIT"
    };
    
    private int selectedIndex = 0;
    private float glowTimer = 0f;
    private float scanLineY = 0f;
    
    // TRON Colors
    private final Color CYAN_GLOW = new Color(0f, 0.85f, 1f, 1f);
    private final Color ORANGE_GLOW = new Color(1f, 0.42f, 0f, 1f);
    private final Color GRID_COLOR = new Color(0f, 0.85f, 1f, 0.15f);
    
    public MenuScreen(Main game) {
        this.game = game;
    }
    
    @Override
    public void show() {
        // Initialize
    }
    
    @Override
    public void render(float delta) {
        // Clear screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //Viewport Setting
        game.viewport.apply();

        //Fix our painters to the coordinates of camera
        //绑定画图工具的坐标到相机坐标系
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);
        game.shapeRenderer.setProjectionMatrix(game.viewport.getCamera().combined);
        
        // Update timers
        glowTimer += delta;
        scanLineY += delta * 50f;
        if (scanLineY > game.viewport.getWorldHeight()) {
            scanLineY = -10f;
        }
        
        // Handle input
        handleInput();
        
        // Draw
        drawBackground();
        drawTitle();
        drawMenu();
        drawScanLine();
        drawCornerDecorations();
    }
    
    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.UP)) {
            selectedIndex = (selectedIndex - 1 + menuItems.length) % menuItems.length;
        }
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.DOWN)) {
            selectedIndex = (selectedIndex + 1) % menuItems.length;
        }
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ENTER)) {
            selectMenuItem();
        }
    }
    
    private void selectMenuItem() {
        switch (selectedIndex) {
            case 0: // START GAME
                game.setScreen(new PlayerSelectionScreen(game));
                break;
            case 1: // LOAD SYSTEM
                // TODO
                break;
            case 2: // OPTIONS
                // TODO
                break;
            case 3: // RANKS
                game.setScreen(new LeaderBoard(game));
                break;
            case 4: // EXIT
                Gdx.app.exit();
                break;
        }
    }
    
    private void drawBackground() {
        game.shapeRenderer.begin(ShapeType.Line);
        game.shapeRenderer.setColor(GRID_COLOR);
        
        // Draw grid
        int gridSize = 50;
        for (int x = 0; x < game.viewport.getWorldWidth(); x += gridSize) {
            game.shapeRenderer.line(x, 0, x, game.viewport.getWorldHeight());
        }
        for (int y = 0; y < game.viewport.getWorldHeight(); y += gridSize) {
            game.shapeRenderer.line(0, y, game.viewport.getWorldWidth(), y);
        }
        
        game.shapeRenderer.end();
    }
    
    private void drawTitle() {
        float viewportW = game.viewport.getWorldWidth();
        float viewportH = game.viewport.getWorldHeight();

        float centerX = viewportW / 2f;
        float titleY = viewportH - 55f;
        
        game.batch.begin();
        
        // Title "Tron"
        BitmapFont font = game.font; // Assume you have a large font
        GlyphLayout layout = new GlyphLayout(font, "TRON");
        
        // Draw glow effect
        float glowAlpha = 0.5f + 0.3f * MathUtils.sin(glowTimer * 2f);
        font.setColor(CYAN_GLOW.r, CYAN_GLOW.g, CYAN_GLOW.b, glowAlpha);
        font.getData().setScale(3f);
        font.draw(game.batch, "TRON", centerX - layout.width * 3 / 2, titleY);
        font.getData().setScale(1f);
        
        // Subtitle
        font.setColor(CYAN_GLOW.r, CYAN_GLOW.g, CYAN_GLOW.b, 0.7f);
        GlyphLayout subtitleLayout = new GlyphLayout(font, "Legacy");
        font.getData().setScale(0.5f);
        font.draw(game.batch, "Legacy", centerX - subtitleLayout.width / 4 + 12, titleY - 35);
        font.getData().setScale(1f);
        
        game.batch.end();
        
        // Draw decorative lines
        game.shapeRenderer.begin(ShapeType.Filled);
        game.shapeRenderer.setColor(CYAN_GLOW);
        game.shapeRenderer.rectLine(centerX - 200, titleY - 60, centerX + 200, titleY - 60, 1);
        game.shapeRenderer.end();
    }
    
    private void drawMenu() {
        float centerX = game.viewport.getWorldWidth() / 2f;
        float itemHeight = 50f;
        float totalMenuHeight = menuItems.length * itemHeight;
        float startY = game.viewport.getWorldHeight() / 2f + (totalMenuHeight / 2f) - 65f;
        
        game.batch.begin();
        
        for (int i = 0; i < menuItems.length; i++) {
            float y = startY - i * itemHeight;
            boolean isSelected = (i == selectedIndex);
            
            BitmapFont font = game.font;
            GlyphLayout layout = new GlyphLayout(font, menuItems[i]);
            
            if (isSelected) {
                // Selected item - orange glow
                float glowAlpha = 0.8f + 0.2f * MathUtils.sin(glowTimer * 5f);
                font.setColor(ORANGE_GLOW.r, ORANGE_GLOW.g, ORANGE_GLOW.b, glowAlpha);
                
                // Draw chevron
                font.draw(game.batch, ">", centerX - layout.width / 2 - 30, y);
            } else {
                // Unselected item - cyan
                font.setColor(CYAN_GLOW.r, CYAN_GLOW.g, CYAN_GLOW.b, 0.6f);
            }
            
            font.draw(game.batch, menuItems[i], centerX - layout.width / 2, y);
        }
        
        game.batch.end();
        
        // Draw selection border
        float selectedY = startY - selectedIndex * itemHeight;
        game.shapeRenderer.begin(ShapeType.Line);
        game.shapeRenderer.setColor(ORANGE_GLOW);
        game.shapeRenderer.rect(centerX - 200, selectedY - 40, 400, 50);
        game.shapeRenderer.end();
    }
    
    private void drawScanLine() {
        game.shapeRenderer.begin(ShapeType.Filled);
        game.shapeRenderer.setColor(CYAN_GLOW.r, CYAN_GLOW.g, CYAN_GLOW.b, 0.1f);
        game.shapeRenderer.rect(0, scanLineY, game.viewport.getWorldWidth(), 10);
        game.shapeRenderer.end();
    }
    
    private void drawCornerDecorations() {
        int cornerSize = 100;
        float w = game.viewport.getWorldWidth();
        float h = game.viewport.getWorldHeight();
        
        game.shapeRenderer.begin(ShapeType.Line);
        game.shapeRenderer.setColor(CYAN_GLOW);
        
        // Top-left
        game.shapeRenderer.line(20, h - 20, 20 + cornerSize, h - 20);
        game.shapeRenderer.line(20, w - 20, 20, h - 20 - cornerSize);
        
        // Top-right
        game.shapeRenderer.line(w - 20, h - 20, 
                               w - 20 - cornerSize, h - 20);
        game.shapeRenderer.line(w - 20, h - 20,
                               w - 20, h - 20 - cornerSize);
        
        // Bottom-left
        game.shapeRenderer.line(20, 20, 20 + cornerSize, 20);
        game.shapeRenderer.line(20, 20, 20, 20 + cornerSize);
        
        // Bottom-right
        game.shapeRenderer.line(w - 20, 20,
                               w - 20 - cornerSize, 20);
        game.shapeRenderer.line(w - 20, 20,
                               w - 20, 20 + cornerSize);
        
        game.shapeRenderer.end();
        
        // Corner dots
        game.shapeRenderer.begin(ShapeType.Filled);
        game.shapeRenderer.setColor(CYAN_GLOW);
        game.shapeRenderer.circle(20, h - 20, 3);
        game.shapeRenderer.circle(w - 20, h - 20, 3);
        game.shapeRenderer.circle(20, 20, 3);
        game.shapeRenderer.circle(w - 20, 20, 3);
        game.shapeRenderer.end();
    }
    
    @Override
    public void resize(int width, int height) {
        game.viewport.update(width, height, true);
    }
    
    @Override
    public void pause() {}
    
    @Override
    public void resume() {}
    
    @Override
    public void hide() {}
    
    @Override
    public void dispose() {}
}
