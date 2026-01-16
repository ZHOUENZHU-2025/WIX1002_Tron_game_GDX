package com.notfound404.tron;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.TextInputListener;
import com.notfound404.character.Player;
import com.notfound404.fileReader.ArchiveManager;

//This class inputs player's ID
public class NameInputListener implements TextInputListener {

    private Main game;
    private Player player;
    private String mapType;

    // Constructor
    public NameInputListener(Main game, Player player, String mapType) {
        this.game = game;
        this.player = player;
        this.mapType = mapType;
    }

    // 3. 当用户点击“确定”时会自动调用这个方法
    @Override
    public void input(String text) {
        // Default Name
        String playerName = text.trim();
        if (playerName.isEmpty()) {
            playerName = "Unknown";
        }

        //Save ur record in the leaderboard
        ArchiveManager.saveScoreLB(new ArchiveManager.ArchiveEntry(playerName, player.getPlayerType(), mapType, player.getLevel(), player.getExp()));

        // Back to Menu
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                game.setScreen(new LeaderBoard(game));
            }
        });
    }

    // 4. Cancel -> do not record
    @Override
    public void canceled() {
        // Back to the main menu
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                game.setScreen(new LeaderBoard(game));
            }
        });
    }
}