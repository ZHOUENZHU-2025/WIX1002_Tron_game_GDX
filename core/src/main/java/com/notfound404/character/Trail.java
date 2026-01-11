package com.notfound404.character;
import java.util.LinkedList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.notfound404.arena.GameArena;

public class Trail {
    /*The Trail is a queue of TrailUnit objects, when it is created, it is added to the arena's trail list.
     * When the trail reaches its maximum length, the first element is removed from the arena's trail list.
     * When the trail is crashed by a bike or a disco, the ID in arena will be set to 0.
     * By rendering the trail, the trail is drawn on the screen.
     * Discovery a unit is of ID 0, it is considered a crash point. Do not paint it.
     * */

    private static int IDNUM = 1;
    private LinkedList<TrailUnit> trailUnits;
    private int maxTrailLength;
    private int length;

    private Bike ownerBike;
    private GameArena arena;

    public final Color color;


    /* Trail unit class */
    class TrailUnit{
        
        //Position of the trail unit
        private int x;
        private int y;

        public TrailUnit(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() { return x; }
        public int getY() { return y; }

    }

    Trail(Bike ownerBike){
        this.ownerBike = ownerBike;
        trailUnits = new LinkedList<TrailUnit>();
        maxTrailLength = ownerBike.trailLength;
        arena = ownerBike.arena;
        length = 0;
        color = ownerBike.color;
    }

    void oneMove(){
        length++;
        int x = ownerBike.getX();
        int y = ownerBike.getY();
        trailUnits.add(new TrailUnit(x, y));

        //The former state of the box is bike -- draw. Or the trail unit should be left empty.
        if(arena.getCellValue(x, y)==2)
            arena.setCellValue(x, y,IDNUM);

        while(length > maxTrailLength){
            int fx = trailUnits.getFirst().x;
            int fy = trailUnits.getFirst().y;
            if(arena.getCellValue(fx, fy) == 1)
                arena.setCellValue(fx, fy, 0);
            trailUnits.removeFirst();
            length--;
        } 
            
    }

    public void dispose(){
        IDNUM = 0;

        //Destroy the whole trail by setting the cell with ID 1(trail) to ID 0(trail)
        for(TrailUnit unit: trailUnits){
            int x = unit.getX();
            int y = unit.getY();
            if(arena.getCellValue(x, y) == 1)
                arena.setCellValue(x, y, IDNUM);
        }
        trailUnits.clear();
    }

    public void drawTrail(ShapeRenderer painter){
        for(TrailUnit unit : trailUnits){
            int x = unit.getX();
            int y = unit.getY();
            painter.setColor(color);
            painter.rect(x*GameArena.CELL_SIZE+64, y*GameArena.CELL_SIZE+4, GameArena.CELL_SIZE, GameArena.CELL_SIZE);
        }
    }
}
