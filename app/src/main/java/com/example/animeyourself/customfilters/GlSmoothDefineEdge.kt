package com.example.animeyourself.customfilters

import android.opengl.GLES20
import com.daasuu.gpuv.egl.filter.GlFilter

class GlSmoothDefineEdge : GlFilter(DEFAULT_VERTEX_SHADER, ANIME_CARTOON_FRAGMENT_SHADER) {
    // Smooth and defined Edge
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
        private const val ANIME_CARTOON_FRAGMENT_SHADER = """
                precision mediump float;
                varying vec2 vTextureCoord; 
               
                uniform sampler2D sTexture; 
                uniform float intensity; 
               
                void main() 
                { 
                    vec3 origTex = texture2D(sTexture, vTextureCoord).rgb; 
                    vec3 smoothTex = texture2D(sTexture, vTextureCoord + vec2(0.001953125, 0.0)).rgb; 
                    vec3 edgeTex = texture2D(sTexture, vTextureCoord + vec2(0.00390625, 0.0)).rgb; 
                    vec3 color = mix(smoothTex, edgeTex, step(length(edgeTex.rgb - origTex.rgb), 0.2));
                    vec3 finalColor = mix(origTex, color, intensity); 
                
                    gl_FragColor = vec4(finalColor, 1.0); 
                }
                """

    }
}