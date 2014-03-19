package biz.lungo.tvchat.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import biz.lungo.tvchat.*;

public class WelcomeFragment extends Fragment implements OnClickListener {
	TextView tvWelcome;
	EditText etName;
	Button bDone;
	ProgressBar pbWait;
	static Handler handler;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		RelativeLayout rl = (RelativeLayout) RelativeLayout.inflate(getActivity(), R.layout.welcome_fragment, null);
		tvWelcome = (TextView) rl.findViewById(R.id.textViewWelcome);
		etName = (EditText) rl.findViewById(R.id.editTextName);
		bDone = (Button) rl.findViewById(R.id.buttonDone);
		pbWait = (ProgressBar) rl.findViewById(R.id.progressBar1);
		
		bDone.setOnClickListener(this);
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				((BaseActivity)getActivity()).toggle();
			}
		};
		
		return rl;
	}
	@Override
	public void onClick(View v) {
		String userName = etName.getText().toString();
		if (!"".equals(userName)){
			bDone.setVisibility(View.GONE);
			pbWait.setVisibility(View.VISIBLE);
			tvWelcome.setText("Welcome, " + userName + "!");
			ChannelFragment.userName = userName;
			new Thread(new Runnable() {				
				@Override
				public void run() {
					handler.sendEmptyMessageDelayed(0, 1500);
				}
			}).start();
		}
		else{
			Toast.makeText(getActivity(), "Enter your name!", Toast.LENGTH_LONG).show();
		}
	}
}
