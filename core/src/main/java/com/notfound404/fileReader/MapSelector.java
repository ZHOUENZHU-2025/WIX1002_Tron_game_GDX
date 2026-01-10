package com.notfound404.filereader;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 地图选择器 - 负责扫描文件并存储当前用户的选择
 */
public class MapSelector {

    // 增加一个变量，用来保存当前选中的地图名
    private String selectedMapName = "Test.txt"; 

    /**
     * 【自动扫描功能】获取所有地图的名字
     */
    public List<String> getAvailableMapNames() {
        List<String> mapNames = new ArrayList<>();
        try {
            URL url = getClass().getResource("/com/notfound404/map/");
            if (url != null) {
                File folder = new File(url.toURI());
                File[] listOfFiles = folder.listFiles();
                if (listOfFiles != null) {
                    for (File file : listOfFiles) {
                        if (file.isFile() && file.getName().endsWith(".txt")) {
                            mapNames.add(file.getName());
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("自动扫描地图文件失败: " + e.getMessage());
            mapNames.add("Test.txt");
        }
        return mapNames;
    }

    /**
     * 【选择功能】设置当前选中的地图
     * 以后 UI 点击某个地图名字时，就调用这个方法
     */
    public void setSelectedMap(String mapName) {
        this.selectedMapName = mapName;
        //System.out.println("MapSelector: 已选择地图 -> " + mapName);
    }

    /**
     * 【获取功能】获取当前选中的地图名
     * MapLoader 会通过这个方法知道该读哪个文件
     */
    public String getSelectedMapName() {
        return selectedMapName;
    }
}