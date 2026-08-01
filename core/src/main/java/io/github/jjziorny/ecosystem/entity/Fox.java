package io.github.jjziorny.ecosystem.entity;

import io.github.jjziorny.ecosystem.world.Position;

public class Fox {

    private Position position;

    public Fox(Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }
}
