package ccl.exercise.githubsearch.utils

import android.widget.Toast
import androidx.annotation.StringRes
import ccl.exercise.githubsearch.App

fun toast(str: String) = Toast.makeText(App.instance, str, Toast.LENGTH_SHORT).show()

fun toast(@StringRes strId: Int) = Toast.makeText(App.instance, strId, Toast.LENGTH_SHORT).show()