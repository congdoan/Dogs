package com.cdoan.dogs.viewmodel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cdoan.dogs.model.DogBreed
import com.cdoan.dogs.model.DogDatabase
import com.cdoan.dogs.model.DogsApiService
import com.cdoan.dogs.util.NotificationsHelper
import com.cdoan.dogs.util.SharedPreferencesHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import java.lang.NumberFormatException

class ListViewModel(application: Application) : BaseViewModel(application) {

    sealed class LoadState
    object LoadStateLoading : LoadState()
    class LoadStateFailure(val error: Throwable) : LoadState()
    class LoadStateSuccess(val dogs: List<DogBreed>) : LoadState()

    val data = MutableLiveData<LoadState>()

    private val dogsApiService = DogsApiService()
    private val disposable = CompositeDisposable()

    private val prefHelper = SharedPreferencesHelper(getApplication())
    private var refreshTime = 5 * 60 * 1_000_000_000L // in nanoseconds

    fun refreshBypassCache(completionHandler: () -> Unit) {
        fetchFromRemote(completionHandler)
    }

    fun refresh() {
        checkCacheDuration()
        val updateTime = prefHelper.getUpdateTime()
        if (updateTime != 0L && System.nanoTime() - updateTime < refreshTime) {
            fetchFromLocal()
        } else {
            fetchFromRemote()
        }
    }

    private fun checkCacheDuration() {
        val cacheDurationString = prefHelper.getCacheDuration()!!
        try {
            val cacheDurationInSecs = cacheDurationString.toInt()
            refreshTime = cacheDurationInSecs * 1_000_000_000L
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }
    }

    private fun fetchFromLocal() {
        launch {
            val dogs = DogDatabase(getApplication()).dogDao().getAllDogs()
            dogsRetrieved(dogs)

            Toast.makeText(getApplication(), "Dogs Retrieved from database", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchFromRemote(completionHandler: (() -> Unit)? = null) {
        disposable.add(
            dogsApiService.getDogs()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<List<DogBreed>>() {
                    override fun onSuccess(dogs: List<DogBreed>) {
                        storeDogsLocally(dogs)
                        completionHandler?.invoke()

                        Toast.makeText(getApplication(), "Dogs Retrieved from endpoint", Toast.LENGTH_SHORT).show()

                        NotificationsHelper(getApplication()).createNotification()
                    }

                    override fun onError(error: Throwable) {
                        data.value = LoadStateFailure(error)
                        completionHandler?.invoke()

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

        prefHelper.saveUpdateTime(System.nanoTime())
    }

    private fun dogsRetrieved(dogs: List<DogBreed>) {
        data.value = LoadStateSuccess(dogs)
    }

    override fun onCleared() {
        super.onCleared()

        disposable.clear()
    }

}