package biz.lungo.tvchat.ui;

import java.text.SimpleDateFormat;
import java.util.Date;
import biz.lungo.tvchat.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TableLayout;
import android.widget.TextView;

public class MessageView extends TableLayout {
    TextView name, time, message;

    public MessageView(Context context, String nameString, String timeString, String messageString){
        super(context);
        LayoutInflater  mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mInflater.inflate(R.layout.message_view, this, true);
        name = (TextView) findViewById(R.id.textViewName);
        time = (TextView) findViewById(R.id.textViewTime);
        message = (TextView) findViewById(R.id.textViewMessage);
        name.setText(nameString + ":");
        time.setText(getTime(timeString));
        message.setText(messageString);
    }
    private String getTime(String timeString) {
        Date date = new Date(Long.parseLong(timeString));
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(date);
    }

}
