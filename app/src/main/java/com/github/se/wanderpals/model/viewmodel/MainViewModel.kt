package com.github.se.wanderpals.model.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.se.wanderpals.model.repository.TripsRepository
import kotlinx.coroutines.Dispatchers


class MainViewModel(application: Application): AndroidViewModel(application) {

    private lateinit var tripsRepository: TripsRepository

    fun initRepository(userId: String) {
        tripsRepository = TripsRepository(userId, Dispatchers.IO)
        tripsRepository.initFirestore()
    }

    fun getTripsRepository(): TripsRepository {
        return tripsRepository
    }

    class MainViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}