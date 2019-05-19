#version 450 core

const vec3 SAND_ALBEDO = vec3(0.85, 0.7, 0.45);//pow(vec3(0.95, 0.88, 0.71), vec3(2.2));
const vec3 GRASS_ALBEDO = vec3(0.55, 0.66, 0.1);//pow(vec3(0.5, 0.9, 0.2), vec3(2.2));//rgb(27%, 46%, 7%)
const vec3 STONE_ALBEDO = vec3(0.61, 0.33, 0.17);//pow(vec3(0.8, 0.6, 0.45), vec3(2.2));//rgb(82%, 71%, 54%)rgb(78%, 88%, 93%)
const vec3 SNOW_ALBEDO = vec3(0.95);

in Vertex {
    vec3 position;
    vec3 normal;
    float altitude;
} vertex;

layout(location = 0) out vec4 outNormal;
layout(location = 1) out vec4 outAlbedo;

layout(std140, binding = 0) uniform CameraBlock {
    mat4 view;
    mat4 viewInv;
    mat4 proj;
    mat4 projInv;
};

float linstep(float min, float max, float x) {
    return clamp((x - min) / (max - min), 0.0, 1.0);
}

void main() {
    float sandFactor = 1.0 - linstep(6.25, 6.75, vertex.altitude);
    float stoneFactor = max(1.0 - (linstep(0.8 , 0.9, vertex.normal.y)), linstep(16.0, 20.0, vertex.altitude));
    float snowFactor = linstep(20.0, 25.0, vertex.altitude);

    stoneFactor = max(linstep(0.0, 0.1, snowFactor), stoneFactor);

    outNormal.xyz = vertex.normal * 0.5 + 0.5;
    outNormal.w = 0.0; // metallic
    outAlbedo.xyz = mix(mix(mix(GRASS_ALBEDO, SAND_ALBEDO, sandFactor), STONE_ALBEDO, stoneFactor), SNOW_ALBEDO, snowFactor);// vertex.color;
    outAlbedo.w = 1.0 - snowFactor * 0.8; // roughness;
    // outMetallicRoughness = vec2(0.0, 0.8);
}