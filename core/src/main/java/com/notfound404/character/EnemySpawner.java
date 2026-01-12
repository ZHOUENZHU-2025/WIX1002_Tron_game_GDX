package com.notfound404.character;
import com.notfound404.arena.GameArena;
import com.badlogic.gdx.graphics.Color;

public class EnemySpawner {

    /**
     * Generate enemies with different difficulty levels
     * @param arena Game arena
     */
    public void spawnAllDifficultyEnemies(GameArena arena) {

        // --- 1. Easy (Difficulty 1) ---
        // EnemyLevelSystem case 1 -> grade 1
        Enemy easyEnemy = new Enemy(
            arena,          // Scene
            100, 200,       // x, y coordinate
            //Color.GREEN,    // Green for Easy level
            1               // Initialized grade：1
        );
        System.out.println("Enemy(Easy): HP=" + easyEnemy.maxLP + ", Speed=" + easyEnemy.speed);


        // --- 2. Medium (Difficulty 2) ---
        //  EnemyLevelSystem case 2 -> grade 5
        Enemy mediumEnemy = new Enemy(
            arena,
            100, 300,
            //Color.YELLOW,   // Yellow for Medium level
            5               // Initialized grade: 2
        );
        System.out.println("Enemy(Medium): HP=" + mediumEnemy.maxLP + ", Speed=" + mediumEnemy.speed);


        // --- 3. Hard (Difficulty 3) ---
        //  EnemyLevelSystem case 3 -> grade 10
        Enemy hardEnemy = new Enemy(
            arena,
            100, 400,
            //Color.ORANGE,   // Orange for Hard level
            10              // Initialized grade: 10
        );
        System.out.println("Enemy(Hard): HP=" + hardEnemy.maxLP + ", Speed=" + hardEnemy.speed);


        // --- 4. Impossible (Difficulty 4) ---
        //  EnemyLevelSystem case 4 -> grade 15
        Enemy extremeEnemy = new Enemy(
            arena,
            100, 500,
            //Color.RED,      // Red for Impossible
            15              // Initialized grade: 15
        );
        System.out.println("Enemy(Impossible): HP=" + extremeEnemy.maxLP + ", Speed=" + extremeEnemy.speed);
    }
}
