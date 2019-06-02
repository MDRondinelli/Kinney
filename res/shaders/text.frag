#version 450 core

in vec2 texcoord;
out vec4 outColor;

uniform vec4 color;

layout(binding = 0) uniform sampler2D font;

void main() {
    outColor = vec4(color.rgb, color.a * texture(font, texcoord).r);
}