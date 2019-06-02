#version 450 core

layout(location = 0) in vec2 inPosition;

uniform mat4 proj;

uniform vec2 translation;
uniform vec2 scale;

void main() {
    gl_Position = proj * vec4(inPosition * scale + translation, 0.0, 1.0);
}