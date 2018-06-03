import cglew.glewExperimental
import cglew.glewInit
import cglew.GLEW_OK
import cglew.glClear
import cglfw.*
import kotlinx.cinterop.*
import platform.OpenGL3.*
import common.*

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

    val program = loadShaders("resources/2/SimpleVertexShader.glsl", "resources/2/SimpleFragmentShader.glsl")

    glClearColor(0.2f, 0.3f, 0.3f, 1f)

    val vao: Int = glGenVertexArrays(1)

    glBindVertexArray(vao)

    val vertexBufferData: FloatArray = floatArrayOf(
            -0.5f, -0.5f,  0f,
             0.5f, -0.5f,  0f,
               0f,  0.5f,  0f
    )

    val vbo: Int = glGenBuffers(1)
    glBindBuffer(GL_ARRAY_BUFFER, vbo)
    glBufferData(GL_ARRAY_BUFFER, (vertexBufferData.size * 4).signExtend(), vertexBufferData.refTo(0), GL_STATIC_DRAW)
    glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE.narrow(), 0, null)
    glEnableVertexAttribArray(0)
    glBindBuffer(GL_ARRAY_BUFFER, 0)

    glBindVertexArray(0)

    while (glfwGetKey(window, GLFW_KEY_ESCAPE) != GLFW_PRESS && glfwWindowShouldClose(window) == 0) {
        glClear(GL_COLOR_BUFFER_BIT)

        glUseProgram(program)
        glBindVertexArray(vao)
        glDrawArrays(GL_TRIANGLES, 0, 3)
        glBindVertexArray(0)

        glfwSwapBuffers(window)
        glfwPollEvents()
    }

    glDeleteBuffers(1, vbo)
    glDeleteProgram(program)
    glDeleteVertexArrays(1, vao)

    glfwTerminate()
}