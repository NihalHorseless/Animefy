package com.example.animeyourself.customfilters

import android.opengl.GLES20
import com.daasuu.gpuv.egl.filter.GlFilter

class GlOrangeFilter : GlFilter(DEFAULT_VERTEX_SHADER, ORANGE_FRAGMENT_SHADER) {
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
        private const val ORANGE_FRAGMENT_SHADER = "" +
                "precision mediump float;\n" +
                "varying vec2 vTextureCoord;\n" +
                "uniform lowp sampler2D sTexture;\n" +
                "uniform mediump float redScale;\n" +
                "uniform mediump float blueScale;\n" +
                "uniform mediump float greenScale;\n" +
                "void main()\n" +
                "{\n" +
                "   lowp vec4 textureColor = texture2D(sTexture, vTextureCoord);\n" +
                "   gl_FragColor = vec4(textureColor.r * redScale, textureColor.g * greenScale, textureColor.b * blueScale, textureColor.a);\n" +
                "}"
    }
}