package common

import kotlin.math.tan

class Matrix4x4() {
    companion object {
        val identity: Matrix4x4 get() {
            return Matrix4x4(
                1f, 0f, 0f, 0f,
                0f, 1f, 0f, 0f,
                0f, 0f, 1f, 0f,
                0f, 0f, 0f, 1f
            )
        }

        fun projection(fovy: Float, aspect: Float, near: Float, far: Float): Matrix4x4 {
            val height = 1f / tan(fovy * 0.5f)
            val width = height * 1f / aspect

            return projection(0f, 0f, width, height, near, far)
        }

        fun projection(x: Float, y: Float, width: Float, height: Float, near: Float, far: Float): Matrix4x4 {
            val distance = far - near
            val farDivDistance = far / distance
            val nearTimesFarDivDistance = near * farDivDistance

            return Matrix4x4(
                    width, 0f, 0f, 0f,
                    0f, height, 0f, 0f,
                    -x, -y, farDivDistance, -nearTimesFarDivDistance,
                    0f, 0f, 1f, 0f
            )
        }

        // FIXME: seems that this is wrong, the result differs from others implementations that works
        fun lookAt(eye: Vector3, at: Vector3, up: Vector3 = Vector3(0f, 0f, 1f)): Matrix4x4 {
            return lookTowards(eye, at - eye, up)
        }

        fun lookTowards(eye: Vector3, forward: Vector3, up: Vector3 = Vector3(0f, 0f, 1f)): Matrix4x4 {
            val f = forward.normalized
            val r = (f * up).normalized
            val u = (r * f).normalized

            return Matrix4x4(Vector4(r), Vector4(u), Vector4(f), Vector4(eye, 1f))
        }
    }

    var values: FloatArray = floatArrayOf(
        0f, 0f, 0f, 0f,
        0f, 0f, 0f, 0f,
        0f, 0f, 0f, 0f,
        0f, 0f, 0f, 0f
    )

    constructor(vararg values: Float): this() {
        if (values.size < 16) throw Error("Matrix contains 16 values, but there was only ${values.size} passed")
        this.values = values
    }

    constructor(col1: Vector4, col2: Vector4, col3: Vector4, col4: Vector4): this(
            col1.x, col1.y, col1.z, col1.w,
            col2.x, col2.y, col2.z, col2.w,
            col3.x, col3.y, col3.z, col3.w,
            col4.x, col4.y, col4.z, col4.w
    )

    operator fun times(vector: Vector4): Vector4 = Vector4(
        this[0, 0] * vector.x + this[0, 1] * vector.y + this[0, 2] * vector.z + this[0, 3] * vector.w,
        this[1, 0] * vector.x + this[1, 1] * vector.y + this[1, 2] * vector.z + this[1, 3] * vector.w,
        this[2, 0] * vector.x + this[2, 1] * vector.y + this[2, 2] * vector.z + this[2, 3] * vector.w,
        this[3, 0] * vector.x + this[3, 1] * vector.y + this[3, 2] * vector.z + this[3, 3] * vector.w
    )

    operator fun times(matrix: Matrix4x4): Matrix4x4 {
        val t = transpose()
        return Matrix4x4(
            t[0].dot(matrix[0]), t[1].dot(matrix[0]), t[2].dot(matrix[0]), t[3].dot(matrix[0]),
            t[0].dot(matrix[1]), t[1].dot(matrix[1]), t[2].dot(matrix[1]), t[3].dot(matrix[1]),
            t[0].dot(matrix[2]), t[1].dot(matrix[2]), t[2].dot(matrix[2]), t[3].dot(matrix[2]),
            t[0].dot(matrix[3]), t[1].dot(matrix[3]), t[2].dot(matrix[3]), t[3].dot(matrix[3])
        )
    }

    operator fun get(row: Int): Vector4 = Vector4(this[row, 0], this[row, 1], this[row, 2], this[row, 3])

    operator fun get(row: Int, col: Int): Float = values[(row * 4) + col]

    operator fun set(row: Int, col: Int, value: Float) {
        values[(row * 4) + col] = value
    }

    fun transpose(): Matrix4x4 = Matrix4x4(
            this[0, 0], this[1, 0], this[2, 0], this[3, 0],
            this[0, 1], this[1, 1], this[2, 1], this[3, 1],
            this[0, 2], this[1, 2], this[2, 2], this[3, 2],
            this[0, 3], this[1, 3], this[2, 3], this[3, 3]
    )

    fun translate(vector: Vector3): Matrix4x4 {
        this[3, 0] = vector.x
        this[3, 1] = vector.y
        this[3, 2] = vector.z
        this[3, 3] = 1f
        return this
    }

    override fun toString(): String {
        return """${super.toString()} [
            ${this[0, 0]}, ${this[0, 1]}, ${this[0, 2]}, ${this[0, 3]},
            ${this[1, 0]}, ${this[1, 1]}, ${this[1, 2]}, ${this[1, 3]},
            ${this[2, 0]}, ${this[2, 1]}, ${this[2, 2]}, ${this[2, 3]},
            ${this[3, 0]}, ${this[3, 1]}, ${this[3, 2]}, ${this[3, 3]}
        ]""".trimIndent()
    }
}