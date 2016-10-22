package com.example.tiffany.multithread;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    public Button create, load, clear;
    public ListView listView;
    public ProgressBar progressBar = null;
    public String filename = "numbers.txt";
    public File file;
    public String[] text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        file = new File(this.getFilesDir(), filename);
        create = (Button) findViewById(R.id.create);
        load = (Button) findViewById(R.id.load);
        clear = (Button) findViewById(R.id.clear);
        listView = (ListView) findViewById(R.id.list);
        progressBar = (ProgressBar) findViewById(R.id.progressBar3);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }


    public void buttonCreate(View view) {
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                String saveText = "";
                for (int i = 1; i <= 10; i++) {
                    saveText += i + "\n";
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                    progressBar.setProgress(i);

                }
                Save(file, saveText);
                progressBar.setProgress(0);
            }
        };
        new Thread(runnable).start();
    }

    public static void Save(File file, String data) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            try {
                fos.write(data.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void buttonLoad(View view) {
        new asyncCreateText().execute();
    }

    private class asyncCreateText extends AsyncTask<Void, Integer, Void> {
        @Override
        protected Void doInBackground(Void... unused) {
            if (file.exists() && !file.isDirectory()) {
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader br = new BufferedReader(isr);

                String Items;
                int item = 0;
                try {
                    while ((Items = br.readLine()) != null) {
                        item++;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    fis.getChannel().position(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String[] array = new String[item];

                String line;
                int i = 0;
                try {
                    while ((line = br.readLine()) != null) {
                        array[i] = line;
                        try {
                            Thread.sleep(250);
                        } catch (InterruptedException ie) {
                            ie.printStackTrace();
                        }
                        i++;
                        publishProgress((int) i);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                text = array;
            }
            return null;
        }

        protected void onProgressUpdate(Integer... progress) {
            progressBar.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(Void unused) {
            if (file.exists() && !file.isDirectory()) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1, text);
                listView.setAdapter(adapter);
                progressBar.setProgress(0);
            } else {
                Context context = getApplicationContext();
                CharSequence text = "No file has been created yet";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        }
    }


    public void buttonClear(View view) {
        String[] empty = new String[0];
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1, empty);
        listView.setAdapter(adapter);
    }
}