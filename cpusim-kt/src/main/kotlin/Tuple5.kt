data class Tuple5<out A, out B, out C, out D, out E>(
    val first: A,
    val second: B,
    val third: C,
    val forth: D,
    val fifth: E
) {
    override fun toString(): String {
        return "($first, $second, $third, $forth, $fifth)"
    }
}

