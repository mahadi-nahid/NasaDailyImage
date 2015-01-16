package com.nahid.nasadailyimage;

import java.net.URL;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.ListFragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nahid.nasadailyimage.EdNewsHandler.NewsItem;
import com.nahid.nasadailyimage.R;

@SuppressLint("NewApi")
public class NasaEdNews extends ListFragment {

        private static final String URL = "http://www.nasa.gov/rss/educationnews.rss";
        @SuppressWarnings("unused")
        private Handler handler;
        private ArrayList<NewsItem> values = new ArrayList<EdNewsHandler.NewsItem>();
        static private EdNewsAdapter listAdapter;

        public ArrayList<NewsItem> getValues() {
                return values;
        }

        public void setValues(ArrayList<NewsItem> values) {
                this.values = values;
        }

        public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                handler = new Handler();
        }

        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                        Bundle savedInstanceState) {
                View view = inflater.inflate(R.layout.ed_news, container, false);
                return view;
        }

        public void onStart() {
                super.onStart();
        }

        public void onActivityCreated(Bundle savedInstanceState) {
                super.onActivityCreated(savedInstanceState);
                refreshFromFeed();
                listAdapter = new EdNewsAdapter(getActivity(), R.layout.ed_news_item,
                                getValues());
                setListAdapter(listAdapter);

        }

        private void refreshFromFeed() {
                Thread th = new Thread(new Runnable() {
                        public void run() {
                                EdNewsHandler edNewsHandler = new EdNewsHandler();
                                try {
                                        edNewsHandler.processFeed(getActivity(), new URL(URL));
                                        setValues(edNewsHandler.getNewsItemList());
                                        listAdapter.setNewsItemList(getValues());
                                        listAdapter.notifyDataSetChanged();
                                } catch (Exception e) {
                                        e.printStackTrace();
                                }
                        }
                });
                th.start();

        }
       
       
}

