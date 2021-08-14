package idnull.znz.losing_weight_is_easy.presentation.setup

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import idnull.znz.losing_weight_is_easy.R
import idnull.znz.losing_weight_is_easy.dataStore
import idnull.znz.losing_weight_is_easy.databinding.SetupFragmentBinding

import idnull.znz.losing_weight_is_easy.utils.APP_ACTIVITY
import idnull.znz.losing_weight_is_easy.utils.Constants.KEY_FIRST_TIME_TOGGLE
import idnull.znz.losing_weight_is_easy.utils.Constants.KEY_NAME
import idnull.znz.losing_weight_is_easy.utils.Constants.KEY_WEIGHT
import idnull.znz.losing_weight_is_easy.utils.showToast
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class SetupFragment : Fragment() {
    @set:Inject
    var isFirstAppOpen = true

    @Inject
    lateinit var sharedPref: SharedPreferences

    private var _binding: SetupFragmentBinding? = null
    val mBinding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SetupFragmentBinding.inflate(layoutInflater, container, false)

        if (!isFirstAppOpen) {
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.setupFragment, true)
                .build()
            findNavController().navigate(
                R.id.action_setupFragment_to_homeFragment,
                savedInstanceState,
                navOptions
            )
        }

        mBinding.tvContinue.setOnClickListener {
            val success = writePersonalDataToSharedPref()
            if (success) {
                findNavController().navigate(R.id.action_setupFragment_to_homeFragment)
            } else {
                showToast("please enter all the fields")
            }
        }

        return mBinding.root
    }


    private fun writePersonalDataToSharedPref(): Boolean {
        val name = mBinding.etName.text.toString()
        val weightText = mBinding.etWeight.text.toString()
        val kCal = mBinding.etKcal.text.toString()
        if (name.isEmpty() || weightText.isEmpty() || kCal.isEmpty()) {
            return false
        }
        sharedPref.edit()
            .putString(KEY_NAME, name)
            .putFloat(KEY_WEIGHT, weightText.toFloat())
            .putBoolean(KEY_FIRST_TIME_TOGGLE, false)
            .apply()
        lifecycleScope.launch {
            try {
                saveMaxKCalInPreferences(kCal.toInt())
            } catch (e: Exception) {
                showToast("input KCal")
            }

        }
        val toolbarText = "Let's go, $name!"
        APP_ACTIVITY.mBinding.tvToolbarTitle.text = toolbarText
        return true
    }

    private suspend fun saveMaxKCalInPreferences(value: Int) {
        val dataStoreKey = stringPreferencesKey("maxkCal")
        context?.dataStore?.edit {
            it[dataStoreKey] = value.toString()
        }
    }
}











