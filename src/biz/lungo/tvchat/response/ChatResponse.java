package biz.lungo.tvchat.response;

public class ChatResponse extends Response {
	
	public ChatResponse(Response response){
		setData(response.getData());
	}
	
	public String getMessages(){		
		return getData();
	}
}
