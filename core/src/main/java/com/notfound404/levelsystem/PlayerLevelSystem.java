// 用途：玩家专用升级系统，包含技能选择功能，每2级提供技能选择选项
package com.notfound404.levelsystem;

import com.notfound404.character.Enemy;

/**
 * 玩家专用升级系统 - 包含技能选择
 */
public class PlayerLevelSystem extends BaseLevelSystem {
    
    // 技能选择记录
    private int bounceCount = 0;        // 飞盘反弹次数
    private int maxDiscs = 1;           // 最大飞盘数量
    private int projectileCount = 1;    // 弹道数量
    private int discsPerShot = 1;       // 每次攻击消耗飞盘数
    
    /**
     * 升级逻辑 - 玩家版
     */
    @Override
    protected void levelUp() {
        currentLevel++;
        
        // 重置当前经验（保留溢出部分）
        currentXP = currentXP - currentXPCap;
        
        // 提升经验上限
        currentXPCap *= XP_CAP_MULTIPLIER;
        
        // 应用基础属性提升（生命值、速度都×1.1）
        applyBaseStatUpgrade();
        
        // 玩家：每2级提供技能选择（从第2级开始）
        if (currentLevel >= 2 && currentLevel % 2 == 0) {
            presentSkillChoice();
        }
        
        // 触发升级事件
        onLevelUp();
    }
    
    /**
     * 基础属性提升：生命值和速度都乘以1.1
     */
    @Override
    protected void applyBaseStatUpgrade() {
        // 这些方法需要在Bike类中实现
        // 生命值 × 1.1
        // 速度 × 1.1
    
    }
    
    /**
     * 显示技能选择界面 - 玩家专用
     */
    public void presentSkillChoice() {
        //这里需要对接ui，选择升级

        // 实际游戏中应该暂停游戏，显示UI让玩家选择
        // 选择逻辑需要在UI中实现
    }
    
    /**
     * 应用技能选择 - 由UI调用
     * @param choice 选择编号 (1, 2, 3)
     */
    public void makeSkillChoice(int choice) {
        switch (choice) {
            case 1:
                bounceCount++;
                //飞盘反弹次数加一
                break;
            case 2:
                maxDiscs++;
                //ui2 增加飞盘上限
                break;
            case 3:
                projectileCount++;
                discsPerShot = projectileCount; // 消耗与弹道数相同
                //ui3 增加弹道
                break;
            default:
                //若无效默认1
                makeSkillChoice(1);
        }
    }
    
    /**
     * 击败敌人增加经验（增强版）
     * @param enemy 被击败的敌人
     */
    public void addXPFromEnemy(Enemy enemy) {
        int xpGained = enemy.getXPForDefeating();
        currentXP += xpGained;
        
        while (currentXP >= currentXPCap) {
            levelUp();
        }
    }
    
    /**
     * 是否可以发射飞盘
     */
    public boolean canShootDisc(int currentDiscs) {
        return currentDiscs >= discsPerShot;
    }
    
    /**
     * 消耗飞盘（发射时）
     */
    public int consumeDiscsForShot() {
        return discsPerShot;
    }
    
    // Getters
    public int getBounceCount() { return bounceCount; }
    public int getMaxDiscs() { return maxDiscs; }
    public int getProjectileCount() { return projectileCount; }
    public int getDiscsPerShot() { return discsPerShot; }

}