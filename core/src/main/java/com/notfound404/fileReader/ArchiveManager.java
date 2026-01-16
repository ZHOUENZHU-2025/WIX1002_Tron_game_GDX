package com.notfound404.fileReader;

import java.util.ArrayList;
import java.util.Collections;
import java.io.*;
import java.util.Scanner;

import com.badlogic.gdx.Gdx;

//This class manages archives from players, which are also entities/lines on the leaderboard.
public class ArchiveManager {

    final private static String LEADERBOARD_FILE = "Score/leaderboard.txt";
    final private static String ARCHIEVE_FILE = "Score/archive.txt";

    public static class ArchiveEntry implements Comparable<ArchiveEntry>{
        public final String playerID;
        public final String heroType;
        public int level;
        public int score;
        public final String map;
        
        //For archives, but not Random map.
        public int x;
        public int y;
        
        public ArchiveEntry(String ID, String heroType, String map,int level, int score){
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

    // Save Score to leaderboard
    // 排行榜录入
    public static void saveScoreLB(ArchiveEntry newRecord) {
        try(PrintWriter lbPW = new PrintWriter(new FileWriter(LEADERBOARD_FILE,true))){
            lbPW.println(newRecord);
        }catch(FileNotFoundException e){
            System.err.println("Score Saving Error: Could not save your score info from internal path.");
            e.printStackTrace();
            System.exit(0);
        }catch(IOException e){
            System.err.println("Score Saving Error: Output Error.");
            e.printStackTrace();
            System.exit(0);
        }
    }

    // Save Score to archive library
    // 存档录入
    public static void saveScoreAch(ArchiveEntry newRecord) {
        try(PrintWriter lbPW = new PrintWriter(new FileWriter(ARCHIEVE_FILE,true))){
            lbPW.println(newRecord);
        }catch(FileNotFoundException e){
            System.err.println("Archive Saving Error: Could not save your score info from internal path.");
            e.printStackTrace();
            System.exit(0);
        }catch(IOException e){
            System.err.println("Archive Saving Error: Output Error.");
            e.printStackTrace();
            System.exit(0);
        }
    }

    // 读取并获取前N名
    // Rank and get the top 10
    public static ArrayList<ArchiveEntry> getTopScores(int limit) {
        ArrayList<ArchiveEntry> scores = new ArrayList<>();
        try (Scanner scanner = new Scanner(Gdx.files.internal(LEADERBOARD_FILE).reader())) {
            while(scanner.hasNextLine()){
                String[] line = scanner.nextLine().split(",");
                try {
                    scores.add(new ArchiveEntry(line[0], line[1],line[2],Integer.parseInt(line[3]),Integer.parseInt(line[4])));
                } catch (NumberFormatException e) {
                    // if there is an error, ignore it and go on.
                }
            }
        } catch (Exception e) {
            System.err.println("Leaderboard Error: Could not read leaderboard info from internal path.");
            e.printStackTrace();
            System.exit(0);
        }

        // 排序：等级从高到低
        // Sort
        Collections.sort(scores);

        // The first N(limit)
        if (scores.size() > limit) {
            return new ArrayList<>(scores.subList(0, limit));
        }
        return scores;
    }

    //存档读取
    //Read an Archive with a given player ID
    public static ArchiveEntry getArchive(String playerID){
        ArrayList<ArchiveEntry> scores = new ArrayList<>();
        try (Scanner scanner = new Scanner(Gdx.files.internal(ARCHIEVE_FILE).reader())) {
            while(scanner.hasNextLine()){
                String[] line = scanner.nextLine().split(",");
                try {
                    scores.add(new ArchiveEntry(line[0], line[1],line[2],Integer.parseInt(line[3]),Integer.parseInt(line[4])));
                } catch (NumberFormatException e) {
                    // if there is an error, ignore it and go on.
                }
            }
        } catch (Exception e) {
            System.err.println("Archive Library Error: Could not read archive library info from internal path.");
            e.printStackTrace();
            System.exit(0);
        }
        for(ArchiveEntry data : scores){
            if(data.playerID.equals(playerID))
                return data;
        }

        return null;
    }

}
