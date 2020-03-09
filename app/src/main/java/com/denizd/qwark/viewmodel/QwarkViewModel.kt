package com.denizd.qwark.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.denizd.qwark.database.QwarkRepository
import com.denizd.qwark.util.Dependencies
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

open class QwarkViewModel(app: Application) : AndroidViewModel(app) {

    protected val repo: QwarkRepository = Dependencies.repo

    fun doAsync(function: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            function()
        }
    }

    fun <T> returnBlocking(function: () -> T): T = runBlocking(Dispatchers.IO) {
        function()
    }
}