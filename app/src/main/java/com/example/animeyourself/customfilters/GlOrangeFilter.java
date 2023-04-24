package com.example.animeyourself.customfilters;

import android.opengl.GLES20;

import com.daasuu.gpuv.egl.filter.GlFilter;

public class GlOrangeFilter extends GlFilter {

    private static final String OBAMAHOPE_FRAGMENT_SHADER = "" +
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
            "}";

    public GlOrangeFilter() {
        super(DEFAULT_VERTEX_SHADER, OBAMAHOPE_FRAGMENT_SHADER);
    }

    private float redScale = 1.3f;
    private float blueScale = 0.6f;
    private float greenScale = 1.0f;

    public void setRedScale(float redScale) {
        this.redScale = redScale;
    }

    public void setBlueScale(float blueScale) {
        this.blueScale = blueScale;
    }

    public void setGreenScale(float greenScale) {
        this.greenScale = greenScale;
    }

    @Override
    public void onDraw() {
        GLES20.glUniform1f(getHandle("redScale"), redScale);
        GLES20.glUniform1f(getHandle("blueScale"), blueScale);
        GLES20.glUniform1f(getHandle("greenScale"), greenScale);
    }
}