package com.multimage.tools;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.multimage.MultiMage;
import com.multimage.item.Item;
import com.multimage.sprites.Enemy;
import com.multimage.sprites.InteractiveTileObject;
import com.multimage.sprites.Mage;

public class WorldContactListener implements ContactListener {

    private boolean movingOut;

    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        if (fixA.getUserData() == "body" || fixB.getUserData() == "body") {
            Fixture head = fixA.getUserData()  == "body" ? fixA : fixB;
            Fixture object = head == fixA ? fixB : fixA;

            if (object.getUserData() != null && InteractiveTileObject.class.isAssignableFrom(object.getUserData().getClass())) {
                ((InteractiveTileObject) object.getUserData()).onBodyHit();
            }
        }

        switch (cDef) {
            case (MultiMage.ENEMY_BODY_BIT | MultiMage.MAGE_BIT) : {
                if (fixA.getFilterData().categoryBits == MultiMage.ENEMY_BODY_BIT) {
                    ((Enemy) fixA.getUserData()).hitOnHead();
                } else if (fixB.getFilterData().categoryBits == MultiMage.ENEMY_BODY_BIT) {
                    ((Enemy) fixB.getUserData()).hitOnHead();
                }
                break;
            } case (MultiMage.ENEMY_BODY_BIT | MultiMage.GROUND_BIT) : {
                if (fixA.getFilterData().categoryBits == MultiMage.ENEMY_BODY_BIT) {
                    ((Enemy) fixA.getUserData()).reverseVelocity(true, false);
                } else {
                    ((Enemy) fixB.getUserData()).reverseVelocity(true, false);
                } break;
            } case (MultiMage.ENEMY_BIT | MultiMage.MAGE_BIT) : {
                // logic to apply damage to wizard here
                if (fixA.getFilterData().categoryBits == MultiMage.MAGE_BIT) {
                    ((Mage) fixA.getUserData()).hit();
                } else {
                    ((Mage) fixB.getUserData()).hit();
                }
                break;
            } case (MultiMage.ITEM_BIT | MultiMage.MAGE_BIT) : {
                if (fixA.getFilterData().categoryBits == MultiMage.ITEM_BIT) {
                    ((Item) fixA.getUserData()).use((Mage) fixB.getUserData());
                } else {
                    ((Item) fixB.getUserData()).use((Mage) fixA.getUserData());
                } break;
            } case (MultiMage.PLATFORM_BIT | MultiMage.MAGE_BIT) : {

                int pointCount = contact.getWorldManifold().getNumberOfContactPoints();
                Vector2[] points = contact.getWorldManifold().getPoints();

                for (int i = 0; i < pointCount; i++){
                    points[i].scl(1 / MultiMage.PPM);
                    Vector2 heroVel, platformVel;
                    if (fixA.getFilterData().categoryBits == MultiMage.MAGE_BIT) {
                        heroVel = fixA.getBody().getLinearVelocityFromWorldPoint(points[i]);
                        platformVel = fixB.getBody().getLinearVelocityFromWorldPoint(points[i]);
                    } else {
                        heroVel = fixB.getBody().getLinearVelocityFromWorldPoint(points[i]);
                        platformVel = fixA.getBody().getLinearVelocityFromWorldPoint(points[i]);
                    }

                    Vector2 relVel = new Vector2(heroVel.x - platformVel.x,heroVel.y - platformVel.y);
                    if (relVel.y < 0f)
                        return;
                }

                movingOut = true;
                break;
            }
        }
    }

    @Override
    public void endContact(Contact contact) {
        movingOut = false;
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        if (movingOut) {
            contact.setEnabled(false);
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) { }
}
