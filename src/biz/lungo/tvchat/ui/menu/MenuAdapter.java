package biz.lungo.tvchat.ui.menu;

import java.util.ArrayList;
import biz.lungo.tvchat.R;
import biz.lungo.tvchat.ui.BaseActivity;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MenuAdapter extends ArrayAdapter<ChannelsMenuItem> {
    Context context;
    int layoutResourceId;
    ArrayList<ChannelsMenuItem> data = null;

    public MenuAdapter(Context context, int resource, ArrayList<ChannelsMenuItem> data) {
        super(context, resource, data);
        this.layoutResourceId = resource;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        PlaceHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) BaseActivity.context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new PlaceHolder();
            holder.imgIcon = (ImageView) row.findViewById(R.id.channelIcon);
            holder.txtTitle = (TextView) row.findViewById(R.id.channelInfo);
            row.setTag(R.string.menu_tag, holder);
        } else {
            holder = (PlaceHolder) row.getTag(R.string.menu_tag);
        }

        ChannelsMenuItem menuItem = data.get(position);
        holder.txtTitle.setText(menuItem.channelName);
        holder.imgIcon.setImageResource(menuItem.channelIcon);
        row.setTag(R.string.channel_tag, menuItem.channelIndex);
        row.setTag(R.string.channel_tag_icon, menuItem.channelIcon);

        return row;
    }

    static class PlaceHolder {
        ImageView imgIcon;
        TextView txtTitle;
    }

}
