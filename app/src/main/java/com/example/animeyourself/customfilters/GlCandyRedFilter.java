package com.example.animeyourself.customfilters;

import com.daasuu.gpuv.egl.filter.GlFilter;

public class GlCandyRedFilter extends GlFilter {

    private static final String BREAKING_BAD_FRAGMENT_SHADER = "" +
            "precision mediump float;\n" +
            "varying vec2 vTextureCoord;\n" +
            "\n" +
            "uniform sampler2D sTexture;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    vec3 tex = texture2D(sTexture, vTextureCoord).rgb;\n" +
            "    float r = tex.r * 1.3;\n" +
            "    float g = tex.g * 0.8;\n" +
            "    float b = tex.b * 0.7;\n" +
            "    gl_FragColor = vec4(r, g, b, 1.0);\n" +
            "}";

    public GlCandyRedFilter() {
        super(DEFAULT_VERTEX_SHADER, BREAKING_BAD_FRAGMENT_SHADER);
    }

    @Override
    public void onDraw() {}
}
