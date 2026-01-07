package com.notfound404.character;

import com.notfound404.arena.GameArena;
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
        this.hasAccelerator = false;
        this.isIneffective = false;
        this.discoSlots = this.discoMAX = 3;
        this.discoRange = 5;
    }
  
    
    public void setDirection(GameArena.Direction dir) {
        this.dir = dir;
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
                isActive = false;
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
                isActive = false;
                break;
        }
    }


    public void shootDisco(int targetX, int targetY) {
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
    }
    @Override
    public boolean isDisposed(){
        return !isActive;
    }

}