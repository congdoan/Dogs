package com.cdoan.dogs.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cdoan.dogs.model.DogBreed

class DetailViewModel : ViewModel() {

    val data = MutableLiveData<DogBreed>()

    fun fetchData() {
        data.value = DogBreed(
            "13", "Corgi", "15 years",
            bredFor = "Design, create, experience Life in own way",
            temperament = "some temperament"
        )
    }

}