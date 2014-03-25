package biz.lungo.tvchat.ui.menu;

import biz.lungo.tvchat.*;
import biz.lungo.tvchat.request.Request;
import biz.lungo.tvchat.response.ChannelsResponse;
import biz.lungo.tvchat.ui.BaseActivity;
import biz.lungo.tvchat.ui.ChannelFragment;
import biz.lungo.tvchat.ui.StartActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import android.app.ListFragment;
import android.app.Fragment;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MenuFragment extends ListFragment{
    protected static final int UPDATE_MENU = 0;
    CountDownLatch latch = new CountDownLatch(1);
    public static String channels[];
    static Handler menuHandler;
    static ArrayAdapter<String> channelsAdapter;
    static MenuAdapter menuAdapter;
    static String visibleChannels[];
    static TypedArray channelIcons;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (BaseActivity.networkOK && BaseActivity.serverOK){
            getChannels();
        }
        else{
            showConnectionErrorDialog();
        }
        channelsAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                new ArrayList<String>(Arrays.asList(getResources().getString(R.string.menu_loading))));
        setListAdapter(channelsAdapter);
        menuHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what){
                    case UPDATE_MENU:
                        visibleChannels = BaseActivity.context.getResources().getStringArray(R.array.channel_names);
                        channelIcons = BaseActivity.context.getResources().obtainTypedArray(R.array.channel_icons);
                        channels = ((String[])msg.obj);
                        ArrayList<ChannelsMenuItem> menuList = new ArrayList<ChannelsMenuItem>();
                        for (int i = 0; i < channels.length; i++){
                            ChannelsMenuItem menuItem = new ChannelsMenuItem(i, channels[i]);
                            if (menuItem.visible){
                                menuList.add(menuItem);
                            }
                        }
                        menuAdapter = new MenuAdapter(getActivity(), R.layout.menu_channel_item, menuList);
                        setListAdapter(menuAdapter);
                        break;
                }
            }
        };
    }

    private void getChannels(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("mode", "channels"));
                params.add(new BasicNameValuePair("test_client", "true"));
                Request mRequest = new Request();
                ChannelsResponse mResponse = new ChannelsResponse(mRequest.execute(params));
                Message msg = new Message();
                msg.what = UPDATE_MENU;
                msg.obj = mResponse.getChannels();
                menuHandler.sendMessage(msg);
            }
        }).start();
    }
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Fragment newFragment = new ChannelFragment();
        Bundle args = new Bundle();
        args.putInt("channelIndex", (Integer)v.getTag(R.string.channel_tag));
        args.putInt("iconResId", (Integer)v.getTag(R.string.channel_tag_icon));
        newFragment.setArguments(args);
        if (newFragment != null){
            switchFragment(newFragment);
        }
    }

    private void switchFragment(Fragment newFragment) {
        if (getActivity() == null)
            return;

        if (getActivity() instanceof StartActivity) {
            StartActivity fca = (StartActivity) getActivity();
            fca.switchContent(newFragment);
        }
    }

    private void showConnectionErrorDialog() {
        // TODO
        Toast.makeText(getActivity(), "Conn error", Toast.LENGTH_LONG).show();
    }

}
