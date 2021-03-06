#version 450 core

layout(location = 0) in vec2 inPosition;
layout(location = 1) in vec2 inOffs0;
layout(location = 2) in vec2 inOffs1;

out Vertex {
    vec3 position;
    vec3 normal;
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
uniform float time;

float rand(vec2 co){
    return fract(sin(dot(co, vec2(12.9898, 78.233))) * 43758.5453);
}

vec3 getWave(vec2 pos) {
    float offs = rand(pos) * 128.0;
    float octave1 = sin(time / 2.0 + offs);
    float octave2 = sin(time       + offs + 123.0) / 2.0;
    float octave3 = sin(time * 2.0 + offs + 456.0) / 4.0;
    return vec3(0.0, octave1 + octave2 + octave3, 0.0) * 0.25;//vec3(sin(time * 0.5 + offs + 123.0), sin(time * 0.75 + offs), cos(time * 0.5 + offs + 456.0)) * vec3(0.5, 0.25, 0.5);
}

void main() {
    vec3 pos0 = vec3(inPosition.x, 0.0, inPosition.y) + getWave(inPosition);
    vec4 worldPos0 = model * vec4(pos0, 1.0);

    gl_Position = proj * view * worldPos0;
    vertex.position = worldPos0.xyz;

    vec2 pos1xy = inPosition + inOffs0;
    vec2 pos2xy = inPosition + inOffs1;

    vec3 pos1 = vec3(pos1xy.x, 0.0, pos1xy.y) + getWave(pos1xy);
    vec3 pos2 = vec3(pos2xy.x, 0.0, pos2xy.y) + getWave(pos2xy);

    vertex.normal = normalize(cross(pos1 - pos0, pos2 - pos0));
}
