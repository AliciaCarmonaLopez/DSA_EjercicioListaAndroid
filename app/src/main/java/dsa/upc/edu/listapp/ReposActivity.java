package dsa.upc.edu.listapp;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.widget.Toast;

import java.util.List;

import dsa.upc.edu.listapp.github.Contributor;
import dsa.upc.edu.listapp.github.GitHub;
import dsa.upc.edu.listapp.github.Repos;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReposActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ReposAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contributor);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        swipeRefreshLayout = findViewById(R.id.my_swipe_refresh);

        // use this setting to
        // improve performance if you know that changes
        // in content do not change the layout size
        // of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(ReposActivity.this);
        recyclerView.setLayoutManager(layoutManager);

        // Set the adapter
        adapter = new ReposAdapter();
        recyclerView.setAdapter(adapter);

        doApiCall(null);

        // Manage swipe on items
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder
                            target) {
                        return false;
                    }
                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                        Intent intent = new Intent(ReposActivity.this, ContributorActivity.class);
                        String getName = " vb";
                        String getRepo = adapter.values.get(viewHolder.getAdapterPosition()).name;
                        intent.putExtra("name", getName);
                        intent.putExtra("repos", getRepo);
                        startActivity(intent);



                        //adapter.remove(viewHolder.getAdapterPosition());
                    }
                };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        doApiCall(swipeRefreshLayout);
                    }
                }
        );

    }

    private void doApiCall(final SwipeRefreshLayout mySwipeRefreshLayout) {
        GitHub gitHubService = GitHub.retrofit.create(GitHub.class);
        Call<List<Repos>> call = gitHubService.repos("AliciaCarmonaLopez");

        call.enqueue(new Callback<List<Repos>>() {
            @Override
            public void onResponse(Call<List<Repos>> call, Response<List<Repos>> response) {
                // set the results to the adapter
                adapter.setData(response.body());

                if(mySwipeRefreshLayout!=null) mySwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<Repos>> call, Throwable t) {
                if(mySwipeRefreshLayout!=null) mySwipeRefreshLayout.setRefreshing(false);

                String msg = "Error in retrofit: "+t.toString();
                //Log.d(TAG,msg);
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG);
            }

        });

    }
}