package com.jnetai.contractnda

import android.app.Application

class App : Application() {
    val database by lazy { com.jnetai.contractnda.data.AppDatabase.getInstance(this) }
}