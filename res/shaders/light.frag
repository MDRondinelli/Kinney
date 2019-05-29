#version 450 core

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
    mat4 dLightViewProj[4];
    vec4 dLightSlices;
    DirectionalLight dLight;
};

layout(binding = 0) uniform sampler2D gbufferTexture0; // depth
layout(binding = 1) uniform sampler2D gbufferTexture1; // rgb: normal, a: metallic
layout(binding = 2) uniform sampler2D gbufferTexture2; // rgb: albedo, a: roughness
layout(binding = 3) uniform sampler2DShadow dLightCascade0;
layout(binding = 4) uniform sampler2DShadow dLightCascade1;
layout(binding = 5) uniform sampler2DShadow dLightCascade2;
layout(binding = 6) uniform sampler2DShadow dLightCascade3;

vec3 decodePosition(vec2 uv) {
    float z = texture(gbufferTexture0, uv).r * 2.0 - 1.0;
    vec4 projected = vec4(uv.xy * 2.0 - 1.0, z, 1.0);
    vec4 view = projInv * projected;
    view /= view.w;
    vec4 world = viewInv * view;
    return world.xyz;
}

float rand(vec2 co){
    return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);
}

float calcShadow(vec3 p) {
    float dist = -(view * vec4(p, 1.0)).z;

    float angle = rand(gl_FragCoord.xy) * 6.2831853;
    float cosTheta = cos(angle);
    float sinTheta = sin(angle);
    mat2 rotation;
    rotation[0] = vec2(cosTheta, sinTheta);
    rotation[1] = vec2(-sinTheta, cosTheta);

    float ret = 0.0;

    if (dist < dLightSlices.x) {
        vec2 texelSize = vec2(1.0) / textureSize(dLightCascade0, 0);
        p = (dLightViewProj[0] * vec4(p, 1.0)).xyz * 0.5 + 0.5;

        for (float x = -1.0; x <= 1.0; x += 1.0) {
            for (float y = -1.0; y <= 1.0; y += 1.0) {
                float bias = length(vec2(x, y)) * -0.0035;
                ret += texture(dLightCascade0, p + vec3(rotation * vec2(x, y) * texelSize, bias)).r;
            }
        }

        return ret / 9.0;
    }

    if (dist < dLightSlices.y) {
        vec2 texelSize = vec2(1.0) / textureSize(dLightCascade1, 0);
        p = (dLightViewProj[1] * vec4(p, 1.0)).xyz * 0.5 + 0.5;

        for (float x = -1.0; x <= 1.0; x += 1.0) {
            for (float y = -1.0; y <= 1.0; y += 1.0) {
                float bias = length(vec2(x, y)) * -0.0035;
                ret += texture(dLightCascade1, p + vec3(rotation * vec2(x, y) * texelSize, bias)).r;
            }
        }

        return ret / 9.0;
    }

    if (dist < dLightSlices.z) {
        vec2 texelSize = vec2(1.0) / textureSize(dLightCascade2, 0);
        p = (dLightViewProj[2] * vec4(p, 1.0)).xyz * 0.5 + 0.5;

        for (float x = -1.0; x <= 1.0; x += 1.0) {
            for (float y = -1.0; y <= 1.0; y += 1.0) {
                float bias = length(vec2(x, y)) * -0.0035;
                ret += texture(dLightCascade2, p + vec3(vec2(x, y) * texelSize, bias)).r;
            }
        }

        return ret / 9.0;
    }

    vec2 texelSize = vec2(1.0) / textureSize(dLightCascade3, 0);
    p = (dLightViewProj[3] * vec4(p, 1.0)).xyz * 0.5 + 0.5;

    for (float x = -1.0; x <= 1.0; x += 1.0) {
        for (float y = -1.0; y <= 1.0; y += 1.0) {
            float bias = length(vec2(x, y)) * -0.0035;
            ret += texture(dLightCascade3, p + vec3(rotation * vec2(x, y) * texelSize, bias)).r;
        }
    }

    return ret / 9.0;
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

    vec3 ambient = vec3(0.1, 0.4, 1.0) * (n.y * 0.5 + 0.5) * 0.15;// * vec3(0.1, 0.8, 1.0);
    vec3 indirect = albedo * ambient;
    vec3 direct = calcShadow(p) * dLight.color.rgb * albedo/*brdf(n, l, v, albedo, params)*/ * clamp(dot(n, l), 0.0, 1.0);

    float depth = max(4.0 - p.y, 0.0);
    vec3 extinction = exp(-depth * vec3(1.0, 0.6, 0.4));

    color = vec4((direct + indirect) * extinction, 1.0);
}