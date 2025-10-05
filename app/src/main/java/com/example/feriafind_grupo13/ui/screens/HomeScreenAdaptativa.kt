package com.example.feriafind_grupo13.ui.screens

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.feriafind_grupo13.viewmodel.MainViewModel

@Composable
fun HomeScreenAdaptativa(
    navControler : NavController,
    viewModel: MainViewModel = viewModel()
) {
    HomeScreenCompacta(navControler,viewModel)
}