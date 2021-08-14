package idnull.znz.losing_weight_is_easy.presentation.tracking

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import idnull.znz.losing_weight_is_easy.R

class CancelTrackingDialog: DialogFragment() {

    private var yesListener:(()->Unit)? = null
    fun setYesListener(Listener:()->Unit){
        yesListener = Listener
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(
            requireContext(),
            R.style.ThemeOverlay_MaterialComponents_Dialog_Alert)
            .setTitle("Cancel the Run")
            .setMessage("Are you sure to cancel the current run and delete all its data? ")
            .setIcon(R.drawable.ic_delete)
            .setPositiveButton("Yes") { _, _ ->
               yesListener?.let { yes ->
                   yes()
               }
            }
            .setNegativeButton("No") { dialogInterface, _ ->
                dialogInterface.cancel()
            }.create()



    }


}