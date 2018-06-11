package common

class Vector4(val x: Float, val y: Float, val z: Float, val w: Float) {
    constructor(): this(0f, 0f, 0f, 0f)
    constructor(vector: Vector3, w: Float): this(vector.x, vector.y, vector.z, w)
    constructor(vector: Vector3) : this(vector.x, vector.y, vector.z, 0f)

    fun dot(vector: Vector4): Float = x * vector.x + y * vector.y + z * vector.z + w * vector.w

    override fun toString(): String {
        return "Vector4($x, $y, $z, $w)"
    }
}