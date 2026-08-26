package io.github.jjziorny.ecosystem.entity;
import io.github.jjziorny.ecosystem.world.Position;
import java.util.concurrent.ThreadLocalRandom;
import java.util.List;

public class Rabbit {

    private static final float SPEED = 10f;
    private static final float INITIAL_ENERGY = 100f;
    private static final float ENERGY_LOSS_PER_SECOND = 10f;
    private static final float MAX_ENERGY = 150f;
    private static final float ENERGY_GAIN_FROM_GRASS = 30f;
    private static final float HUNGER_THRESHOLD = 100f;
    private static final float REPRODUCTION_ENERGY_THRESHOLD = 125f;
    private static final float REPRODUCTION_ENERGY_COST = 60f;
    private static final float NEWBORN_ENERGY = 50f;
    private static final float REPRODUCTION_COOLDOWN_DURATION = 8f;
    private static final float MIN_LIFESPAN_SECONDS = 45f;
    private static final float MAX_LIFESPAN_SECONDS = 75f;
    private static final float FOX_DETECTION_DISTANCE = 15f;

    private Position position;
    private float directionX;
    private float directionY;
    private float energy = INITIAL_ENERGY;
    private float reproductionCooldown;
    private float age;
    private final float lifespan;

    public Rabbit(Position position) {
        this.position = position;
        chooseRandomDirection();
        lifespan = (float) ThreadLocalRandom.current().nextDouble(
            MIN_LIFESPAN_SECONDS,
            MAX_LIFESPAN_SECONDS
        );
    }

    public Object update(
        float deltaTime,
        List<Grass> grassPatches,
        List<Fox> foxes,
        float eatingDistance,
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
        age += deltaTime;
        Grass closestGrass = null;
        Fox closestFox = findClosestFox(foxes);

        if (closestFox != null) {
            pointAwayFrom(closestFox.getPosition());
        } else if (isHungry()) {
            closestGrass = findClosestGrass(grassPatches);

            if (closestGrass != null) {
                pointToward(closestGrass.getPosition());
            }
        }
        float nextX = position.x() + directionX * SPEED * deltaTime;
        float nextY = position.y() + directionY * SPEED * deltaTime;

        if (nextX < minX || nextX > maxX) {
            directionX *= -1f;
            nextX = clamp(nextX, minX, maxX);
        }

        if (nextY < minY || nextY > maxY) {
            directionY *= -1f;
            nextY = clamp(nextY, minY, maxY);
        }

        position = new Position(nextX, nextY);
        if (
            closestGrass != null
                && position.distanceTo(closestGrass.getPosition()) <= eatingDistance
        ) {
            gainEnergy(ENERGY_GAIN_FROM_GRASS);
            return closestGrass;
        }


        return null;
    }

    private Fox findClosestFox(List<Fox> foxes) {
        Fox closestFox = null;
        float smallestDistance = FOX_DETECTION_DISTANCE;

        for (Fox fox : foxes) {
            float distance = position.distanceTo(
                fox.getPosition()
            );

            if (distance < smallestDistance) {
                smallestDistance = distance;
                closestFox = fox;
            }
        }

        return closestFox;
    }

    private void pointAwayFrom(Position threat) {
        float deltaX = position.x() - threat.x();
        float deltaY = position.y() - threat.y();
        float distance = position.distanceTo(threat);

        if (distance <= 0.0001f) {
            directionX = -directionX;
            directionY = -directionY;
            return;
        }

        directionX = deltaX / distance;
        directionY = deltaY / distance;
    }
    private void gainEnergy(float amount) {
        energy = Math.min(MAX_ENERGY, energy + amount);
    }

    private void chooseRandomDirection() {
        double angle = ThreadLocalRandom.current()
            .nextDouble(0, Math.PI * 2);

        directionX = (float) Math.cos(angle);
        directionY = (float) Math.sin(angle);
    }

    private float clamp(float value, float minimum, float maximum) {
        return Math.max(minimum, Math.min(value, maximum));
    }

    public Position getPosition() {
        return position;
    }
    private void loseEnergy(float deltaTime) {
        energy = Math.max(
            0f,
            energy - ENERGY_LOSS_PER_SECOND * deltaTime
        );
    }

    public boolean isAlive() {
        return energy > 0f && age < lifespan;
    }

    public float getEnergy() {
        return energy;
    }
    private Grass findClosestGrass(List<Grass> grassPatches) {
        Grass closestGrass = null;
        float smallestDistance = Float.MAX_VALUE;

        for (Grass grass : grassPatches) {
            float distance = position.distanceTo(grass.getPosition());

            if (distance < smallestDistance) {
                smallestDistance = distance;
                closestGrass = grass;
            }
        }

        return closestGrass;
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
    private boolean isHungry() {
        return energy < HUNGER_THRESHOLD;
    }
    public boolean canReproduce() {
        return isAlive()
            && energy >= REPRODUCTION_ENERGY_THRESHOLD
            && reproductionCooldown <= 0f;
    }
    public Rabbit reproduce() {
        if (!canReproduce()) {
            return null;
        }

        energy -= REPRODUCTION_ENERGY_COST;
        reproductionCooldown = REPRODUCTION_COOLDOWN_DURATION;

        Rabbit newborn = new Rabbit(
            new Position(position.x(), position.y())
        );

        newborn.energy = NEWBORN_ENERGY;
        newborn.reproductionCooldown =
            REPRODUCTION_COOLDOWN_DURATION;

        return newborn;
    }
}
