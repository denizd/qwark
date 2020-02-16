package com.denizd.qwark.viewmodel

import androidx.lifecycle.ViewModel

// workaround to preserve Fragment with constructor on rotation change
class ConfirmDeletionViewModel : ViewModel() {

    var confirmedFunction: () -> Unit = {}
    var textBody = ""
}