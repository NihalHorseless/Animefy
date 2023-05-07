package com.example.animefy.customfilters

import com.daasuu.gpuv.egl.filter.GlFilter

class GlCandyRedFilter : GlFilter(DEFAULT_VERTEX_SHADER, CANDY_RED_FRAGMENT_SHADER) {
    // Increases the Red Color Channel of the video
    public override fun onDraw() {}

    companion object {
        private const val CANDY_RED_FRAGMENT_SHADER = """
                precision mediump float;
                varying vec2 vTextureCoord;
              
                uniform sampler2D sTexture;
              
                void main() 
                { 
                    vec3 tex = texture2D(sTexture, vTextureCoord).rgb;
                    float r = tex.r * 1.3;
                    float g = tex.g * 0.8;
                    float b = tex.b * 0.7;
                    gl_FragColor = vec4(r, g, b, 1.0);
                }
                """

    }
}