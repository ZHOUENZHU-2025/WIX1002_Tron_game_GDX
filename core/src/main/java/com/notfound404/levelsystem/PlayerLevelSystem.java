package com.notfound404.levelsystem;

import com.notfound404.character.Enemy;
import com.notfound404.character.Player;

/**
 * 玩家专用升级系统 - 扩展了技能选择功能（每2级一次）
 */
public class PlayerLevelSystem extends BaseLevelSystem {
    
    // 玩家特有的技能统计
    private Player player; // 持有玩家引用，用于修改属性

    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * 玩家升级逻辑
     */
    @Override
    protected void levelUp() {
        currentLevel++;
        currentXP = currentXP - currentXPCap; // 扣除消耗掉的经验，保留溢出的部分
        currentXPCap *= XP_CAP_MULTIPLIER;   // 提高下一级的难度
        
        // 1. 调用通用的基础属性提升
        applyBaseStatUpgrade();
        
        // 2. 玩家特有逻辑：每2级获得一次技能强化机会
        if (currentLevel >= 2 && currentLevel % 2 == 0) {
            if (player != null) {
            player.setDiscoMAX(player.getDiscoMAX() + 1);
            player.setDiscoSlots(player.getDiscoSlots() + 1); // 升级奖励补弹
            player.setDiscoRange(player.getDiscoRange() + 1);
        }
    }
    }

    /**
     * 直接修改Player实体类中的属性字段
     */
    @Override
    protected void applyBaseStatUpgrade() {
        if (player != null) {
            
            // --- 1. 血量升级逻辑 (上限设为 25) ---
            float newMaxLP = (float)(player.getMaxLP() * STAT_MULTIPLIER);
            // 使用 Math.min(计算值, 25.0f) 确保血量上限不会超过 25
            player.setMaxLP(Math.min(newMaxLP, 25.0f)); 
            player.setLP(player.getMaxLP()); // 升级补满血

            // --- 2. 速度升级逻辑 (上限设为 9) ---
            float newSpeed = (float)(player.getSpeed() * STAT_MULTIPLIER);
            // 使用 Math.min(计算值, 9.0f) 确保速度不会过快导致无法操作
            player.setSpeed(Math.min(newSpeed, 9.0f));

            // 同步等级和经验
            player.setLevel(currentLevel);
            player.setExp((int)currentXP);
        }
    }

    /**
     * 根据敌人难度和等级获取经验值
     */
    public void addXPFromEnemy(Enemy enemy) {
        addXP(enemy.getXPForDefeating());
    }
    
    // --- 飞盘射击逻辑判断接口 ---
    public boolean canShootDisc(int currentDiscs) { return currentDiscs >= 1; }
    public int consumeDiscsForShot() { return 1; }

    // --- Getter 供 Player 实体调用 ---
    public int getDiscsPerShot() { return 1; }
}