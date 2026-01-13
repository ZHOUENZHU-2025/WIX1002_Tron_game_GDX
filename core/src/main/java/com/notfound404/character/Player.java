package com.notfound404.character;

//升级系统
import com.notfound404.levelsystem.PlayerLevelSystem;
//.....
import com.notfound404.arena.GameArena;

import java.io.File;
import java.io.FileInputStream;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

import java.util.Scanner;

public class Player extends Bike {
    public final String playerType;//"Tron" or "Kevin"
    protected String playerID;//Enter by player, used for RANKING system

    private PlayerLevelSystem levelSystem; // 玩家的升级系统

    private static String[] heroName;
    private static int[][] heroProperty;

    //Read the players' info when first time we create a Player Object
    static {
        // 1. 直接通过 LibGDX 的 internal 句柄获取 reader
        // 这样即使在 VS Code 里，它也能自动定位到 assets/rider/Players.txt
        try (Scanner scanner = new Scanner(Gdx.files.internal("rider/Players.txt").reader())) {
            heroName = new String[2];
            heroProperty = new int[2][2];
            
            for (int i = 0; i < 2; i++) {
                if (scanner.hasNextLine()) {
                    String[] heroLine = scanner.nextLine().split(",");
                    heroName[i] = heroLine[0];
                    heroProperty[i][0] = Integer.parseInt(heroLine[1]);
                    heroProperty[i][1] = Integer.parseInt(heroLine[2]);
                }
            }
        } catch (Exception e) {
            // 保持你的错误处理逻辑
            System.err.println("PlayerLoader Error: Could not read player info from internal path.");
            e.printStackTrace();
            // 注意：在某些环境下 System.exit(0) 可能会导致调试中断，但保留你的原始逻辑
            System.exit(0);
        }
    }

    public Player(String playerType, int startX, int startY, GameArena arena) {
        super(arena, startX, startY, 2, Color.CYAN);
        this.playerType = playerType;
        this.playerID = "John Doe";

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
            for(int i = 0;i<heroName.length ;i++){
                if(playerType.equalsIgnoreCase(heroName[i])){
                    this.maxLP = this.lp = heroProperty[i][0];
                    this.speed = heroProperty[i][1];
                    return;
                }
            }
        }
        //Default Value
        this.maxLP = this.lp = 5;
        this.speed = 5;
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
    //生成敌人系统调用get
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
        //exp+=Enemy.EXPplus;
        // 修改点：直接将全局经验加入到升级系统中
        levelSystem.addXP(Enemy.EXPplus); 
        Enemy.resetExpAcc();
        Enemy.EXPplus = 0;
    }
}