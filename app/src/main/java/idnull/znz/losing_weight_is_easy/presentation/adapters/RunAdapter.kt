package idnull.znz.losing_weight_is_easy.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import idnull.znz.losing_weight_is_easy.R
import idnull.znz.losing_weight_is_easy.databinding.ItemRunBinding
import idnull.znz.losing_weight_is_easy.domain.Run
import idnull.znz.losing_weight_is_easy.utils.UtilsFun
import java.text.SimpleDateFormat
import java.util.*

class RunAdapter:RecyclerView.Adapter<RunAdapter.RunViewHolder>() {

    val diffCallback = object : DiffUtil.ItemCallback<Run>() {
        override fun areItemsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }
    val differ = AsyncListDiffer(this,diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunViewHolder {
        return RunViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_run,parent, false
            )
        )
    }
    inner class RunViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val binding = ItemRunBinding.bind(itemView)
        fun bind(run:Run)= with(binding){
            Glide.with(itemView).load(run.img).into(ivRunImage)
            val calendar = Calendar.getInstance().apply {
                timeInMillis = run.timeStamp
            }
            val dateFormat = SimpleDateFormat("dd.mm.yy",Locale.getDefault())
            tvDate.text = dateFormat.format(calendar.time)

            val avgSpeed = "${run.avgSpeedInKMH} km/h"
            tvAvgSpeed.text = avgSpeed

            val distanceInKm = "${run.distanceInMeters/1000f}km"
            tvDistance.text = distanceInKm

            tvTime.text = UtilsFun.getFormattedStopWatchTime(run.timeInMills)

            val caloriesBurned = "${run.caloriesBurned}kcal"
            tvCalories.text = caloriesBurned
        }
    }
    override fun onBindViewHolder(holder: RunViewHolder, position: Int) {
        val run = differ.currentList[position]
        holder.bind(run)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
fun submitList(list:List<Run>) = differ.submitList(list)
}







