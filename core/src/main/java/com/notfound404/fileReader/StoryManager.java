package com.notfound404.fileReader;

import java.util.ArrayList;
import java.util.List;

public class StoryManager {
    public static class Dialogue {
        public String name, portrait, text;
        Dialogue(String n, String p, String t) { name = n; portrait = p; text = t; }
    }

    private List<Dialogue> sequence = new ArrayList<>();
    private int index = 0;
    private boolean active = false;

    public void trigger(String type) {
        sequence.clear();
        if (type.equals("START")) {
            sequence.add(new Dialogue("HEROINE", "女主3.0.png", "Large number of pursuers detected from the rear!"));
            sequence.add(new Dialogue("HEROINE", "女主3.0.png", "I think we have to take care of them right here."));
            sequence.add(new Dialogue("BOSS", "boss.png", "Running fast, little mouse? Let me show you some discipline."));
            sequence.add(new Dialogue("BOSS", "boss.png", "Minions, ATTACK!"));
            sequence.add(new Dialogue("SOLDIER", "小兵.png", "HA HA HA HA!"));
        } else if (type.equals("REINFORCE")) {
            sequence.add(new Dialogue("HEROINE", "女主3.0.png", "Watch out! They are still sending reinforcements."));
            sequence.add(new Dialogue("SOLDIER", "小兵.png", "You can't run away!"));
        } else if (type.equals("STRONGER")) {
            sequence.add(new Dialogue("HEROINE", "女主3.0.png", "The main hostiles appear to be getting stronger."));
        } else if (type.equals("FINAL")) {
            sequence.add(new Dialogue("HEROINE", "女主3.0.png", "Warning! Powerful energy reading detected. Be careful!"));
            sequence.add(new Dialogue("BOSS", "boss.png", "It seems I have to finish this personally!"));
        }
        index = 0; active = true;
    }

    public boolean next() {
        index++;
        if (index >= sequence.size()) { active = false; return false; }
        return true;
    }

    public Dialogue cur() { return active ? sequence.get(index) : null; }
    public boolean isActive() { return active; }
}