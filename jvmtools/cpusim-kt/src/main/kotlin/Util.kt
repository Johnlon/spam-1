fun tobin(value: Int, pad: Int): String {
    return tobin(value, pad, pad-1,0)
}

// return single bit
fun tobin(value: Int, pad: Int, left: Int): String {
    return tobin(value, pad, left,left)
}

fun tobin(value: Int, pad: Int, left: Int, right: Int): String {
    var v = value.toString(2).padStart(pad, '0')
    val leftIdx = (pad - left) - 1
    val rightIdx = (pad - right) - 1
    v = v.slice(leftIdx..rightIdx)

    return v
}

fun slice(biValue: String, left: Int, right: Int): Int {
    val leftIdx = (biValue.length - left) - 1
    val rightIdx = (biValue.length - right) - 1
    val v = biValue.slice(leftIdx..rightIdx)
    return v.toInt(2)
}


fun tobin(value: Boolean): String {
    return if (value) "1" else "0"
}
