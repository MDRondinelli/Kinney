#version 450 core

const float FOG_BEGIN = 40.0;
const float FOG_END = 120.0;
const vec3 SKY_UP = vec3(0.1, 0.4, 1.0);
const vec3 SKY_DOWN = vec3(1.0, 1.0, 1.0);

in vec2 texcoord;

layout(location = 0) out vec4 outColor;

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

layout(std140, binding = 1) uniform LightBlock {
    mat4 dLightViewProj[4];
    vec3 dLightSlices; // 16 bytes each
    DirectionalLight dLight;
};

layout(binding = 0) uniform sampler2D depthTexture;
layout(binding = 1) uniform sampler2D colorTexture;

vec3 decodePosition(vec2 uv) {
    float z = texture(depthTexture, uv).r * 2.0 - 1.0;
    vec4 projected = vec4(uv.xy * 2.0 - 1.0, z, 1.0);
    vec4 view = projInv * projected;
    view /= view.w;
    vec4 world = viewInv * view;
    return world.xyz;
}

vec3 tonemap(vec3 x)
{
    float a = 2.51;
    float b = 0.03;
    float c = 2.43;
    float d = 0.59;
    float e = 0.14;
    return clamp((x * (a * x + b)) / (x * (c * x + d) + e), 0.0, 1.0);
}

float linstep(float min, float max, float x) {
    return clamp((x - min) / (max - min), 0.0, 1.0);
}

void main() {
    vec3 position = decodePosition(texcoord);
    vec3 camera = viewInv[3].xyz;
    vec3 v = camera - position;
    float distance = length(v);
    v /= distance;

    float skyFactor = smoothstep(-0.3, 0.2, -v.y);
    vec3 skyColor = mix(SKY_DOWN, SKY_UP, skyFactor) + linstep(0.0, 0.5, pow(clamp(dot(v, dLight.direction.xyz), 0.0, 1.0), 512.0)) * vec3(1.0, 1.0, 0.5);

    float fogFactor = smoothstep(FOG_BEGIN, FOG_END, distance);

    vec3 color = mix(texture(colorTexture, texcoord).rgb, skyColor, fogFactor);
    outColor = vec4(tonemap(color), 1.0);
}