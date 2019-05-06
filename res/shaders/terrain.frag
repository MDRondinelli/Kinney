#version 450 core

const float FOG_BEGIN = 20.0;
const float FOG_END = 200.0;

in Vertex {
    vec3 position;
    vec3 normal;
    vec3 color;
} vertex;

layout(location = 0) out vec4 color;

layout(std140, binding = 0) uniform FrameBlock {
    mat4 view;
    mat4 viewInv;
    mat4 proj;
    mat4 projInv;
};

void main() {
    vec3 n = normalize(vertex.normal);
    vec3 l = normalize(vec3(1.0, 1.0, 0.0));

    vec3 indirect = vertex.color * vec3(0.7, 0.5, 0.5) * 0.03;
    vec3 direct = vertex.color * clamp(dot(n, l), 0.0, 1.0);

    vec3 camera = viewInv[3].xyz;
    // float fogFactor = exp(-distance * 0.01);
    float distance = length(camera - vertex.position);
    float fogFactor = clamp((distance - FOG_BEGIN) / (FOG_END - FOG_BEGIN), 0.0, 1.0);

    color = vec4(mix(direct + indirect, vec3(0.7, 0.5, 0.5), fogFactor), 1.0);
}