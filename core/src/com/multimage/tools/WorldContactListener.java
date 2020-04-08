package com.multimage.tools;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.multimage.MultiMage;
import com.multimage.item.Item;
import com.multimage.screens.PlayScreen;
import com.multimage.sprites.Enemy;
import com.multimage.sprites.InteractiveTileObject;
import com.multimage.sprites.Mage;

public class WorldContactListener implements ContactListener {

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
                } break;
            } case (MultiMage.ENEMY_BODY_BIT | MultiMage.GROUND_BIT) : {
                if (fixA.getFilterData().categoryBits == MultiMage.ENEMY_BODY_BIT) {
                    ((Enemy) fixA.getUserData()).reverseVelocity(true, false);
                } else {
                    ((Enemy) fixB.getUserData()).reverseVelocity(true, false);
                } break;
            } case (MultiMage.ENEMY_BIT | MultiMage.MAGE_BIT) : {
                // logic to apply damage to wizard here
                break;
            } case (MultiMage.ITEM_BIT | MultiMage.MAGE_BIT) : {
                if (fixA.getFilterData().categoryBits == MultiMage.ITEM_BIT) {
                    ((Item) fixA.getUserData()).use((Mage) fixB.getUserData());
                } else {
                    ((Item) fixB.getUserData()).use((Mage) fixA.getUserData());
                } break;
            }
        }
    }

    @Override
    public void endContact(Contact contact) { }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) { }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) { }
}
