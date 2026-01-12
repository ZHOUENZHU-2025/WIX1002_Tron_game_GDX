package com.notfound404.tron;

//This class display the Screen to select a player
//Get a String and pass it to the next screen
//At last initialize GameArena with the String  

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

public class PlayerSelectionScreen implements Screen {
    private final Main game;
    private final String[] heroes = {"TRON", "KEVIN"};
    private int selectedIndex = 0;

    private final Color CYAN_GLOW = new Color(0f, 0.85f, 1f, 1f);
    private final Color ORANGE_GLOW = new Color(1f, 0.42f, 0f, 1f);

    public PlayerSelectionScreen(Main game) {
        this.game = game;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        handleInput();
        drawUI();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            selectedIndex = (selectedIndex - 1 + heroes.length) % heroes.length;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) || Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            selectedIndex = (selectedIndex + 1) % heroes.length;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            // 跳转到地图选择，并将选择的英雄名称传过去
            game.setScreen(new MapSelectionScreen(game, heroes[selectedIndex]));
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MenuScreen(game));
        }
    }

    private void drawUI() {
        float centerX = Gdx.graphics.getWidth() / 2f;
        game.batch.begin();

        // 标题
        game.font.setColor(CYAN_GLOW);
        game.font.draw(game.batch, "SELECT YOUR PROGRAM", centerX - 120, Gdx.graphics.getHeight() - 100);

        // 选项渲染
        for (int i = 0; i < heroes.length; i++) {
            float y = Gdx.graphics.getHeight() / 2f - i * 60f;
            GlyphLayout layout = new GlyphLayout(game.font, heroes[i]);
            
            if (i == selectedIndex) {
                game.font.setColor(ORANGE_GLOW);
                game.font.draw(game.batch, "> " + heroes[i] + " <", centerX - (layout.width / 2) - 20, y);
            } else {
                game.font.setColor(Color.GRAY);
                game.font.draw(game.batch, heroes[i], centerX - layout.width / 2, y);
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