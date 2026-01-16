package com.notfound404.tron;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

//This class is the screen for MapSelection

public class MapSelectionScreen implements Screen {

    private final Main game;

    protected String heroType;

    private final String[] maps = {
        "Mirror Battery.txt",
        "Death Canyon.txt",
        "Accelerate Territory.txt",
        "RANDOM"
    };

    private int selectedIndex = 0;
    private final Color CYAN_GLOW = new Color(0f, 0.85f, 1f, 1f);
    private final Color ORANGE_GLOW = new Color(1f, 0.42f, 0f, 1f);

    //Constructor: Passing the Main done.
    public MapSelectionScreen(Main game, String heroType) {
        this.game = game;
        this.heroType = heroType;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //Viewport Setting
        game.viewport.apply();

        //Fix our painters to the coordinates of camera
        //绑定画图工具的坐标到相机坐标系
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);
        game.shapeRenderer.setProjectionMatrix(game.viewport.getCamera().combined);

        handleInput();
        drawUI();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selectedIndex = (selectedIndex - 1 + maps.length) % maps.length;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selectedIndex = (selectedIndex + 1) % maps.length;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            // Pass the selected map name to GameScreen
            game.setScreen(new GameScreen(game, maps[selectedIndex], heroType));
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new PlayerSelectionScreen(game));
        }
    }

    private void drawUI() {
        float centerX = game.viewport.getWorldWidth() / 2f;
        float startY = game.viewport.getWorldHeight() / 2f + 50f;
        
        game.batch.begin();
        
        // Header
        game.font.setColor(CYAN_GLOW);
        game.font.draw(game.batch, "SELECT GRID", centerX - 100, game.viewport.getWorldHeight() - 100);

        // List
        for (int i = 0; i < maps.length; i++) {
            float y = startY - i * 60f;
            BitmapFont font = game.font;
            GlyphLayout layout = new GlyphLayout(font, maps[i].replace(".txt", ""));

            if (i == selectedIndex) {
                font.setColor(ORANGE_GLOW);
                font.draw(game.batch, "> " + maps[i].replace(".txt", ""), centerX - layout.width / 2 - 20, y);
            } else {
                font.setColor(Color.GRAY);
                font.draw(game.batch, maps[i].replace(".txt", ""), centerX - layout.width / 2, y);
            }
        }
        game.batch.end();
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) { game.viewport.update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {}
}