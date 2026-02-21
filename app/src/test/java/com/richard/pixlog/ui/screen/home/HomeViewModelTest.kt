package com.richard.pixlog.ui.screen.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.richard.pixlog.data.local.entity.ListStoryEntity
import com.richard.pixlog.data.repository.PixlogRepository
import com.richard.pixlog.utils.DataDummy
import com.richard.pixlog.utils.MainDispatcherRule
import com.richard.pixlog.utils.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class HomeViewModelTest {

    @get:Rule
    val instantExecutorRule  = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatchRule = MainDispatcherRule()

    @Mock
    private lateinit var pixlogRepository: PixlogRepository

    @Test // +
    fun `when get story should not null and return data`() = runTest {
        val dataDummy = DataDummy.generateDataDummy()
        val data: PagingData<ListStoryEntity> = StoryPagingSource.snapshot(dataDummy)
        val expectedResult  = MutableLiveData<PagingData<ListStoryEntity>>()
        expectedResult.value = data
        Mockito.`when`(pixlogRepository.getAllStory()).thenReturn(expectedResult)

        val homeViewModel = HomeViewModel(pixlogRepository)
        val actualStory: PagingData<ListStoryEntity> = homeViewModel.getStory.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
            diffCallback = HomeAdapter.DIFF_CALLBACK
        )
        differ.submitData(actualStory)

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dataDummy.size, differ.snapshot().size)
        Assert.assertEquals(dataDummy[0], differ.snapshot()[0])
    }

    @Test // -
    fun `when Get Story Empty Should Return No Data`() = runTest {
        val data: PagingData<ListStoryEntity> = PagingData.from(emptyList())
        val expectedQuote = MutableLiveData<PagingData<ListStoryEntity>>()
        expectedQuote.value = data
        Mockito.`when`(pixlogRepository.getAllStory()).thenReturn(expectedQuote)
        val mainViewModel = HomeViewModel(pixlogRepository)
        val actualStory: PagingData<ListStoryEntity> = mainViewModel.getStory.getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = HomeAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)
        Assert.assertEquals(0, differ.snapshot().size)
    }

    class StoryPagingSource : PagingSource<Int, LiveData<List<ListStoryEntity>>>() {
        companion object {
            fun snapshot(items: List<ListStoryEntity>): PagingData<ListStoryEntity> {
                return PagingData.from(items)
            }
        }
        override fun getRefreshKey(state: PagingState<Int, LiveData<List<ListStoryEntity>>>): Int {
            return 0
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<ListStoryEntity>>> {
            return LoadResult.Page(emptyList(), 0, 1)
        }
    }

}
val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}