package idnull.znz.losing_weight_is_easy.utils

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import idnull.znz.losing_weight_is_easy.App
import idnull.znz.losing_weight_is_easy.presentation.MainActivity


lateinit var APP_ACTIVITY: MainActivity
lateinit var APP: App

fun showToast(massage: String) {
    Toast.makeText(APP_ACTIVITY, massage, Toast.LENGTH_LONG).show()
}