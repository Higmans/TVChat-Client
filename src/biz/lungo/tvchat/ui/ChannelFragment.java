package biz.lungo.tvchat.ui;

import biz.lungo.tvchat.*;
import biz.lungo.tvchat.request.Request;
import biz.lungo.tvchat.response.ChatResponse;
import biz.lungo.tvchat.response.ProgramResponse;
import biz.lungo.tvchat.response.TimeResponse;
import biz.lungo.tvchat.ui.menu.MenuFragment;
import biz.lungo.tvchat.updater.ChatUpdater;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class ChannelFragment extends Fragment implements OnClickListener, OnTouchListener {
	static final int SET_PROGRAM = 0;
	public static final int UPDATE_CHAT = 1;
	static final ExecutorService executorMain = Executors.newSingleThreadExecutor();
	public final static int hardcodedLimit = 50;
	static String userName = "Anonymous";
	public static long last = -1;
	long timestamp = -1;
	public static Handler handlerUI;
	int channelIndex;
	int iconResId;
	public static Activity activity;
	TextView tvProgramActionBar;
	LinearLayout llChat;
	ScrollView svChat;
	ImageButton ibSend;
	EditText etMessage;
	ChatUpdater chatUpdater;
	TypedArray channelIconsArray;
	
	public ChannelFragment(){
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		RelativeLayout rl = (RelativeLayout) RelativeLayout.inflate(getActivity(), R.layout.channel_fragment, null);
		activity = getActivity();		
		ibSend = (ImageButton) rl.findViewById(R.id.buttonSend);
		etMessage = (EditText) rl.findViewById(R.id.editTextMessage);
		llChat = (LinearLayout) rl.findViewById(R.id.llChat);
		svChat = (ScrollView) rl.findViewById(R.id.scrollViewChat);
		channelIconsArray = activity.getResources().obtainTypedArray(R.array.channel_icons);
		getFields(savedInstanceState);
		
		RelativeLayout rlActionBar = (RelativeLayout) RelativeLayout.inflate(getActivity(), R.layout.action_bar_channel, null);
		tvProgramActionBar = (TextView) rlActionBar.findViewById(R.id.textViewProgram);
		ActionBar actionBar = (getActivity()).getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | 
									ActionBar.DISPLAY_HOME_AS_UP | 
									ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setCustomView(rlActionBar);
		actionBar.setIcon(iconResId);
		
		ibSend.setOnClickListener(this);
		ibSend.setOnTouchListener(this);
		etMessage.setOnTouchListener(this);
		
		last = -1;
		setChannelTitle();
		getNewMessages();
		chatUpdater = new ChatUpdater(channelIndex);
		handlerUI = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what){
				case SET_PROGRAM:
					String value = msg.obj.toString();
					tvProgramActionBar.setText(value);
					break;
				case UPDATE_CHAT:
					updateChat(msg);
					break;
				}
			}
		};
		chatUpdater.start();
		return rl;
	}
	
	private void getFields(Bundle savedInstanceState) {
		if (savedInstanceState == null){
			channelIndex = getArguments().getInt("channelIndex");
			iconResId = getArguments().getInt("iconResId");
		}
		else{
			channelIndex = savedInstanceState.getInt("channelIndex");
			iconResId = savedInstanceState.getInt("iconResId");
		}
	}
	
	private void setChannelTitle() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Request mRequest = new Request();
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("mode", "info"));
				params.add(new BasicNameValuePair("channel", MenuFragment.channels[channelIndex]));
				final ProgramResponse mResponse = new ProgramResponse(mRequest.execute(params));
				Message msg = new Message();
				msg.what = SET_PROGRAM;
				msg.obj = mResponse.getProgram();
				handlerUI.sendMessage(msg);
			}
		}).start();
	}
	
	@Override
	public void onClick(View v) {
		executorMain.submit(new Runnable() {			
			@Override
			public void run() {
				syncTime();
			}
		});
		String message = etMessage.getText().toString();
		final String validMessage = validateMessage(message);
		if (validMessage.length() > 0) {
			executorMain.submit(new Runnable() {				
				@Override
				public void run() {
					sendChatMessage(validMessage);
				}				
			});			
		}
		else{
			showToast(getResources().getString(R.string.error_empty_message));
		}
		etMessage.setText("");
	}
	
	private void sendChatMessage(String messageToSend) {
		JSONObject messageJO = new JSONObject();
		try {
			messageJO.put("name", userName)
					 .put("timestamp", timestamp).put("message", messageToSend)
					 .put("channelIndex", channelIndex);
		} catch (JSONException e) {
			e.printStackTrace();
		}
        Request mRequest = new Request();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("mode", "chat"));
		params.add(new BasicNameValuePair("action", "put"));
		params.add(new BasicNameValuePair("message", messageJO.toString()));
		mRequest.execute(params);
		JSONObject jsonSingleUpdate = new JSONObject();
		try {
			jsonSingleUpdate.put(messageJO.getString("timestamp"),
                    messageJO);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Message msg = new Message();
		msg.what = UPDATE_CHAT;
		msg.obj = jsonSingleUpdate;
		handlerUI.sendMessage(msg);
	}

	private void syncTime() {
		// time in chat is always server's
		last = timestamp;
		updateTimeStamp();
	}
	
	private String validateMessage(String message) {
		//no spaces at the beginning of a message
		//no empty message full of spaces
        int spacesCount = 0;
		for (int i = 0; i < message.length(); i++){
			if (' ' == message.charAt(i)){
				spacesCount++;
			}
			else{
				break;
			}
		}
		return message.substring(spacesCount);
	}
	
	private void updateTimeStamp() {
		// server responds with long System.currentTimeMillis()
		Request mRequest = new Request();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("mode", "time"));
		TimeResponse response = new TimeResponse(mRequest.execute(params));
		timestamp = response.getTime();
	}
	
	private void getNewMessages() {
		executorMain.submit(new Runnable() {
			@Override
			public void run() {
				Request mRequest = new Request();
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("mode", "chat"));
				params.add(new BasicNameValuePair("action", "update"));
				params.add(new BasicNameValuePair("channelIndex", channelIndex + ""));
				params.add(new BasicNameValuePair("last", last + ""));
				params.add(new BasicNameValuePair("limit", hardcodedLimit + ""));
				ChatResponse response = new ChatResponse(mRequest.execute(params));
				JSONObject jsonResponse = null;
				try {
					jsonResponse = new JSONObject(response.getMessages());
				} catch (JSONException e) {
					e.printStackTrace();
				}
				Message msg = new Message();
				msg.what = UPDATE_CHAT;
				msg.obj = jsonResponse;
				handlerUI.sendMessage(msg);
			}
		});	
	}
	
	private void updateChat(Message msg){
		//should be used only via handlerUI
		//msg.obj - JSONObject with messages
		//msg.what - UPDATE_CHAT (1)
		JSONObject dataJson = null;
		try {
			dataJson = new JSONObject(msg.obj.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		HashMap<Long, SingleMessage> unsortedMessages = new HashMap<Long, SingleMessage>();
		@SuppressWarnings("unchecked")
		Iterator<String> iter = dataJson.keys();
	    while (iter.hasNext()) {
	        String key = iter.next();
	        Object messageData = null;
	        try {
	            messageData = dataJson.get(key);
	        } catch (JSONException e) {
                e.printStackTrace();
	        }
	        JSONObject messageJson = null;
	        try {
				messageJson = new JSONObject(messageData.toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}
	        String name = "";
	        String timestamp = "";
	        String message = "";
			try {
				name = messageJson.getString("name");
				timestamp = messageJson.getString("timestamp");
				message = messageJson.getString("message");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			SingleMessage singleMessage = new SingleMessage();
			singleMessage.setName(name);
			singleMessage.setTimestamp(timestamp);
			singleMessage.setMessage(message);
			unsortedMessages.put(Long.parseLong(singleMessage.getTimestamp()), singleMessage);
	    }
	    List<Long> sortedTimestamps = new ArrayList<Long>(unsortedMessages.keySet());
	    Collections.sort(sortedTimestamps);
		for (Long key : sortedTimestamps) {
			SingleMessage messageToPut = unsortedMessages.get(key);
			llChat.addView(new MessageView(getActivity(), 
								messageToPut.getName(), 
								messageToPut.getTimestamp(), 
								messageToPut.getMessage()));
		}
		if (!sortedTimestamps.isEmpty()){
		    last = sortedTimestamps.get(sortedTimestamps.size() - 1);
		    scrollDown();
		}
	}
	
	private void scrollDown() {
		//called when chat ScrollView needs to get scrolled to the bottom
		svChat.post(new Runnable() {
			@Override
			public void run() {
				svChat.fullScroll(ScrollView.FOCUS_DOWN);
			}
		});
	}
	public static void showToast(String string) {
		Toast.makeText(activity, string, Toast.LENGTH_SHORT).show();
	}
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if ("EditText".equals(v.getTag())){
			scrollDown();
		}
		else{
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				ibSend.setBackgroundColor(Color.LTGRAY);
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				ibSend.setBackgroundColor(Color.TRANSPARENT);
			}
		}		
		return false;
	}	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt("iconResId", iconResId);
		outState.putInt("channelIndex", channelIndex);
		super.onSaveInstanceState(outState);
	}
	@Override
	public void onDestroy() {
		chatUpdater.setRunning(false);
		super.onDestroy();
	}
}
