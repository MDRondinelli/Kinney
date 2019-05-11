#version 450 core

in Vertex {
    vec3 position;
    vec3 normal;
} vertex;

layout(location = 0) out vec4 outNormal;
layout(location = 1) out vec4 outAlbedo;

uniform vec3 albedo;

void main() {
    outNormal.xyz = normalize(vertex.normal) * 0.5 + 0.5;
    outNormal.w = 0.0; // metallic
    outAlbedo.xyz = albedo;
    outAlbedo.w = 0.5; // roughness
}