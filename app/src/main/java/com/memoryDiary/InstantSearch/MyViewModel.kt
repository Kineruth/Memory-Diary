package com.memoryDiary.InstantSearch

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.algolia.instantsearch.core.connection.ConnectionHandler
import com.algolia.instantsearch.helper.android.list.SearcherSingleIndexDataSource
import com.algolia.instantsearch.helper.android.searchbox.SearchBoxConnectorPagedList
import com.algolia.instantsearch.helper.searcher.SearcherSingleIndex
import com.algolia.instantsearch.helper.stats.StatsConnector
import com.algolia.search.client.ClientSearch
import com.algolia.search.model.APIKey
import com.algolia.search.model.ApplicationID
import com.algolia.search.model.IndexName
import com.memoryDiary.Adapter.MemoryAdapter
import com.memoryDiary.Entity.Memory
import io.ktor.client.features.logging.LogLevel
import kotlinx.serialization.json.content

class MyViewModel : ViewModel() {
    val client = ClientSearch(ApplicationID("M3U4UXDFPP"), APIKey("b35f7c791eff3e9ab52d23679bce52c4"), LogLevel.ALL)
    val index = client.initIndex(IndexName("memory_diary"))
    val searcher = SearcherSingleIndex(index)

    val dataSourceFactory = SearcherSingleIndexDataSource.Factory(searcher) { hit ->
        Memory(
                hit.json.getPrimitive("userId").content,
                hit.json.getPrimitive("objectID").content,
                hit.json.getPrimitive("memoryTitle").content,
                hit.json.getPrimitive("description").content,
                hit.json.getPrimitive("creationTime").content.toLong(),
                hit.json.getPrimitive("imagePath").content,
                hit.json.getArray("imageLabels").content.map { jsnelm -> jsnelm.content }
        )
    }
    val pagedListConfig = PagedList.Config.Builder().setPageSize(50).build()
    @JvmField val memories: LiveData<PagedList<Memory>> = LivePagedListBuilder(dataSourceFactory, pagedListConfig).build()
    val adapterMemory = MemoryAdapter()

    @JvmField val searchBox = SearchBoxConnectorPagedList(searcher, listOf(memories))
    val stats = StatsConnector(searcher)

    val connection = ConnectionHandler()

    init {
        connection += searchBox
        connection += stats
    }
    override fun onCleared() {
        super.onCleared()
        searcher.cancel()
        connection.disconnect()
    }
}