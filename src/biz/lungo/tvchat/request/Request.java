package biz.lungo.tvchat.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import biz.lungo.tvchat.response.Response;

public class Request {
    public static final String SERVER_URL = "http://188.231.137.161:8085/TestServer/testing";
    public static final String CHARSET = "UTF-8";
    public static final String CONTENT_TYPE = "application/x-www-form-urlencoded;charset=utf-8";

    public Request() {

    }

    public Response execute(List<NameValuePair> params) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost post = new HttpPost(SERVER_URL);
        post.addHeader("Content-Type", CONTENT_TYPE);
        try {
            post.setEntity(new UrlEncodedFormEntity(params, CHARSET));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        HttpResponse response = null;
        try {
            response = httpclient.execute(post);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpEntity entity = null;
        if (response != null) {
            entity = response.getEntity();
        }
        String responseGetStr = "";
        if (entity != null) {

            InputStream inStream = null;
            try {
                inStream = entity.getContent();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            responseGetStr = convertStreamToString(inStream);
        }
        Response mResponse = new Response();
        mResponse.setData(responseGetStr);
        return mResponse;
    }
    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

}
