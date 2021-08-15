package idnull.znz.losing_weight_is_easy.presentation.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import idnull.znz.losing_weight_is_easy.R
import idnull.znz.losing_weight_is_easy.databinding.SettingsFragmentBinding
import idnull.znz.losing_weight_is_easy.utils.showToast


@AndroidEntryPoint
class SettingsFragment : Fragment() {
    private var _binding: SettingsFragmentBinding? = null
    val mBinding get() = _binding!!

    private val viewModel: SettingsViewModel by viewModels()


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
        viewModel.obtainEvent(SettingsEvent.ScreenShown)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.viewStates().observe(viewLifecycleOwner, Observer { bindViewState(it) })
        viewModel.viewAction().observe(viewLifecycleOwner, Observer {  bindViewAction(it)})

        mBinding.btnApplyChanges.setOnClickListener {
            viewModel.obtainEvent(SettingsEvent.ClickUpdate)
            val nameText = mBinding.etName.text.toString()
            val weightText = mBinding.etWeight.text.toString()
            val kCal = mBinding.etKcal.text.toString()
            viewModel.applyChangesToSharedPref(nameText,weightText,kCal)
        }
    }


    private fun bindViewState(viewState: SettingsViewState) {
        when (viewState.fetchStatus) {
            FetchStatus.Load -> loadData(name = viewState.nameText,weight = viewState.weightText,kCal = viewState.kCal)
            FetchStatus.Update -> loadData(name = viewState.nameText,weight = viewState.weightText,kCal = viewState.kCal)
        }
    }


    fun loadData (name:String,weight:String,kCal:String){
        mBinding.etName.setText(name)
        mBinding.etWeight.setText(weight)
        mBinding.etKcal.setText(kCal)

    }

    private fun bindViewAction(viewAction: SettingsAction) {
        when (viewAction) {
            is SettingsAction.ShowToast -> showToast(viewAction.message)
        }
    }


}


