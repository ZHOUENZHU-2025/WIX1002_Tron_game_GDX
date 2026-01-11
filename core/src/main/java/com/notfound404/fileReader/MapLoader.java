package com.notfound404.fileReader;

import com.notfound404.arena.GameArena;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.notfound404.mapsystem.MapSelector;
import com.notfound404.mapsystem.RandomMapGenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * 地图加载器 - 负责执行最终的地图写入动作
 */
public class MapLoader {

    /**
     * 核心加载方法
     * @param arena 竞技场对象
     * @param selector 传入选择器实例，自动判断加载方式
     */
    public void loadMap(GameArena arena, MapSelector selector) {
        if (selector.isRandomMode()) {
            // 模式 A: 现场随机生成
            loadRandomGeneratedMap(arena);
        } else {
            // 模式 B: 读取指定的 txt 文件
            loadMapFromFile(arena, selector.getSelectedMapName());
        }
    }

    /**
     * 逻辑 A: 直接调用生成器获取矩阵并写入 Arena
     */
    private void loadRandomGeneratedMap(GameArena arena) {
        RandomMapGenerator generator = new RandomMapGenerator();
        int[][] matrix = generator.generateMatrix(); // 获取随机生成的 44x44 矩阵
        
        for (int r = 0; r < 44; r++) {
            for (int c = 0; c < 44; c++) {
                arena.setCellValue(c, r, matrix[r][c]);
            }
        }
        //System.out.println("MapLoader: 随机地图已即时生成并加载完成。");
    }

    /**
     * 逻辑 B: 原有的文件读取逻辑
     */
    private void loadMapFromFile(GameArena arena, String mapName) {
        // 1. 尝试直接读取
        File mapFile = new File("assets/map/" + mapName);
        
        // 2. 如果不存在，尝试加上项目前缀读取
        if (!mapFile.exists()) {
            mapFile = new File("WIX1002_Tron_game_GDX-main/assets/map/" + mapName);
        }

        try (FileInputStream fis = new FileInputStream(mapFile);
             BufferedReader reader = new BufferedReader(new InputStreamReader(fis))) {

            String line;
            int row = 0;
            while ((line = reader.readLine()) != null && row < 44) {
                String[] values = line.trim().split("[,\\s]+");
                for (int col = 0; col < values.length && col < 44; col++) {
                    arena.setCellValue(col, row, Integer.parseInt(values[col]));
                }
                row++;
            }
            //System.out.println("MapLoader: 成功从 " + mapFile.getPath() + " 加载地图");
        } catch (Exception e) {
            System.err.println("MapLoader 错误: 无法找到文件 " + mapFile.getAbsolutePath());
        }
    }
}