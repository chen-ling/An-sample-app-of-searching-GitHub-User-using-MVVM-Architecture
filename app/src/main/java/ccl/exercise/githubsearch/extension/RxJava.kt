package ccl.exercise.githubsearch.extension

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

fun <T> Single<T>.fromIo(): Single<T> = subscribeOn(Schedulers.io())

fun <T> Single<T>.toMain(): Single<T> = observeOn(AndroidSchedulers.mainThread())

fun <T> Single<T>.fromIoToMain(): Single<T> = fromIo().toMain()