#version 450 core

const float FOG_BEGIN = 20.0;
const float FOG_END = 200.0;

in Vertex {
    vec3 position;
    vec3 normal;
    vec3 color;
} vertex;

//layout(location = 0) out vec4 color;
layout(location = 0) out vec3 normal;
layout(location = 1) out vec3 albedo;

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

void main() {
//    vec3 n = normalize(vertex.normal);
    normal = normalize(vertex.normal) * 0.5 + 0.5;
    albedo = vertex.color;
//
//    vec3 l = normalize(-dLight.direction.xyz);
//
//    vec3 ambient = clamp(n.y * 0.5 + 0.5, 0.0, 1.0) * vec3(0.14, 0.1, 0.1);
//
//    vec3 indirect = vertex.color * ambient;
//    vec3 direct = vertex.color * clamp(dot(n, l), 0.0, 1.0) * dLight.color.rgb;
//
//    vec3 camera = viewInv[3].xyz;
//    float distance = length(camera - vertex.position);
//    float fogFactor = clamp((distance - FOG_BEGIN) / (FOG_END - FOG_BEGIN), 0.0, 1.0);
//
//    color = vec4(mix(direct + indirect, vec3(0.7, 0.5, 0.5), fogFactor), 1.0);
}