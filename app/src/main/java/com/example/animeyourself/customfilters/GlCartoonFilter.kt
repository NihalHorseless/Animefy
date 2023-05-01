package com.example.animeyourself.customfilters

import android.opengl.GLES20
import com.daasuu.gpuv.egl.filter.GlFilter

class GlCartoonFilter : GlFilter(DEFAULT_VERTEX_SHADER, CARTOON_FRAGMENT_SHADER) {
    private val edgeStrengthLocation = 0
    private val edgeThresholdLocation = 0
    private val quantizationLevelsLocation = 0
    private val inputWidthLocation = 0
    private val inputHeightLocation = 0
    private var edgeStrength = 2.0f
    private var edgeThreshold = 0.3f
    private var quantizationLevels = 8.0f
    fun setEdgeStrength(value: Float) {
        edgeStrength = value
    }

    fun setEdgeThreshold(value: Float) {
        edgeThreshold = value
    }

    fun setQuantizationLevels(value: Float) {
        quantizationLevels = value
    }

    public override fun onDraw() {
        GLES20.glUniform1f(edgeStrengthLocation, edgeStrength)
        GLES20.glUniform1f(edgeThresholdLocation, edgeThreshold)
        GLES20.glUniform1f(quantizationLevelsLocation, quantizationLevels)
    }

    companion object {
        private const val CARTOON_FRAGMENT_SHADER = (""
                + "precision highp float;\n" +
                "varying highp vec2 vTextureCoord;\n" +
                "uniform sampler2D sTexture;\n" +
                "uniform float edgeStrength;\n" +
                "uniform float edgeThreshold;\n" +
                "uniform float quantizationLevels;\n" +
                "uniform float inputWidth;\n" +
                "uniform float inputHeight;\n" +
                "\n" +
                "const highp vec3 W = vec3(0.2125, 0.7154, 0.0721);\n" +
                "\n" +
                "void main() {\n" +
                "    highp vec2 texel = vec2(1.0 / inputWidth, 1.0 / inputHeight);\n" +
                "\n" +
                "    // Get the grayscale color of the texture\n" +
                "    highp vec4 color = texture2D(sTexture, vTextureCoord);\n" +
                "    float luminance = dot(color.rgb, W);\n" +
                "\n" +
                "    // Edge detection\n" +
                "    highp vec3 edge;\n" +
                "    edge.x = texture2D(sTexture, vTextureCoord + texel * vec2(-1, -1)).r;\n" +
                "    edge.y = texture2D(sTexture, vTextureCoord + texel * vec2(-1,  1)).r;\n" +
                "    edge.z = texture2D(sTexture, vTextureCoord + texel * vec2( 1,  0)).r;\n" +
                "    edge = abs(edge - luminance);\n" +
                "    edge = step(edgeThreshold, edge);\n" +
                "    edge = 1.0 - edge;\n" +
                "    edge *= edgeStrength;\n" +
                "\n" +
                "    // Quantization\n" +
                "    highp vec4 posterizedImage;\n" +
                "    posterizedImage.xyz = floor((color.rgb * quantizationLevels) + 0.5) / quantizationLevels;\n" +
                "    posterizedImage.w = color.w;\n" +
                "\n" +
                "    // Combine edge detection and posterization\n" +
                "    gl_FragColor = mix(color, posterizedImage, edge.x * edge.y * edge.z);\n" +
                "}")
    }
}