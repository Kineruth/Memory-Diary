package com.memoryDiary.Activity.Start;

import androidx.appcompat.app.AppCompatActivity;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;
import com.algolia.search.saas.RequestOptions;
import com.memoryDiary.Holder.UserDataHolder;
import com.memoryDiary.R;

import org.json.JSONObject;

public class SearchActivity extends AppCompatActivity {

    Client client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        this.client = new Client("M3U4UXDFPP", "2d9101367028675dc18153dac35d8c2a");
        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
        }
    }

    private void doMySearch(String queryStr) {
        Index index = client.getIndex("memory_diary");
        Query query = new Query(queryStr)
                .setFilters("userId:" + UserDataHolder.getUserDataHolder().getUser().getUid())
                .setHitsPerPage(50);
        index.searchAsync(query, new CompletionHandler() {
            @Override
            public void requestCompleted(JSONObject content, AlgoliaException error) {
                // [...]
            }
        });
    }
}
