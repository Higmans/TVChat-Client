package biz.lungo.tvchat.updater;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.Message;
import biz.lungo.tvchat.request.Request;
import biz.lungo.tvchat.response.ChatResponse;
import biz.lungo.tvchat.ui.ChannelFragment;

public class ChatUpdater extends Thread {
    int channelIndex;
    boolean isRunning;

    public ChatUpdater(int channelIndex){
        this.channelIndex = channelIndex;
        isRunning = true;
    }

    static final long DELAY = 5000;
    @Override
    public void run() {
        while (isRunning) {
            try {
                Thread.sleep(DELAY);
            } catch (InterruptedException e) {
            }
            if (isRunning) {
                Request mRequest = new Request();
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("mode", "chat"));
                params.add(new BasicNameValuePair("action", "update"));
                params.add(new BasicNameValuePair("channelIndex", channelIndex + ""));
                params.add(new BasicNameValuePair("last", ChannelFragment.last + ""));
                params.add(new BasicNameValuePair("limit", ChannelFragment.hardcodedLimit + ""));
                ChatResponse response = new ChatResponse(mRequest.execute(params));
                JSONObject jsonResponse = null;
                try {
                    jsonResponse = new JSONObject(response.getMessages());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Message msg = new Message();
                msg.what = ChannelFragment.UPDATE_CHAT;
                msg.obj = jsonResponse;
                ChannelFragment.handlerUI.sendMessage(msg);
            }
        }
    }
    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }
}
