package com.meetapp.onlinechat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import include.AsyncBitmap;
import include.AsyncResponse;
import include.OnlineDating;
import include.TouchImageView;
import lazylist.ChatImageLoader;


public class ImagePreview extends AppCompatActivity implements AsyncResponse {

    private OnlineDating onlineDating;
    private AsyncBitmap asyncBitmap;

    private ProgressBar progressBar;
    private String ImageName;
    private String gender;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.image_preview);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        Bundle extras = getIntent().getExtras();

        String username = extras.getString("username");
        String age = extras.getString("age");
        gender = extras.getString("gender");

        ImageName = extras.getString("ImageName");

        onlineDating = new OnlineDating();
        onlineDating.init(this.getBaseContext());

        setTitle(username + ", " + age + ", " + gender);

        asyncBitmap = new AsyncBitmap();
        asyncBitmap.delegate = this;
        asyncBitmap.execute(ImageName);

    }


    @Override
    public void processFinish(String output) {

    }

    @Override
    public void processBitmapFinish(Bitmap output) {

        if (output == null) {

            int _ThumbName = (gender.equals("Female")) ? R.drawable.female_icon: R.drawable.man_icon;
            TouchImageView touch = (TouchImageView) findViewById(R.id.imagePreview);
            touch.setImageResource(_ThumbName);

            progressBar.setVisibility(View.GONE);

            return;
        }

        // imgPreview.setImageBitmap(output);
        TouchImageView touch = (TouchImageView) findViewById(R.id.imagePreview);

        // get phone display size
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = size.y;

        Bitmap sbmp = Bitmap.createScaledBitmap(output, height, height, false);
        touch.setImageBitmap(sbmp);

        progressBar.setVisibility(View.GONE);

    }

    @Override
    public void processMessageFinish(String output) {
        //
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home: {
                this.finish();
                return true;
            }

            case R.id.share: {

                String text = "Meet New People, Online Dating";
                String subject = "https://play.google.com/store/apps/details?id=lovezone.onlinedating.com";
                String shareText = "Share this app with friends";

                Intent sendIntent = new Intent(android.content.Intent.ACTION_SEND);
                sendIntent.setType("text/plain");
                sendIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, text);
                sendIntent.putExtra(android.content.Intent.EXTRA_TEXT, subject);
                startActivity(Intent.createChooser(sendIntent, shareText));
                //this.startActivity(sendIntent);

                return true;

            }

            default:
                return super.onOptionsItemSelected(item);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem thumb_up = menu.findItem(R.id.thumb_up);
        MenuItem addPhoto = menu.findItem(R.id.addPhoto);
        MenuItem removePhoto = menu.findItem(R.id.removePhoto);

        thumb_up.setVisible(false);
        addPhoto.setVisible(false);
        removePhoto.setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }


}