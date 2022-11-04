package com.multimage.tools;

import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.multimage.sprites.Enemy;

public class SteeringBehaviourAI implements Steerable<Vector2> {

    private Body body;
    private boolean tagged;
    private float boundingRadius;
    private float maxLinearSpeed, maxLinearAcceleration;
    private float maxAngularSpeed, maxAngularAcceleration;

    private SteeringBehavior<Vector2> behaviour;
    private SteeringAcceleration<Vector2> steeringOutput;

    public SteeringBehaviourAI(Body body, float boundingRadius) {
        this.body = body;
        this.boundingRadius = boundingRadius;

        this.maxLinearSpeed = 450;
        this.maxLinearAcceleration = 300;
        this.maxAngularSpeed = 30;
        this.maxAngularAcceleration = 5;

        this.tagged = false;
        this.steeringOutput = new SteeringAcceleration<Vector2>(new Vector2());
        this.body.setUserData(this);
    }

    public void update(float delta) {
        if (behaviour != null) {
            behaviour.calculateSteering(steeringOutput);
            applySteering(delta);
        }
    }

    private void applySteering(float delta) {
        boolean anyAccelerations = false;

        if (!steeringOutput.linear.isZero()) {
            Vector2 force = steeringOutput.linear.scl(delta);
            body.applyForceToCenter(force, true);
            anyAccelerations = true;
        }
        // if (steeringOutput.angular != 0) {
        //     body.applyTorque(steeringOutput.angular, true);
        //     anyAccelerations = true;
        // } else {
        //     Vector2 linVel = getLinearVelocity();
        //     if (!linVel.isZero()) {
        //         float newOrientation = vectorToAngle(linVel);
        //         body.setAngularVelocity((newOrientation - getAngularVelocity()) * delta);
        //         body.setTransform(body.getPosition(), newOrientation);
        //     }
        // }
        if (anyAccelerations) {
            Vector2 velocity = body.getLinearVelocity();
            float currentSpeedSquare = velocity.len2();
            if (currentSpeedSquare > maxLinearSpeed * maxLinearSpeed) {
                body.setLinearVelocity(velocity.scl(maxLinearSpeed / (float) Math.sqrt(currentSpeedSquare)));
            }

            if (body.getAngularVelocity() > maxAngularSpeed) {
                body.setAngularVelocity(maxAngularSpeed);
            }
        }
    }

    @Override
    public Vector2 getLinearVelocity() {
        return body.getLinearVelocity();
    }

    @Override
    public float getAngularVelocity() {
        return body.getAngularVelocity();
    }

    @Override
    public float getBoundingRadius() {
        return boundingRadius;
    }

    @Override
    public boolean isTagged() {
        return tagged;
    }

    @Override
    public void setTagged(boolean tagged) {
        this.tagged = tagged;
    }

    @Override
    public float getZeroLinearSpeedThreshold() {
        return 0;
    }

    @Override
    public void setZeroLinearSpeedThreshold(float value) { }

    @Override
    public float getMaxLinearSpeed() {
        return maxLinearSpeed;
    }

    @Override
    public void setMaxLinearSpeed(float maxLinearSpeed) {
        this.maxLinearSpeed = maxLinearSpeed;
    }

    @Override
    public float getMaxLinearAcceleration() {
        return maxLinearAcceleration;
    }

    @Override
    public void setMaxLinearAcceleration(float maxLinearAcceleration) {
        this.maxLinearAcceleration = maxLinearAcceleration;
    }

    @Override
    public float getMaxAngularSpeed() {
        return maxAngularSpeed;
    }

    @Override
    public void setMaxAngularSpeed(float maxAngularSpeed) {
        this.maxAngularSpeed = maxAngularSpeed;
    }

    @Override
    public float getMaxAngularAcceleration() {
        return maxAngularAcceleration;
    }

    @Override
    public void setMaxAngularAcceleration(float maxAngularAcceleration) {
        this.maxAngularAcceleration = maxAngularAcceleration;
    }

    @Override
    public Vector2 getPosition() {
        return body.getPosition();
    }

    @Override
    public float getOrientation() {
        return body.getAngle();
    }

    @Override
    public void setOrientation(float orientation) { }

    public Vector2 newVector() {
        return new Vector2();
    }

    @Override
    public float vectorToAngle(Vector2 vector) {
        return SteeringUtils.vectorToAngle(vector);
    }

    @Override
    public Vector2 angleToVector(Vector2 outVector, float angle) {
        return SteeringUtils.angleToVector(outVector, angle);
    }

    @Override
    public Location<Vector2> newLocation() {
        return null;
    }

    public Body getBody() {
        return body;
    }

    public void setBehaviour(SteeringBehavior<Vector2> behaviour) {
        this.behaviour = behaviour;
    }

    public SteeringBehavior<Vector2> getBehaviour() {
        return behaviour;
    }
}
