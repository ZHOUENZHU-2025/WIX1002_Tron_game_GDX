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
        this.exp = 0;
        //The slot will be decided by the input file
        this.discoSlots = this.discoMAX =  3;

        this.dir = GameArena.Direction.UP;
        this.accumulator = 0;

        
        // 初始化玩家专用升级系统
        this.levelSystem = new PlayerLevelSystem();
        // 根据playerType初始化不同的基础属性
        initializeStatsByPlayerType();
    }
    /**
     * 根据玩家类型初始化属性
     */
    private void initializeStatsByPlayerType() {
        if (playerType != null) {
            switch (playerType.toLowerCase()) {
                case "tron":
                    // Tron: 平衡型角色
                    this.lp = this.maxLP = 120;
                    this.speed = 12;
                    break;
                case "kevin":
                case "kelvin": // 容错处理
                    // Kevin: 高速度型角色
                    this.lp = this.maxLP = 100;
                    this.speed = 15;
                    break;
                default:
                    // 默认值
                    this.lp = this.maxLP = 110;
                    this.speed = 13;
            }
        }
    }
@Override
    public void onMove(int cellsMoved) {
        super.onMove(cellsMoved);
        // 玩家移动获得经验
        levelSystem.addXPFromMovement(cellsMoved);
    }
    
    /**
     * 击败敌人
     */
    public void onEnemyDefeated(Enemy enemy) {
        // 使用增强版经验获取方法
        levelSystem.addXPFromEnemy(enemy);
    }

    //显示升级选择界面（玩家专用）
    // 这个方法应该被游戏UI系统调用
     
    public void showLevelUpChoice() {
        // 占位，实际应该由UI系统处理
        // UI系统调用 levelSystem.presentSkillChoice() 来显示技能选择界面
    }
    



    //应用技能选择选择编号 (1, 2, 3)
    
    public void makeSkillChoice(int choice) {
        levelSystem.makeSkillChoice(choice);
    }
    
    /**
     * 升级时应用基础属性提升
     */
    public void applyLevelUpStats() {
        // 生命值提升
        this.maxLP = (int)(this.maxLP * PlayerLevelSystem.getStatMultiplier());
        this.lp = this.maxLP; // 升级时回满血
        
        // 速度提升
        this.speed *= PlayerLevelSystem.getStatMultiplier();
    }
    
    // 获取升级系统
    public PlayerLevelSystem getLevelSystem() {
        return levelSystem;
    }
    
    /**
     * 获取玩家当前等级
     */
    public int getPlayerLevel() {
        return levelSystem.getCurrentLevel();
    }
    
    /**
     * 获取玩家当前经验
     */
    public double getCurrentXP() {
        return levelSystem.getCurrentXP();
    }
    
    /**
     * 获取玩家经验上限
     */
    public double getXPCap() {
        return levelSystem.getCurrentXPCap();
    }
    
    /**
     * 获取经验百分比（用于UI进度条）
     */
    public float getXPPercentage() {
        return levelSystem.getXPPercentage();
    }
    
    /**
     * 是否可以发射飞盘
     */
    public boolean canShootDisc() {
        // 假设当前飞盘数量从discoSlots获取
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
    
    /**
     * 检查是否达到技能选择等级（每2级一次）
     */
    public boolean isReadyForSkillChoice() {
        int level = getPlayerLevel();
        return level >= 2 && level % 2 == 0;
    }
}