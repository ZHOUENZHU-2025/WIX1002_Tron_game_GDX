package com.notfound404.character;

import com.badlogic.gdx.graphics.Color;
import com.notfound404.arena.GameArena.Direction;
public /** Disco
 * 飞碟类
 * It breaks through trails and damages bikes on contact.
 * It stops after a certain duration.
 */
class Disco extends Mobile {

    private int discoRange;
    private Bike masterBike;
    private float kx;
    private float ky;
    private float accumulatorX;
    private float accumulatorY;
    private Direction verDir;
    private Direction horiDir;
    private Color color;

    Disco(Bike master, int x, int y) {
        super(master.getX(), master.getY(), 5);
        arena = master.arena;
        masterBike = master;
        float vx = x-this.x;
        float vy = y-this.y;
        float normV = (float) Math.sqrt(vx*vx + vy*vy);
        this.kx = vx / normV;
        this.ky = vy / normV;
        horiDir = kx >= 0 ? Direction.RIGHT : Direction.LEFT; 
        verDir = ky >= 0 ? Direction.UP : Direction.DOWN;
        this.kx = Math.abs(kx);
        this.ky = Math.abs(ky);
        this.discoRange = master.discoRange;
        isActive = true;
        accumulatorX = accumulatorY =0;
        color = master.color;
        speed = 20f;
    }


    @Override

    public void update(float deltaTime) {
        super.update(deltaTime);

        //if dead disco(did not really run the the update)
        if(idNum ==6){
            if(masterBike.getX() == x && masterBike.getY() ==y){
                masterBike.pickDisco();
                dispose();
            }
        }
    }

    @Override
    protected void moveOneStep(){
        if(discoRange<=0){
            landDown();
            return;
        }
        //The disco cross something, then it must be empty.(The bike will move away leaving it empty)
        int currentVal = arena.getCellValue(x, y);
        if(currentVal == 5 || currentVal == 6) {
            arena.setCellValue(x, y, 0);
        }

        accumulatorX+=kx;
        accumulatorY+=ky;
        if(accumulatorX>=1){
            accumulatorX--;
            discoRange--;
            switch (horiDir) {
                case RIGHT:
                    x++;
                    break;
                default:
                    x--;
            }
        }
        if(accumulatorY>=1){
            accumulatorY--;
            discoRange--;
            switch (verDir) {
                case UP:
                    y++;
                    break;
                default:
                    y--;
            }
        }

        crashHandle();
    }

    private void landDown(){
        idNum = 6;
        isActive = false;
        markSelf();
    }

    private void crashHandle(){
        switch (arena.getCellValue(x, y)) {
            case 4://Wall collision
                arena.addExplosion(x, y);
            case -1://Fly out of the Arena
                dispose();
            case 5:
            case 6:
                break;
            case 1://Kill the Trail
                trailCrash();
            case 2://Cover the Bike, bike handles its collision
            case 3://Kill/Cover Accelerator
            case 0://Nothing
            default:
                markSelf();
        }
    }

    private void trailCrash(){
        for(int i = -1; i<=1;i++){
            for(int j = -1; j<=1; j++){
                if(arena.getCellValue(x+i, y+j)==1)
                    arena.setCellValue(x+i, y+j, 0);
            }
        }
        arena.addExplosion(x, y);
        markSelf();        
    }

    @Override
    public void dispose(){
        //Never draw again, the position on the map becomes empty.
        isActive = false;
        idNum = 0;

        //Mark itself if disco is the only thing on the ground
        int currentVal = arena.getCellValue(x, y);
        if(currentVal == 5 || currentVal == 6) {
            markSelf();
        }
        
    }

    @Override
    public boolean isDisposed(){
        return idNum == 0;
    }

    public Color getColor(){
        return color;
    }
}
