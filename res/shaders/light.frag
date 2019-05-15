#version 450 core

const vec3 SKY = vec3(0.2, 0.7, 1.0);

in vec2 texcoord;

layout(location = 0) out vec4 color;

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
    DirectionalLight dLight;
};

layout(binding = 0) uniform sampler2D gbufferTexture0; // depth
layout(binding = 1) uniform sampler2D gbufferTexture1; // rgb: normal, a: metallic
layout(binding = 2) uniform sampler2D gbufferTexture2; // rgb: albedo, a: roughness

vec3 decodePosition(vec2 uv) {
    float z = texture(gbufferTexture0, uv).r * 2.0 - 1.0;
    vec4 projected = vec4(uv.xy * 2.0 - 1.0, z, 1.0);
    vec4 view = projInv * projected;
    view /= view.w;
    vec4 world = viewInv * view;
    return world.xyz;
}

//vec3 decodeNormal(vec2 uv) {
//    return texture(normalTexture, uv).rgb * 2.0 - 1.0;
//}
//
//vec3 decodeAlbedo(vec2 uv) {
//    return texture(albedoTexture, uv).rgb;
//}

//vec2 decodeMetallicRoughness(vec) {
//    return texture(metallicRoughnessTexture, uv).rg;
//}

vec3 f(vec3 h, vec3 l, vec3 f0) {
    float base = 1.0 - clamp(dot(h, l), 0.0, 1.0);
    float pow5 = base * base;
    pow5 *= pow5;
    pow5 *= base;
    return f0 + (1.0 - f0) * pow5;
}

float g(vec3 n, vec3 l, vec3 v, float a) {
    float nDotL = abs(dot(n, l));
    float nDotV = abs(dot(n, v));
    return 0.5 / mix(2.0f * nDotL * nDotV, nDotL + nDotV, a);
}

float d(vec3 n, vec3 h, float a) {
    float a2 = a * a;

    float nDotM = dot(n, h);
    float denom = 1.0f + nDotM * nDotM * (a2 - 1.0f);

    return a2 / (denom * denom);
}

vec3 brdf(vec3 n, vec3 l, vec3 v, vec3 albedo, vec2 params) {
    float a = max(params.x * params.x, 0.016f);

    vec3 h = normalize(l + v);
    vec3 f0 = mix(vec3(0.04f), albedo, params.y);

    vec3 fresnel = f(h, l, f0);
    float geometry = g(n, l, v, a);
    float ndf = d(n, h, a);

    vec3 specular = fresnel * geometry * ndf;

    vec3 kD = (1.0f - fresnel) * (1.0f - params.yyy);
    vec3 diffuse = kD * albedo;

    return specular + diffuse;
}

void main() {
    vec4 gbuffer1 = texture(gbufferTexture1, texcoord);
    vec4 gbuffer2 = texture(gbufferTexture2, texcoord);
    vec3 p = decodePosition(texcoord);
    vec3 n = normalize(gbuffer1.xyz * 2.0 - 1.0);
    vec3 albedo = gbuffer2.xyz;
    vec2 params = vec2(gbuffer2.w, gbuffer1.w);

    vec3 v = normalize(viewInv[3].xyz - p);
    vec3 l = normalize(-dLight.direction.xyz);

    vec3 ambient = albedo * clamp(n.y * 0.5 + 0.5, 0.0, 1.0) * SKY * 0.15;
    vec3 indirect = albedo * ambient;
    vec3 direct = dLight.color.rgb * brdf(n, l, v, albedo, params) * clamp(dot(n, l), 0.0, 1.0);

    float depth = max(4.0 - p.y, 0.0);
    vec3 extinction = exp(-depth * vec3(1.0, 0.6, 0.4));

    color = vec4((direct + indirect) * extinction, 1.0);
}