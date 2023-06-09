package ru.garshishka.walknmap.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.garshishka.walknmap.data.MapPoint
import ru.garshishka.walknmap.databinding.PlacesListLayoutBinding

class PlacesAdapter(
    private val onInteractionListener: OnInteractionListener,
) :
    ListAdapter<MapPoint, PlacesViewHolder>(PlaceDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacesViewHolder {
        val binding =
            PlacesListLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlacesViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: PlacesViewHolder, position: Int) {
        val place = getItem(position)
        holder.bind(place)
    }
}

class PlacesViewHolder(
    private val binding: PlacesListLayoutBinding,
    private val onInteractionListener: OnInteractionListener,
) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(place: MapPoint) {
        binding.apply {
            val lat = place.lat.toString()
            val long = place.lon.toString()
            placeCoords.text = "$lat | $long"

            placeCard.setOnClickListener {
                onInteractionListener.onPlaceClick(place)
            }
            deleteButton.setOnClickListener {
                onInteractionListener.onDeleteClick(place)
            }
        }
    }
}

class PlaceDiffCallback : DiffUtil.ItemCallback<MapPoint>() {
    override fun areItemsTheSame(oldItem: MapPoint, newItem: MapPoint): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: MapPoint, newItem: MapPoint): Boolean {
        return oldItem == newItem
    }
}