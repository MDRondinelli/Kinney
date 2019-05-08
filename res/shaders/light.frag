#version 450 core

const float FOG_BEGIN = 20.0;
const float FOG_END = 200.0;

in vec2 texcoord;

layout(location = 0) out vec4 color;

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
layout(binding = 1) uniform sampler2D normalTexture;
layout(binding = 2) uniform sampler2D albedoTexture;

vec3 decodePosition(vec2 uv) {
    float z = texture(depthTexture, uv).r;
    vec4 projected = vec4(uv.xy * 2.0 - 1.0, z, 1.0);
    vec4 view = projInv * projected;
    view /= view.w;
    vec4 world = viewInv * view;
    return world.xyz;
}

vec3 decodeNormal(vec2 uv) {
    return texture(normalTexture, uv).rgb * 2.0 - 1.0;
}

vec3 decodeAlbedo(vec2 uv) {
    return texture(albedoTexture, uv).rgb;
}

void main() {
    vec3 p = decodePosition(texcoord);
    vec3 n = decodeNormal(texcoord);
    vec3 albedo = decodeAlbedo(texcoord);

    vec3 l = normalize(-dLight.direction.xyz);

    vec3 ambient = clamp(n.y * 0.5 + 0.5, 0.0, 1.0) * vec3(0.14, 0.1, 0.1);
    vec3 indirect = albedo * ambient;
    vec3 direct = albedo * dLight.color.rgb * clamp(dot(n, l), 0.0, 1.0);

    vec3 camera = viewInv[3].xyz;
    float distance = length(camera - p);
    float fogFactor = clamp((distance - FOG_BEGIN) / (FOG_END - FOG_BEGIN), 0.0, 1.0);

    color = vec4(mix(direct + indirect, vec3(0.7, 0.5, 0.5), fogFactor), 1.0);
}