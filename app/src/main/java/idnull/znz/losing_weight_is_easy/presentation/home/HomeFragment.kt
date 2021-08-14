package idnull.znz.losing_weight_is_easy.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.shunan.circularprogressbar.CircularProgressBar
import dagger.hilt.android.AndroidEntryPoint
import idnull.znz.losing_weight_is_easy.databinding.HomeFragmentBinding
import idnull.znz.losing_weight_is_easy.utils.showToast
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: HomeFragmentBinding? = null
    private val binding get() = _binding!!
    private var progressbar: CircularProgressBar? = null
    private val viewModel: HomeViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().finish()
        }
        viewModel.setInitialValue()
        _binding = HomeFragmentBinding.inflate(layoutInflater, container, false)
        progressbar = binding.circleProgressBar
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        setupProgressbar()
    }

    override fun onStart() {
        super.onStart()
        buttonSetOnClickListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    fun initData() {
        lifecycleScope.launchWhenStarted {
            viewModel.kCal.collect {
                binding.kCalText.text = it.toString()
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.lostValueKCal.collect {
                binding.lostKcal.text = it.toString()
            }
        }
        lifecycleScope.launch {
            binding.target.text = viewModel.readMaxKCalInPreferences().toString()
        }
    }

    fun setupProgressbar() {
        progressbar?.apply {
            lifecycleScope.launchWhenStarted {
                viewModel.kCalPercent.collect {
                    progress = it
                }
                progressCap = 100
            }
        }
    }

    fun buttonSetOnClickListener() {
        binding.button.setOnClickListener {
            val value = binding.editQuery.text.toString()
            if (value.isEmpty() || value.toInt() <= 0 || value.toInt() > 10000) {
                showToast("input KCal")
            } else {
                try {
                    viewModel.addKCal(value.toInt())
                    binding.editQuery.setText("")
                } catch (e: Exception) {
                    showToast("input KCal")
                }
            }
        }
    }

}