package com.notfound404.character;

import com.notfound404.levelsystem.EnemyLevelSystem;

import com.notfound404.arena.GameArena;
import com.badlogic.gdx.graphics.Color;
import com.notfound404.levelsystem.*;

public class Enemy extends Bike {
    public static int enemyCount = 0;
    private EnemyLevelSystem levelSystem; // 敌人的升级系统
    private int difficulty; // 敌人难度级别

    public Enemy(GameArena arena, int x, int y, Color color, int difficulty) {
        super(arena, x, y, 2, color);
        this.difficulty = difficulty;
        this.levelSystem = new EnemyLevelSystem(difficulty);
        
        initializeStatsByLevel();

        enemyCount++;
    }


private void initializeStatsByLevel() {
        int level = levelSystem.getCurrentLevel();
        
        // 生命值 = 基础值 × (1.1^(等级-1))
        this.maxHealth = (int)(100 * Math.pow(BaseLevelSystem.getStatMultiplier(), level - 1));
        this.currentHealth = maxHealth;
        
        // 速度 = 基础值 × (1.1^(等级-1))
        this.speed = 1.0 * Math.pow(BaseLevelSystem.getStatMultiplier(), level - 1);
    }
    
    @Override
    public void onMove(int cellsMoved) {
        super.onMove(cellsMoved);
        // 敌人移动也可以获得经验，和玩家一样
        levelSystem.addXPFromMovement(cellsMoved);
    }
    
    // 获取升级系统
    public EnemyLevelSystem getLevelSystem() {
        return levelSystem;
    }
    
    /**
     * 敌人被击败时，给玩家提供经验
     * 最高等级敌人（Clu）提供1000经验
     */
    public int getXPForDefeating() {
        int level = levelSystem.getCurrentLevel();
        
        // 基础经验 = 10 × 等级
        int baseXP = 10 * level;
        
        // 难度加成
        int difficultyBonus = 0;
        switch (difficulty) {
            case 1: // 简单
                difficultyBonus = level * 5;
                break;
            case 2: // 中等
                difficultyBonus = level * 10;
                break;
            case 3: // 困难
                difficultyBonus = level * 20;
                break;
            case 4: // 极难（Clu）- 保证至少1000经验
                difficultyBonus = Math.max(1000 - baseXP, level * 30);
                break;
        }
        
        int totalXP = baseXP + difficultyBonus;
        
        // 确保Clu提供1000经验
        if (difficulty == 4 && totalXP < 1000) {
            totalXP = 1000;
        }
        
        return totalXP;
    }
    /**
     * 获取敌人等级
     */
    public int getEnemyLevel() {
        return levelSystem.getCurrentLevel();
    }
    
    /**
     * 获取敌人难度
     */
    public int getDifficulty() {
        return difficulty;
    }
}