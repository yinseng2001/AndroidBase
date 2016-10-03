package ApiRequest;

import android.os.AsyncTask;
import android.util.Log;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import Configuration.ApiConfigs;
import Models.Body;
import Models.Response.Response;

/**
 * Created by yinseng on 9/5/16.
 */

public class HttpSignin extends AsyncTask<String, Void, Response> {
//    private String SERVICE_APP_ID = "ZTZiODU0ZjEtMmMzYi00NDgzLWI1MDAtZGZlZmJiNzk0NTFj";
//    private String API_KEY = "Im0yHnq877SiicbPtl0RROaNCj9skcBvaSKfew/C7h0=";


    @Override
    protected Response doInBackground(String... params) {


        String RSA_Cipher = params[0];
        String AES_Cipher = params[1];
        String Device_ID  = params[2];

        Log.e("RSA Cipher Text", RSA_Cipher);
        Log.e("AES Cipher Text", AES_Cipher);
        Log.e("Device ID", Device_ID);


        try {
            // Create and populate a simple object to be used in the request

            Body body = new Body();
            body.setData(AES_Cipher);

            // Set the Content-Type header
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setContentType(new MediaType("application", "json"));
            requestHeaders.add("X-AL-Sign-Key", RSA_Cipher);
            requestHeaders.add("X-AL-Connect-ID", "POSTMAN");
            requestHeaders.add("X-AL-Request-ID", "VPt6ixFFnEYiKfGL/UdYTaCYQv7zSki/8OHyPxvtvXg=");




            HttpEntity<Body> requestEntity = new HttpEntity<Body>(body, requestHeaders);

            // Create a new RestTemplate instance
            RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());

            // Add the Jackson and String message converters
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

            String url = ApiConfigs._Server + ApiConfigs._Login;
//            HttpEntity<Response> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Response.class);
            Response response = restTemplate.postForObject(url, requestEntity, Response.class);


            return response;
        } catch (Exception e) {
            Log.e("MainActivity", e.getMessage(), e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Response response) {
        Log.e("Access-token", response.getResult().getAccess_token());
    }


}