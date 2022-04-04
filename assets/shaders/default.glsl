#type vertex
#version 330 core

layout (location=0) in vec3 aPos;
layout (location=1) in vec4 aColor;
layout (location=2) in vec2 aTexCoords;
layout (location=3) in float aTexID;

uniform mat4 uProjection;
uniform mat4 uView;

out vec4 fColor;
out vec2 fTexCoords;
out float fTexID;

void main()
{
    fColor = aColor;
    fTexCoords = aTexCoords;
    fTexID = aTexID;
    gl_Position = uProjection * uView * vec4(aPos, 1.0);
}

#type fragment
#version 330 core

in vec4 fColor;
in vec2 fTexCoords;
in float fTexID;

uniform sampler2D uTextures[16]; // 16 is the min number of texture slots specified by OpenGL

out vec4 color;

void main()
{
    if (fTexID == 0.0) // 0 is reserved for no texture
    {
        color = fColor;
    }
    else
    {
        color = fColor * texture(uTextures[int(fTexID)], fTexCoords);
    }
}