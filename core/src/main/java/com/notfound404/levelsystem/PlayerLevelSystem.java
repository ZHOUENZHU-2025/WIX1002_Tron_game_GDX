package com.notfound404.levelsystem;

import com.notfound404.character.Enemy;
import com.notfound404.character.Player;

/**
 * 玩家专用升级系统 - 扩展了技能选择功能（每2级一次）
 */
public class PlayerLevelSystem extends BaseLevelSystem {
    
    // 玩家特有的技能统计
    private int bounceCount = 0;      // 飞盘反弹次数
    private int maxDiscs = 3;         // 最大持有飞盘数
    private int projectileCount = 1;  // 同时发射的弹道数
    private int discsPerShot = 1;     // 每次发射消耗的飞盘
    
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
            presentSkillChoice();
        }
    }
    
    /**
     * 直接修改Player实体类中的属性字段
     */
    @Override
    protected void applyBaseStatUpgrade() {
        if (player != null) {
            // 生命值同步：基础属性提高1.1倍，并补满当前血量
            player.maxLP = (int)(player.maxLP * STAT_MULTIPLIER);
            player.lp = player.maxLP; 
            
            // 速度同步：提高1.1倍
            player.speed = player.speed * STAT_MULTIPLIER;
            
            // 将等级和经验同步回Player类中的变量，方便UI读取
            player.level = currentLevel;
            player.exp = (int)currentXP;
        }
    }
    
    /**
     * 技能选择界面入口（由UI系统重写或对接）目前预留在player里的ui对接
     */
    public void presentSkillChoice() {
        // 此处应触发UI显示，暂停游戏等
        //也许未来的逻辑是把player里的这个选择检查以及一些系列的ui跳转挪到这里来，再来对接ui
    }
    
    /**
     * 玩家做出选择后的逻辑处理
     * @param choice 1:增加反弹, 2:增加上限, 3:增加弹道
     */
    public void makeSkillChoice(int choice) {
        switch (choice) {
            case 1: bounceCount++; break;
            case 2: maxDiscs++; break;
            case 3: 
                projectileCount++;
                discsPerShot = projectileCount; // 弹道越多，单次消耗越多
                break;
            default: makeSkillChoice(1); // 默认选1
        }
    }

    /**
     * 根据敌人难度和等级获取经验值
     */
    public void addXPFromEnemy(Enemy enemy) {
        addXP(enemy.getXPForDefeating());
    }
    
    // --- 飞盘射击逻辑判断接口 ---
    public boolean canShootDisc(int currentDiscs) { return currentDiscs >= discsPerShot; }
    public int consumeDiscsForShot() { return discsPerShot; }

    // --- Getter 供 Player 实体调用 ---
    public int getBounceCount() { return bounceCount; }
    public int getMaxDiscs() { return maxDiscs; }
    public int getProjectileCount() { return projectileCount; }
    public int getDiscsPerShot() { return discsPerShot; }
}