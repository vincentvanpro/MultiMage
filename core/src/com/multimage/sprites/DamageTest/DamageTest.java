//   package com.multimage.sprites.DamageTest;
//
//   import com.multimage.sprites.Demon;
//   import com.multimage.sprites.Fireball;
//   import com.multimage.sprites.Ghost;
//   import com.multimage.sprites.Mage;
//   import org.junit.jupiter.api.BeforeEach;
//   import org.junit.jupiter.api.Test;

// HAD TO COMMENT IT BECAUSE WERE PROBLEMS WITH build.gradle (Junit tests don't run on it)
//   /* to run test go to
//         Settings > Build, Execution, Deployment > Build Tools > Gradle
//         and change Run tests using && Build and Run: from Gradle (Default) to IntelliJ IDEA.
//         Then uncomment it
//         you can't run game on gradle when it is not commented */


//
//   import static org.junit.jupiter.api.Assertions.assertEquals;
//
//   public class DamageTest {
//       public static final int DAMAGE = 50;
//       Ghost ghost;
//       Demon demon;
//       Fireball fireball;
//       Mage mage;
//
//       @BeforeEach
//       void setUp() {
//           ghost = new Ghost();
//           demon = new Demon();
//           fireball = new Fireball(DAMAGE);
//           mage = new Mage();
//       }
//
//       @Test
//       public void damage() {
//           ghost.damage(fireball);
//           assertEquals(ghost.getHealth(), 50);
//           assertEquals(ghost.getHealthPercent(), 0.5);
//
//           demon.damage(fireball);
//           assertEquals(demon.getHealth(), 250);
//           assertEquals(demon.getHealthPercent(), 0.8333333134651184);
//
//           mage.hit();
//           mage.hit();
//           assertEquals(mage.getHealth(), 61f);
//       }
//   }
//