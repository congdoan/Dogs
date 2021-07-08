package com.cdoan.dogs.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cdoan.dogs.model.DogBreed

class ListViewModel : ViewModel() {

    sealed class LoadState
    object LoadStateLoading : LoadState()
    object LoadStateError : LoadState()
    class LoadStateSuccess(val dogs: List<DogBreed>) : LoadState()

    val data = MutableLiveData<LoadState>()

    fun refresh() {
        data.value = LoadStateSuccess(
            dogs = listOf(
                DogBreed("1", "Corgi", "15 years"),
                DogBreed("2", "Labrador", "10 years"),
                DogBreed("3", "Rottweiler", "20 years")
            )
        )
    }

}