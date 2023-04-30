package com.example.animeyourself.customfilters

import com.daasuu.gpuv.egl.filter.GlFilter

class GlCandyRedFilter : GlFilter(DEFAULT_VERTEX_SHADER, CANDY_RED_FRAGMENT_SHADER) {
    public override fun onDraw() {}

    companion object {
        private const val CANDY_RED_FRAGMENT_SHADER = "" +
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
                "}"
    }
}