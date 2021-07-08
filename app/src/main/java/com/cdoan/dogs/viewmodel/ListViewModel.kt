package com.cdoan.dogs.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cdoan.dogs.model.DogBreed

class ListViewModel : ViewModel() {

    val dogs = MutableLiveData<List<DogBreed>>()
    val loadError = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()

    fun refresh() {
        dogs.value = listOf(
            DogBreed("1", "Corgi", "15 years"),
            DogBreed("2", "Labrador", "10 years"),
            DogBreed("3", "Rottweiler", "20 years")
        )
        loadError.value = false
        loading.value = false
    }

}