package com.surajmanshal.bcapracticaladmin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.surajmanshal.bcapracticaladmin.databinding.ItemDemoImageBinding
import com.surajmanshal.bcapracticaladmin.viewmodel.BooksViewModel

class BookDemoImagesAdapter(val vm: BooksViewModel, val chooseImage: () -> Unit) :
    RecyclerView.Adapter<BookDemoImagesAdapter.BookDemoImageViewHolder>() {

    val list = vm.demoImages.value

    class BookDemoImageViewHolder(val binding: ItemDemoImageBinding) : ViewHolder(binding.root) {
        val imgDemo = binding.imgDemoBook
        val btnRemove = binding.imgRemove
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookDemoImageViewHolder {
        return BookDemoImageViewHolder(
            ItemDemoImageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: BookDemoImageViewHolder, position: Int) {
        val image = list?.get(position)
        val context = holder.itemView.context
        image?.let {
            if(image.isEmpty()){
                holder.imgDemo.setOnClickListener {
                    chooseImage()
                }
            }else{
                Glide.with(context).load(image).into(holder.imgDemo)
                holder.btnRemove.apply {
                    visibility = View.VISIBLE
                    setOnClickListener {
                        vm.removeImage(image)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return if (list.isNullOrEmpty())
            0
        else
            list.size
    }
}