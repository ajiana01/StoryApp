package com.ajiananta.submisiintermediate.utils

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ajiananta.submisiintermediate.api.response.ListStoryItem

class StoriesPagingSrc: PagingSource<Int, LiveData<List<ListStoryItem>>>() {

    override fun getRefreshKey(state: PagingState<Int, LiveData<List<ListStoryItem>>>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<ListStoryItem>>> {
        return  LoadResult.Page(emptyList(), 0, 1)
    }
    companion object {
        fun getSnapshot(items: List<ListStoryItem>): PagingData<ListStoryItem> {
            return PagingData.from(items)
        }
    }
}