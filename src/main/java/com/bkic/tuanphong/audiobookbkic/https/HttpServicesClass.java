package com.bkic.tuanphong.audiobookbkic.https;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;

public class HttpServicesClass {
    private int responseCode;

    public String message;

    private String response;

    private ArrayList<NameValuePair> ArrayListParams;

    private ArrayList<NameValuePair> headers;

    private String UrlHolder;

    public String getResponse()
    {
        return response;
    }

    public String getErrorMessage()
    {
        return message;
    }

    public int getResponseCode()
    {
        return responseCode;
    }

    public HttpServicesClass(String url)
    {
        HttpServicesClass.this.UrlHolder = url;

        ArrayListParams = new ArrayList<>();

        headers = new ArrayList<>();
    }

    public void AddParam(String name, String value)
    {
        ArrayListParams.add(new BasicNameValuePair(name, value));
    }

    public void AddHeader(String name, String value)
    {
        headers.add(new BasicNameValuePair(name, value));
    }

    public void ExecuteGetRequest() throws Exception
    {
        StringBuilder MixParams = new StringBuilder();

        if(!ArrayListParams.isEmpty())
        {
            MixParams.append("?");

            for(NameValuePair p : ArrayListParams)
            {
                String paramString = p.getName() + "=" + URLEncoder.encode(p.getValue(),"UTF-8");

                if(MixParams.length() > 1)
                {
                    MixParams.append("&").append(paramString);
                }
                else
                {
                    MixParams.append(paramString);
                }
            }
        }

        HttpGet httpGet = new HttpGet(UrlHolder + MixParams);

        for(NameValuePair h : headers)
        {
            httpGet.addHeader(h.getName(), h.getValue());
        }

        executeRequest(httpGet, UrlHolder);
    }

    public void ExecutePostRequest() throws Exception
    {
        HttpPost httpPost = new HttpPost(UrlHolder);
        for(NameValuePair h : headers)
        {
            httpPost.addHeader(h.getName(), h.getValue());
        }

        if(!ArrayListParams.isEmpty())
        {
            httpPost.setEntity(new UrlEncodedFormEntity(ArrayListParams, HTTP.UTF_8));
        }

        executeRequest(httpPost, UrlHolder);
    }

    private void executeRequest(HttpUriRequest request, String url)
    {
        HttpParams httpParameters = new BasicHttpParams();

        HttpConnectionParams.setConnectionTimeout(httpParameters, 10000);

        HttpConnectionParams.setSoTimeout(httpParameters, 10000);

        HttpClient httpClient = new DefaultHttpClient(httpParameters);

        HttpResponse httpResponse;
        try
        {
            httpResponse = httpClient.execute(request);
            responseCode = httpResponse.getStatusLine().getStatusCode();
            message = httpResponse.getStatusLine().getReasonPhrase();

            HttpEntity entity = httpResponse.getEntity();
            if (entity != null)
            {
                InputStream inputStream = entity.getContent();

                response = convertStreamToString(inputStream);

                inputStream.close();
            }
        }
        catch (ClientProtocolException e)
        {
            httpClient.getConnectionManager().shutdown();
            e.printStackTrace();
        }
        catch (IOException e)
        {
            httpClient.getConnectionManager().shutdown();
            e.printStackTrace();
        }
    }

    private String convertStreamToString(InputStream is)
    {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));

        StringBuilder stringBuilder = new StringBuilder();

        String line;
        try
        {
            while ((line = bufferedReader.readLine()) != null)
            {
                stringBuilder.append(line).append("\n");
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                is.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return stringBuilder.toString();
    }

}