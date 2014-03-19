package biz.lungo.tvchat.response;

public class ProgramResponse extends Response {
	
	public ProgramResponse(Response response){
		setData(response.getData());
	}
	
	public String getProgram(){		
		return getData();
	}

}
