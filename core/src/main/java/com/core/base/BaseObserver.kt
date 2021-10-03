package com.core.base


import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import timber.log.Timber
import java.io.File
import java.util.concurrent.Executors

/**
 * Created by aMiir on 1/31/21
 * Drunk, Fix Later
 */
interface BaseObserver {

    companion object {
        private val disposables = CompositeDisposable()

        private val ioThreads = Executors.newFixedThreadPool((Runtime.getRuntime().availableProcessors() * 2))
    }

    fun addDisposable(disposable: Disposable) {
        disposables.add(disposable)
    }

    fun disposeDisposables() {
        disposables.clear()
    }

    fun File.toMultiPart(name: String = "file"): MultipartBody.Part {
        return MultipartBody.Part.createFormData(
            name,
            this.name,
            this.asRequestBody("image/*".toMediaType())
        )
    }

    fun <T> addExecutorThreads(
        observable: Maybe<T>,
        onSuccess: ((T) -> Unit)? = null,
        onError: ((Throwable) -> Unit)? = null
    ) {
        addDisposable(
            observable
                .subscribeOn(Schedulers.from(ioThreads))
                .subscribe({ result ->
                    onSuccess?.invoke(result)
                }, { throwable ->
                    Timber.e(throwable)
                    onError?.invoke(throwable)
                })
        )
    }

    fun <T> addExecutorThreads(
        observable: Single<T>,
        onSuccess: ((T) -> Unit)? = null,
        onError: ((Throwable) -> Unit)? = null
    ) {
        addDisposable(
            observable
                .subscribeOn(Schedulers.from(ioThreads))
                .subscribe({ result ->
                    onSuccess?.invoke(result)
                }, { throwable ->
                    Timber.e(throwable)
                    onError?.invoke(throwable)
                })
        )
    }

    fun <T> addExecutorThreads(
        observable: Observable<T>,
        onSuccess: ((T) -> Unit)? = null,
        onError: ((Throwable) -> Unit)? = null
    ) {
        addDisposable(
            observable
                .subscribeOn(Schedulers.from(ioThreads))
                .subscribe({ result ->
                    onSuccess?.invoke(result)
                }, { throwable ->
                    Timber.e(throwable)
                    onError?.invoke(throwable)
                })
        )
    }

    fun addExecutorThreads(
        observable: Completable,
        onSuccess: (() -> Unit)? = null,
        onError: ((Throwable) -> Unit)? = null
    ) {
        addDisposable(
            observable
                .subscribeOn(Schedulers.from(ioThreads))
                .subscribe({
                    onSuccess?.invoke()
                }, { throwable ->
                    Timber.e(throwable)
                    onError?.invoke(throwable)
                })
        )
    }
}