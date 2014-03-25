package biz.lungo.tvchat.response;

public class TimeResponse extends Response {

    public TimeResponse(Response response){
        setData(response.getData());
    }

    public long getTime(){
        String timeString = getData();
        String timeSubString = timeString.substring(0, timeString.length() - 1);
        return Long.parseLong(timeSubString);
    }
}
