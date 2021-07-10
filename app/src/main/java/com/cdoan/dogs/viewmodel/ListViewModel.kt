package com.cdoan.dogs.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cdoan.dogs.model.DogBreed
import com.cdoan.dogs.model.DogsApiService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class ListViewModel : ViewModel() {

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
                        data.value = LoadStateSuccess(dogs)
                    }

                    override fun onError(error: Throwable) {
                        data.value = LoadStateFailure(error)

                        Log.e(this.javaClass.simpleName, "getDogs error $error")
                    }
                })
        )
    }

    override fun onCleared() {
        super.onCleared()

        disposable.clear()
    }

}