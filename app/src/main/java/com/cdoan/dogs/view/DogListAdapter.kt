package com.cdoan.dogs.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cdoan.dogs.R
import com.cdoan.dogs.model.DogBreed
import kotlinx.android.synthetic.main.fragment_detail.view.*
import kotlinx.android.synthetic.main.item_dog.view.*

class DogListAdapter(private var dogList: List<DogBreed>) : RecyclerView.Adapter<DogListAdapter.DogViewHolder>() {

    class DogViewHolder(var view: View) : RecyclerView.ViewHolder(view)

    fun updateDogList(newDogList: List<DogBreed>) {
        dogList = newDogList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        // java.lang.IllegalStateException: ViewHolder views must not be attached when created.
        // Ensure that you are not passing 'true' to the attachToRoot parameter of LayoutInflater.inflate(..., boolean attachToRoot)
        val view = inflater.inflate(R.layout.item_dog, parent, false)
        return DogViewHolder(view)
    }

    override fun onBindViewHolder(holder: DogViewHolder, position: Int) {
        holder.view.name.text = dogList[position].dogBreed
        holder.view.lifespan.text = dogList[position].lifespan
    }

    override fun getItemCount() = dogList.size

}