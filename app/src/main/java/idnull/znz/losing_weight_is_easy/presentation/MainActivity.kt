package idnull.znz.losing_weight_is_easy.presentation

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import idnull.znz.losing_weight_is_easy.R
import idnull.znz.losing_weight_is_easy.databinding.ActivityMainBinding
import idnull.znz.losing_weight_is_easy.utils.APP_ACTIVITY
import idnull.znz.losing_weight_is_easy.utils.Constants.ACTION_SHOW_TRACKING_FRAGMENT

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    val mBinding get() = _binding!!
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        APP_ACTIVITY = this
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        setSupportActionBar(mBinding.toolbar)
        navigationToTrackingFragmentIfNeeded(intent)
        val navView = mBinding.bottomNavigationView
        navController = Navigation.findNavController(this, R.id.navHostFragment)
        navView.setupWithNavController(navController)
        mBinding.bottomNavigationView.setOnNavigationItemReselectedListener {
        /**do not touch, is need   */ }
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.settingsFragment, R.id.runFragment, R.id.homeFragment -> navView.visibility =
                    View.VISIBLE
                else -> navView.visibility = View.GONE
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { navigationToTrackingFragmentIfNeeded(it) }
    }

    private fun navigationToTrackingFragmentIfNeeded(intent: Intent) {
        if (intent.action == ACTION_SHOW_TRACKING_FRAGMENT) {
            APP_ACTIVITY.navController.navigate(R.id.action_global_trackingFragment)
        }
    }

}









