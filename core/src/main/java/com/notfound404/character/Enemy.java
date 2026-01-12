package com.notfound404.character;

import com.notfound404.arena.GameArena;
import com.notfound404.arena.GameArena.Direction;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.notfound404.levelsystem.EnemyLevelSystem;
import com.notfound404.levelsystem.BaseLevelSystem;

import java.io.File;
import java.io.FileInputStream;
import java.util.Random;
import java.util.Scanner;

public class Enemy extends Bike {
    public static int enemyCount = 0;
    public static int EXPplus = 0;
    private final String enemyTypeName;
    
    private EnemyLevelSystem levelSystem; // 敌人的升级系统
    private int difficulty; // 敌人难度级别

    // AI 相关参数 (从 AI_system 移入)
    private Random random = new Random();
    private float aiTimer = 0;      // 移动决策计时器
    private float shootTimer = 0;   // 射击冷却计时器
    private float moveInterval;     // 移动反应间隔
    private float shootCooldown;    // 射击冷却时间
    private Bike targetPlayer;      // 锁定的目标(玩家)

    
    //Info List, index is the difficulty-------- 1(Cannon Fodder) to 4(BOSS)
    private static String[] enemyName;
    private static Color[] enemyColors;
    //Read the Enemies' info when first time we create a Player Object
    static {
        // 关键改动：使用 Gdx.files.internal(...).reader() 代替 .file()
        // 这会自动在 VS Code 的 assets 目录下查找，无需写死全路径
        try (Scanner scanner = new Scanner(Gdx.files.internal("rider/Enemies.txt").reader())) {
            enemyName = new String[4];
            enemyColors = new Color[4];
            
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.trim().isEmpty()) continue; // 跳过空行

                String[] entity = line.split(",");
                // 保持你的逻辑：解析难度索引并设置名称和颜色
                int difficultyIndex = Integer.parseInt(entity[1]) - 1;
                enemyName[difficultyIndex] = entity[0];
                
                switch (entity[2]) {
                    case "Gold":
                        enemyColors[difficultyIndex] = Color.GOLD;
                        break;
                    case "Red":
                        enemyColors[difficultyIndex] = Color.RED;
                        break;
                    case "Yellow":
                        enemyColors[difficultyIndex] = Color.YELLOW;
                        break;
                    case "Green":
                    default:
                        enemyColors[difficultyIndex] = Color.GREEN;
                        break;
                }
            }
        } catch (Exception e) {
            // 保持你的错误处理和退出逻辑
            System.err.println("Enemy Info Loader Error: Could not read internal enemy file.");
            e.printStackTrace();
            System.exit(0);
        }
    }

    public Enemy(GameArena arena, int x, int y, int difficulty) {
        super(arena, x, y, 2, enemyColors[difficulty-1]);
        this.difficulty = difficulty;

        this.levelSystem = new EnemyLevelSystem(difficulty);
        this.levelSystem.setEnemy(this);
        this.enemyTypeName = enemyName[difficulty];

        // 初始化 AI 参数 (难度越高，反应越快，射速越快)
        // 简化：用公式代替 switch 配置难度参数
        this.moveInterval = Math.max(0, 0.6f - (difficulty * 0.15f)); 
        this.shootCooldown = Math.max(0.2f, 3.5f - (difficulty * 0.8f)); 

        // 根据初始等级同步 Bike 变量
        syncInitialStats();

        enemyCount++;
    }

    private void syncInitialStats() {
        // 从 System 获取初始等级（根据难度决定的等级）
        this.level = levelSystem.getCurrentLevel();
        double multiplier = Math.pow(BaseLevelSystem.getStatMultiplier(), level - 1);
        
        // 初始化 Bike 变量
        this.maxLP = (int)(10 * multiplier);
        this.lp = maxLP;
        this.speed = (int)(3.0 * multiplier);
        this.discoRange = 30 + (level / 2);
        this.discoMAX = 2; // 敌人默认飞盘上限
        this.discoSlots = 2;
    }
    
    /**
     * 重写 update 方法，整合 AI 逻辑
     */
    @Override
    public void update(float deltaTime) {
        // 0. 如果已经死亡，不执行任何操作
        if (this.lp <= 0 || !this.isActive) return;

        // 1. 获取目标 (懒加载：如果还没找到玩家，就向竞技场要)
        if (targetPlayer == null) {
            targetPlayer = arena.getPlayer();
        }

        // 2. AI 决策逻辑
        // A. 移动逻辑 (带反应延迟)
        aiTimer += deltaTime;
        if (aiTimer >= moveInterval) {
            makeMoveDecision();
            aiTimer = 0;
        }

        // B. 射击逻辑
        shootTimer += deltaTime;
        if (shootTimer >= shootCooldown) {
            tryShoot();
        }

        // 3. 执行父类的物理/移动逻辑 (Mobile.update -> moveOneStep)
        super.update(deltaTime);
    }

    @Override
    public void dispose(){
        if(isDisposed()){
            return;
        }
        super.dispose();
        EXPplus+=getXPForDefeating();
        enemyCount--;
    }
    // ==========================================
    //           以下为整合的 AI 核心逻辑
    // ==========================================

    private void makeMoveDecision() {
        if (targetPlayer == null) return;

        // 预判前方坐标
        int nx = this.x + getDX(this.dir);
        int ny = this.y + getDY(this.dir);

        // 决策：如果前方危险，或者 (随机概率满足难度要求 且 有更好路径) -> 改变方向
        boolean isFrontBlocked = !isSafe(nx, ny);
        boolean wantsToHunt = random.nextDouble() < (this.difficulty * 0.25); // 25%~100% 进攻欲

        if (isFrontBlocked || wantsToHunt) {
            Direction bestDir = findBestDirection();
            if (bestDir != null) {
                // 直接调用父类 Bike 的 setDirection
                this.setDirection(bestDir);
            }
        }
    }

    // 寻找最佳方向（优先安全，其次离玩家近）
    private Direction findBestDirection() {
        if (targetPlayer == null) return null;

        Direction best = null;
        double minDst = Double.MAX_VALUE;

        for (Direction d : Direction.values()) {
            // 排除反方向（Bike.setDirection 本身也会防，但这里排除能省计算）
            if (isOpposite(d, this.dir)) continue;

            int nx = this.x + getDX(d);
            int ny = this.y + getDY(d);

            if (isSafe(nx, ny)) {
                // 计算距离玩家的距离 (加入一点随机干扰防止走位太死板)
                double dst = Math.pow(nx - targetPlayer.getX(), 2) + Math.pow(ny - targetPlayer.getY(), 2);
                
                // 低难度下增加随机性，让敌人显得笨一点
                if (this.difficulty < 3) dst += random.nextInt(50);

                if (dst < minDst) {
                    minDst = dst;
                    best = d;
                }
            }
        }
        return best; // 如果全是死路，返回 null (听天由命)
    }

    private void tryShoot() {
        if (targetPlayer == null) return;
        
        // 没子弹就别算了
        if (this.discoSlots <= 0) return;

        // 判断是否同行或同列
        int dx = targetPlayer.getX() - this.x;
        int dy = targetPlayer.getY() - this.y;
        boolean alignX = (dx == 0), alignY = (dy == 0);

        if (!alignX && !alignY) return; // 不在直线上

        // 判断距离限制 (难度4无限距离，其他难度递增)
        int dist = Math.abs(dx + dy);
        if (this.difficulty < 4 && dist > this.difficulty * 15) return;

        // 判断朝向是否正确 (必须面向玩家才能射击)
        boolean facingPlayer = (alignX && ((dy > 0 && this.dir == Direction.UP) || (dy < 0 && this.dir == Direction.DOWN))) ||
            (alignY && ((dx > 0 && this.dir == Direction.RIGHT) || (dx < 0 && this.dir == Direction.LEFT)));

        if (facingPlayer) {
            // 执行射击，传入目标坐标
            this.shootDisco(targetPlayer.getX(), targetPlayer.getY());
            shootTimer = 0;
        }
    }

    // === AI 辅助工具方法 ===

    private boolean isSafe(int x, int y) {
        int v = arena.getCellValue(x, y);
        // 0=空, 3=加速, 5=飞盘 是安全的
        // 注意：2(Bike) 视为不安全，避免直接撞上去
        return v == 0 || v == 3 || v == 5;
    }

    private boolean isOpposite(Direction d1, Direction d2) {
        return (getDX(d1) + getDX(d2) == 0) && (getDY(d1) + getDY(d2) == 0);
    }

    // 将方向转换为坐标增量的简易写法
    private int getDX(Direction d) { return d == Direction.RIGHT ? 1 : (d == Direction.LEFT ? -1 : 0); }
    private int getDY(Direction d) { return d == Direction.UP ? 1 : (d == Direction.DOWN ? -1 : 0); }

    // ==========================================
    //           原有 Enemy 方法
    // ==========================================

    @Override
    protected void moveOneStep() {
        super.moveOneStep(); // 先执行父类的移动逻辑
       // 敌人移动也可以获得经验，和玩家一样
        levelSystem.addXPFromMovement(1);
    }
    
    /**
     * 敌人被击败时，给玩家提供经验
     */
    public int getXPForDefeating() {
        int level = levelSystem.getCurrentLevel();
        // 基础经验 = 10 × 等级
        int baseXP = 10 * level;
        // 难度加成
        int difficultyBonus = 0;
        
        switch (difficulty) {
            case 1: difficultyBonus = level * 5; break;
            case 2: difficultyBonus = level * 10; break;
            case 3: difficultyBonus = level * 20; break;
            case 4: difficultyBonus = Math.max(1000 - baseXP, level * 30); break;
        }
        
        int totalXP = baseXP + difficultyBonus;
        if (difficulty == 4 && totalXP < 1000) totalXP = 1000;
        return totalXP;
    }

    public int getEnemyLevel() { return levelSystem.getCurrentLevel(); }
    public int getDifficulty() { return difficulty; }
    public EnemyLevelSystem getLevelSystem() { return levelSystem; }

    public static void resetExpAcc(){EXPplus = 0; }
}