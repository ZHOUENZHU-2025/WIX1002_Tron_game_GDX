package com.notfound404.levelsystem;

/**
 * 升级系统基类 - 包含所有角色通用的经验计算和升级逻辑
 */
public abstract class BaseLevelSystem {
    
    // --- 基础配置常量 ---
    protected static final double BASE_XP_PER_CELL = 5; // 每移动一格获得的经验
    protected static final int BASE_XP_CAP = 300;        // 1级升2级的基础经验上限
    protected static final double XP_CAP_MULTIPLIER = 1.5; // 每升一级，下一级所需经验翻1.5倍
    protected static final double STAT_MULTIPLIER = 1.1;   // 每次升级属性（血量/速度）提升10%
    
    // --- 动态运行数据 ---
    protected int currentLevel = 1;      // 当前等级
    protected double currentXP = 0;      // 当前累积的经验值
    protected double currentXPCap = BASE_XP_CAP; // 当前等级的经验上限
    
    /**
     * 移动时增加经验的方法
     * @param cellsMoved 移动的格子数
     */
    public void addXPFromMovement(int cellsMoved) {
        if (cellsMoved <= 0) return;
        
        double xpGained = cellsMoved * BASE_XP_PER_CELL;
        currentXP += xpGained;
        
        // 检查是否满足升级条件（使用while处理连续升级的情况）
        checkLevelUp();
    }
    
    /**
     * 通用的增加经验接口（用于击败敌人等场景）
     */
    public void addXP(double amount) {
        if (amount <= 0) return;
        currentXP += amount;
        checkLevelUp();
    }

    /**
     * 核心逻辑：循环检查当前经验是否超过上限，超过则执行升级
     */
    private void checkLevelUp() {
        while (currentXP >= currentXPCap) {
            levelUp();
        }
    }
    
    // 抽象方法：由玩家或敌人系统自行实现具体的升级行为
    protected abstract void levelUp();
    protected abstract void applyBaseStatUpgrade();
    
    // --- 工具Getter方法 ---
    public static double getStatMultiplier() { return STAT_MULTIPLIER; }
    public int getCurrentLevel() { return currentLevel; }
    public double getCurrentXP() { return currentXP; }
    public double getCurrentXPCap() { return currentXPCap; }
    
    /**
     * 返回当前等级经验进度百分比（0.0-1.0），用于UI进度条显示
     */
    public float getXPPercentage() {
        return (float)(currentXP / currentXPCap);
    }
}