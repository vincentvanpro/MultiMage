package com.multimage.tools;

import com.multimage.item.Item;

import java.util.HashMap;

public interface Character {

    float getArmour();
    int getLevel();
    float getHealth();

    void levelUp();
    void getBonusesFromItems(String item);

    void getPassiveSkillEffect();

    void addItem(String item);

}
