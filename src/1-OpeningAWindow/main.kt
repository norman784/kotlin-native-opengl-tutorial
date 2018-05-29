import cglew.glewExperimental
import cglew.glewInit
import cglew.GLEW_OK
import cglew.glClear
import cglfw.*
import kotlinx.cinterop.*
import platform.OpenGL3.*

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

    while (glfwGetKey(window, GLFW_KEY_ESCAPE) != GLFW_PRESS && glfwWindowShouldClose(window) == 0) {
        glfwPollEvents()
        glClear(GL_COLOR_BUFFER_BIT)
        glfwSwapBuffers(window)

    }

    glfwTerminate()
}