package com.lev.poalimtv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.lev.poalimtv.ui.home.HomeScreen
import com.lev.poalimtv.ui.theme.PoalimTVTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PoalimTVTheme {
                HomeScreen()
            }
        }
    }
}
