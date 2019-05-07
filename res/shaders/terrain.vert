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

layout(std140, binding = 0) uniform FrameBlock {
    mat4 view;
    mat4 viewInv;
    mat4 proj;
    mat4 projInv;
    DirectionalLight dLight;
};

void main() {
    gl_Position = proj * view * vec4(inPosition, 1.0);
    vertex.position = inPosition;
    vertex.normal = inNormal;
    vertex.color = inColor;
}