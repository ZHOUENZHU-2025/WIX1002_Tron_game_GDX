package com.notfound404.fileReader;

import com.badlogic.gdx.Gdx;
import com.notfound404.arena.GameArena;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.io.FileInputStream;

import java.util.Random;

/**
 * 地图加载器 - 负责执行最终的地图写入动作
 */
public class MapLoader {

    //How many rows and cols we have for the file
    //Same as Arena 40 * 40
    private final int PLAY_SIZE = 40;
    private final int BORDER_WIDTH = 2;
    private final int FINAL_SIZE = 44;
    private final Random rand = new Random();

    public void loadMap(GameArena arena, String mapName) {
        //Random Map Mode
        if (mapName == null || mapName.equalsIgnoreCase("RANDOM")) {
            loadRandomMap(arena);
        } else {
            //Choose a map
            loadMapFromFile(arena, mapName);
        }
    }

    /**
     * Generates a random map.
     */
    public void loadRandomMap(GameArena arena) {
        // 1. 随机边界类型
        arena.setBorderType(rand.nextBoolean() ? 'B' : 'U');

        // 2. 清空竞技场 (全部设为 0)
        arena.clearMap();

        // 3. 生成加速带 (3) - 数量显著增加，确保 3 > 4
        // 至少17个
        int speedLines = rand.nextInt(7) + 17; 
        for (int i = 0; i < speedLines; i++) {
            int startX = rand.nextInt(PLAY_SIZE) + 2;
            int startY = rand.nextInt(PLAY_SIZE) + 2;
            // 长度较长，模拟道路
            int length = rand.nextInt(10) + 12; 
            drawCluster(arena, startX, startY, length, 3);
        }

        // 4. 生成山体 (4) - 数量增加但少于加速带
        // 至少15个
        int mountainClusters = rand.nextInt(5) + 15; 
        for (int i = 0; i < mountainClusters; i++) {
            int startX = rand.nextInt(PLAY_SIZE) + 2;
            int startY = rand.nextInt(PLAY_SIZE) + 2;
            // 山簇大小适中，保证不封死地图
            int clusterSize = rand.nextInt(6) + 4; 
            drawCluster(arena, startX, startY, clusterSize, 4);
        }

        // 5. 初始化列表 (光盘、坦克位置等扫描)
        arena.scanAndInitLists();
    }

    /**
     * 蔓延算法：通过随机步进让物体成群出现
     */
    private void drawCluster(GameArena arena, int x, int y, int size, int type) {
        for (int i = 0; i < size; i++) {
            // 确保在 40x40 游玩区域内 (坐标 2 到 41)
            if (x >= 2 && x < 42 && y >= 2 && y < 42) {
                // 如果当前位置已经是另一种物体，不再覆盖，以维持比例平衡
                if (arena.getCellValue(x, y) == 0) {
                    arena.setCellValue(x, y, type);
                }
            }
            // 随机位移：让形状更自然，不只是直线
            x += rand.nextInt(3) - 1; // -1, 0, 1
            y += rand.nextInt(3) - 1; // -1, 0, 1
        }
    }

    
    private void loadMapFromFile(GameArena arena, String mapName) {
    // 关键改动：直接使用 Gdx.files.internal(...).reader()
    // 这样在 VS Code 里它会自动从 assets 开始找，不需要写全路径
    try (BufferedReader reader = new BufferedReader(Gdx.files.internal("map/" + mapName).reader())) {

        // 1. Read Header
        String header = reader.readLine();
        if (header != null) {
            header = header.trim().toUpperCase();
            if (header.length() > 0) {
                arena.setBorderType(header.charAt(0));
            }
        }

        // Fill the arena grids based on the new Border Type set above
        arena.clearMap();

        // 2. Read the Map Body
        String line;
        int row = 0;
        while ((line = reader.readLine()) != null && row < PLAY_SIZE) {
            String[] values = line.trim().split("[,\\s]+");
            for (int col = 0; col < values.length && col < 44; col++) {
                // 保持你的变量名和逻辑不变
                arena.setCellValue(col + 2, row + 2, Integer.parseInt(values[col]));
            }
            row++;
        }
        
    } catch (Exception e) {
        // 如果读取失败，打印错误并进入随机地图模式
        Gdx.app.error("MapLoader", "Error loading map: " + mapName, e);
        loadRandomMap(arena);
    }

    // 4. Initialize the border lists
    arena.scanAndInitLists();
}
}