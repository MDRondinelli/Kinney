#version 450 core

layout(location = 0) in vec3 inPosition;

struct DirectionalLight {
    vec4 color;
    vec4 direction;
};

layout(std140, binding = 1) uniform LightBlock {
    mat4 dLightViewProj[4];
    vec3 dLightSlices; // 16 bytes each
    DirectionalLight dLight;
};

uniform int slice;
uniform mat4 model;

void main() {
    gl_Position = dLightViewProj[slice] * model * vec4(inPosition, 1.0);
}