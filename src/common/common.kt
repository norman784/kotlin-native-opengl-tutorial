package common

import kotlinx.cinterop.*
import platform.OpenGL3.*
import platform.OpenGLCommon.GLintVar

fun generateBuffer(): Int = memScoped {
    val bufferVar: IntVarOf<Int> = alloc()
    glGenBuffers(1, bufferVar.ptr)
    bufferVar.value
}

fun checkError(message: String?) {
    val error = glGetError()
    if (error != 0) {
        val errorString = when (error) {
            GL_INVALID_ENUM -> "GL_INVALID_ENUM"
            GL_INVALID_VALUE -> "GL_INVALID_VALUE"
            GL_INVALID_OPERATION -> "GL_INVALID_OPERATION"
            GL_INVALID_FRAMEBUFFER_OPERATION -> "GL_INVALID_FRAMEBUFFER_OPERATION"
            GL_OUT_OF_MEMORY -> "GL_OUT_OF_MEMORY"
            else -> "unknown"
        }

        if (message != null) println("- $message")
        throw Exception("\tGL error: 0x${error.toString(16)} ($errorString)")
    }
}

fun checkProgramStatus(program: Int) = memScoped {
    val status = alloc<GLintVar>()
    glGetProgramiv(program, GL_LINK_STATUS, status.ptr)
    if (status.value != GL_TRUE) {
        val log = allocArray<ByteVar>(512)
        glGetProgramInfoLog(program, 512, null, log)
        throw Error("Program linking errors: ${log.toKString()}")
    }
}

fun compileShader(type: Int, source: String) = memScoped {
    val shader = glCreateShader(type)

    if (shader == 0) throw Error("Failed to create shader")

    glShaderSource(shader, 1, cValuesOf(source.cstr.getPointer(memScope)), null)
    glCompileShader(shader)

    val status = alloc<GLintVar>()
    glGetShaderiv(shader, GL_COMPILE_STATUS, status.ptr)

    if (status.value != GL_TRUE) {
        val log = allocArray<ByteVar>(512)
        glGetShaderInfoLog(shader, 512, null, log)
        throw Error("Shader compilation failed: ${log.toKString()}")
    }

    checkError("glShaderSource")

    shader
}