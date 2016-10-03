package ApiRequest;

import android.os.AsyncTask;
import android.util.Log;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import Models.Greeting;

/**
 * Created by yinseng on 9/2/16.
 */

public class HttpRequestTask extends AsyncTask<Void, Void, Greeting> {
    @Override
    protected Greeting doInBackground(Void... params) {

        try {
            Log.e("rest","HttpRequestTask");
            final String url = "http://rest-service.guides.spring.io/greeting";
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            Greeting greeting = restTemplate.getForObject(url, Greeting.class);
            return greeting;
        } catch (Exception e) {
            Log.e("MainActivity", e.getMessage(), e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Greeting greeting) {
//        TextView greetingIdText = (TextView) findViewById(R.id.id_value);
//        TextView greetingContentText = (TextView) findViewById(R.id.content_value);
//        greetingIdText.setText(greeting.getId());
//        greetingContentText.setText(greeting.getContent());
        Log.e("rest",greeting.getContent());
    }

}