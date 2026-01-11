package com.notfound404.character;

import com.notfound404.arena.GameArena;
import com.notfound404.arena.GameArena.Direction;
import com.badlogic.gdx.graphics.Color;

//Superclass for all bike types
//所有战车类型的父类
public abstract class Bike extends Mobile{
    
    protected GameArena.Direction dir;

    //Accumulator to handle `float` movement versus `int` grids conflicts
    //用于处理“float”移动与“int”网格冲突的累加器
    protected float accumulator;

    //Properties of the bike
    //战车的属性

    protected Color color;
    protected float lp;
    protected float maxLP;
    protected int exp;//For enemy bikes, exp awarded when destroyed; for player bike, current exp
    protected int level;
    protected boolean hasAccelerator;
    protected boolean isIneffective;
    protected int discoSlots;
    protected int discoMAX;
    

    //Constants for bike behavior
    //战车行为常量
    protected final static int ACCELERATOR_DURATION = 3; //Duration of accelerator effect in seconds
    protected final static int INEFFECTIVE_DURATION = 3; //Duration of disco/trail collision effects in distance units

    //Attack properties
    //攻击属性
    protected int discoRange;
    protected int trailLength;
    protected Trail bikeTrail;

    //Constructor
    public Bike(GameArena arena, int x, int y, int idNum, Color color) {
        super(x, y, idNum);
        this.arena = arena;
        this.color = color;
        this.dir = GameArena.Direction.UP;
        this.trailLength = 45;
        this.hasAccelerator = false;
        this.isIneffective = false;
        this.discoSlots = this.discoMAX = 3;
        this.discoRange = 10;
        this.bikeTrail = new Trail(this);
        arena.addTrail(bikeTrail);
        markSelf();
    }
  
    
    public void setDirection(GameArena.Direction dir) {
        //Bike cannot turn around
        if(!isOpposite(dir))
            this.dir = dir;
    }

    //Usage: refuse the opposite command
    private boolean isOpposite(Direction dir_in){
        switch(dir){
            case UP:
                return dir_in == Direction.DOWN;
            case DOWN:
                return dir_in == Direction.UP;
            case LEFT:
                return dir_in == Direction.RIGHT;
            case RIGHT:
            default:
                return dir_in == Direction.LEFT;
        }
    }

    protected void moveOneStep() {
        bikeTrail.oneMove();
        switch (dir) {
            case UP:
                y += 1;
                break;
            case DOWN:
                y -= 1;
                break;
            case LEFT:
                x -= 1;
                break;
            case RIGHT:
                x += 1;
                break;
        }
        //Collisions
        switch(arena.getCellValue(x, y)){
            case 0: //Empty cell
                markSelf();
                break;
            case 1: //Trail cell
                if (!isIneffective) {
                    lp -= 0.5f;
                    isIneffective = true;
                }
                arena.addExplosion(x, y);
                markSelf();
                break;
            case 2: //Bike cell
                lp -= 1.0f;
                break;
            case 3: //Accelerator cell
                hasAccelerator = true;
                markSelf();
                break;
            case 4: //Wall cell
                lp =0;
                break;
            case 5: //The disco damage: -1
                lp-=1.0f;
                markSelf();
                break;
            case 6://The disco function handle the picking
                break;
            case -1:
            default: //Out of bounds/cliff
                lp = 0;
                break;
        }
        if(lp<=0)
            dispose();
    }


    public void shootDisco(int targetX, int targetY) {
        if(targetX == x && targetY == y)
            return;
        if (discoSlots > 0) {
            arena.addDisco(new Disco(this, targetX, targetY));
            discoSlots -= 1;
            //Add disco to arena's disco list
        }
    }

    public void pickDisco(){
        if(discoSlots<discoMAX){
            discoSlots++;
        }
    }



    @Override
    public void dispose(){
        isActive = false;
        bikeTrail.dispose();
    }
    @Override
    public boolean isDisposed(){
        return !isActive;
    }

    public Color getColor(){ return color; }
    public Direction gDirection(){ return dir; }

    //升级系统用
    public float getLP() { return lp; }
    public void setLP(float lp) { this.lp = lp; }

    public float getMaxLP() { return maxLP; }
    public void setMaxLP(float maxLP) { this.maxLP = maxLP; }

    public int getExp() { return exp; }
    public void setExp(int exp) { this.exp = exp; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public float getSpeed() { return speed; }
    public void setSpeed(float speed) { this.speed = speed; }

    public int getDiscoMAX() { return discoMAX; }
    public void setDiscoMAX(int discoMAX) { this.discoMAX = discoMAX; }

    public int getDiscoSlots() { return discoSlots; }
    public void setDiscoSlots(int discoSlots) { this.discoSlots = discoSlots; }

    public int getDiscoRange() { return discoRange; }
    public void setDiscoRange(int discoRange) { this.discoRange = discoRange; }

}