package lazylist;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.Spanned;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import include.OnlineDating;
import include.User;
import com.meetapp.onlinechat.R;

public class LazyAdapter extends BaseAdapter {

	private static LayoutInflater inflater = null;
	private final Context mcontext;
	public ImageLoader imageLoader;
	private static final int NO_OF_EMOTICONS = 54;
	public ArrayList<User> users;

	private User user;

	private boolean viewMode;

	public LazyAdapter(Context context, ArrayList<User> _users, boolean _viewMode) {
		mcontext = context;
		users = _users;
		viewMode = _viewMode;

		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new ImageLoader(mcontext);

	}

	public int getCount() {
		return users.toArray().length;
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		View vi = convertView;

		user = users.get(position);

		if (convertView == null) {

			if (viewMode)
				vi = inflater.inflate(R.layout.item, null);
			else
				vi = inflater.inflate(R.layout.user_item, null);

		}

		TextView text = (TextView) vi.findViewById(R.id.text);
		ImageView image = (ImageView) vi.findViewById(R.id.image);

		text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
		text.setGravity(Gravity.START);

		if (viewMode) {

			text.setTextColor(Color.DKGRAY);
			String gender = (user.getGender().equals("Man")) ? "M" : "F";
			String s = "<h3>" + user.getUsername() + ", " + user.getAge()
					+ " / " + gender + "</h3><small></i>";

		}

		return vi;
	}

	public ImageGetter imageGetter(final String imgIndex) {

		return new ImageGetter() {
			public Drawable getDrawable(String source) {
				Drawable d = new BitmapDrawable(mcontext.getResources(),
						OnlineDating.emoticons[Integer.valueOf(imgIndex) - 1]);
				d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
				return d;
			}
		};

	}
}