package com.notfound404.levelsystem;

/**
 * 升级系统基类 - 包含公共逻辑
 */
public abstract class BaseLevelSystem {
    
    // 基础配置
    protected static final double BASE_XP_PER_CELL = 0.5;
    protected static final int BASE_XP_CAP = 300;
    protected static final double XP_CAP_MULTIPLIER = 1.5;
    protected static final double STAT_MULTIPLIER = 1.1; // 生命值和速度都乘以1.1
    
    // 升级相关数据
    protected int currentLevel = 1;
    protected double currentXP = 0;
    protected double currentXPCap = BASE_XP_CAP;
    
    /**
     * 移动时增加经验
     */
    public void addXPFromMovement(int cellsMoved) {
        if (cellsMoved <= 0) return;
        
        double xpGained = cellsMoved * BASE_XP_PER_CELL;
        currentXP += xpGained;
        
        while (currentXP >= currentXPCap) {
            levelUp();
        }
    }
    
    /**
     * 击败敌人增加经验（基础版）
     */
    public void addXPFromEnemy(int enemyLevel) {
        double xpGained = 10 * enemyLevel;
        currentXP += xpGained;
        
        while (currentXP >= currentXPCap) {
            levelUp();
        }
    }
    
    /**
     * 抽象方法：升级逻辑（由子类实现）
     */
    protected abstract void levelUp();
    
    /**
     * 抽象方法：基础属性提升（由子类实现）
     */
    protected abstract void applyBaseStatUpgrade();
    
    /**
     * 获取属性提升乘数
     */
    public static double getStatMultiplier() {
        return STAT_MULTIPLIER;
    }
    
    /**
     * 获取基础经验上限
     */
    public static int getBaseXpCap() {
        return BASE_XP_CAP;
    }
    
    // Getters
    public int getCurrentLevel() { return currentLevel; }
    public double getCurrentXP() { return currentXP; }
    public double getCurrentXPCap() { return currentXPCap; }
    
    /**
     * 获取经验百分比（用于UI进度条）
     */
    public float getXPPercentage() {
        return (float)(currentXP / currentXPCap);
    }
}