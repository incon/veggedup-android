package com.veggedup.veggedup;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.veggedup.veggedup.data.TestUtil;
import com.veggedup.veggedup.data.VeggedupContract;
import com.veggedup.veggedup.data.VeggedupDbHelper;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private RecipeListAdapter mAdapter;
    private SQLiteDatabase mDb;
    private final static String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recipeRecyclerView;

        // Set local attributes to corresponding views
        recipeRecyclerView = (RecyclerView) this.findViewById(R.id.recipe_list_view);

        // Set layout for the RecyclerView, because it's a list we are using the linear layout
        recipeRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create a DB helper (this will create the DB if run for the first time)
        VeggedupDbHelper dbHelper = new VeggedupDbHelper(this);

        // Keep a reference to the mDb until paused or killed. Get a writable database
        // because you will be adding restaurant customers
        mDb = dbHelper.getWritableDatabase();

        // Setup some mock data
        TestUtil.insertFakeData(mDb);

        // Get all guest info from the database and save in a cursor
        Cursor cursor = getAllRecipes();

        // Create an adapter for that cursor to display the data
        mAdapter = new RecipeListAdapter(this, cursor);

        // Link the adapter to the RecyclerView
        recipeRecyclerView.setAdapter(mAdapter);
    }

    public class TestOkHttpTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("http://veggedup.com")
                    .build();
            String results = null;
            try {
                Response response = client.newCall(request).execute();
                results = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return results;
        }

        @Override
        protected void onPostExecute(String results) {
            Log.d("Test", results);
        }
    }

//    public void onClickTestOkHttp(View view) throws IOException {
//        new TestOkHttpTask().execute("http://veggedup.com");
//    }

    /**
     * Query the mDb and get all guests from the waitlist table
     *
     * @return Cursor containing the list of guests
     */
    private Cursor getAllRecipes() {
        return mDb.query(
                VeggedupContract.Recipe.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                VeggedupContract.Recipe.COLUMN_RECIPE_ID
        );
    }
}
