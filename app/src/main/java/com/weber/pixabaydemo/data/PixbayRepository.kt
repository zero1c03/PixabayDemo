package com.weber.pixabaydemo.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.weber.pixabaydemo.network.PixabayService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PixbayRepository @Inject constructor(private val service: PixabayService) {

    fun getPhoto(q: String?): Flow<PagingData<Hits>> {
        return Pager(
            config = PagingConfig(
                enablePlaceholders = false,
                pageSize = NETWORK_PAGE_SIZE
            ), pagingSourceFactory = { PixabayPagingSource(q, service) }
        ).flow
    }

    companion object {
        const val KEY = "23477716-55fb49ab8be9cc63b66ca9fe4"
        const val NETWORK_PAGE_SIZE = 20
    }
}