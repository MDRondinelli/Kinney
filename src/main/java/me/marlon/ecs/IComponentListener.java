package me.marlon.ecs;

public interface IComponentListener {
    void onComponentAdded(int entity);
    void onComponentRemoved(int entity);
}
