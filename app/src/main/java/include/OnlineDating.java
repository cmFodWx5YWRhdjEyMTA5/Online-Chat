package include;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.preference.PreferenceManager;
import android.widget.ListView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import include.MySocket;

/**
 * Created by meet app on 28.05.2017 г..
 */

public class OnlineDating extends Activity implements AsyncResponse {

    public final String IS_LOGIN = "isLogin";
    private SharedPreferences sharedPrefs;
    public Context context;
    public static MySocket Mysocket;
    public User currUser = null;
    private boolean Session;
    public static String IMAGE_URL = "http://kazanlachani.com/FreeChat/uploads/";
    public static String SERVICE_URL = "http://kazanlachani.com/FreeChat/services/";
    public static String interstitial_key = "ca-app-pub-2108590561691007/4300312372";
    public static InterstitialAd interstitial;
    public static ListView ChatListView;
    public static Bitmap[] emoticons;
    public static final int NO_OF_EMOTICONS = 54;
    public static ChatArrayAdapter chatArrayAdapter;

    public OnlineDating(){

        this.Session = false;
    }

    public void init(Context context) {

        this.context = context;
        this.Session = this.getSesson();
        this.InterstitialAd();

    }

    public boolean getSesson() {

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.context);
        this.Session = sharedPrefs.getBoolean(IS_LOGIN, false);

        if (this.Session) {

            this.currUser = new User();

            this.currUser.user_id = sharedPrefs.getString("user_id", "");
            this.currUser.username = sharedPrefs.getString("username", "");
            this.currUser.age = sharedPrefs.getString("age", "");
            this.currUser.gender = sharedPrefs.getString("gender", "");
            this.currUser.hasPhoto = sharedPrefs.getBoolean("hasPhoto", false);

            if (this.currUser.hasPhoto) {
                this.currUser.ThumbName = sharedPrefs.getString("thumbName", "");
                this.currUser.ImageName = sharedPrefs.getString("imageName", "");
            }
            else
            {
                this.currUser.ThumbName = (this.currUser.gender.equals("Female")) ? "http://kazanlachani.com/ify/images/female_icon.png": "http://kazanlachani.com/ify/images/man_icon.png";
                this.currUser.ImageName = this.currUser.ThumbName;
            }


        }

        return this.Session;
    }

    public void setSession(boolean _value) {

        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putBoolean(IS_LOGIN, _value);

        editor.putString("user_id", currUser.getId());
        editor.putString("username", currUser.getUsername());
        editor.putString("age", currUser.getAge());
        editor.putString("gender", currUser.getGender());
        editor.putString("thumbName", currUser.getThumbName());
        editor.putString("imageName", currUser.getImageName());
        editor.putBoolean("hasPhoto", currUser.getHasPhoto());

        editor.commit();

    }

    public void BannerAd(AdView adView) {

        // Request for Ads
        AdRequest adRequest = new AdRequest.Builder().build();

        // Load ads into Banner Ads
        if (adView != null)
            adView.loadAd(adRequest);
    }


    public void InterstitialAd() {

        interstitial = new InterstitialAd(this.context);
        interstitial.setAdUnitId(this.interstitial_key);

        AdRequest adRequest = new AdRequest.Builder().build();
        interstitial.loadAd(adRequest);

        interstitial.setAdListener(new AdListener() {
            public void onAdLoaded() {
                //
            }
        });
    }

    public void sendNewMessage(String message) {

        Mysocket.sendMessageTo(message);
        sendDBMessage(message);
    }

    public void sendDBMessage(String _message) {

        AsyncRequest request = new AsyncRequest();
        request.delegate = this; // listen for callback

        try {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String currentDateandTime = sdf.format(new Date());

            String username = URLEncoder.encode(currUser.getUsername(), "utf-8");
            String message = URLEncoder.encode(_message, "utf-8");
            currentDateandTime = URLEncoder.encode(currentDateandTime, "utf-8");
            String ThumbName = URLEncoder.encode(currUser.getThumbName(), "utf-8");
            String ImageName = URLEncoder.encode(currUser.getImageName(), "utf-8");

            String url = SERVICE_URL + "insert.php?user_id="
                    + currUser.getId() + "&username=" + username
                    + "&date=" + currentDateandTime
                    + "&message=" + message
                    + "&ThumbName=" +ThumbName
                    + "&ImageName=" + ImageName;

            request.execute(url);

        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
        }


    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bmp, int _radius) {

        Bitmap sbmp;
        int radius = _radius;
        if (bmp.getWidth() != radius || bmp.getHeight() != radius)
            sbmp = Bitmap.createScaledBitmap(bmp, radius, radius, false);
        else
            sbmp = bmp;
        Bitmap output = Bitmap.createBitmap(sbmp.getWidth(), sbmp.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor("#BAB399"));
        canvas.drawCircle(sbmp.getWidth() / 2 + 0.7f,
                sbmp.getHeight() / 2 + 0.7f, sbmp.getWidth() / 2 + 0.1f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(sbmp, rect, rect, paint);

        return output;
    }

    @Override
    public void processFinish(String output) {

    }

    @Override
    public void processBitmapFinish(Bitmap output) {

    }

    @Override
    public void processMessageFinish(String output) {

    }
}
