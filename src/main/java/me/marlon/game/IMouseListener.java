package me.marlon.game;

import org.joml.Vector2f;

public interface IMouseListener {
    void onButtonPressed(int button, Vector2f position);
    void onButtonReleased(int button, Vector2f position);
    void onMouseMoved(Vector2f position, Vector2f velocity);
}
