package com.meetapp.onlinechat;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import include.AsyncBitmap;
import include.AsyncRequest;
import include.AsyncResponse;
import include.ChatArrayAdapter;
import include.ChatMessage;
import include.IntentHelper;
import include.MySocket;
import include.OnlineDating;
import lazylist.ChatImageLoader;
import upload.UploadActivity;

public class ChatView extends AppCompatActivity implements AsyncResponse{

    private OnlineDating onlineDating;
    public AsyncResponse delegate = null;
    private AsyncRequest request;
    public static EditText edtMessage;
    private ArrayList<ChatMessage> messages;
    private AsyncBitmap asyncBitmap;
    private LinearLayout linearLayout;
    private ImageView imageView;
    private Button btn_chat_send;
    private ProgressBar progressBar;
    private PopupWindow popupWindow;
    private int keyboardHeight;
    private AdView MainAdView;

    private Uri fileUri;
    private String imagepath;
    private static final int GALLERY_PICTURE = 1;
    private static final int COMPRESS = 100;

    private final static int callRemoveMethod = 1;
    private final static int callThumbMethod = 2;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_chat_view);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        onlineDating = new OnlineDating();
        onlineDating.init(this.getBaseContext());

        // init controls
        onlineDating.ChatListView = (ListView) findViewById(R.id.msgview);

        edtMessage = (EditText) findViewById(R.id.msg);
        btn_chat_send = (Button) findViewById(R.id.send);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        imageView = (ImageView) findViewById(R.id.userPhoto);
        linearLayout = (LinearLayout) findViewById(R.id.userPhoto_layout);
        linearLayout.setVisibility(View.GONE);

        // init ads
        MainAdView = (AdView) findViewById(R.id.mainAdView);
        final AdRequest adRequest = new AdRequest.Builder().build();
        MainAdView.loadAd(adRequest);

        initAdapter();
        setupActionBar();

        progressBar.setVisibility(View.VISIBLE);

        MainAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {

                if (messages == null) {
                    if (onlineDating.currUser != null) {

                        onlineDating.interstitial.show();
                        chatMessages();

                    }
                }

            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdClosed() {
                progressBar.setVisibility(View.GONE);
            }

        });


        // keyboard is opened
        edtMessage.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {

                        Rect r = new Rect();
                        edtMessage.getWindowVisibleDisplayFrame(r);
                        int screenHeight = edtMessage.getRootView().getHeight();

                        int keypadHeight = screenHeight - r.bottom;

                        if (keypadHeight > screenHeight * 0.15) {
                            // keyboard is opened
                            MainAdView.setVisibility(View.GONE);
                        } else {

                            MainAdView.setVisibility(View.VISIBLE);

                        }

                    }

                });



		/*---------------------------1----------------------------- */

        btn_chat_send.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {

                String message = edtMessage.getText().toString();

                if (!message.isEmpty()) {
                    onlineDating.sendNewMessage(message);
                    edtMessage.setText("");
                    linearLayout.setVisibility(View.GONE);
                }
            }
        });


    }

    private void SetSocket() {


        /*-----------------------------2--------------------------- */
        OnlineDating.Mysocket.socket.on("send_group_message", new Emitter.Listener() {

            @Override
            public void call(final Object... arg0) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) arg0[0];

                        try {
                            JSONObject content = new JSONObject(data.toString());

                            String json = content.getString("content");
                            JSONObject relJosn = new JSONObject(json);

                            String user_id = relJosn.getString("user_id");
                            String username = relJosn.getString("username");
                            String message = relJosn.getString("message");
                            String age = relJosn.getString("age");
                            String gender = relJosn.getString("gender");

                            String ThumbName = "";
                            if (relJosn.has("ThumbName"))
                                ThumbName = relJosn.getString("ThumbName");
                            else
                                ThumbName = (gender.equals("Female")) ? "http://kazanlachani.com/ify/images/female_icon.png": "http://kazanlachani.com/ify/images/man_icon.png";

                            String ImageName = "";
                            if (relJosn.has("ImageName"))
                                ImageName = relJosn.getString("ImageName");
                            else
                                ImageName = ThumbName;

                            ChatMessage chatMessage = new ChatMessage(user_id, username, message, age, gender, ThumbName, ImageName);
                            OnlineDating.chatArrayAdapter.add(chatMessage);

                        } catch (JSONException e) {
                            return;
                        }

                    }
                });

            }

        });


    }

    private void chatMessages() {

        // prepare server request
        request = new AsyncRequest();
        request.delegate = this; // listen for callback

        String url = OnlineDating.SERVICE_URL + "load.php";
        request.execute(url);


    }

    @Override
    public void processFinish(String output) {

        ChatMessage Message = new ChatMessage();

        messages = Message.parseJson(output);

        if (messages.isEmpty())
            linearLayout.setVisibility(View.VISIBLE);
        else {
            for (ChatMessage message : messages) {

                if (message.getThumbName().length() <= 0)
                {
                    String _ThumbName = (message.getGender().equals("Female")) ? "http://kazanlachani.com/ify/images/female_icon.png" : "http://kazanlachani.com/ify/images/man_icon.png";
                    message.setThumbName(_ThumbName);
                    message.setImageName(_ThumbName);
                }

                OnlineDating.chatArrayAdapter.add(message);
            }

            progressBar.setVisibility(View.GONE);

            // create new instance main socket
            OnlineDating.Mysocket = new MySocket(onlineDating.context);
            SetSocket();

        }

    }


    private void initAdapter() {

        OnlineDating.ChatListView.setAdapter(null);
        OnlineDating.chatArrayAdapter = new ChatArrayAdapter(onlineDating.context, R.layout.left);
        OnlineDating.ChatListView.setAdapter(OnlineDating.chatArrayAdapter);
        OnlineDating.ChatListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        OnlineDating.ChatListView.setAdapter(OnlineDating.chatArrayAdapter);

    }


    @Override
    public void processBitmapFinish(Bitmap output) {
        //
    }

    @Override
    public void processMessageFinish(String output) {
        //
    }


    private void setupActionBar() {

        android.support.v7.app.ActionBar ab = getSupportActionBar();
        ab.setDisplayShowCustomEnabled(true);
        ab.setDisplayShowTitleEnabled(true);

        LayoutInflater inflator = (LayoutInflater) getBaseContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.action_bar_title, null);

        ImageView titleImageView = (ImageView) v.findViewById(R.id.userPhoto);

        // set title image view
        //titleImageView.getLayoutParams().height = 220;
        //titleImageView.getLayoutParams().width = 220;
        titleImageView.getLayoutParams().height = (int) getResources().getDimension(R.dimen.imageview_height);;
        titleImageView.getLayoutParams().width = (int) getResources().getDimension(R.dimen.imageview_width);;


        ChatImageLoader ChatLoader = new ChatImageLoader(onlineDating.context);
        ChatLoader.DisplayImage(onlineDating.currUser.getThumbName(), titleImageView);

        ab.setCustomView(v);

        setTitle(onlineDating.currUser.getUsername() + ", " + onlineDating.currUser.getAge());

        titleImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent i = new Intent(onlineDating.context, ImagePreview.class);
                i.putExtra("username", onlineDating.currUser.getUsername());
                i.putExtra("age", onlineDating.currUser.getAge());
                i.putExtra("gender", onlineDating.currUser.getGender());
                i.putExtra("ImageName", onlineDating.currUser.getImageName());
                startActivity(i);

            }

        });

    }

    @Override
    public void onDestroy() {
        OnlineDating.Mysocket.socket.disconnect();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar actions click
        switch (item.getItemId()) {
            case android.R.id.home: {

                Intent i = new Intent(onlineDating.context, ImagePreview.class);
                i.putExtra("user_id", onlineDating.currUser.getId());
                i.putExtra("ImageName", onlineDating.currUser.getImageName());
                startActivity(i);
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

            case R.id.thumb_up: {
                String message = "If you enjoy using Online Dating, would you mind taking a moment to rate it? It won’t take more than a minute. Thanks for your support!";
                AlertBox(message, callThumbMethod);
                return true;

            }
            case R.id.removePhoto: {

                String message = "Are you sure you want to remove existing photo?";
                AlertBox(message, callRemoveMethod);
                return true;
            }

            case R.id.addPhoto: {
                Toast.makeText(this, "Add profile photo", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(
                        Intent.createChooser(intent, "Complete action using"),
                        GALLERY_PICTURE);

            }

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem removePhoto = menu.findItem(R.id.removePhoto);
        if (onlineDating.currUser.getHasPhoto())
            removePhoto.setVisible(onlineDating.currUser.getHasPhoto());
        else
            removePhoto.setVisible(false);

        return super.onCreateOptionsMenu(menu);

    }


    private void ApkGooglePlayUrl() {

        Uri uri = Uri.parse("market://details?id="
                + onlineDating.context.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + onlineDating.context.getPackageName())));
        }
    }

    private void AlertBox(String message, final int _method){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(message)
                .setTitle("Confirmation")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {

                                switch (_method) {
                                    case callThumbMethod: {
                                        ApkGooglePlayUrl();
                                        return;
                                    }

                                    case callRemoveMethod: {
                                        removePhoto();
                                        return;
                                    }

                                }


                            }
                        })
                .setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();

    }


    private void removePhoto(){

        onlineDating.currUser.setHasPhoto(false);
        onlineDating.setSession(true);
        Intent i = new Intent(onlineDating.context, ChatView.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == GALLERY_PICTURE && resultCode == Activity.RESULT_OK) {
            launchUploadFromGallery(data);
        }
        else {

            //
        }

    }

    private void launchUploadFromGallery(Intent data) {

        if (Build.VERSION.SDK_INT < 19) {
            Uri selectedImageUri = data.getData();
            imagepath = getPath(selectedImageUri);
            File imageFile = new File(imagepath);

            fileUri = Uri.fromFile(imageFile);
            IntentHelper.addObjectForKey(fileUri, "file_uri");

            launchUploadActivity(true);
        }
        else
        {
            InputStream imInputStream = null;
            try {
                imInputStream = getContentResolver().openInputStream(data.getData());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Bitmap bitmap = BitmapFactory.decodeStream(imInputStream);
            String smallImagePath = saveGalaryImageOnLitkat(bitmap);

            File imageFile = new File(smallImagePath);

            fileUri = Uri.fromFile(imageFile);
            IntentHelper.addObjectForKey(fileUri, "file_uri");

            launchUploadActivity(true);
        }

    }

    private String saveGalaryImageOnLitkat(Bitmap bitmap) {
        try {
            File cacheDir;
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
                cacheDir = new File(Environment.getExternalStorageDirectory(), getResources().getString(R.string.app_name));
            else
                cacheDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            if (!cacheDir.exists())
                cacheDir.mkdirs();
            String filename = System.currentTimeMillis() + ".jpg";
            File file = new File(cacheDir, filename);
            File temp_path = file.getAbsoluteFile();
            // if(!file.exists())
            file.createNewFile();
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESS, out);
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    public String getPath(Uri uri) {

        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = onlineDating.context.getContentResolver().query(uri, proj, null,
                null, null);
        if (cursor.moveToFirst()) {
            ;
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;

    }

    private void launchUploadActivity(boolean isImage) {

        IntentHelper.addObjectForKey(onlineDating.currUser, "key");
        fileUri = (Uri) IntentHelper.getObjectForKey("file_uri");

        Intent i = new Intent(onlineDating.context, UploadActivity.class);
        i.putExtra("filePath", fileUri.getPath());
        i.putExtra("isImage", isImage);
        startActivity(i);

    }

}