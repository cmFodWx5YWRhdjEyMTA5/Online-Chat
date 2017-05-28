package include;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.IO.Options;
import com.github.nkzawa.socketio.client.Socket;

import android.content.Context;
import android.util.Log;

public class MySocket extends OnlineDating {

	public Socket socket;
	private Options opts;

	public MySocket(Context _context) {

		super();
		init(_context);

		try {
			opts = new Options();
			opts.forceNew = true;

			socket = IO.socket("http://ifychat.herokuapp.com:80", opts);

		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

			@Override
			public void call(Object... args) {
				Log.d("ify - EVENT_CONNECT", "EVENT_CONNECT");
				join();
			}

		}).on("event", new Emitter.Listener() {

			@Override
			public void call(Object... args) {
				//
			}

		}).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

			@Override
			public void call(Object... args) {

				Log.d("ify - EVENT_DISCONNECT", "EVENT_DISCONNECT");
			}

		}).on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {

			@Override
			public void call(Object... args) {

				//Log.d("ify - EVENT_CONNECT_ERROR", "EVENT_CONNECT_ERROR");
			}

		});

		socket.connect();

	}

	public void join() {

		JSONObject user = new JSONObject();
		try {
			String id = String.valueOf(currUser.getId());
			user.putOpt("id", id);
			socket.emit("join", user);

		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private JSONObject getUserData(String _message) {

		JSONObject message = new JSONObject();
		try {

			Calendar c = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String strDate = sdf.format(c.getTime());

			String user_id = String.valueOf(currUser.getId());
			message.putOpt("user_id", user_id);
			message.putOpt("username", currUser.username);
			message.putOpt("age", currUser.age);
			message.putOpt("gender", currUser.gender);
			message.putOpt("date", strDate);
			message.putOpt("message", _message);
			message.putOpt("ThumbName", currUser.getThumbName());
			message.putOpt("ImageName", currUser.getImageName());
			message.putOpt("code", 0);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return message;

	}

	public void sendMessageTo(String _message) {

		JSONObject message = getUserData(_message);
		if (socket != null) {
			socket.emit("send_group_message", message);
		}

	}
}
