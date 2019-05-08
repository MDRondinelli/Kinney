#version 450 core

layout(location = 0) in vec3 inPosition;
layout(location = 1) in vec3 inNormal;

out Vertex {
    vec3 position;
    vec3 normal;
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

uniform mat4 model;

void main() {
    vertex.position = (model * vec4(inPosition, 1.0)).xyz;
    vertex.normal = (model * vec4(inNormal, 0.0)).xyz;
    gl_Position = proj * view * vec4(vertex.position, 1.0);
}