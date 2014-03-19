package biz.lungo.tvchat.ui;

import android.os.Bundle;
import android.app.Fragment;
import biz.lungo.tvchat.*;
import biz.lungo.tvchat.ui.menu.MenuFragment;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class StartActivity extends BaseActivity {
	
	private Fragment mContent;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
            mContent = getFragmentManager().getFragment(savedInstanceState, "mContent");
        if (mContent == null)
            mContent = new WelcomeFragment();
                
        setContentView(R.layout.content_frame);
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, mContent)
                .commit();

        setBehindContentView(R.layout.menu_frame);
        getFragmentManager().beginTransaction()
						        	.replace(R.id.menu_frame, new MenuFragment())
						        	.commit();

        getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
    }
    
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getFragmentManager().putFragment(outState, "mContent", mContent);
    }

    public void switchContent(Fragment fragment) {
        mContent = fragment;
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
        getSlidingMenu().showContent();
    }
}
