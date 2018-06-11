import cglew.glewExperimental
import cglew.glewInit
import cglew.GLEW_OK
import cglew.glClear
import cglfw.*
import cstb.*
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
    glfwSetFramebufferSizeCallback(window, staticCFunction(::framebufferSizeCallback))

    val program = ShaderProgram("resources/5/vertex.glsl", "resources/5/fragment.glsl")

    val vertices: FloatArray = floatArrayOf(
        // positions          // colors           // texture coords
         0.5f,  0.5f, 0.0f,   1.0f, 0.0f, 0.0f,   1.0f, 1.0f, // top right
         0.5f, -0.5f, 0.0f,   0.0f, 1.0f, 0.0f,   1.0f, 0.0f, // bottom right
        -0.5f, -0.5f, 0.0f,   0.0f, 0.0f, 1.0f,   0.0f, 0.0f, // bottom left
        -0.5f,  0.5f, 0.0f,   1.0f, 1.0f, 0.0f,   0.0f, 1.0f  // top left
    )

    val indices: IntArray = intArrayOf(
        0, 1, 3, // first triangle
        1, 2, 3  // second triangle
    )

    val vao: Int = glGenVertexArrays(1)
    val vbo: Int = glGenBuffers(1)
    val ebo: Int = glGenBuffers(1)

    glBindVertexArray(vao)

    glBindBuffer(GL_ARRAY_BUFFER, vbo)
    glBufferData(GL_ARRAY_BUFFER, (vertices.size * 4).signExtend(), vertices.refTo(0), GL_STATIC_DRAW)

    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo)
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, (indices.size * 4).signExtend(), indices.refTo(0), GL_STATIC_DRAW)

    glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE.narrow(), 8 * 4, null)
    glEnableVertexAttribArray(0)

    glVertexAttribPointer(1, 3, GL_FLOAT, GL_FALSE.narrow(), 8 * 4, cValuesOf((3 * 4)))
    glEnableVertexAttribArray(1)

    glVertexAttribPointer(2, 2, GL_FLOAT, GL_FALSE.narrow(), 8 * 4, cValuesOf((6 * 4)))
    glEnableVertexAttribArray(2)

    val texture1: Int = loadTexture("resources/5/logo.png")
    val texture2: Int = loadTexture("resources/5/smiley.png")

    // Texture uniforms
    program.use()
    program.setUniform("texture1", 0)
    program.setUniform("texture2", 1)
    program.reset()

    while (glfwGetKey(window, GLFW_KEY_ESCAPE) != GLFW_PRESS && glfwWindowShouldClose(window) == 0) {
        glClearColor(0.2f, 0.3f, 0.3f, 1f)
        glClear(GL_COLOR_BUFFER_BIT)

        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_2D, texture1)

        glActiveTexture(GL_TEXTURE1)
        glBindTexture(GL_TEXTURE_2D, texture2)

        program.use()

        glBindVertexArray(vao)
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, null)

        program.reset()

        glfwSwapBuffers(window)
        glfwPollEvents()
    }

    glDeleteVertexArrays(1, vao)
    glDeleteBuffers(1, vbo)
    glDeleteBuffers(1, ebo)
    program.delete()

    glfwTerminate()
}

private fun framebufferSizeCallback(window: CPointer<GLFWwindow>?, width: Int, height: Int) {
    glViewport(0, 0, width, height)
}

private fun loadTexture(filename: String) = memScoped {
    val width: IntVar = alloc()
    val height: IntVar = alloc()
    val channels: IntVar = alloc()

    val data = stbi_load(filename, width.ptr, height.ptr, channels.ptr, 0)

    if (data == null) {
        var error = "Error:\n\tdata: null"
        if (width.value == 0) error += "\twidth: 0"
        if (height.value == 0) error += "\theight: 0"
        throw Error(error)
    } else {
        val textureIdInterop: IntVarOf<Int> = alloc()
        glGenTextures(1, textureIdInterop.ptr)
        val textureId: Int = textureIdInterop.value

        glBindTexture(GL_TEXTURE_2D, textureId)

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)

        println("Texture $filename ${width.value}x${height.value} (${channels.value})")
        val format = if (channels.value == 4) GL_RGBA else GL_RGB

        glTexImage2D(GL_TEXTURE_2D, 0, format, width.value, height.value, 0, format, GL_UNSIGNED_BYTE, data)

        stbi_image_free(data)

        glBindTexture(GL_TEXTURE_2D, 0)

        textureId
    }
}