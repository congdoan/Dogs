package com.cdoan.dogs.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.cdoan.dogs.R
import com.cdoan.dogs.databinding.ItemDogBinding
import com.cdoan.dogs.model.DogBreed
import kotlinx.android.synthetic.main.item_dog.view.*

class DogListAdapter(private var dogList: List<DogBreed>) : RecyclerView.Adapter<DogListAdapter.DogViewHolder>(),
    DogClickListener {

    class DogViewHolder(var view: ItemDogBinding) : RecyclerView.ViewHolder(view.root)

    fun updateDogList(newDogList: List<DogBreed>) {
        dogList = newDogList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        // java.lang.IllegalStateException: ViewHolder views must not be attached when created.
        // Ensure that you are not passing 'true' to the attachToRoot parameter of LayoutInflater.inflate(..., boolean attachToRoot)
        val view = DataBindingUtil.inflate<ItemDogBinding>(inflater, R.layout.item_dog, parent, false)
        return DogViewHolder(view)
    }

    override fun onBindViewHolder(holder: DogViewHolder, position: Int) {
        holder.view.dog = dogList[position]
        holder.view.listener = this
    }

    override fun getItemCount() = dogList.size

    override fun onDogClicked(view: View) {
        val action = ListFragmentDirections.actionDetailFragment().apply {
            dogUuid = view.dogId.text.toString().toInt()
        }
        Navigation.findNavController(view).navigate(action)
    }

}