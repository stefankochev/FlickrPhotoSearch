package com.example.stefan.flickrphotosearch;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stefan.flickrphotosearch.model.GalleryItem;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;


public class MainActivity extends AppCompatActivity {

    private ListView itemslist;
    private GridView gridView;
    private ArrayAdapter<String> adapter;
    ArrayList<GalleryItem> items;
    ArrayList<String> strings;
    private GridView movieGrid;

    private static int SPLASH_TIME_OUT = 3000;
    private static final int KEEP_TIME = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);



        // Create default options which will be used for every
        //  displayImage(...) call if no options will be passed to this method
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
        .cacheInMemory(true)
                .cacheOnDisk(true)
        .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
        .defaultDisplayImageOptions(defaultOptions)
        .build();
        ImageLoader.getInstance().init(config); // Do it on Application start




        itemslist = (ListView)findViewById(R.id.list);
        gridView = (GridView)findViewById(R.id.gridVieww);
        // GalleryItems list init
        items=new ArrayList<>();

        Button btn = (Button)findViewById(R.id.btnSearch);
        final EditText query = (EditText)findViewById(R.id.queryText);

        btn.setOnClickListener(new View.OnClickListener(){


            @Override
            public void onClick(View view) {
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                if(!items.isEmpty()){
                    items.clear();
                }
                new JSONTask().execute("https://api.flickr.com/services/rest/?method=flickr.photos.search&%20api_key=178069b03af62f5735258c0a10a14d6e&format=json&nojsoncallback=1&text="+query.getText().toString());

            }
        });

        itemslist.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, DisplayImage.class);
                intent.putExtra("imgURL",((GalleryItem)itemslist.getItemAtPosition(i)).getURL());
                startActivity(intent);
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, DisplayImage.class);
                intent.putExtra("imgURL",((GalleryItem)itemslist.getItemAtPosition(i)).getURL());
                startActivity(intent);
            }
        });


    }

    public void showToast(final String toast){
        runOnUiThread(new Runnable() {
            public void run()
            {
                Toast.makeText(MainActivity.this, toast, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public class JSONTask extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... urls) {

            HttpsURLConnection connection;
            URL url = null;
            try {
                url = new URL(urls[0]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            try {
                connection = (HttpsURLConnection) url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                StringBuilder sb = new StringBuilder();
                while((line=br.readLine())!=null){
                    sb.append(line);
                }
                return sb.toString();

            } catch (IOException e) {
                //e.printStackTrace();
                showToast("No internet connection!");
            }

            return null;
        }



        @Override
        protected void onPostExecute(String result) {
            if(result == null){
                return;
            }

            super.onPostExecute(result);

            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(jsonObject!=null){
                try {
                    JSONObject all = (JSONObject) jsonObject.get("photos");
                    JSONArray itemlist = all.getJSONArray("photo");
                    for(int i=0;i<itemlist.length();i++){
                        JSONObject o = (JSONObject)itemlist.get(i);
                        GalleryItem item = new GalleryItem(o.get("id").toString(),o.get("secret").toString(),
                                o.get("server").toString(),o.get("farm").toString(),o.get("title").toString());
                        items.add(item);
                    }

                    ItemsAdapter itemsAdapter = new ItemsAdapter(getApplicationContext(),R.layout.customlayout,items);
                    itemslist.setAdapter(itemsAdapter);
                    GridItemsAdapter gridItemsAdapter = new GridItemsAdapter(getApplicationContext(),R.layout.customgrid,items);
                    gridView.setAdapter(gridItemsAdapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public class ItemsAdapter extends ArrayAdapter{

        private  List<GalleryItem> items;
        private int resource;

        private LayoutInflater inflater;

        public ItemsAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<GalleryItem> objects) {
            super(context, resource, objects);
            items = objects;
            this.resource = resource;
            inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            if(convertView == null){
                convertView = inflater.inflate(resource,null);
            }

            ImageView imageView = (ImageView)convertView.findViewById(R.id.imageViewCustom);
            ImageLoader.getInstance().displayImage(items.get(position).getURL(), imageView);

            TextView textView = (TextView)convertView.findViewById(R.id.textViewCustom);
            String text = items.get(position).getTitle();
            if(text.length()>20){
                text=text.substring(0,20);
            }
            textView.setText(text);

            return convertView;
        }
    }

    public class GridItemsAdapter extends ArrayAdapter{

        private  List<GalleryItem> items;
        private int resource;

        private LayoutInflater inflater;

        public GridItemsAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<GalleryItem> objects) {
            super(context, resource, objects);
            items = objects;
            this.resource = resource;
            inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            if(convertView == null){
                convertView = inflater.inflate(resource,null);
            }

            ImageView imageView = (ImageView)convertView.findViewById(R.id.gridImageView);
            ImageLoader.getInstance().displayImage(items.get(position).getURL(), imageView);

            return convertView;
        }
    }

}
