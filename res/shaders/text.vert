#version 450 core

layout(location = 0) in vec2 inPosition;

out vec2 texcoord;

uniform mat4 proj;
uniform vec2 translation;
uniform float scale;

uniform vec2 texcoordOffset;
uniform float texcoordScale;

void main() {
    vec2 baseTexcoord = inPosition * 0.5 + 0.5;
    baseTexcoord.y = 1.0 - baseTexcoord.y;

    texcoord = (baseTexcoord + texcoordOffset) * texcoordScale;
    gl_Position = proj * vec4(inPosition * scale + translation, 0.0, 1.0);
}