@file:Suppress("DEPRECATION")

package com.ajiananta.submisiintermediate.view.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.ajiananta.submisiintermediate.api.response.ListStoryItem
import com.ajiananta.submisiintermediate.api.response.StoriesResponse
import com.ajiananta.submisiintermediate.data.StoriesRepos
import com.ajiananta.submisiintermediate.utils.DummyData
import com.ajiananta.submisiintermediate.utils.StoriesPagingSrc
import com.ajiananta.submisiintermediate.utils.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@Suppress("DEPRECATION")
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storiesRepos: StoriesRepos
    private lateinit var mainViewModel: MainViewModel
    private val dummyStory = DummyData.generateDummyStoriesEntity()
    private val dummyStoriesResponse = DummyData.generateDummyStoriesResponse()
    private val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLUxLVjk3Um11WjB1V3FTMXoiLCJpYXQiOjE2OTkxODE4MDl9.zfN_xVSderY6fH09siMk99MfYdG1xfr_DWxeHbPWAKA"
    private val testCoroutineScheduler = TestCoroutineScheduler()
    private val testDispatcher = TestCoroutineDispatcher(testCoroutineScheduler)

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        mainViewModel = MainViewModel(storiesRepos)
        Dispatchers.setMain(testDispatcher)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `when Get Story Should Not Null and Return Data`() = runTest {
        val data: PagingData<ListStoryItem> = StoriesPagingSrc.getSnapshot(dummyStory)
        val expectedStory = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStory.value = data
        Mockito.`when`(storiesRepos.getStories(token)).thenReturn(expectedStory)

        val actualStory: PagingData<ListStoryItem> = mainViewModel.getAllStories(token).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoriesAdapter.DIFF_CALLBACK,
            updateCallback = updateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)

        assertNotNull(differ.snapshot())
        assertEquals(dummyStory.size, differ.snapshot().size)
        assertEquals(dummyStory[0], differ.snapshot()[0])
    }

    @Test
    fun `when Get Story Empty Should Return No Data`() = runTest {
        val emptyList = emptyList<ListStoryItem>()
        val expectedEmptyStory = MutableLiveData<PagingData<ListStoryItem>>()
        expectedEmptyStory.value = PagingData.from(emptyList)
        Mockito.`when`(storiesRepos.getStories(token)).thenReturn(expectedEmptyStory)

        val actualEmptyStory: PagingData<ListStoryItem> = mainViewModel.getAllStories(token).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoriesAdapter.DIFF_CALLBACK,
            updateCallback = updateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualEmptyStory)
        assertNotNull(differ.snapshot())
        assertEquals(0, differ.snapshot().size)
    }

    @Test
    fun `When Get Story with Location`() {
        val expectStories = MutableLiveData<StoriesResponse>()
        expectStories.postValue(dummyStoriesResponse)
        Mockito.`when`(storiesRepos.getMapsStories(token)).thenReturn(expectStories)
        val actualStories = mainViewModel.getListMapsStories(token).getOrAwaitValue()
        Mockito.verify(storiesRepos).getMapsStories(token)
        assertNotNull(actualStories)
        assertEquals(expectStories.value, actualStories)
    }
}

private val updateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}

