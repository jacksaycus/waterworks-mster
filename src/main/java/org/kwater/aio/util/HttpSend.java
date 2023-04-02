package org.kwater.aio.util;

//import io.swagger.models.HttpMethod;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseFactory;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicStatusLine;

import java.io.IOException;

public class HttpSend
{
    public static HttpResponse send(HttpUriRequest request)
    {
        HttpUriRequest httpUriRequest = request;
        HttpResponseFactory factory = new DefaultHttpResponseFactory();
        HttpResponse response = factory.newHttpResponse(
                new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_METHOD_NOT_ALLOWED, null),
                null);

        try
        {
            HttpClient client = HttpClientBuilder.create().build();
            response = client.execute(request);
        }
        catch(HttpHostConnectException e)
        {
            return null;
        }
        catch(ClientProtocolException e)
        {
            return null;
        }
        catch(IOException e)
        {
            return null;
        }

        return response;
    }
}
