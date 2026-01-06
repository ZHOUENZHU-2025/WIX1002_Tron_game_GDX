// 用途：敌人AI专用升级系统，包含难度设置，只有基础属性升级
package com.notfound404.levelsystem;

/**
 * 敌人AI专用升级系统 - 包含难度设置，只有基础属性升级
 */
public class EnemyLevelSystem extends BaseLevelSystem {
    
    private int difficulty; // 敌人难度级别
    
    public EnemyLevelSystem(int difficulty) {
        super();
        this.difficulty = difficulty;
        // 根据难度设置初始等级
        initializeLevelByDifficulty();
    }
    
    /**
     * 根据难度初始化等级
     */
    private void initializeLevelByDifficulty() {
        // 难度1-4对应不同的初始等级
        switch (difficulty) {
            case 1: // 简单
                currentLevel = 1;
                break;
            case 2: // 中等
                currentLevel = 5;
                break;
            case 3: // 困难
                currentLevel = 10;
                break;
            case 4: // 极难
                currentLevel = 15;
                break;
            default:
                currentLevel = 1;
        }
        
        // 根据等级计算经验上限
        for (int i = 1; i < currentLevel; i++) {
            currentXPCap *= XP_CAP_MULTIPLIER;
        }
    }
    
    /**
     * 升级逻辑 - 敌人版（无技能选择）
     */
    @Override
    protected void levelUp() {
        currentLevel++;
        
        // 重置当前经验
        currentXP = currentXP - currentXPCap;
        
        // 提升经验上限
        currentXPCap *= XP_CAP_MULTIPLIER;
        
        // 应用基础属性提升（生命值、速度都×1.1）
        applyBaseStatUpgrade();
    }
    
    /**
     * 基础属性提升：生命值和速度都乘以1.1
     */
    @Override
    protected void applyBaseStatUpgrade() {
        // 敌人基础属性提升逻辑
        // 生命值 × 1.1
        // 速度 × 1.1
        // 实际游戏中这些属性提升应该在对应的游戏对象中应用
    }
    
    /**
     * 获取敌人难度
     */
    public int getDifficulty() {
        return difficulty;
    }
}