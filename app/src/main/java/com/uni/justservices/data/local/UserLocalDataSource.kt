package com.uni.justservices.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.mazenrashed.moshikotlinextensions.deserialize
import com.mazenrashed.moshikotlinextensions.serialize
import com.squareup.moshi.Moshi
import com.uni.justservices.data.LocalUser
import com.uni.justservices.data.User
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.security.AccessControlContext

class UserLocalDataSource(private val context: Context) {
    private val userData = stringPreferencesKey("USER_DATA")


    suspend fun saveUserData(user: LocalUser){
        context.appDataStore.edit {
            it[userData] = user.serialize()
        }
    }

    suspend fun getUserData():LocalUser?{
        return context.appDataStore.data.map {
            it[userData]
        }.firstOrNull()?.deserialize<LocalUser>()
    }

    suspend fun clearUserData(){
        context.appDataStore.edit{
            it.remove(userData)
        }
    }

    companion object {
        private val Context.appDataStore: DataStore<Preferences> by preferencesDataStore("app-preferences")
        private val moshi = Moshi.Builder().build()
        private const val USER_DATA="USER_DATA"
    }
}