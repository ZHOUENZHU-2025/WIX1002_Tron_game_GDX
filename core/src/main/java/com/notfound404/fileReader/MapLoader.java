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
    private final int FILE_ROWS = 40;
    private final int FILE_COLS = 40;

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
    private void loadRandomMap(GameArena arena) {
        Random rand = new Random();
        
        // 1. Randomly decide border type (50% chance)
        boolean isBordered = rand.nextBoolean();
        arena.setBorderType(isBordered ? 'B' : 'U');

        // 2. Clear arena first
        arena.clearMap();

        // 3. Generate internal 40x40
        for (int r = 0; r < FILE_ROWS; r++) {
            for (int c = 0; c < FILE_COLS; c++) {
                int value = 0;
                // 10% chance for wall, 5% for accelerator
                float chance = rand.nextFloat();
                if (chance < 0.1f) value = 4; // Wall
                else if (chance < 0.15f) value = 3; // Accelerator
                
                // Set into arena (Offset by 2 for border padding)
                arena.setCellValue(c + 2, r + 2, value);
            }
        }
        
        // 4. Initialize the border lists
        arena.scanAndInitLists();
    }
    
    private void loadMapFromFile(GameArena arena, String mapName) {
        File mapFile = Gdx.files.internal("map/" + mapName).file();

        if (!mapFile.exists()) {
            System.err.println("MapLoader Error: File not found " + mapFile.getAbsolutePath());
            // Fallback to random if file fails
            loadRandomMap(arena);
            return;
        }

        try (FileInputStream fis = new FileInputStream(mapFile);
             BufferedReader reader = new BufferedReader(new InputStreamReader(fis))) {

            // Read Header
            String header = reader.readLine();
            if (header != null) {
                header = header.trim().toUpperCase();
                if (header.length() > 0) {
                    arena.setBorderType(header.charAt(0));
                }
            }

            //Fill the arena grids based on the new Border Type set above
            arena.clearMap();

            //Read the Map Body
            String line;
            int row = 0;
            while ((line = reader.readLine()) != null && row < FILE_ROWS) {
                String[] values = line.trim().split("[,\\s]+");
                for (int col = 0; col < values.length && col < 44; col++) {
                    arena.setCellValue(col, row, Integer.parseInt(values[col]));
                }
                row++;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            //Emergency: Do it Randomly
            loadRandomMap(arena);
        }
    }
}