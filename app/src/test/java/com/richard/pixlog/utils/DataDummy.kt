package com.richard.pixlog.utils

import com.richard.pixlog.data.local.entity.ListStoryEntity
import com.richard.pixlog.data.remote.response.ListStory
import com.richard.pixlog.data.remote.response.StoryResponse

object DataDummy {
    fun generateDataDummy(): List<ListStoryEntity> {
        val items: MutableList<ListStoryEntity> = arrayListOf()
        for (i in 0..10) {
            val story = ListStoryEntity(
                id = i.toString(),
                name = "name $i",
                description = "description $i",
                photoUrl = "photoUrl $i",
                createdAt = "createdAt $i",
                lat = i.toDouble(),
                lon = i.toDouble()
            )
            items.add(story)
        }
        return items
    }
    fun generateResponseListStoryDummy() : StoryResponse {
        val listStory = generateDataDummy().map {
            ListStory(
                id = it.id,
                name = it.name,
                description = it.description,
                photoUrl = it.photoUrl,
                createdAt = it.createdAt,
                lat = it.lat,
            )
        }
        val response = StoryResponse(
            error = false,
            message = "Stories fetched successfully",
            listStory = listStory
        )
        return  response
    }

}