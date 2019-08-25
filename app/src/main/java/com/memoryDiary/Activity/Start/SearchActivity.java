package com.memoryDiary.Activity.Start;

import androidx.appcompat.app.AppCompatActivity;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;
import com.algolia.search.saas.RequestOptions;
//import com.google.gson.Gson;
import com.memoryDiary.Entity.Memory;
import com.memoryDiary.Holder.UserDataHolder;
import com.memoryDiary.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kotlinx.serialization.json.JsonArray;

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
                try {
                    JSONArray hits = content.getJSONArray("hits");
                    ArrayList<Memory> results = new ArrayList<>();
                    for(int i = 0; i < hits.length(); i++){
                        JSONObject jsnobj = hits.getJSONObject(i);
//                        Gson gson = new Gson();
//                        Memory m = gson.fromJson(jsnobj.toString(), Memory.class);
//                        results.add(m);
                    }
                    //ArrayAdapter<Memory> arrayAdapter = new ArrayAdapter<Memory>(this, R.layout.activity_search, results);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
