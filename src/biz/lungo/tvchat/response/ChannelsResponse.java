package biz.lungo.tvchat.response;

public class ChannelsResponse extends Response {

	public ChannelsResponse(Response response) {
		setData(response.getData());
	}

	public String[] getChannels() {
		String result = getData();
		result = result.substring(1, result.length() - 2);
		return result.split(", ");
	}

}
