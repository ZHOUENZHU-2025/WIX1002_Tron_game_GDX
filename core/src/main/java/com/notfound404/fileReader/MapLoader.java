package com.notfound404.filereader;

import com.notfound404.arena.GameArena;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 地图加载器 - 负责从 MapSelector 获取选中的地图并加载
 */
public class MapLoader {

    /**
     * 加载指定名称的地图文件
     * @param arena 目标竞技场对象
     * @param mapName 地图文件名 (例如 "map1.txt")
     */
    public void loadMap(GameArena arena, MapSelector selector) {
        
        String mapName = selector.getSelectedMapName();
        String path = "/com/notfound404/map/" + mapName;
        
        try (InputStream is = getClass().getResourceAsStream(path);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            if (is == null) {
                System.err.println("错误: 找不到地图文件 " + path);
                return;
            }

            String line;
            int row = 0;
            // 逐行读取文件内容
            while ((line = reader.readLine()) != null && row < 44) {
                // 假设 txt 文件中数字以空格或逗号分隔
                String[] values = line.trim().split("\\s+");
                
                for (int col = 0; col < values.length && col < 44; col++) {
                    try {
                        int cellValue = Integer.parseInt(values[col]);
                        // 调用 GameArena 的 setCellValue 设置网格值
                        arena.setCellValue(col, row, cellValue);
                    } catch (NumberFormatException e) {
                        // 如果遇到非数字字符，默认设为 0 (空地)
                        arena.setCellValue(col, row, 0);
                    }
                }
                row++;
            }
            //也许可能会加ui？跳什么窗口？
            //System.out.println("成功加载地图: " + mapName);  //读取成功

        } catch (Exception e) {
            System.err.println("读取地图文件时发生异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
}