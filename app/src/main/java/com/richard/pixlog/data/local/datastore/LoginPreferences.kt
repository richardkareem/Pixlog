package com.richard.pixlog.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.richard.pixlog.data.remote.response.LoginResponse
import com.richard.pixlog.data.remote.response.LoginResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "login")

class LoginPreferences private constructor(
    private val dataStore: DataStore<Preferences>
){
    private val token = stringPreferencesKey("token")
    private val name = stringPreferencesKey("name")

    suspend fun saveDataLogin(responseLogin : LoginResponse){
        val loginResult = LoginResult(token = responseLogin.loginResult.token, name = responseLogin.loginResult.name, userId = responseLogin.loginResult.userId)
        dataStore.edit {
            it[token] = loginResult.token
            it[name] = loginResult.name
        }
    }

     fun checkToken(): Flow<Boolean> {
        return dataStore.data.map { prefToken ->
            !prefToken[token].isNullOrEmpty()
        }
    }

    fun getToken(): Flow<String>{
        return dataStore.data.map { preferences ->
            preferences[token]?.takeIf { it.isNotEmpty() } ?: ""
        }
    }

    fun getName(): Flow<String>{
        return dataStore.data.map { preferences ->
            preferences[name]?.takeIf { it.isNotEmpty() } ?: ""
        }
    }

    suspend fun clearToken(){
        dataStore.edit {
            it.remove(token)
            it.remove(name)
        }
    }

    companion object{
        @Volatile
        private var INSTANCE : LoginPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>) : LoginPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = LoginPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}