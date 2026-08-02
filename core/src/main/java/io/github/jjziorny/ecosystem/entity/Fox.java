package io.github.jjziorny.ecosystem.entity;

import io.github.jjziorny.ecosystem.world.Position;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Fox {

    private static final float SPEED = 12f;

    private static final float INITIAL_ENERGY = 100f;
    private static final float MAX_ENERGY = 150f;
    private static final float HUNGER_THRESHOLD = 100f;
    private static final float ENERGY_LOSS_PER_SECOND = 7f;
    private static final float ENERGY_GAIN_FROM_RABBIT = 60f;
    private static final float REPRODUCTION_ENERGY_THRESHOLD = 140f;
    private static final float REPRODUCTION_ENERGY_COST = 80f;
    private static final float NEWBORN_ENERGY = 60f;
    private static final float REPRODUCTION_COOLDOWN_DURATION = 15f;

    private Position position;
    private float directionX;
    private float directionY;
    private float energy;
    private float reproductionCooldown;

    public Fox(Position position) {
        this.position = position;
        energy = INITIAL_ENERGY;

        double angle = ThreadLocalRandom.current().nextDouble(
            0,
            Math.PI * 2
        );

        directionX = (float) Math.cos(angle);
        directionY = (float) Math.sin(angle);
    }

    public Rabbit update(
        float deltaTime,
        List<Rabbit> rabbits,
        float captureDistance,
        float minX,
        float maxX,
        float minY,
        float maxY
    ) {
        loseEnergy(deltaTime);
        reproductionCooldown = Math.max(
            0f,
            reproductionCooldown - deltaTime
        );

        if (!isAlive()) {
            return null;
        }

        Rabbit closestRabbit = null;

        if (isHungry()) {
            closestRabbit = findClosestRabbit(rabbits);

            if (closestRabbit != null) {
                pointToward(closestRabbit.getPosition());
            }
        }

        float nextX =
            position.x() + directionX * SPEED * deltaTime;

        float nextY =
            position.y() + directionY * SPEED * deltaTime;

        if (nextX < minX) {
            nextX = minX;
            directionX = Math.abs(directionX);
        } else if (nextX > maxX) {
            nextX = maxX;
            directionX = -Math.abs(directionX);
        }

        if (nextY < minY) {
            nextY = minY;
            directionY = Math.abs(directionY);
        } else if (nextY > maxY) {
            nextY = maxY;
            directionY = -Math.abs(directionY);
        }

        position = new Position(nextX, nextY);

        if (
            closestRabbit != null
                && position.distanceTo(
                closestRabbit.getPosition()
            ) <= captureDistance
        ) {
            gainEnergy(ENERGY_GAIN_FROM_RABBIT);
            return closestRabbit;
        }

        return null;
    }

    private Rabbit findClosestRabbit(List<Rabbit> rabbits) {
        Rabbit closestRabbit = null;
        float smallestDistance = Float.MAX_VALUE;

        for (Rabbit rabbit : rabbits) {
            float distance =
                position.distanceTo(rabbit.getPosition());

            if (distance < smallestDistance) {
                smallestDistance = distance;
                closestRabbit = rabbit;
            }
        }

        return closestRabbit;
    }

    private void pointToward(Position target) {
        float deltaX = target.x() - position.x();
        float deltaY = target.y() - position.y();
        float distance = position.distanceTo(target);

        if (distance <= 0.0001f) {
            return;
        }

        directionX = deltaX / distance;
        directionY = deltaY / distance;
    }

    private void loseEnergy(float deltaTime) {
        energy -= ENERGY_LOSS_PER_SECOND * deltaTime;
    }

    private void gainEnergy(float amount) {
        energy = Math.min(MAX_ENERGY, energy + amount);
    }

    private boolean isHungry() {
        return energy < HUNGER_THRESHOLD;
    }

    public boolean isAlive() {
        return energy > 0f;
    }

    public Position getPosition() {
        return position;
    }
    public boolean canReproduce() {
        return isAlive()
            && energy >= REPRODUCTION_ENERGY_THRESHOLD
            && reproductionCooldown <= 0f;
    }
    public Fox reproduce() {
        if (!canReproduce()) {
            return null;
        }

        energy -= REPRODUCTION_ENERGY_COST;
        reproductionCooldown = REPRODUCTION_COOLDOWN_DURATION;

        Fox newborn = new Fox(
            new Position(position.x(), position.y())
        );

        newborn.energy = NEWBORN_ENERGY;
        newborn.reproductionCooldown =
            REPRODUCTION_COOLDOWN_DURATION;

        return newborn;
    }
}
