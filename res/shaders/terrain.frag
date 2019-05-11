#version 450 core

const float FOG_BEGIN = 20.0;
const float FOG_END = 200.0;

in Vertex {
    vec3 position;
    vec3 normal;
    vec3 color;
} vertex;

layout(location = 0) out vec4 outNormal;
layout(location = 1) out vec4 outAlbedo;

layout(std140, binding = 0) uniform CameraBlock {
    mat4 view;
    mat4 viewInv;
    mat4 proj;
    mat4 projInv;
};

void main() {
    outNormal.xyz = normalize(vertex.normal) * 0.5 + 0.5;
    outNormal.w = 0.0; // metallic
    outAlbedo.xyz = vertex.color;
    outAlbedo.w = 0.8; // roughness;
    // outMetallicRoughness = vec2(0.0, 0.8);
}