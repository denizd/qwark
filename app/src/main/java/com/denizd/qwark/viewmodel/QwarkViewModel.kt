package com.denizd.qwark.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

open class QwarkViewModel(app: Application) : AndroidViewModel(app) {

    fun doAsync(function: () -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                function()
            }
        }
    }

    fun <T> returnBlocking(function: () -> T): T = runBlocking {
        withContext(Dispatchers.IO) {
            function()
        }
    }
}