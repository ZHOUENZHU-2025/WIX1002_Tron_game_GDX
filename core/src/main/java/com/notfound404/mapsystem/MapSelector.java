package com.notfound404.mapsystem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 地图选择器 - 负责管理地图名单、自动扫描文件并存储当前用户的选择
 */
public class MapSelector {

    private String selectedMapName = "Test.txt"; // 默认地图文件名
    private boolean isRandomMode = false;        // 标记当前是否选择了随机生成模式
    public static final String RANDOM_MAP_KEY = "RANDOM_GENERATOR_MAP"; // 随机地图的特殊标识符

    /**
     * 【自动扫描功能】
     * 扫描 /com/notfound404/map/ 目录下的所有 .txt 文件
     * 同时在列表最前面加入“随机地图”选项
     */
    public List<String> getAvailableMapNames() {
        List<String> mapNames = new ArrayList<>();
        // 先加入随机地图选项，方便 UI 显示
        mapNames.add(RANDOM_MAP_KEY);

        // 获取 assets 目录
        File folder = new File("assets/map/");

        if (!folder.exists() || !folder.isDirectory()) {
            // 尝试直接通过全名探测（根据你提供的路径结构）
            folder = new File("WIX1002_Tron_game_GDX-main/assets/map/");
        }

        if (folder.exists() && folder.isDirectory()) {
            File[] listOfFiles = folder.listFiles();
            if (listOfFiles != null) {
                for (File file : listOfFiles) {
                    // 过滤出所有 .txt 文件，排除随机模式标识本身
                    if (file.isFile() && file.getName().endsWith(".txt")) {
                        mapNames.add(file.getName());
                    }
                }
            }
        } else {
            // 调试用：如果还是找不到，打印当前程序到底在哪个目录下运行
            System.err.println("MapSelector: 找不到地图目录。程序运行在: " + System.getProperty("user.dir"));
        }
        return mapNames;
    }

    /**
     * 【选择功能】
     * 当用户在 UI 上点击某个地图时调用此方法
     */
    public void setSelectedMap(String mapName) {
        if (RANDOM_MAP_KEY.equals(mapName)) {
            this.isRandomMode = true;
            this.selectedMapName = "Randomly Generated";
        } else {
            this.isRandomMode = false;
            this.selectedMapName = mapName;
        }
        //System.out.println("MapSelector: 当前选择已切换至 -> " + selectedMapName);
    }

    /**
     * 获取当前选中的地图名
     */
    public String getSelectedMapName() {
        return selectedMapName;
    }

    /**
     * 判断当前是否为随机模式
     */
    public boolean isRandomMode() {
        return isRandomMode;
    }
}