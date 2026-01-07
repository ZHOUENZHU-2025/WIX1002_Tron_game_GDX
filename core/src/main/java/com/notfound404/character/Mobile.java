package com.notfound404.character;

import com.notfound404.arena.GameArena;

//This class is the abstraction of all the objects that have their positions
//and moves on time.
public abstract class Mobile {
    //Position in the Arena Grid
    //Unit: Game Arena cells
    //位置和速度
    //单位：竞技场单元格
    
    protected int x;
    protected int y;
    //The velocity
    protected int speed;

    //The arena where the object is located
    //所在的竞技场
    protected GameArena arena;

    //ID
    /*The id representing the state of a cell in arena
    * Each cell can have the following values:
    * 0 = empty cell
    * 1 = trail cell
    * 2 = bike cell
    * 3 = accelerator cell
    * 4 = wall cell
    * 5 = disco cell
    * 6 = dead disco cell
    * 
    * -1 = out of bounds/cliff
    * */
    protected int idNum;

    protected boolean isActive;

    //Accumulator to handle `float` movement versus `int` grids conflicts
    //用于处理“float”移动与“int”网格冲突的累加器
    protected float accumulator;

    Mobile(int x, int y, int idNum){
        this.x = x;
        this.y = y;
        speed = 0;
        this.idNum = idNum;
        isActive = true;
        accumulator = 0f;
    }

    void update(float deltaTime){
        if(!isActive)
            return;
        accumulator += deltaTime * speed;
        for(;accumulator >= 1;accumulator++){
            moveOneStep();
        }
    }

    //Move one step and update self state
    abstract protected void moveOneStep();

    protected void markSelf(){
        arena.setCellValue(x, y, idNum);
    }

    public boolean isActive(){return isActive;}

    //The object is dead and disposal operation.
    abstract public void dispose();
    abstract public boolean isDisposed();

    public int getX() {return x;}
    public int getY() {return y;}

}
