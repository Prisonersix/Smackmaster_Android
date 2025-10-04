package com.smackmaster.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.smackmaster.android.ui.SmackMasterApp
import com.smackmaster.android.ui.SmackMasterViewModel

class MainActivity : ComponentActivity() {
    private val smackMasterViewModel: SmackMasterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmackMasterApp(
                viewModel = smackMasterViewModel,
                onClose = { finish() }
            )
        }
    }
}
