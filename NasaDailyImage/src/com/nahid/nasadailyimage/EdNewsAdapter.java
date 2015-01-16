package com.nahid.nasadailyimage;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nahid.nasadailyimage.EdNewsHandler.NewsItem;
import com.nahid.nasadailyimage.R;


public class EdNewsAdapter extends ArrayAdapter<NewsItem> {
       
        private ArrayList<NewsItem> newsItemList = new ArrayList<EdNewsHandler.NewsItem>();
        private Context context;
       
        public ArrayList<NewsItem> getNewsItemList() {
                return newsItemList;
        }

        public void setNewsItemList(ArrayList<NewsItem> newsItemList) {
                this.newsItemList = newsItemList;
        }

        public Context getContext() {
                return context;
        }

        public void setContext(Context context) {
                this.context = context;
        }
       
       

        public EdNewsAdapter(Context context, int textViewResourceId, ArrayList<NewsItem> newsItemList) {
                super(context,textViewResourceId,newsItemList);
                setContext(context);
                setNewsItemList(newsItemList);
        }
       
        @Override
        public int getCount() {
                Log.d("tag", "getCount() got called, returning this size: " + getNewsItemList().size());
                return getNewsItemList().size();
        }
       
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
                Log.d("EdNewsAdapter","Got called for position " + position);
                Log.d("EdNewsAdapter","Working for view: " + convertView);
                View v = convertView;
               
                if (v == null) {
                        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        v = inflater.inflate(R.layout.ed_news_item, null);
                }
               
                NewsItem newsItem = newsItemList.get(position);
                if (newsItem != null) {
                        // Set the title
                        TextView title = (TextView) v.findViewById(R.id.newsTitle);
                        title.setText(newsItem.getTitle());
                        // Set the description
                        TextView description = (TextView) v.findViewById(R.id.newsDescription);
                        description.setText(newsItem.getDescription());
                        // Set the date
                        TextView date = (TextView) v.findViewById(R.id.newsDate);
                        date.setText(newsItem.getDate());
                        // Set the URL
                        TextView url = (TextView) v.findViewById(R.id.newsUrl);
                        url.setText(newsItem.getUrl());
                }
               
                return v;
        }

}

