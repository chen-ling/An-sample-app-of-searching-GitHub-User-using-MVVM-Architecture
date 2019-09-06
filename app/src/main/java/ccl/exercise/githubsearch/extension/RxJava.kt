package ccl.exercise.githubsearch.extension

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

fun <T> Observable<T>.fromIo(): Observable<T> = subscribeOn(Schedulers.io())

fun <T> Observable<T>.toMain(): Observable<T> = observeOn(AndroidSchedulers.mainThread())

fun <T> Observable<T>.fromIoToMain(): Observable<T> = fromIo().toMain()