package idnull.znz.losing_weight_is_easy.presentation.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey

import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import idnull.znz.losing_weight_is_easy.R
import idnull.znz.losing_weight_is_easy.dataStore
import idnull.znz.losing_weight_is_easy.databinding.SettingsFragmentBinding

import idnull.znz.losing_weight_is_easy.utils.APP_ACTIVITY
import idnull.znz.losing_weight_is_easy.utils.Constants.KEY_NAME
import idnull.znz.losing_weight_is_easy.utils.Constants.KEY_WEIGHT
import idnull.znz.losing_weight_is_easy.utils.showToast
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class SettingsFragment : Fragment() {

    @Inject
    lateinit var sharedPref: SharedPreferences

    private var _binding: SettingsFragmentBinding? = null
    val mBinding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigate(
                R.id.homeFragment
            )
        }
        _binding = SettingsFragmentBinding.inflate(layoutInflater, container, false)

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadFieldsFromSharedPref()
        mBinding.btnApplyChanges.setOnClickListener {
            val success = applyChangesToSharedPref()
            if (success) {
                showToast("Saved changes")
            } else {
                showToast("Please fill out all fields")

            }
        }
    }

    private fun applyChangesToSharedPref(): Boolean {
        val nameText = mBinding.etName.text.toString()
        val weightText = mBinding.etWeight.text.toString()
        val kCal = mBinding.etKcal.text.toString()
        if (nameText.isEmpty() || weightText.isEmpty() || kCal.isEmpty()) {
            return false
        }
        sharedPref.edit()
            .putString(KEY_NAME, nameText)
            .putFloat(KEY_WEIGHT, weightText.toFloat())
            .apply()
        val toolbarText = "Let's go, $nameText!"
        lifecycleScope.launch {
            try {
                saveMaxKCalInPreferences(kCal.toInt())
            } catch (e: Exception) {
                showToast("input KCal")
            }

        }
        APP_ACTIVITY.mBinding.tvToolbarTitle.text = toolbarText
        return true
    }

    private fun loadFieldsFromSharedPref() {
        val name = sharedPref.getString(KEY_NAME, "")
        val weight = sharedPref.getFloat(KEY_WEIGHT, 80f)
        var Kcal = ""
        lifecycleScope.launch {
            Kcal = readMaxKCalInPreferences().toString()

        }.onJoin

        mBinding.etName.setText(name.toString())
        mBinding.etWeight.setText(weight.toString())
        mBinding.etKcal.setText(Kcal)
    }


    private suspend fun saveMaxKCalInPreferences(value: Int) {
        val dataStoreKey = stringPreferencesKey("maxkCal")
        context?.dataStore?.edit {
            it[dataStoreKey] = value.toString()
        }

    }

    private suspend fun readMaxKCalInPreferences(): Int {
        val dataStoreKey = stringPreferencesKey("maxkCal")
        val data = context?.dataStore!!.data.first()
        return if (data[dataStoreKey] == null) 0 else data[dataStoreKey].toString().toInt()
    }
}


