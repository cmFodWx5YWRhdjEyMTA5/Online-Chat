package include;

import android.app.Activity;

import org.json.JSONException;
import org.json.JSONObject;

public class User{

    public String user_id;
    public String username;
    public String age;
    public String gender;
    public String ThumbName;
    public String ImageName;
    public boolean hasPhoto;

    public String getId() {
        return user_id;
    }

    public String getUsername() {
        return username;
    }

    public String getAge() {
        return age;
    }

    public String getGender() {

        return gender;
    }

    public String getThumbName() {

        return ThumbName;
    }

    public void setThumbName(String _value) {
        ThumbName = _value;
    }

    public String getImageName() {
        return ImageName;
    }

    public void setImageName(String _value) {
        ImageName = _value;
    }


    public boolean getHasPhoto() {
        return hasPhoto;
    }


    public void setHasPhoto(boolean _value) {
        hasPhoto = _value;
    }

    public User () {

        this.user_id = "";
        this.username = "";
        this.age = "";
        this.gender = "";
        this.ThumbName = "";
        this.ImageName = "";
        this.hasPhoto = false;

    }

    public User (String Id, String Username, String age, String gender, String ThumbName, String ImageName) {

        this.user_id = Id;
        this.username = Username;
        this.age = age;
        this.gender = gender;
        this.ThumbName = ThumbName;
        this.ImageName = ImageName;
        this.hasPhoto = false;

    }

}
