package common

import kotlinx.cinterop.*
import platform.OpenGL3.*
import platform.posix.fclose
import platform.posix.fopen
import platform.posix.fread
import platform.posix.stat
import kotlin.math.PI

fun Float.toRadians(): Float {
    return this * PI.toFloat() / 180
}

fun glGenBuffers(n: Int): Int = memScoped {
    val bufferVar: IntVarOf<Int> = alloc()
    glGenBuffers(n, bufferVar.ptr)
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

fun glUniformMatrix4fv(location: Int, count: Int, transpose: Boolean, value: FloatArray) = memScoped {
    val _transpose = if (transpose) GL_TRUE else GL_FALSE
    platform.OpenGL3.glUniformMatrix4fv(location, count, _transpose.narrow(), value.refTo(0))
}

fun glDeleteBuffers(n: Int, buffers: Int) = memScoped {
    val int = alloc<IntVar>()
    int.value = buffers
    platform.OpenGL3.glDeleteBuffers(n, int.ptr)
}

fun glDeleteVertexArrays(n: Int, arrays: Int) = memScoped {
    val int = alloc<IntVar>()
    int.value = arrays
    platform.OpenGL3.glDeleteVertexArrays(n, int.ptr)
}

fun checkProgramStatus(program: Int) = memScoped {
    val status = alloc<IntVar>()
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

    val status = alloc<IntVar>()
    glGetShaderiv(shader, GL_COMPILE_STATUS, status.ptr)

    if (status.value != GL_TRUE) {
        val log = allocArray<ByteVar>(512)
        glGetShaderInfoLog(shader, 512, null, log)
        throw Error("Shader compilation failed: ${log.toKString()}")
    }

    checkError("glShaderSource")

    shader
}

fun glGenVertexArrays(n: Int): Int = memScoped {
    val resultVar: IntVarOf<Int> = alloc()
    glGenVertexArrays(n, resultVar.ptr)
    resultVar.value
}

fun loadShaders(vertexShaderFile: String, fragmentShaderFile: String): Int {
    val vertexShaderSource = readFile(vertexShaderFile) ?: throw Error("File $vertexShaderFile not found")
    val fragmentShaderSource = readFile(fragmentShaderFile) ?: throw Error("File $fragmentShaderFile not found")
    val vertexShader = compileShader(GL_VERTEX_SHADER, vertexShaderSource)
    val fragmentShader = compileShader(GL_FRAGMENT_SHADER, fragmentShaderSource)

    val program = glCreateProgram()
    glAttachShader(program, vertexShader)
    glAttachShader(program, fragmentShader)
    glLinkProgram(program)

    checkProgramStatus(program)

    glDeleteShader(vertexShader)
    glDeleteShader(fragmentShader)

    return program
}

fun readFile(path: String): String? = memScoped {
    val info = alloc<stat>()
    if (stat(path, info.ptr) != 0) return null
    val size = info.st_size.toInt()
    val result = ByteArray(size)
    val file = fopen(path, "rb") ?: return null
    var position = 0
    while (position < size) {
        val toRead = minOf(size - position, 4096)
        val read = fread(result.refTo(position), 1, toRead.signExtend(), file).toInt()
        if (read <= 0) break
        position += read
    }
    fclose(file)
    return result.stringFromUtf8(0, result.size)
}