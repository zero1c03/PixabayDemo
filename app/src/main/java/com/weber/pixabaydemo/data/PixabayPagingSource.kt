package com.weber.pixabaydemo.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.weber.pixabaydemo.data.PixbayRepository.Companion.KEY
import com.weber.pixabaydemo.data.PixbayRepository.Companion.NETWORK_PAGE_SIZE
import com.weber.pixabaydemo.network.PixabayService

private const val STARTING_PAGE_INDEX = 1

class PixabayPagingSource(private val q: String?, private val service: PixabayService) :
    PagingSource<Int, Hits>() {
    override fun getRefreshKey(state: PagingState<Int, Hits>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Hits> {
        val page = params.key ?: STARTING_PAGE_INDEX
        return try {
            val response = service.getPhotos(KEY, q, page)
            val pixabayData = response.body() as PixabayData
            val photos = pixabayData.hits
            LoadResult.Page(
                data = photos,
                prevKey = if (page == STARTING_PAGE_INDEX) null else page - NETWORK_PAGE_SIZE,
                nextKey = page + STARTING_PAGE_INDEX
            )
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }
}