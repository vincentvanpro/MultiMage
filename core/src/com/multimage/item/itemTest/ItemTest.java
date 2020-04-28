// package com.multimage.item.itemTest;
//
// import com.multimage.sprites.Mage;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
//
// HAD TO COMMENT IT BECAUSE WERE PROBLEMS WITH build.gradle (junit tests don't run on it)
/* to run test go to
      Settings > Build, Execution, Deployment > Build Tools > Gradle
      and change Run tests using && Build and Run: from Gradle (Default) to IntelliJ IDEA.
      Then uncomment it
      you can't run game on gradle when it is not commented */
// import static org.junit.jupiter.api.Assertions.assertEquals;
//
// class ItemTest {
//
//     Mage mage;
//
//     @BeforeEach
//     void setUp() {
//         mage = new Mage();
//     }
//
//     @Test
//     void getBonusesFromItems() {
//         mage.addItem("Ambrosia");
//         mage.addItem("Ambrosia");
//
//         mage.addItem("Book");
//         mage.addItem("Book");
//         assertEquals(mage.getXpBoostPercent(), 0.30f);
//         assertEquals(mage.getHealth(), 106.08f);
//
//         mage.addItem("Boots");
//         mage.addItem("Boots");
//         assertEquals(mage.getSpeed(), 0.28875f);
//
//         mage.addItem("Crown");
//         mage.addItem("Crown");
//         assertEquals(mage.getArmour(), 5.775f);
//         assertEquals(mage.getHealth(), 122.5224f);
//
//         mage.addItem("Hat");
//         mage.addItem("Hat");
//         assertEquals(mage.getDamage(), 13.2f);
//
//         mage.addItem("Ring");
//         mage.addItem("Ring");
//         assertEquals(mage.jump(), 6.25f);
//
//         mage.addItem("Shield");
//         mage.addItem("Shield");
//         assertEquals(mage.getArmour(), 20.775f);
//
//         mage.addItem("Sword");
//         mage.addItem("Sword");
//         mage.addItem("Sword");
//         mage.addItem("Sword");
//         mage.addItem("Sword");
//         mage.addItem("Sword");
//         mage.addItem("Sword");
//         mage.addItem("Sword");
//         assertEquals(mage.getChanceToInstantKill(), 3f);
//     }
//
//     @Test
//     void addItem() {
//         mage.addItem("Ambrosia");
//         mage.addItem("Ambrosia");
//         mage.addItem("Ring");
//         assertEquals(mage.getItems().size(), 2);
//     }
//
//     @Test
//     void getItems() {
//         assertEquals(mage.getItems().size(), 0);
//         mage.addItem("Book");
//         mage.addItem("Ambrosia");
//         mage.addItem("Ring");
//         assertEquals(mage.getItems().size(), 3);
//     }
// }
