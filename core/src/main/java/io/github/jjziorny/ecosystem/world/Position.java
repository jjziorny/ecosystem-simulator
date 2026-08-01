package io.github.jjziorny.ecosystem.world;

public record Position(float x, float y) {

    public float distanceTo(Position other) {
        float deltaX = other.x() - x;
        float deltaY = other.y() - y;

        return (float) Math.sqrt(
            deltaX * deltaX + deltaY * deltaY
        );
    }
}
