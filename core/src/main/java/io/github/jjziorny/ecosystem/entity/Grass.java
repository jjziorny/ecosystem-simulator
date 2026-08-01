package io.github.jjziorny.ecosystem.entity;

import io.github.jjziorny.ecosystem.world.Position;

public class Grass {

    private final Position position;

    public Grass(Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }
}
