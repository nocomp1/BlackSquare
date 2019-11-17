package com.example.nextsoundz

import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity

class SettingsDialogActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialog_settings_layout)




    }



}