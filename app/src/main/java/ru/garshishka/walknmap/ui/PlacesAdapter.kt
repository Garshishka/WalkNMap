package ru.garshishka.walknmap.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.garshishka.walknmap.data.GridPoint
import ru.garshishka.walknmap.databinding.PlacesListLayoutBinding

class PlacesAdapter(
    private val onInteractionListener: OnInteractionListener,
) :
    ListAdapter<GridPoint, PlacesViewHolder>(PlaceDiffCallback()) {

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
    fun bind(place: GridPoint) {
        binding.apply {
            val lat = place.point.latitude.toString().take(10) + "..."
            val long = place.point.longitude.toString().take(10) + "..."
            placeCoords.text = "$lat | $long"

            placeCard.setOnClickListener {
                onInteractionListener.onPlaceClick(place)
            }
            editButton.setOnClickListener {
                onInteractionListener.onEditClick(place)
            }
            deleteButton.setOnClickListener {
                onInteractionListener.onDeleteClick(place)
            }
        }
    }
}

class PlaceDiffCallback : DiffUtil.ItemCallback<GridPoint>() {
    override fun areItemsTheSame(oldItem: GridPoint, newItem: GridPoint): Boolean {
        return oldItem.point == newItem.point
    }

    override fun areContentsTheSame(oldItem: GridPoint, newItem: GridPoint): Boolean {
        return oldItem == newItem
    }
}