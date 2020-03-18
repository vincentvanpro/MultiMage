package com.multimage.tools;

import java.util.HashMap;

public interface Character {

    int getArmour();
    int getLevel();
    float getHealth();

    void levelUp();
    void getBonusesFromItems();

    void getPassiveSkillEffect();

    // HashMap<Item, Integer> getItems();
    // void addItem(Item item);

}
