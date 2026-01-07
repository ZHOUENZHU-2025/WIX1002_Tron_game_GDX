package com.notfound404.tron;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.ScreenUtils;

/** First screen of the application. Displayed after the application is created. */
public class FirstScreen implements Screen {

    public final Main game;

    FirstScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        // Prepare your screen here.
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);

        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);

        //Here we draw the First Menu
        //这里就是主菜单界面，需要画主菜单的在这里写或者调用
        drawMenu();
        launch_game();
    }

    private void drawMenu() {

        //绘制主菜单，后续可以在此基础上更改
        String title = "Welcome to Tron!!!";
        String subtitle = "Click anywhere to start!";

        // 1. 使用 GlyphLayout 计算文字的宽度
        GlyphLayout layout1 = new GlyphLayout(game.font, title);
        GlyphLayout layout2 = new GlyphLayout(game.font, subtitle);

        // 2. 计算坐标： (视口宽度 - 文字宽度) / 2
        float x1 = (game.viewport.getWorldWidth() - layout1.width) / 2;
        float x2 = (game.viewport.getWorldWidth() - layout2.width) / 2;

        game.batch.begin();
        // 绘制标题（y 轴设为中线稍微偏上一点）
        game.font.draw(game.batch, title, x1, game.viewport.getWorldHeight() / 2 + 50);
        // 绘制副标题（y 轴设为中线稍微偏下一点）
        game.font.draw(game.batch, subtitle, x2, game.viewport.getWorldHeight() / 2 - 20);
        game.batch.end();
    }

    private void launch_game() {
        if(Gdx.input.isTouched()){
            game.setScreen(new GameScreen(game));
            dispose();
        }
    }

    @Override
    public void resize(int width, int height) {
        // If the window is minimized on a desktop (LWJGL3) platform, width and height are 0, which causes problems.
        // In that case, we don't resize anything, and wait for the window to be a normal size before updating.
        if(width <= 0 || height <= 0) return;

        // Resize your screen here. The parameters represent the new window size.
        game.viewport.update(width, height, true);
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