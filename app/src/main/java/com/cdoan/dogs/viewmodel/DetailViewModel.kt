package com.cdoan.dogs.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cdoan.dogs.model.DogBreed
import com.cdoan.dogs.model.DogDatabase
import kotlinx.coroutines.launch

class DetailViewModel(application: Application) : BaseViewModel(application) {

    val data = MutableLiveData<DogBreed>()

    fun fetchData(dogUuid: Int) {
        launch {
            DogDatabase(getApplication()).dogDao().getDog(dogUuid)?.let {
                data.value = it
            }
        }
    }

}