package io.github.jjziorny.ecosystem.entity;
import io.github.jjziorny.ecosystem.world.Position;
import java.util.concurrent.ThreadLocalRandom;

public class Rabbit {

    private static final float SPEED = 5f;
    private static final float INITIAL_ENERGY = 100f;
    private static final float ENERGY_LOSS_PER_SECOND = 10f;

    private Position position;
    private float directionX;
    private float directionY;
    private float energy = INITIAL_ENERGY;

    public Rabbit(Position position) {
        this.position = position;
        chooseRandomDirection();
    }

    public void update(
        float deltaTime,
        float minX,
        float maxX,
        float minY,
        float maxY

    ) {
        loseEnergy(deltaTime);

        if (!isAlive()) {
            return;
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
        return energy > 0f;
    }

    public float getEnergy() {
        return energy;
    }
}
