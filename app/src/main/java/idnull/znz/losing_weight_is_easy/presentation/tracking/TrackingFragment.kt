package idnull.znz.losing_weight_is_easy.presentation.tracking

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import dagger.hilt.android.AndroidEntryPoint
import idnull.znz.losing_weight_is_easy.R
import idnull.znz.losing_weight_is_easy.data.services.Polyline
import idnull.znz.losing_weight_is_easy.data.services.TrackingServices
import idnull.znz.losing_weight_is_easy.databinding.TrackingFragmentBinding
import idnull.znz.losing_weight_is_easy.domain.Run
import idnull.znz.losing_weight_is_easy.utils.Constants.ACTION_PAUSE_SERVICES
import idnull.znz.losing_weight_is_easy.utils.Constants.ACTION_START_OR_RESUME_SERVICES
import idnull.znz.losing_weight_is_easy.utils.Constants.ACTION_STOP_SERVICES
import idnull.znz.losing_weight_is_easy.utils.Constants.MAP_ZOOM
import idnull.znz.losing_weight_is_easy.utils.Constants.POLYLINE_COLOR
import idnull.znz.losing_weight_is_easy.utils.Constants.POLYLINE_WIDTH
import idnull.znz.losing_weight_is_easy.utils.UtilsFun
import idnull.znz.losing_weight_is_easy.utils.showToast
import java.util.*
import javax.inject.Inject
import kotlin.math.round


const val CANCEL_TRACKING_DIALOG_TAG = "CancelDialog"


@AndroidEntryPoint
class TrackingFragment : Fragment() {
    private var _binding: TrackingFragmentBinding? = null
    val mBinding get() = _binding!!
    private var isTracking = false
    private var pathPoint = mutableListOf<Polyline>()
    private var map: GoogleMap? = null
    private var curTimeInMils = 0L

    private var menu: Menu? = null

    @set:Inject
    var weight = 80f

    private val viewModel: TrackingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TrackingFragmentBinding.inflate(layoutInflater, container, false)
        setHasOptionsMenu(true)
        if(savedInstanceState!= null){
            val cancelTrackingDialog = parentFragmentManager.findFragmentByTag(
                CANCEL_TRACKING_DIALOG_TAG) as CancelTrackingDialog?

            cancelTrackingDialog?.setYesListener {
                stopRun()
            }
        }

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.mapView.onCreate(savedInstanceState)
        mBinding.mapView.getMapAsync {
            map = it
            addAllPolyline()
        }
        subscribeToObserver()

        mBinding.btnToggleRun.setOnClickListener { toggleRun() }
        mBinding.btnFinishRun.setOnClickListener {
            zoomYoSeeWholeTrack()
            endRunAndSaveToDB()
        }
    }

    override fun onResume() {
        super.onResume()
        mBinding.mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        mBinding.mapView.onStart()
    }

    override fun onPause() {
        super.onPause()
        mBinding.mapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mBinding.mapView.onLowMemory()
    }

    override fun onStop() {
        super.onStop()
        mBinding.mapView.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mBinding.mapView.onSaveInstanceState(outState)
    }


    private fun sendCommandToService(action: String) =
        Intent(requireContext(), TrackingServices::class.java).also {
            it.action = action
            requireContext().startService(it)
        }

    private fun addLatestPolyline() {
        if (pathPoint.isNotEmpty() && pathPoint.last().size > 1) {
            val preLastLatLong = pathPoint.last()[pathPoint.last().size - 2]
            val lastLatLong = pathPoint.last().last()
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .add(preLastLatLong)
                .add(lastLatLong)
            map?.addPolyline(polylineOptions)


        }
    }

    private fun zoomYoSeeWholeTrack() {
        val bounds = LatLngBounds.Builder()
        for (polyline in pathPoint) {
            for (pos in polyline) {
                bounds.include(pos)
            }
        }
        map?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(),
                mBinding.mapView.width,
                mBinding.mapView.height,
                (mBinding.mapView.height * 0.06f).toInt()
            )
        )
    }


    private fun endRunAndSaveToDB() {
        map?.snapshot { bmp ->
            var distanceInMetre = 0
            for (polylin in pathPoint) {
                distanceInMetre += UtilsFun.calculatePolylineLength(polylin).toInt()
            }

            val avgSpeed =
                round(distanceInMetre / 1000f) / (curTimeInMils / 1000f / 60 / 60 * 10) / 10f

            val dateTimeStamp = Calendar.getInstance().timeInMillis
            val caloriesBurned = ((distanceInMetre / 1000f) * weight).toInt()
            val run =
                Run(bmp, dateTimeStamp, avgSpeed, distanceInMetre, curTimeInMils, caloriesBurned)

            viewModel.insertRun(run)
            showToast("Run saved")
            stopRun()

        }
    }

    private fun addAllPolyline() {
        for (polyline in pathPoint) {
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .addAll(polyline)

            map?.addPolyline(polylineOptions)
        }
    }

    private fun moveCameraToUser() {
        if (pathPoint.isNotEmpty() && pathPoint.last().isNotEmpty()) {
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoint.last().last(),
                    MAP_ZOOM
                )
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar_traking_menu, menu)
        this.menu = menu
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if (curTimeInMils > 0L) {
            this.menu?.getItem(0)?.isVisible = true

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.miCancelTracking -> {
                showCancelTrackingDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun showCancelTrackingDialog() {
        CancelTrackingDialog().apply {
            setYesListener {
                stopRun()
            }
        }.show(parentFragmentManager, CANCEL_TRACKING_DIALOG_TAG)


    }


    private fun stopRun() {

        mBinding.tvTimer.text = "00:00:00:00"
        sendCommandToService(ACTION_STOP_SERVICES)
        findNavController().navigate(R.id.action_trackingFragment_to_runFragment)
    }

    private fun updateTracking(isTracking: Boolean) {

        this.isTracking = isTracking
        if (!isTracking && curTimeInMils > 0L) {

            mBinding.btnToggleRun.text = "start"
            mBinding.btnFinishRun.visibility = View.VISIBLE
        } else if (isTracking) {
            mBinding.btnToggleRun.text = "stop"
            menu?.getItem(0)?.isVisible = true
            mBinding.btnFinishRun.visibility = View.GONE
        }

    }

    private fun toggleRun() {
        if (isTracking) {

            menu?.getItem(0)?.isVisible = true
            sendCommandToService(ACTION_PAUSE_SERVICES)
        } else {

            sendCommandToService(action = ACTION_START_OR_RESUME_SERVICES)
        }
    }


    private fun subscribeToObserver() {
        TrackingServices.isTracking.observe(viewLifecycleOwner, Observer {
            updateTracking(it)
        })

        TrackingServices.pathPoints.observe(viewLifecycleOwner, Observer {
            pathPoint = it
            addLatestPolyline()
            moveCameraToUser()
        })

        TrackingServices.timeRunInMillis.observe(viewLifecycleOwner, Observer {
            curTimeInMils = it
            val formattedTime = UtilsFun.getFormattedStopWatchTime(curTimeInMils, true)
            mBinding.tvTimer.text = formattedTime
        })
    }
}








