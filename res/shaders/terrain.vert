#version 450 core

layout(location = 0) in vec3 inPosition;
layout(location = 1) in vec3 inNormal;
layout(location = 2) in vec3 inColor;

out Vertex {
    vec3 position;
    vec3 normal;
    vec3 color;
} vertex;

struct DirectionalLight {
    vec4 color;
    vec4 direction;
};

layout(std140, binding = 0) uniform CameraBlock {
    mat4 view;
    mat4 viewInv;
    mat4 proj;
    mat4 projInv;
};

uniform mat4 model;

void main() {
    vec4 worldSpace = model * vec4(inPosition, 1.0);

    gl_Position = proj * view * model * worldSpace;
    vertex.position = worldSpace.xyz;
    vertex.normal = inNormal;
    vertex.color = inColor;
}