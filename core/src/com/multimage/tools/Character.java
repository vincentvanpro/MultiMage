package com.multimage.tools;

import com.multimage.item.Item;

import java.util.HashMap;

public interface Character {

    int getArmour();
    int getLevel();
    float getHealth();

    void levelUp();
    void getBonusesFromItems();

    void getPassiveSkillEffect();

    void addItem(String item);

}
