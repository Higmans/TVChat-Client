package biz.lungo.tvchat.ui;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.ListFragment;
import android.view.Menu;
import android.view.MenuItem;
import biz.lungo.tvchat.R;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

public class BaseActivity extends SlidingFragmentActivity {
	protected ListFragment mFrag;
    public static Context context;
    public static boolean networkOK;
    public static boolean serverOK;

    public BaseActivity() {
    }
    

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
    	networkOK = checkNetwork();
    	serverOK = checkConnection();
        setTitle(getResources().getString(R.string.app_name));
        SlidingMenu sm = getSlidingMenu();
        sm.setShadowWidthRes(R.dimen.shadow_width);
        sm.setShadowDrawable(R.drawable.shadow);
        sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        sm.setFadeDegree(0.35f);
        sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add("Home");
    	return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                toggle();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onResume() {
    	networkOK = checkNetwork();
    	serverOK = checkConnection();
    	super.onResume();
    }


	private boolean checkConnection() {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null) {
			return false;
		}
		if (!ni.isConnected()) {
			return false;
		}
		if (!ni.isAvailable()) {
			return false;
		}
		return true;
	}


	private boolean checkNetwork() {
		//TODO
		return true;		
	}
}
