package ru.smak.julia

import org.lwjgl.BufferUtils
import org.lwjgl.glfw.*
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.ARBVertexArrayObject.glBindVertexArray
import org.lwjgl.opengl.ARBVertexArrayObject.glGenVertexArrays
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL20.*
import org.lwjgl.system.MemoryStack.stackPush
import org.lwjgl.system.MemoryUtil.NULL
import java.io.*
import java.net.URL
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread


internal val resourceLocator = DefaultResourceLocator()

class JuliaSetWindow  {

    private val isLaunchedAtomic = AtomicBoolean()
    val isLaunched: Boolean
      get() = isLaunchedAtomic.get()

    private val offsetX = AtomicInteger()
    private val offsetY = AtomicInteger()

    private val width = AtomicInteger()
    private val height = AtomicInteger()

    var colorScheme: Int = 1
      set(value) {
          field = value
          scheme.set(1 shr value)
      }

    private var scheme = AtomicInteger(0x1)

    private var glCapabilitiesCreated = false

    fun updateState(x: Double, y: Double) {
        this.offsetX.set(java.lang.Float.floatToIntBits(x.toFloat()))
        this.offsetY.set(java.lang.Float.floatToIntBits(y.toFloat()))
    }

    fun launch() {
        // Launch GLFW window instance in the dedicated thread
        isLaunchedAtomic.set(true)
        thread {
            init()
            loop()

            shader?.dispose()
            // Free the window callbacks and destroy the window
            glfwFreeCallbacks(window)
            glfwDestroyWindow(window)

            // Terminate GLFW and free the error callback
            glfwTerminate()
            glfwSetErrorCallback(null)?.free()
            isLaunchedAtomic.set(false)
        }
    }

    private var window: Long = 0

    private var shader: Shader? = null

    private fun init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set()

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        check(glfwInit()) { "Unable to initialize GLFW" }

        // Configure GLFW
        glfwDefaultWindowHints() // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE) // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE) // the window will be resizable

        // Create the window
        window = glfwCreateWindow(1024, 768, "Множество Жюлиа", NULL, NULL)
        if (window == NULL) throw RuntimeException("Failed to create the GLFW window")

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window) { window: Long, key: Int, _: Int, action: Int, _: Int ->
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) glfwSetWindowShouldClose(window, true) // We will detect this in the rendering loop
        }

        glfwSetWindowSizeCallback(window) { window: Long, width: Int, height: Int ->
            if (glCapabilitiesCreated) {
                glViewport(0, 0, width, height)
            }
            this.width.set(width)
            this.height.set(height)
        }

        stackPush().use { stack ->
            val pWidth = stack.mallocInt(1) // int*
            val pHeight = stack.mallocInt(1) // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight)

            // Get the resolution of the primary monitor
            val vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor())!!

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth[0]) / 2,
                    (vidmode.height() - pHeight[0]) / 2
            )
        }

        // Make the OpenGL context current
        glfwMakeContextCurrent(window)
        // Enable v-sync
        glfwSwapInterval(1)

        // Make the window visible
        glfwShowWindow(window)

        GL.createCapabilities()
        glCapabilitiesCreated = true
        // Create shader program
        shader = Shader("shaders/julia.vert", "shaders/julia.frag")
        val positions = floatArrayOf(
                -1.0f, -1.0f, 0.0f,
                1.0f, 1.0f, 0.0f,
                -1.0f, 1.0f, 0.0f,
                -1.0f, -1.0f, 0.0f,
                1.0f, -1.0f, 0.0f,
                1.0f, 1.0f, 0.0f
        )

        vao = glGenVertexArrays()
        glBindVertexArray(vao)

        val positionVbo = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, positionVbo)
        glBufferData(
                GL_ARRAY_BUFFER,
                BufferUtils.createFloatBuffer(positions.size).put(positions).flip(),
                GL_STATIC_DRAW
        )
        glEnableVertexAttribArray(0)
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0)

    }

    private var vao = 0

    private fun loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities()

        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f)

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT) // clear the framebuffer
            shader?.use {
                val ox = java.lang.Float.intBitsToFloat(offsetX.get())
                val oy = java.lang.Float.intBitsToFloat(offsetY.get())
                glBindVertexArray(vao)
                it.setUniform("u_resolution",
                        width.get().toFloat(),
                        height.get().toFloat()
                )
                it.setUniform("u_offset",
                        ox, oy
                )
                glDrawArrays(GL_TRIANGLES, 0, 6)
                glBindVertexArray(0)
            }
            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwSwapBuffers(window) // swap the color buffers

            glfwPollEvents()
        }
    }
}

internal fun readFile(filename: String): String {
    val stream = resourceLocator.getResourceAsStream(filename)
    if (stream != null) {
        val sBuffer = StringBuffer()
        val br = BufferedReader(InputStreamReader(stream))
        val buffer = CharArray(1024)

        var cnt: Int
        while (br.read(buffer, 0, buffer.size).also { cnt = it } > -1) {
            sBuffer.append(buffer, 0, cnt)
        }
        br.close()
        stream.close()
        return sBuffer.toString()
    }
    return ""
}

internal class DefaultResourceLocator : ResourceLocator {
    override fun getResourceAsStream(ref: String): InputStream? {
        val `in`: InputStream? = JuliaSetWindow::class.java.classLoader.getResourceAsStream(ref)
        if (`in` == null) { // try file system
            try {
                return FileInputStream(createFile(ref))
            } catch (e: IOException) {
            }
        }
        return `in`
    }

    override fun getResource(ref: String): URL? {
        val url: URL? = JuliaSetWindow::class.java.classLoader.getResource(ref)
        if (url == null) {
            try {
                val f = createFile(ref)
                if (f.exists()) return f.toURI().toURL()
            } catch (e: IOException) {
            }
        }
        return url
    }

    companion object {
        val ROOT = File(".")
        private fun createFile(ref: String): File {
            var file = File(ROOT, ref)
            if (!file.exists()) {
                file = File(ref)
            }
            return file
        }
    }
}

internal interface ResourceLocator {
    fun getResource(ref: String): URL?
    fun getResourceAsStream(ref: String): InputStream?
}