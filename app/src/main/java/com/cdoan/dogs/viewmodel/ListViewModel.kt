package com.cdoan.dogs.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cdoan.dogs.model.DogBreed
import com.cdoan.dogs.model.DogDatabase
import com.cdoan.dogs.model.DogsApiService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch

class ListViewModel(application: Application) : BaseViewModel(application) {

    sealed class LoadState
    object LoadStateLoading : LoadState()
    class LoadStateFailure(val error: Throwable) : LoadState()
    class LoadStateSuccess(val dogs: List<DogBreed>) : LoadState()

    val data = MutableLiveData<LoadState>()

    private val dogsApiService = DogsApiService()
    private val disposable = CompositeDisposable()

    fun refresh() {
        fetchFromRemote()
    }

    private fun fetchFromRemote() {
        disposable.add(
            dogsApiService.getDogs()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<List<DogBreed>>() {
                    override fun onSuccess(dogs: List<DogBreed>) {
                        storeDogsLocally(dogs)
                    }

                    override fun onError(error: Throwable) {
                        data.value = LoadStateFailure(error)

                        Log.e(this.javaClass.simpleName, "getDogs error $error")
                    }
                })
        )
    }

    private fun storeDogsLocally(dogs: List<DogBreed>) {
        launch {
            val dao = DogDatabase(getApplication()).dogDao()
            dao.deleteAllDogs()
            val dogIds = dao.insertDogs(*dogs.toTypedArray())
            for (i in dogs.indices) {
                dogs[i].uuid = dogIds[i].toInt()
            }

            dogsRetrieved(dogs)
        }
    }

    private fun dogsRetrieved(dogs: List<DogBreed>) {
        data.value = LoadStateSuccess(dogs)
    }

    override fun onCleared() {
        super.onCleared()

        disposable.clear()
    }

}