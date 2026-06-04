package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.data.AppDatabase
import com.example.data.ProgressRepository
import com.example.ui.ScienceViewModel
import com.example.ui.ScienceViewModelFactory
import com.example.ui.screens.AppNavigationWrapper
import com.example.ui.theme.ScienceYear5Theme

class MainActivity : ComponentActivity() {

    private val database by lazy { AppDatabase.getDatabase(this) }
    private val repository by lazy { ProgressRepository(database.progressDao()) }
    
    private val viewModel by viewModels<ScienceViewModel> {
        ScienceViewModelFactory(repository, applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ScienceYear5Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        AppNavigationWrapper(viewModel = viewModel)
                    }
                }
            }
        }
    }
}
