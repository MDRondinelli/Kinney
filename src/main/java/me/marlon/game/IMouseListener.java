package me.marlon.game;

import org.joml.Vector2f;

public interface IMouseListener {
    void onButtonPressed(int button);
    void onButtonReleased(int button);
    void onMouseMoved(Vector2f position, Vector2f velocity);
}
