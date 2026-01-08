package com.notfound404.character;

//升级系统
import com.notfound404.levelsystem.PlayerLevelSystem;
//.....
import com.notfound404.arena.GameArena;
import com.notfound404.character.Bike;
import com.badlogic.gdx.graphics.Color;

public class Player extends Bike {
    public final String playerType;//"Tron" or "Kelvin"
    protected final String playerID;//Enter by player, used for RANKING system
    private PlayerLevelSystem levelSystem; // 玩家的升级系统

    //The constructor should later be modified as IO
    //这里之后改成文件读取

    public Player(String playerType, String playerID, int startX, int startY, GameArena arena) {
        super(arena, startX, startY, 2, Color.CYAN);
        this.playerType = playerType;
        this.playerID = playerID;

        initializeStatsByPlayerType();

        this.exp = 0;
        //The slot will be decided by the input file
        this.discoSlots = this.discoMAX =  3;

        this.dir = GameArena.Direction.UP;
        this.accumulator = 0;

        // 初始化玩家专用升级系统
        this.levelSystem = new PlayerLevelSystem();
        this.levelSystem.setPlayer(this);
    }


    /**
     * 根据玩家类型初始化属性
     */
    private void initializeStatsByPlayerType() {
        if (playerType != null) {
            switch (playerType.toLowerCase()) {
                case "tron":
                    // Tron: 平衡型角色
                    this.lp = this.maxLP = 50;
                    this.speed = 9;
                    break;
                case "kevin":
                case "kelvin": // 容错处理
                    // Kevin: 高速度型角色
                    this.lp = this.maxLP = 20;
                    this.speed = 15;
                    break;
                default:
                    // 默认值
                    this.lp = this.maxLP = 90;
                    this.speed = 9;
            }
        }
    }

    // ==========================================================
    //                    UI 系统对接接口
    // ==========================================================

    /**
     * 【UI对接点 1：检查升级弹窗】
     * UI系统在主循环中检测此状态。
     * @return true 表示达到偶数等级，UI应弹出技能三选一界面
     */
    public boolean isReadyForSkillChoice() {
        int level = getPlayerLevel();
        return level >= 2 && level % 2 == 0;
    }

    /**
     * 【UI对接点 2：处理玩家点击】
     * 玩家在UI界面点击按钮后，由UI系统回调此方法。
     * @param choice 按钮编号 (1:反弹, 2:上限, 3:弹道)
     */
    public void makeSkillChoice(int choice) {
        levelSystem.makeSkillChoice(choice);
    }

    /**
     * 【UI对接点 3：经验进度条】
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
    
    //获取经验百分比（用于UI进度条）
    public float getXPPercentage() {
        return levelSystem.getXPPercentage();
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
    
    /**
     * 回收飞盘
     * @param count 回收的数量
     */
    public void retrieveDiscs(int count) {
        int maxDiscs = levelSystem.getMaxDiscs();
        this.discoSlots = Math.min(this.discoSlots + count, maxDiscs);
    }
    
    /**
     * 获取飞盘反弹次数
     */
    public int getDiscBounceCount() {
        return levelSystem.getBounceCount();
    }
    
    /**
     * 获取最大飞盘数量
     */
    public int getMaxDiscs() {
        return levelSystem.getMaxDiscs();
    }
    
    /**
     * 获取弹道数量
     */
    public int getProjectileCount() {
        return levelSystem.getProjectileCount();
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
 
}