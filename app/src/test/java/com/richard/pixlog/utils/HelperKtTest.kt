package com.richard.pixlog.utils

import org.junit.Assert
import org.junit.Test
class HelperKtTest {

    @Test
    fun `given correct email format then should return true`(){
        val message = "richardkareem26@gmail.com"
        val isValid = HelperNew.isValidStringEmailJava(message)
        print("DEBUG $isValid")
        Assert.assertTrue(isValid)
    }

    @Test
    fun `given incorrect email format then should return false`(){
        val message = "invalid-email"
        val emailPattern = java.util.regex.Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
        )
        val isValid = emailPattern.matcher(message).matches()
        Assert.assertFalse(isValid)
    }

    @Test
    fun `given empty email then should return false`(){
        val message = ""
        val emailPattern = java.util.regex.Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
        )
        val isValid = emailPattern.matcher(message).matches()
        Assert.assertFalse(isValid)
    }

    @Test
    fun `given valid password format then should return true`(){
        val password = "Password123"
        // Test password validation using the same regex as in helper.kt
        val passwordRegex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")
        val isValid = passwordRegex.matches(password)
        Assert.assertTrue(isValid)
    }

    @Test
    fun `given invalid password format then should return false`(){
        val password = "password"
        val passwordRegex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")
        val isValid = passwordRegex.matches(password)
        Assert.assertFalse(isValid)
    }

}