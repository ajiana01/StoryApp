package com.ajiananta.submisiintermediate.utils

import androidx.lifecycle.MutableLiveData
import com.ajiananta.submisiintermediate.api.response.ListStoryItem
import com.ajiananta.submisiintermediate.api.response.StoriesResponse

object DummyData {
    fun generateDummyStoriesEntity(): List<ListStoryItem> {
        val storiesList: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..10) {
            val stories = ListStoryItem(
                "$i",
                "Aji$i",
                "wleowleowleo",
                "https://makassar.terkini.id/wp-content/uploads/2022/03/terkiniid_jokowi_terkini.png",
                "2022-02-22T22:22:22Z",
                22.8922,
                22.8922
            )
            storiesList.add(stories)
        }
        return storiesList
    }

    fun generateDummyStoriesResponse(): StoriesResponse {
        return StoriesResponse(
            generateDummyStoriesEntity(),
            false,
            "Stories fetched successfully"
        )
    }
}