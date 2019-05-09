#version 450 core

const vec3 WATER_ALBEDO = vec3(0.0, 0.25, 0.5);

in Vertex {
    vec3 position;
    vec3 normal;
} vertex;

layout(location = 0) out vec4 outColor;

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

layout(binding = 0) uniform sampler2D depthTexture;

vec3 decodePosition(vec2 uv) {
    float z = texture(depthTexture, uv).r * 2.0 - 1.0;
    vec4 projected = vec4(uv.xy * 2.0 - 1.0, z, 1.0);
    vec4 view = projInv * projected;
    view /= view.w;
    vec4 world = viewInv * view;
    return world.xyz;
}

void main() {
    float depth = length(vertex.position - decodePosition(gl_FragCoord.xy / textureSize(depthTexture, 0)));
    float alpha = 1.0 - exp(-depth * 0.25);

    vec3 n = normalize(vertex.normal);
    vec3 l = normalize(-dLight.direction.xyz);

    vec3 diffuse = WATER_ALBEDO * dLight.color.rgb * clamp(dot(n, l), 0.0, 1.0);

    outColor = vec4(diffuse, alpha);
}