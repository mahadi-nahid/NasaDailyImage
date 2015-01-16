package com.nahid.nasadailyimage;

import java.io.IOException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nahid.nasadailyimage.R;

@SuppressLint("NewApi")
public class NasaIotd extends Fragment implements IotdHandlerListener {
        private final static String TAG = NasaIotd.class.getName();
        private static final String URL = "http://www.nasa.gov/rss/image_of_the_day.rss";

        private static Handler handler;
        private ProgressDialog dialog;
        private static Bitmap image;
        private static String imageUrl;
        private Thread imageThread;

        public static String getImageUrl() {
                return imageUrl;
        }

        public static void setImageUrl(String imageUrl) {
                NasaIotd.imageUrl = imageUrl;
        }

        public static Bitmap getImage() {
                return image;
        }

        public static void setImage(Bitmap image) {
                NasaIotd.image = image;
        }

        public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                handler = new Handler();
        }

        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                        Bundle savedInstanceState) {
                View view = inflater.inflate(R.layout.iotd, container, false);
                final Button refreshButton = (Button) view.findViewById(R.id.refreshIotdButton);
            refreshButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    refreshFromFeed();
                }
            });
            final Button wallpaperButton = (Button) view.findViewById(R.id.setWallpaperButton);
            wallpaperButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    onSetWallpaper(v);
                }
            });
                return view;
        }

        public void onStart() {
                super.onStart();
                refreshFromFeed();
        }

        private void refreshFromFeed() {
                dialog = ProgressDialog.show(getActivity(), "Loading",
                                "Loading the Image of the Day");
                Thread th = new Thread(new Runnable() {
                        public void run() {
                                IotdHandler iotdHandler = new IotdHandler();
                                iotdHandler.setListener(NasaIotd.this);
                                try {
                                        iotdHandler.processFeed(getActivity(), new URL(URL));
                                } catch (Exception e) {
                                        e.printStackTrace();
                                }
                        }
                });
                th.start();
        }

        public void onRefreshButtonClicked(View view) {
                refreshFromFeed();
        }

        public void onSetWallpaper(View view) {
                Thread th = new Thread() {
                        public void run() {
                                try {
                                        WallpaperManager wallpaperManager = WallpaperManager
                                                        .getInstance(getActivity());
                                        wallpaperManager.setBitmap(image);
                                        handler.post(new Runnable() {
                                                public void run() {
                                                        Toast.makeText(getActivity(), "Wallpaper set",
                                                                        Toast.LENGTH_SHORT).show();
                                                }
                                        });
                                } catch (Exception e) {
                                        handler.post(new Runnable() {
                                                public void run() {
                                                        Toast.makeText(getActivity(),
                                                                        "Error setting wallpaper",
                                                                        Toast.LENGTH_SHORT).show();
                                                }
                                        });
                                        e.printStackTrace();
                                }
                        }
                };
                th.start();
        }

        public void iotdParsed(final String url, final String title,
                        final String description, final String date) {
                handler.post(new Runnable() {
                        public void run() {
                                TextView titleView = (TextView) getActivity().findViewById(
                                                R.id.imageTitle);
                                titleView.setText(title);

                                TextView dateView = (TextView) getActivity().findViewById(
                                                R.id.imageDate);
                                dateView.setText(date);

                                ImageView imageView = (ImageView) getActivity().findViewById(
                                                R.id.imageDisplay);
                                setImageUrl(url);
                                imageThread = new RefreshImageThread();
                                imageThread.start();
                                while (imageThread.isAlive()) {
//                                      //Just wait. The thread needs to be finished, otherwise getImage() will return null.
//                                      //If you want to see LogCat be filled up with messages, just put a System.out.println() here,
//                                      //it'll give you an idea on how long the thread is running.
//                                      //Please note: this is NOT the best way to work in Android, since your application will
//                                      //actually be paused until the thread finishes. In this case, that is not bad, actually:
//                                      //we are showing the user a dialog. You could, however, implement behavior here that after
//                                      //a certain amount of time, the thread is stopped, just to make sure the application will not
//                                      //hang completely on a slow connection.
                                }
                                imageView.setImageBitmap(getImage());

                                TextView descriptionView = (TextView) getActivity()
                                                .findViewById(R.id.imageDescription);
                                descriptionView.setText(description);
                        }
                });
                dialog.dismiss();
        }

        static private Bitmap getBitmap(String url) throws IOException {

                HttpUriRequest request = new HttpGet(url.toString());
                HttpClient httpClient = new DefaultHttpClient();
                HttpResponse response = httpClient.execute(request);

                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();
                if (statusCode == 200) {
                        HttpEntity entity = response.getEntity();
                        byte[] bytes = EntityUtils.toByteArray(entity);

                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0,
                                        bytes.length);
                        return bitmap;
                } else {
                        throw new IOException("Download failed, HTTP response code "
                                        + statusCode + " - " + statusLine.getReasonPhrase());
                }
        }

        static public class RefreshImageThread extends Thread {
               
                @Override
                public void run() {
                        try {
                                setImage(getBitmap(getImageUrl()));
                        } catch (IOException e) {
                                Log.e(TAG,"Getting the bitmap failed!");
                        } finally {

                        }
                }
        }

}

