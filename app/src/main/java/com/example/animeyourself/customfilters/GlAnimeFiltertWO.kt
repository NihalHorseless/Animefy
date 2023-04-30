package com.example.animeyourself.customfilters

import android.opengl.GLES20
import com.daasuu.gpuv.egl.filter.GlFilter

class GlAnimeFiltertWO : GlFilter(DEFAULT_VERTEX_SHADER, ANIME_CARTOON_FRAGMENT_SHADER) {
    private var intensity = 4f
    fun setIntensity(intensity: Float) {
        if (intensity < 0.0f) {
            this.intensity = 0.0f
        } else if (intensity > 1.0f) {
            this.intensity = 1.0f
        } else {
            this.intensity = intensity
        }
    }

    public override fun onDraw() {
        GLES20.glUniform1f(getHandle("intensity"), intensity)
    }

    companion object {
        private const val ANIME_CARTOON_FRAGMENT_SHADER = "" +
                "precision mediump float;\n" +
                "varying vec2 vTextureCoord;\n" +
                "\n" +
                "uniform sampler2D sTexture;\n" +
                "uniform float intensity;\n" +
                "\n" +
                "void main()\n" +
                "{\n" +
                "    vec3 origTex = texture2D(sTexture, vTextureCoord).rgb;\n" +
                "    vec3 smoothTex = texture2D(sTexture, vTextureCoord + vec2(0.001953125, 0.0)).rgb;\n" +
                "    vec3 edgeTex = texture2D(sTexture, vTextureCoord + vec2(0.00390625, 0.0)).rgb;\n" +
                "\n" +
                "    vec3 color = mix(smoothTex, edgeTex, step(length(edgeTex.rgb - origTex.rgb), 0.2));\n" +
                "    vec3 finalColor = mix(origTex, color, intensity);\n" +
                "\n" +
                "    gl_FragColor = vec4(finalColor, 1.0);\n" +
                "}"
    }
}