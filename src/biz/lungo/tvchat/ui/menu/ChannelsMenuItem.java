package biz.lungo.tvchat.ui.menu;


public class ChannelsMenuItem {
    public int channelIcon;
    public int channelIndex;
    public String channelName;
    public boolean visible;

    public ChannelsMenuItem(){
        super();
    }

    public ChannelsMenuItem(int channelIndex, String channelName) {
        super();
        for (int i = 0; i < MenuFragment.visibleChannels.length; i++){
            if (channelName.equals(MenuFragment.visibleChannels[i])){
                visible = true;
                channelIcon = MenuFragment.channelIcons.getResourceId(i, 0);
            }
        }
        this.channelName = channelName;
        this.channelIndex = channelIndex;
    }
}
