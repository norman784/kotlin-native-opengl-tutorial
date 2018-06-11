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

    val program = ShaderProgram("resources/3/SimpleTransform.glsl", "resources/3/SingleColor.glsl")

    glClearColor(0.2f, 0.3f, 0.3f, 1f)

    val vao: Int = glGenVertexArrays(1)

    glBindVertexArray(vao)

    val matrixID = glGetUniformLocation(program.id, "MVP")

    val projection = Matrix4x4.projection(45f.toRadians(), 4f / 3f, 0.1f, 100f)
    val view = Matrix4x4.lookAt(
        Vector3(4f, 3f, 3f),
        Vector3(0f, 0f, 0f),
        Vector3(0f, 1f, 0f)
    )
    val model = Matrix4x4.identity
    val mvp = projection * view * model

    println("projection $projection")
    println("view $view")
    println("model $model")
    println("mvp $mvp")

    val vertexBufferData: FloatArray = floatArrayOf(
        -0.5f, -0.5f,  0f,
        0.5f, -0.5f,  0f,
        0f,  0.5f,  0f
    )

    val vbo: Int = glGenBuffers(1)
    glBindBuffer(GL_ARRAY_BUFFER, vbo)
    glBufferData(GL_ARRAY_BUFFER, (vertexBufferData.size * 4).signExtend(), vertexBufferData.refTo(0), GL_STATIC_DRAW)

    while (glfwGetKey(window, GLFW_KEY_ESCAPE) != GLFW_PRESS && glfwWindowShouldClose(window) == 0) {
        glClear(GL_COLOR_BUFFER_BIT)

        program.use()

        glUniformMatrix4fv(matrixID, 1, false, mvp.values)

        glEnableVertexAttribArray(0)
        glBindBuffer(GL_ARRAY_BUFFER, vbo)
        glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE.narrow(), 0, null)
        glDrawArrays(GL_TRIANGLES, 0, 3)
        glDisableVertexAttribArray(0)

        glfwSwapBuffers(window)
        glfwPollEvents()
    }

    glDeleteVertexArrays(1, vao)
    glDeleteBuffers(1, vbo)
    program.delete()

    glfwTerminate()
}