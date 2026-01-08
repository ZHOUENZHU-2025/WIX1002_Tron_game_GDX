package com.notfound404.levelsystem;

import com.notfound404.character.Enemy;

/**
 * 敌人专用升级系统 - 简化版，只负责随难度初始化和自动成长
 */
public class EnemyLevelSystem extends BaseLevelSystem {
    
    private int difficulty; // 1-简单, 2-中等, 3-困难, 4-极难
    private Enemy enemy;

    public EnemyLevelSystem(int difficulty) {
        super();
        this.difficulty = difficulty;
        // 根据难度预设等级
        initializeLevelByDifficulty();
    }

    public void setEnemy(Enemy enemy) {
        this.enemy = enemy;
    }

    /**
     * 根据选择的难度，决定敌人出生时的等级和经验门槛
     */
    private void initializeLevelByDifficulty() {
        switch (difficulty) {
            case 1: currentLevel = 1; break;
            case 2: currentLevel = 5; break;
            case 3: currentLevel = 10; break;
            case 4: currentLevel = 15; break;
            default: currentLevel = 1;
        }
        
        // 根据等级通过指数公式对齐当前的经验上限
        this.currentXPCap = BASE_XP_CAP * Math.pow(XP_CAP_MULTIPLIER, currentLevel - 1);
    }

    @Override
    protected void levelUp() {
        currentLevel++;
        currentXP = Math.max(0, currentXP - currentXPCap);
        currentXPCap *= XP_CAP_MULTIPLIER;
        
        // 敌人升级也按1.1倍提升属性
        applyBaseStatUpgrade();
    }

    @Override
    protected void applyBaseStatUpgrade() {
        if (enemy != null) {
            enemy.maxLP = (int)(enemy.maxLP * STAT_MULTIPLIER);
            enemy.lp = enemy.maxLP; // 升级补血
            enemy.speed = enemy.speed * STAT_MULTIPLIER;
        }
    }
}