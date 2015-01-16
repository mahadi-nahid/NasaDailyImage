package com.nahid.nasadailyimage;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

public class EdNewsHandler extends DefaultHandler {
       
        private static final String TAG = EdNewsHandler.class.getSimpleName();

        private boolean inTitle = false;
        private boolean inDescription = false;
        private boolean inItem = false;
        private boolean inDate = false;
        private boolean inChannel = false;
        private boolean inUrl = false;

        private String url = null;
        private StringBuffer title = new StringBuffer();
        private StringBuffer description = new StringBuffer();
        private String date = null;

        private ArrayList<NewsItem> newsItemList = new ArrayList<EdNewsHandler.NewsItem>();
       
        public class NewsItem {
                private String title;
                private String date;
                private String description;
                private String url;

                public String getTitle() {
                        return title;
                }

                public void setTitle(String title) {
                        this.title = title;
                }

                public String getDate() {
                        return date;
                }

                public void setDate(String date) {
                        this.date = date;
                }

                public String getDescription() {
                        return description;
                }

                public void setDescription(String description) {
                        this.description = description;
                }

                public String getUrl() {
                        return url;
                }

                public void setUrl(String url) {
                        this.url = url;
                }
               
                public String toString() {
                        return "Just say hello!";
                }
        }

        public void startElement(String uri, String localName, String qName,
                        Attributes attributes) throws SAXException {

                if (localName.equals("channel")) {
                        inChannel = true;
                } else {
                        if (inChannel) {
                                if (localName.startsWith("item")) {
                                        inItem = true;
                                } else {
                                        if (inItem) {
                                                if (localName.equals("title")) {
                                                        inTitle = true;
                                                } else {
                                                        inTitle = false;
                                                }

                                                if (localName.equals("description")) {
                                                        inDescription = true;
                                                } else {
                                                        inDescription = false;
                                                }

                                                if (localName.equals("link")) {
                                                        inUrl = true;
                                                } else {
                                                        inUrl = false;
                                                }

                                                if (localName.equals("pubDate")) {
                                                        inDate = true;
                                                } else {
                                                        inDate = false;
                                                }
                                        }
                                }
                        }
                }
        }

        @SuppressLint("NewApi")
	public void characters(char ch[], int start, int length) {
                String chars = (new String(ch).substring(start, start + length));
                if (!chars.trim().isEmpty()) {

                        if (inTitle) {
                                title.append(chars);
                        }

                        if (inDescription) {
                                description.append(chars);
                        }

                        if (inUrl) {
                                setUrl(chars);
                        }

                        if (inDate) {
                                // Example: Tue, 21 Dec 2010 00:00:00 EST
                                String rawDate = chars;
                                if (!rawDate.isEmpty()) {
                                        try {
                                                SimpleDateFormat parseFormat = new SimpleDateFormat(
                                                                "EEE, dd MMM yyyy HH:mm:ss");
                                                Date sourceDate = parseFormat.parse(rawDate);

                                                SimpleDateFormat outputFormat = new SimpleDateFormat(
                                                                "EEE, dd MMM yyyy");
                                                setDate(outputFormat.format(sourceDate));
                                        } catch (Exception e) {
                                                e.printStackTrace();
                                        }
                                }
                        }
                }

        }

        public void processFeed(Context context, URL url) {
                try {

                        SAXParserFactory spf = SAXParserFactory.newInstance();
                        SAXParser sp = spf.newSAXParser();
                        XMLReader xr = sp.getXMLReader();
                        xr.setContentHandler(this);
                        xr.parse(new InputSource(url.openStream()));

                } catch (IOException e) {
                        Log.e(TAG, e.toString());
                } catch (SAXException e) {
                        Log.e(TAG, e.toString());
                } catch (ParserConfigurationException e) {
                        Log.e(TAG, e.toString());
                }
        }

        public void endElement(String uri, String localName, String qName) {

                // End of an item
                // If an item is complete, we can fill up the model object, and add it
                // to the list
                // Afterwards, clear all values, to start the next item
                if (localName.equals("item")) {
                        NewsItem addedNewsItem = new NewsItem();
                        addedNewsItem.setTitle(getTitle().toString());
                        addedNewsItem.setDescription(getDescription().toString());
                        addedNewsItem.setUrl(getUrl());
                        addedNewsItem.setDate(date);
                        newsItemList.add(addedNewsItem);
                        setTitle(new StringBuffer());
                        setDescription(new StringBuffer());
                        setDate(null);
                        setUrl(null);
                        inItem = false;
                }

        }

        public String getDate() {
                return date;
        }

        public void setDate(String date) {
                this.date = date;
        }

        public String getUrl() {
                return url;
        }

        public void setUrl(String url) {
                this.url = url;
        }

        public StringBuffer getTitle() {
                return title;
        }

        public void setTitle(StringBuffer title) {
                this.title = title;
        }

        public StringBuffer getDescription() {
                return description;
        }

        public void setDescription(StringBuffer description) {
                this.description = description;
        }

        public ArrayList<NewsItem> getNewsItemList() {
                return newsItemList;
        }

}

