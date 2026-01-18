package com.notfound404.fileReader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class ArchiveManager {

    // 确保路径指向你验证成功的嵌套位置
    final private static String LEADERBOARD_FILE = "WIX1002_Tron_game_GDX-main/assets/Score/leaderboard.txt";
    final private static String ARCHIVE_FILE = "WIX1002_Tron_game_GDX-main/assets/Score/archive.txt";

    public static class ArchiveEntry implements Comparable<ArchiveEntry>{
        public final String playerID;
        public final String heroType;
        public int level;
        public int score;
        public final String map;
        
        public ArchiveEntry(String ID, String heroType, String map, int level, int score){
            this.playerID = ID;
            this.heroType = heroType;
            this.map = map;
            this.level = level;
            this.score = score;
        }

        @Override
        public String toString(){
            return playerID +","+ heroType +","+ map +","+level+","+score;
        }

        @Override
        public int compareTo(ArchiveEntry p2) {
            return Integer.compare(p2.level, this.level);
        }
    }

    public static void saveScoreLB(ArchiveEntry newRecord) {
        try {
            // 使用 GDX 的 FileHandle 进行写入
            FileHandle file = Gdx.files.local(LEADERBOARD_FILE);
            file.writeString(newRecord.toString() + "\n", true);
        } catch (Exception e) {
            System.err.println("Score Saving Error: " + e.getMessage());
        }
    }

    public static void saveScoreAch(ArchiveEntry newRecord) {
        try {
            FileHandle file = Gdx.files.local(ARCHIVE_FILE);
            file.writeString(newRecord.toString() + "\n", true);
        } catch (Exception e) {
            System.err.println("Archive Saving Error: " + e.getMessage());
        }
    }

    public static ArrayList<ArchiveEntry> getTopScores(int limit) {
        ArrayList<ArchiveEntry> scores = new ArrayList<>();
        FileHandle file = Gdx.files.local(LEADERBOARD_FILE);
        
        if (!file.exists()) return scores;

        // 保持你原始的 Scanner 逻辑
        try (Scanner scanner = new Scanner(file.reader())) {
            while(scanner.hasNextLine()){
                String lineStr = scanner.nextLine();
                if(lineStr.trim().isEmpty()) continue;
                String[] line = lineStr.split(",");
                try {
                    scores.add(new ArchiveEntry(line[0], line[1], line[2], Integer.parseInt(line[3].trim()), Integer.parseInt(line[4].trim())));
                } catch (Exception e) { }
            }
        } catch (Exception e) {
            System.err.println("Leaderboard Error: " + e.getMessage());
        }

        Collections.sort(scores);
        if (scores.size() > limit) {
            return new ArrayList<>(scores.subList(0, limit));
        }
        return scores;
    }

    public static ArchiveEntry getArchive(String playerID){
        if(playerID == null || playerID.length() == 0) return null;
        
        ArrayList<ArchiveEntry> records = new ArrayList<>();
        FileHandle file = Gdx.files.local(ARCHIVE_FILE);
        
        if (!file.exists()) return null;

        // 保持你原始的 Scanner + ArrayList 结构
        try (Scanner scanner = new Scanner(file.reader())) {
            while(scanner.hasNextLine()){
                String lineStr = scanner.nextLine();
                if(lineStr.trim().isEmpty()) continue;
                String[] line = lineStr.split(",");
                try {
                    records.add(new ArchiveEntry(line[0], line[1], line[2], Integer.parseInt(line[3].trim()), Integer.parseInt(line[4].trim())));
                } catch (Exception e) { }
            }
        } catch (Exception e) {
            System.err.println("Archive Library Error: " + e.getMessage());
        }

        // 原始循环比对逻辑
        for(ArchiveEntry data : records){
            if(data.playerID.equals(playerID))
                return data;
        }
        return null;
    }
}