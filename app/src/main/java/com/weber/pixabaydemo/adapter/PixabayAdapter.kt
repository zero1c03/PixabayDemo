package com.weber.pixabaydemo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.weber.pixabaydemo.GlideApp
import com.weber.pixabaydemo.data.Hits
import com.weber.pixabaydemo.databinding.GridItemPixabayBinding
import com.weber.pixabaydemo.databinding.ListItemPixabayBinding
import com.weber.pixabaydemo.viewmodels.PixabayViewModel
import javax.inject.Inject

class PixabayAdapter @Inject constructor(
    private val context: Context,
    private val pixabayViewModel: PixabayViewModel
) : PagingDataAdapter<Hits, RecyclerView.ViewHolder>(PixabayDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            PixabayViewModel.Companion.LayoutType.LIST.ordinal -> {
                PixabayListViewHolder(
                    ListItemPixabayBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            else -> {
                PixabayGridViewHolder(
                    GridItemPixabayBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val hits = getItem(position)
        if (hits != null) {
            if (pixabayViewModel.layoutType.value == PixabayViewModel.Companion.LayoutType.LIST.ordinal) {
                (holder as PixabayListViewHolder).run {
                    GlideApp.with(context)
                        .load(hits.previewURL)
                        .centerCrop()
                        .into(photo)

                    detailField.visibility = View.VISIBLE
                    likes.text = hits.likes.toString()
                    views.text = hits.views.toString()
                }
            } else {
                (holder as PixabayGridViewHolder).run {
                    GlideApp.with(context)
                        .load(hits.previewURL)
                        .centerCrop()
                        .into(photo)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return pixabayViewModel.layoutType.value!!
    }


    class PixabayListViewHolder(
        binding: ListItemPixabayBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        val photo = binding.ivPhoto
        val detailField = binding.llDetail
        val likes = binding.tvLike
        val views = binding.tvViews
    }

    class PixabayGridViewHolder(
        binding: GridItemPixabayBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        val photo = binding.ivPhoto
    }

    class PixabayDiffCallback : DiffUtil.ItemCallback<Hits>() {
        override fun areItemsTheSame(oldItem: Hits, newItem: Hits): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Hits, newItem: Hits): Boolean {
            return oldItem == newItem
        }
    }
}