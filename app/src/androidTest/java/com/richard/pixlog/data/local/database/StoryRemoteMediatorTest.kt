package com.richard.pixlog.data.local.database

import androidx.paging.*
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.richard.pixlog.data.local.entity.ListStoryEntity
import com.richard.pixlog.data.remote.request.LoginRequest
import com.richard.pixlog.data.remote.request.RegisterRequest
import com.richard.pixlog.data.remote.response.ListStory
import com.richard.pixlog.data.remote.response.LoginResponse
import com.richard.pixlog.data.remote.response.RegisterResponse
import com.richard.pixlog.data.remote.response.StoryResponse
import com.richard.pixlog.data.remote.response.UploadResponse
import com.richard.pixlog.data.remote.retrofit.ApiService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@ExperimentalPagingApi
@RunWith(AndroidJUnit4::class)
class QuoteRemoteMediatorTest {

    private var mockApi: ApiService = FakeApiService()
    private var mockDb: PixlogDatabase = Room.inMemoryDatabaseBuilder(
        ApplicationProvider.getApplicationContext(),
        PixlogDatabase::class.java
    ).allowMainThreadQueries().build()

    @Test
    fun refreshLoadReturnsSuccessResultWhenMoreDataIsPresent() = runTest {
        val remoteMediator = StoryRemoteMediator(
            mockDb,
            mockApi,
        )
        val pagingState = PagingState<Int, ListStoryEntity>(
            listOf(),
            null,
            PagingConfig(10),
            10
        )
        val result = remoteMediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @After
    fun tearDown() {
        mockDb.clearAllTables()
    }
}

class FakeApiService : ApiService {

    override suspend fun register(requestBody: RegisterRequest): RegisterResponse {
        TODO("Not yet implemented")
    }

    override suspend fun login(requestBody: LoginRequest): LoginResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getAllStory(
        page: Int?,
        size: Int?,
        location: Int?
    ): StoryResponse {
        val listStory : MutableList<ListStory> = arrayListOf()
        for (i in 0..100) {
            val story = ListStory(
                id = i.toString(),
                name = "name $i",
                description = "description $i",
                photoUrl = "photoUrl $i",
                createdAt = "createdAt $i",
            )
            listStory.add(story)
        }
        val items = StoryResponse(
            error = false,
            message = "story fetched successfully",
            listStory = listStory
        )
        return  items
    }

    override suspend fun addStory(
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody?,
        lon: RequestBody?
    ): UploadResponse {
        TODO("Not yet implemented")
    }
}