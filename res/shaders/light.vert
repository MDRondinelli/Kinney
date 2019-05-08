#version 450 core

layout(location = 0) in vec3 position;

out vec2 texcoord;

void main() {
    texcoord = position.xy * 0.5 + 0.5;
    gl_Position = vec4(position, 1.0);
}