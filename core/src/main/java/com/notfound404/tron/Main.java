package com.notfound404.tron;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {

    public FitViewport viewport;
    public ShapeRenderer shapeRenderer;
    public BitmapFont font;
    public SpriteBatch batch;

    @Override
    public void create() {
        viewport = new FitViewport(528, 396);
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        batch = new SpriteBatch();

        font.setUseIntegerPositions(false);
        font.getData().setScale(viewport.getWorldHeight()/Gdx.graphics.getHeight());

        // Set the first screen
        this.setScreen(new FirstScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        batch.dispose();
        font.dispose();
    }

}