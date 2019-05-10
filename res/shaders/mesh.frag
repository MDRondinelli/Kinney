#version 450 core

in Vertex {
    vec3 position;
    vec3 normal;
} vertex;

layout(location = 0) out vec3 outNormal;
layout(location = 1) out vec3 outAlbedo;
layout(location = 2) out vec2 outMetallicRoughness;

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

uniform vec3 albedo;

void main() {
//    vec3 n = normalize(vertex.normal);
    outNormal = normalize(vertex.normal) * 0.5 + 0.5;
    outAlbedo = albedo;
    outMetallicRoughness = vec2(0.0, 0.5);

//    vec3 l = normalize(-dLight.direction.xyz);
//
//    vec3 albedo = vec3(0.5);
//
//    vec3 ambient = clamp(n.y * 0.5 + 0.5, 0.0, 1.0) * vec3(0.14, 0.1, 0.1);
//
//    vec3 indirect = albedo * ambient;
//    vec3 direct = albedo * clamp(dot(n, l), 0.0, 1.0) * dLight.color.rgb;
//
//    vec3 camera = viewInv[3].xyz;
//    float distance = length(camera - vertex.position);
//    float fogFactor = clamp((distance - FOG_BEGIN) / (FOG_END - FOG_BEGIN), 0.0, 1.0);
//
//    color = vec4(mix(direct + indirect, vec3(0.7, 0.5, 0.5), fogFactor), 1.0);
}