package com.notfound404.fileReader;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.notfound404.arena.GameArena;
import com.notfound404.character.Bike;
import com.notfound404.character.Disco;
import com.notfound404.arena.GameArena.Direction;
import java.io.FileInputStream;
import java.util.Scanner;

public class ImageHandler {

    private final static int CELL_SIZE = 8;//Pixels width/height for a grid

    //Some offset because the unmatched size of Viewport and Arena
    //Add it when mapping Arena co-ordinates to Viewport pixel co-ordinate
    private final static int OFFSET_X = 64;
    private final static int OFFSET_Y = 4;
    
    //Patter for bike and disco
    //0 for background, 1 for white pixels, 2 for self color 
    private int[][] bikeShape;
    private int[][] discoShape;

    /** Can store wall in a Sprite file
     *  But not in a matrix, which consume to much time for each time we draw
     * 
     * The same for trailUnit
     * 
     * 墙体较多，不存在矩阵中
     * 目前仅以纯白色格点表示
     * 如果需要一定的图形，要存在Sprite图中（要么一开始就有png，要么用 shaperenderer 根据矩阵画一张静态图储存成sprite）
     * 轨道单元同理
     */

    private GameArena arena;
    private ShapeRenderer shaper;

    public ImageHandler(GameArena arena, ShapeRenderer shapeRdr){
        this.arena = arena;
        this.shaper = shapeRdr;
        //正式版这里读文件，现在用手动的矩阵取代
        bikeShape = bikeShapeReader("bike.txt");
        discoShape = discoShapeReader("disco.txt");
    }

    private static int[][] bikeShapeReader(String fin){
        int[][] shape = new int[8][8];
        String path = "image/" + fin; 
        Scanner scanner = null;
        try {
            
            String content = com.badlogic.gdx.Gdx.files.internal(path).readString();
            content = content.replace(",", " ");
            scanner = new Scanner(content);
            
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    if (scanner.hasNextInt()) {
                        shape[row][col] = scanner.nextInt();
                    }
                }
            }
        } catch (Exception e) {
            // 如果读取失败，打印错误，并生成一个默认的 8x8 方块，保证你能看到车
            System.err.println("错误：无法读取模型文件 " + path + "，使用默认方块代替。");
            e.printStackTrace();
            for(int i=0; i<8; i++) 
                for(int j=0; j<8; j++) 
                    shape[i][j] = 1; // 填充为1，确保能画出来
        } finally{
            if(scanner!=null)
                scanner.close();
        }
        return shape;
    }

    private static int[][] discoShapeReader(String fin){
        int[][] discoShape = new int[8][8];
        String path = "image/" + fin;
        Scanner scanner = null;
        try{
            String content = com.badlogic.gdx.Gdx.files.internal(path).readString();
            content = content.replace(",", " ");
            scanner = new Scanner(content);
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    if (scanner.hasNextInt()) {
                        discoShape[row][col] = scanner.nextInt();
                    }
                }
            }
        }catch (Exception e) {
            // 如果读取失败，打印错误，并生成一个默认的 8x8 方块，保证你能看到
            System.err.println("错误：无法读取模型文件 " + path + "，使用默认方块代替。");
            e.printStackTrace();
            for(int i=0; i<8; i++) 
                for(int j=0; j<8; j++) 
                    discoShape[i][j] = 1; // 填充为1，确保能画出来
        }finally{
            if(scanner!=null)
                scanner.close();
        }

        return discoShape;
    }

    public void drawBike(Bike bike){
          
        int baseX = bike.getX();
        int baseY = bike.getY();
        Color color = bike.getColor();
        Direction dir = bike.gDirection();
        baseX *=CELL_SIZE;
        baseX +=64;
        baseY *=CELL_SIZE;
        baseY +=4;
        for (int r = 0; r < 8; r++) {       // rows
            for (int c = 0; c < 8; c++) {   // columns
                int row = 0;
                int col = 0;
                switch(dir){
                    case UP:
                        row = 7-c;
                        col = r;
                        break;
                    case DOWN:
                        row = c;
                        col = 7-r;
                        break;
                    case LEFT:
                        row = 7-r;
                        col = 7-c;
                        break;
                    case RIGHT:
                    default:
                        row = r;
                        col = c;
                        break;
                }
                int pixelColor = bikeShape[row][col];
                if(pixelColor == 0) continue;
                shaper.setColor(pixelColor == 1 ? Color.WHITE : color);

                float drawX = baseX + c;
                float drawY = baseY + r;

                shaper.rect(drawX, drawY, 1, 1);

            }
        }
    }


    public void drawDisco(Disco disco){
        int baseX = disco.getX();
        int baseY = disco.getY();

        Color color = disco.getColor();
        baseX *=CELL_SIZE;
        baseX +=64;
        baseY *=CELL_SIZE;
        baseY +=4;
        for (int r = 0; r < 8; r++) {       // rows
            for (int c = 0; c < 8; c++) {   // columns
                int pixelColor = discoShape[r][c];
                if(pixelColor == 0) continue;
                shaper.setColor(pixelColor == 1 ? Color.WHITE : color);

                float drawX = baseX + c;
                float drawY = baseY + r;

                shaper.rect(drawX, drawY, 1, 1);

            }
        }
        
        

    }

    public void drawWall(int x, int y){
        shaper.setColor(Color.WHITE);
        
        float screenX = x * CELL_SIZE + OFFSET_X;
        float screenY = y * CELL_SIZE + OFFSET_Y;
        
        shaper.rect(screenX, screenY, CELL_SIZE, CELL_SIZE);
    }

}