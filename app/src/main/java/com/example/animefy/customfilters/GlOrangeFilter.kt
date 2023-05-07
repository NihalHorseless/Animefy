package com.example.animefy.customfilters

import android.opengl.GLES20
import com.daasuu.gpuv.egl.filter.GlFilter

class GlOrangeFilter : GlFilter(DEFAULT_VERTEX_SHADER, ORANGE_FRAGMENT_SHADER) {
    // Increases  Red and Green scale creating an Orange Filter Effect
    private var redScale = 1.3f
    private var blueScale = 0.6f
    private var greenScale = 1.0f
    fun setRedScale(redScale: Float) {
        this.redScale = redScale
    }

    fun setBlueScale(blueScale: Float) {
        this.blueScale = blueScale
    }

    fun setGreenScale(greenScale: Float) {
        this.greenScale = greenScale
    }

    public override fun onDraw() {
        GLES20.glUniform1f(getHandle("redScale"), redScale)
        GLES20.glUniform1f(getHandle("blueScale"), blueScale)
        GLES20.glUniform1f(getHandle("greenScale"), greenScale)
    }

    companion object {
        private const val ORANGE_FRAGMENT_SHADER = """ precision mediump float; 
                varying vec2 vTextureCoord; 
                uniform lowp sampler2D sTexture; 
                uniform mediump float redScale; 
                uniform mediump float blueScale; 
                uniform mediump float greenScale; 
                void main() 
                { 
                   lowp vec4 textureColor = texture2D(sTexture, vTextureCoord);
                   gl_FragColor = vec4(textureColor.r * redScale, textureColor.g * greenScale, textureColor.b * blueScale, textureColor.a);
                }
                """

    }
}