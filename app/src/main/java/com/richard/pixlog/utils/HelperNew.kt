package com.richard.pixlog.utils
import java.util.regex.Pattern // Import Java's Pattern
object HelperNew {
    private val EMAIL_ADDRESS_PATTERN_JAVA: Pattern = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")

    fun isValidStringEmailJava(email: String): Boolean {
        return EMAIL_ADDRESS_PATTERN_JAVA.matcher(email).matches()
    }
}
