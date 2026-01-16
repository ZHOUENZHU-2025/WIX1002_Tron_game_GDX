package com.notfound404.tron;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.notfound404.fileReader.ArchiveManager;
import com.notfound404.fileReader.ArchiveManager.ArchiveEntry;

public class LeaderBoard implements Screen{
    final Main game;
    ArrayList<ArchiveManager.ArchiveEntry> topPlayers;

    public LeaderBoard(Main game){
        this.game = game;
        topPlayers = ArchiveManager.getTopScores(10);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);

        game.batch.begin();
        game.font.setColor(Color.YELLOW);
        game.font.draw(game.batch, "TOP AGENTS", 200, 350);
        
        game.font.setColor(Color.WHITE);
        float y = 300;
        int rank = 1;

        String title = "Rank        ID        LV    SCORE    HERO";
        game.font.draw(game.batch, title, 150, y);
        y-=30;
        
        for (ArchiveEntry entry : topPlayers) {
            String text = String.format("%-2d.    %12s   %3d          %6d        %5s", rank, entry.playerID, entry.level, entry.score, entry.level);
            game.font.draw(game.batch, text, 150, y);
            y -= 30;
            rank++;
        }
        
        game.font.setColor(Color.GRAY);
        game.font.draw(game.batch, "Press ESC to Return", 150, 50);
        game.batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MenuScreen(game));
        }
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) { game.viewport.update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {}
}
