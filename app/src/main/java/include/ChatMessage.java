package include;
import android.annotation.SuppressLint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatMessage {
	public String user_id;
	private String username;
	private String message;
	private String age;
	private String gender;
	private String date;
	private String ThumbName;
	private String ImageName;
	private ChatMessage ChatMessage;

	public ChatMessage() {

		this.user_id = "";
		this.username = "";
		this.message = "";
		this.age = "";
		this.gender = "";
		this.date = "";
		this.ThumbName = "";
		this.ImageName = "";
		this.ChatMessage = null;

	}

	@SuppressLint("SimpleDateFormat")
	public ChatMessage(String user_id, String username, String message, String age, String gender, String ThumbName, String ImageName) {

		super();

		this.user_id = user_id;
		this.username = username;
		this.age = age;
		this.gender = gender;
		this.message = message;
		this.ThumbName = ThumbName;
		this.ImageName = ImageName;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String currentDateandTime = sdf.format(new Date());
		this.date = currentDateandTime;
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

	public String getMessage() {
		return message;
	}

	public String getDate() {
		return date;
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

	public ArrayList<ChatMessage> parseJson(String result) {

		JSONArray jsonArray = null;
		try {

			jsonArray = new JSONArray(result);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ArrayList<ChatMessage> messages = new ArrayList<ChatMessage>();

		//if (jsonArray != null) {
			for (int i = 0; i < jsonArray.length(); ++i) {

				JSONObject element = null;
				try {
					element = jsonArray.getJSONObject(i);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ChatMessage newMessage = null;
				try {
					newMessage = new ChatMessage(element);
					messages.add(newMessage);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		//}

		return messages;

	}

	public ChatMessage(JSONObject element) throws JSONException {

		this.user_id = element.getString("user_id");
		this.username = element.getString("c_username");
		this.age = element.getString("age");
		this.gender = element.getString("gender");
		this.message = element.getString("message");
		this.date = element.getString("date");

		String _ThumbName = "";
		if (element.has("ThumbName"))
			_ThumbName = element.getString("ThumbName");
		else
			_ThumbName = (gender.equals("Female")) ? "http://kazanlachani.com/ify/images/female_icon.png": "http://kazanlachani.com/ify/images/man_icon.png";

		String _ImageName = "";
		if (element.has("ImageName"))
			_ImageName = element.getString("ImageName");
		else
			_ImageName = _ThumbName;

		this.ThumbName = _ThumbName.replaceAll(" ", "%20");
		this.ImageName = _ImageName.replaceAll(" ", "%20");

	}


}