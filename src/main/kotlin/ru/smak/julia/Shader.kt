package ru.smak.julia

import org.lwjgl.opengl.GL20.*

internal class Shader(
        vertexShaderFilename: String, fragmentShaderFilename: String
) {
    private val program: Int

    init {
        val vert = createShader(readFile(vertexShaderFilename), GL_VERTEX_SHADER)
        val frag = createShader(readFile(fragmentShaderFilename), GL_FRAGMENT_SHADER)

        program = glCreateProgram()
        glAttachShader(program, vert)
        glAttachShader(program, frag)
        glLinkProgram(program)
        glValidateProgram(program)

        glDeleteShader(vert)
        glDeleteShader(frag)
    }

    private fun createShader(shaderSource: String, type: Int): Int {
        val shader = glCreateShader(type)
        glShaderSource(shader, shaderSource)
        glCompileShader(shader)
        if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
            System.err.println("Could not compile shader")
            System.err.println(glGetShaderInfoLog(shader))
        }
        return shader
    }

    fun use(action: (Shader) -> Unit) {
        glUseProgram(program)
        action(this)
        glUseProgram(0)
    }

    fun setUniform(name: String, v0: Float, v1: Float, v2: Float, v3: Float) {
        val loc = glGetUniformLocation(program, name)
        glUniform4f(loc, v0, v1, v2, v3)
    }

    fun setUniform(name: String, v0: Float, v1: Float) {
        val loc = glGetUniformLocation(program, name)
        glUniform2f(loc, v0, v1)
    }

    fun dispose() {
        glDeleteProgram(program)
    }
}