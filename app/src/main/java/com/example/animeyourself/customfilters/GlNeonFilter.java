package com.example.animeyourself.customfilters;

import android.graphics.Color;
import android.opengl.GLES20;

import com.daasuu.gpuv.egl.filter.GlFilter;

public class GlNeonFilter extends GlFilter {

    private static final String NEON_FRAGMENT_SHADER = ""
            + "precision mediump float;"
            + "varying vec2 vTextureCoord;"
            + "uniform lowp sampler2D sTexture;"
            + "uniform vec3 neonColor;"
            + "uniform float threshold;"
            + "uniform float distance;"
            + "uniform float smoothing;"
            + "void main() {"
            + "    vec4 color = texture2D(sTexture, vTextureCoord);"
            + "    vec3 luminanceVector = vec3(0.2125, 0.7154, 0.0721);"
            + "    float luminance = dot(color.rgb, luminanceVector);"
            + "    vec4 neon = vec4(neonColor, 1.0);"
            + "    vec4 intensity = vec4(vec3(smoothing), 1.0);"
            + "    vec4 edge = vec4(1.0, 1.0, 1.0, 1.0) * smoothstep(threshold - distance, threshold + distance, luminance);"
            + "    gl_FragColor = mix(color, neon, edge * intensity);"
            + "}";

    private float threshold = 0.5f;
    private float distance = 0.1f;
    private float smoothing = 0.1f;
    private int neonColor = Color.rgb(0, 255, 255);

    public GlNeonFilter() {
        super(DEFAULT_VERTEX_SHADER, NEON_FRAGMENT_SHADER);
    }

    public void setThreshold(float threshold) {
        this.threshold = threshold;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public void setSmoothing(float smoothing) {
        this.smoothing = smoothing;
    }

    public void setNeonColor(int neonColor) {
        this.neonColor = neonColor;
    }

    @Override
    public void onDraw() {
        GLES20.glUniform1f(getHandle("threshold"), threshold);
        GLES20.glUniform1f(getHandle("distance"), distance);
        GLES20.glUniform1f(getHandle("smoothing"), smoothing);
        GLES20.glUniform3fv(getHandle("neonColor"), 1, getFloatArrayFromColor(neonColor), 0);
    }

    private float[] getFloatArrayFromColor(int color) {
        float[] colorArray = new float[3];
        colorArray[0] = Color.red(color) / 255.0f;
        colorArray[1] = Color.green(color) / 255.0f;
        colorArray[2] = Color.blue(color) / 255.0f;
        return colorArray;
    }
}
