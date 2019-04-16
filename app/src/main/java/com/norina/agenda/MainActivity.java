package com.norina.agenda;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText title ;
    Button search;
    ListView listView;
    ImageView imageView;
    ProgressBar progressBar;
    DataAdapter adapter;
    String book_title, GOOGLE_BOOKS_API;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        title = findViewById(R.id.etSearch);
        search = findViewById(R.id.btnSearch);
        listView = findViewById(R.id.lvListView);
        imageView = findViewById(R.id.noteBook);
        progressBar = findViewById(R.id.pbProgressBar);

        adapter = new DataAdapter(getApplicationContext(), 0, new ArrayList<Data>());
        listView.setAdapter(adapter);

        imageView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Data currentbook = adapter.getItem(position);

                String title = currentbook.getTitle();
                String author = currentbook.getAuthor();
                String publisher = currentbook.getPublisher();
                String Date = currentbook.getDate();
                String description = currentbook.getDescription();
                String url = currentbook.getImageUrl();

                Intent intent = new Intent(getApplicationContext(), BookinfoActivity.class);

                intent.putExtra("title", title);
                intent.putExtra("author", author);
                intent.putExtra("pub", publisher);
                intent.putExtra("date", Date);
                intent.putExtra("desc", description);
                intent.putExtra("url", url);

                startActivity(intent);
            }
        });


        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                book_title = title.getText().toString();

                if (book_title.length() == 0)
                    Toast.makeText(MainActivity.this, "Please Enter Book Title", Toast.LENGTH_SHORT).show();
                else
                {
                    imageView.setVisibility(View.GONE);

                    GOOGLE_BOOKS_API ="https://www.googleapis.com/books/v1/volumes?q="+book_title;

                    books books = new books();
                    books.execute(GOOGLE_BOOKS_API);

                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);

                    inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY,0);

                    title.setText("");
                }
            }
        });
    }

    private class books extends AsyncTask<String, Void, ArrayList<Data>> {

        @Override
        protected ArrayList<Data> doInBackground(String... strings) {

            if (strings.length <1 || strings[0] == null)
                return null;

            ArrayList<Data> books =Utils.fetchBooksData(strings[0]);

            return books;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(ArrayList<Data> data) {
            super.onPostExecute(data);
            adapter.clear();

            if (data != null && !data.isEmpty())
            {
                progressBar.setVisibility(View.INVISIBLE);
                adapter.addAll(data);
            }


        }
    }
}
