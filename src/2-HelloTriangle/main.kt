import cglew.glewExperimental
import cglew.glewInit
import cglew.GLEW_OK
import cglew.glClear
import cglfw.*
import kotlinx.cinterop.*
import platform.OpenGL3.*
import common.*

private const val vertexShaderSource: String = """#version 330 core
layout (location = 0) in vec3 position;
void main() {
  gl_Position = vec4(position.x, position.y, position.z, 1.0);
}
"""
private const val fragmentShaderSource: String = """#version 330 core
out vec4 color;
void main() {
  color = vec4(1.0f, 0.5f, 0.2f, 1.0f);
}
"""

fun main(args: Array<String>) {
    glewExperimental = GL_TRUE.narrow()

    if (glfwInit() == GL_FALSE) {
        throw Error("Failed to initialize GLFW")
    }

    glfwWindowHint(GL_SAMPLES, 4)
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3)
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3)
    glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE)
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)

    val window = glfwCreateWindow(1024, 768, "GLLab", null, null) ?:
        throw Error("Failed to open GLFW window. If you have an Intel GPU, they are not 3.3 compatible. Try the 2.1 version of the tutorials.")

    glfwMakeContextCurrent(window)
    glewExperimental = GL_TRUE.narrow()

    if (glewInit() != GLEW_OK) {
        throw Error("Failed to initialize GLEW")
    }

    glfwSetInputMode(window, GLFW_STICKY_KEYS, GL_TRUE)

    glViewport(0, 0, 1024, 768)

    val vertexShader = compileShader(GL_VERTEX_SHADER, vertexShaderSource)
    val fragmentShader = compileShader(GL_FRAGMENT_SHADER, fragmentShaderSource)

    val shaderProgram = glCreateProgram()
    glAttachShader(shaderProgram, vertexShader)
    glAttachShader(shaderProgram, fragmentShader)
    glLinkProgram(shaderProgram)

    checkProgramStatus(shaderProgram)

    glDeleteShader(vertexShader)
    glDeleteShader(fragmentShader)

    val vao: Int = memScoped {
        val resultVar: IntVarOf<Int> = alloc()
        glGenVertexArrays(1, resultVar.ptr)
        resultVar.value
    }

    glBindVertexArray(vao)

    val vertexBufferData: FloatArray = floatArrayOf(
            -0.5f, -0.5f,  0f,
             0.5f, -0.5f,  0f,
               0f,  0.5f,  0f
    )

    val vbo: Int = generateBuffer()
    glBindBuffer(GL_ARRAY_BUFFER, vbo)
    glBufferData(GL_ARRAY_BUFFER, (vertexBufferData.size * 4).signExtend(), vertexBufferData.refTo(0), GL_STATIC_DRAW)
    glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE.narrow(), 0, null)
    glEnableVertexAttribArray(0)
    glBindBuffer(GL_ARRAY_BUFFER, 0)

    glBindVertexArray(0)

    while (glfwGetKey(window, GLFW_KEY_ESCAPE) != GLFW_PRESS && glfwWindowShouldClose(window) == 0) {
        glfwPollEvents()
        checkError("glfwPollEvents")
        glClearColor(0.2f, 0.3f, 0.3f, 1f)
        checkError("glClearColor")
        glClear(GL_COLOR_BUFFER_BIT)
        checkError("glClear")

        glUseProgram(shaderProgram)
        glBindVertexArray(vao)
        glDrawArrays(GL_TRIANGLES, 0, 3)
        checkError("glDrawArrays")
        glBindVertexArray(0)

        glfwSwapBuffers(window)

    }

    glfwTerminate()
}