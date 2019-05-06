#version 450 core

in Vertex {
    vec3 position;
    vec3 normal;
} vertex;

layout(location = 0) out vec4 color;

void main() {
    color = vec4(0.5, 0.5, 0.5, 1.0);
}