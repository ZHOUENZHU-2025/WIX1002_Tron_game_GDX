package com.notfound404.character;

//升级系统
import com.notfound404.levelsystem.PlayerLevelSystem;
//.....
import com.notfound404.arena.GameArena;
import com.badlogic.gdx.graphics.Color;

public class Player extends Bike {
    public final String playerType;//"Tron" or "Kevin"
    protected final String playerID;//Enter by player, used for RANKING system

    private PlayerLevelSystem levelSystem; // 玩家的升级系统

    //The constructor should later be modified as IO
    //这里之后改成文件读取

    public Player(String playerType, String playerID, int startX, int startY, GameArena arena) {
        super(arena, startX, startY, 2, Color.CYAN);
        this.playerType = playerType;
        this.playerID = playerID;

        // 1. 设置初始等级和经验 (使用从 Bike 继承的变量)
        this.level = 1;
        this.exp = 0;

        initializeStatsByPlayerType();

        this.exp = 0;
        //The slot will be decided by the input file
        this.discoSlots = this.discoMAX =  3;
        this.discoRange = 5;

        this.dir = GameArena.Direction.LEFT;
        this.accumulator = 0;

        this.levelSystem = new PlayerLevelSystem();
        this.levelSystem.setPlayer(this);
    }

    // 飞盘逻辑直接读取 Bike 的变量
    public void retrieveDiscs(int count) {
        // 此时 discoMAX 已经是被 PlayerLevelSystem 升级过的值
        this.discoSlots = Math.min(this.discoSlots + count, this.discoMAX);
    }


    /**
     * 根据玩家类型初始化属性
     */
    private void initializeStatsByPlayerType() {
        if (playerType != null) {
            switch (playerType.toLowerCase()) {
                case "tron":
                    // Tron: 平衡型角色
                    this.lp = this.maxLP = 5;
                    this.speed = 5;
                    break;
                case "kevin":
                    // Kevin: 高速度型角色
                    this.lp = this.maxLP = 3;
                    this.speed = 8;
                    break;
                default:
                    // 默认值
                    this.lp = this.maxLP = 5;
                    this.speed = 5;
            }
        }
    }

    // ==========================================================
    //                    UI 系统对接接口
    // ==========================================================

    
    /**
     * 【UI对接点：经验进度条】
     * 供UI渲染层调用，获取当前等级经验百分比。
     */
    public float getXPPercentage() {
        return levelSystem.getXPPercentage();
    }

    
    //移动获得经验
    @Override
    protected void moveOneStep() {
        super.moveOneStep(); // 先执行父类的移动逻辑
        // 玩家移动获取经验（每次移动1格）
        levelSystem.addXPFromMovement(1);
    }
    
    /**
     * 击败敌人
     */
    public void onEnemyDefeated(Enemy enemy) {
        // 使用增强版经验获取方法
        levelSystem.addXPFromEnemy(enemy);
    }

    public void applyLevelUpStats() {
        // 逻辑已在 System 类中处理，此处不再重复
    }
    
    
    // 获取升级系统
    public PlayerLevelSystem getLevelSystem() {
        return levelSystem;
    }

    //获取玩家当前等级
    public int getPlayerLevel() {
        return levelSystem.getCurrentLevel();
    }
    
    // 获取玩家当前经验
    public double getCurrentXP() {
        return levelSystem.getCurrentXP();
    }
    
    // 获取玩家经验上限
    public double getXPCap() {
        return levelSystem.getCurrentXPCap();
    }

    
    //是否可以发射飞盘
    public boolean canShootDisc() {
        // 直接转发给你的系统
        return levelSystem.canShootDisc(this.discoSlots);
    }
    
    /**
     * 消耗飞盘进行射击
     * @return 实际消耗的飞盘数量
     */
    public int consumeDiscsForShot() {
        int discsToConsume = levelSystem.consumeDiscsForShot();
        if (this.discoSlots >= discsToConsume) {
            this.discoSlots -= discsToConsume;
            return discsToConsume;
        }
        return 0;
    }
    
    
    
    @Override
    public int getDiscoMAX() {
        return this.discoMAX; 
}
    /**
     * 获取每次射击消耗的飞盘数
     */
    public int getDiscsPerShot() {
        return levelSystem.getDiscsPerShot();
    }
    
    /**
     * 获取玩家类型
     */
    public String getPlayerType() {
        return playerType;
    }
    
    /**
     * 获取玩家ID
     */
    public String getPlayerID() {
        return playerID;
    }
     
    @Override
    public void update(float deltaTime){
        super.update(deltaTime);
        exp+=Enemy.EXPplus;
        Enemy.resetExpAcc();
    }
}