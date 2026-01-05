package com.notfound404.character;

import com.badlogic.gdx.Game;
import com.notfound404.arena.GameArena;
import com.badlogic.gdx.graphics.Color;

//Superclass for all bike types
//所有战车类型的父类
public abstract class Bike {
    
    //Position and speed of the bike
    //Unit: Game Arena cells
    //位置和速度
    //单位：竞技场单元格

    protected int x;
    protected int y;
    protected int speed;

    protected GameArena.Direction dir;

    //Accumulator to handle `double` movement versus `int` grids conflicts
    //用于处理“double”移动与“int”网格冲突的累加器
    protected double accumulator;

    //The arena where the bike is located
    //战车所在的竞技场
    protected GameArena arena;

    //Properties of the bike
    //战车的属性

    protected Color color;
    protected int lp;
    protected int maxLP;
    protected int exp;
    protected int level;
    protected boolean isAlive;
    protected boolean hasAccelerator;
    protected boolean isIneffective;
    protected int discoSlots;
    protected int discoMAX;
    

    //Constants for bike behavior
    //战车行为常量
    protected final static int ACCELERATOR_DURATION = 3; //Duration of accelerator effect in seconds
    protected final static double INEFFECTIVE_DURATION = 0.5; //Duration of collision effects in seconds




    public int getX() {return x;}
    public int getY() {return y;}

}
class disco {
    
}

class trail{
    
}