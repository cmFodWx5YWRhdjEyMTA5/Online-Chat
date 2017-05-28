package include;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import lazylist.ChatImageLoader;
import com.meetapp.onlinechat.ChatView;
import com.meetapp.onlinechat.ImagePreview;
import com.meetapp.onlinechat.R;

public class ChatArrayAdapter extends ArrayAdapter<ChatMessage> {

	private TextView chatText;
	private ArrayList<ChatMessage> chatMessageList = new ArrayList<ChatMessage>();
	private ImageView chat_send_photo;
	private LayoutInflater inflater;
	private ChatMessage chatMessageObj;
	private EditText edtMessage;

	@Override
	public void add(ChatMessage object) {

		chatMessageList.add(object);
		super.add(object);
	}

	public ChatArrayAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);

	}

	public int getCount() {
		return this.chatMessageList.size();
	}

	public ChatMessage getItem(int index) {
		return this.chatMessageList.get(index);
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		chatMessageObj = getItem(position);

		View row = convertView;
		if (row == null) {
			inflater = (LayoutInflater) this.getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
		}

		row = inflater.inflate(R.layout.left, parent, false);
		chat_send_photo = (ImageView) row.findViewById(R.id.left_chat_send_photo);

		chatText = (TextView) row.findViewById(R.id.msgr);

		if (chatMessageObj.getMessage().length() > 0) {

			String s = "<big>" + chatMessageObj.getUsername()
					+ "</big><br/>"
					+ chatMessageObj.getMessage()
					+ "<br/>"
					+ "<small><i>" + chatMessageObj.getDate()
					+ "</i></small>";

			chatText.setText(Html.fromHtml(s));

			chat_send_photo.getLayoutParams().height = (int) row.getResources().getDimension(R.dimen.imageview_height);;
			chat_send_photo.getLayoutParams().width = (int) row.getResources().getDimension(R.dimen.imageview_width);;

			ChatImageLoader ChatLoader = new ChatImageLoader(getContext());
			ChatLoader.DisplayImage(chatMessageObj.getThumbName(), chat_send_photo);

			chatText.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					ChatMessage ch = chatMessageList.get(position);
					ChatView.edtMessage.setText(ch.getUsername() + " ");

				}
			});


			chat_send_photo.setOnClickListener(new View.OnClickListener() {
				ChatMessage ch = chatMessageList.get(position);
				@Override
				public void onClick(View v) {
					Intent i = new Intent(getContext(), ImagePreview.class);
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

					i.putExtra("username", ch.getUsername());
					i.putExtra("age", ch.getAge());
					i.putExtra("gender", ch.getGender());

					i.putExtra("ImageName", ch.getImageName());
					getContext().startActivity(i);

				}
			});


		}

		return row;
	}

}
