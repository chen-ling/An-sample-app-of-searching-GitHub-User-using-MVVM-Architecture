package ccl.exercise.githubsearch.utils

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.StringRes
import ccl.exercise.githubsearch.App

fun toast(str: String) = Toast.makeText(App.instance, str, Toast.LENGTH_SHORT).show()

fun toast(@StringRes strId: Int) = Toast.makeText(App.instance, strId, Toast.LENGTH_SHORT).show()

fun getStr(@StringRes strId: Int) = App.instance.getString(strId)

fun hideKeyboard(activity: Activity?) {
    activity?.let {
        val view = it.currentFocus ?: return
        val inputManager = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }
}