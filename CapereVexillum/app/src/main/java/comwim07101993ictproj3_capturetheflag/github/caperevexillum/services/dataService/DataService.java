package comwim07101993ictproj3_capturetheflag.github.caperevexillum.services.dataService;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import comwim07101993ictproj3_capturetheflag.github.caperevexillum.helpers.quiz.QuizOld;
import comwim07101993ictproj3_capturetheflag.github.caperevexillum.services.Variables;

/**
 * Created by georg on 16/11/2017.
 */
@Deprecated
public class DataService implements IDataService {
    private static final String SERVER_IP = "10.0.2.2";
    //Virtual devices connect to local host through 10.0.2.2
    private static final String API_URL = "http://33bb0bc5.ngrok.io/api/v1/";
    private static final String GET_ALL = "GET/Vragen";
    private static final String GET_RANDOM = "GET/RandomVragen/";

    private RequestQueue queue;

    public DataService(Context context) {
        queue = Volley.newRequestQueue(context);
    }

    @Override
    public void getRandomQuestions(final Response.Listener<List<QuizOld>> listener, final Response.ErrorListener errorListener, int amount, int category) {


        createRequestAndAddToQueue(listener, errorListener, Variables.REST_API+GET_RANDOM+category+"/"+ amount);
    }

    private void createRequestAndAddToQueue(final Response.Listener<List<QuizOld>> listener,
                                            final Response.ErrorListener errorListener,
                                            String url) {
        createRequestAndAddToQueue(listener, errorListener, url, Request.Method.GET, null);
    }

    private void createRequestAndAddToQueue(final Response.Listener<List<QuizOld>> listener,
                                            final Response.ErrorListener errorListener,
                                            String url, int method, Map<String, Object> data) {
        // create response listener that handles the response and notifies listener
        final Response.Listener<String> originalResponseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    // convert response to sheet music and notify listener

                    Type collectionType = new TypeToken<List<Question>>() {
                    }.getType();
                    //String nlResponse= response.replace("Question","vraag");
                    //nlResponse=nlResponse.replace("Answers","totaalAntwoorden");
                    String nlResponse="[{'vraag':'Dit is vraag 1','fouteAntwoorden':['fout','fout','fout'],juisteAntwoord:'juist'},{'vraag':'Dit is vraag 3','fouteAntwoorden':['fout','fout','fout'],juisteAntwoord:'juist'},{'vraag':'Dit is vraag 2','fouteAntwoorden':['fout','fout','fout'],juisteAntwoord:'juist'}]";
                    List<Question> questions = new Gson().fromJson(response,collectionType);
                    List<QuizOld> boxSearchCollection=new ArrayList<QuizOld>();
                    for(Question question : questions){
                        ArrayList<String> fouteVragen=new ArrayList<>();
                        String juisteVraag=null;
                        for(Answers answer : question.Answers){
                            if(answer.Correct.equals("1")){
                                juisteVraag=answer.Answer;}
                            else{
                                fouteVragen.add(answer.Answer);}
                        }

                        QuizOld vraag = new QuizOld(question.Question,fouteVragen,juisteVraag);
                        boxSearchCollection.add(vraag);
                    }

                    //List<QuizOld> boxSearchCollection = new Gson().fromJson(response, collectionType);


                    listener.onResponse(boxSearchCollection);
                } catch (Exception e) {
                    // if an error occurred, notify listener
                    errorListener.onErrorResponse(new VolleyError(e));
                }
            }
        };

        // create new hashmap with all the data as json to send with the request
        final Map<String, String> params = new HashMap<>();
        if (data != null) {
            Gson gson = new Gson();
            for (String key : data.keySet()) {
                params.put(key, gson.toJson(data.get(key)));
            }
        }

        // create new request
        StringRequest request = new StringRequest(method, url,
                originalResponseListener, errorListener) {
            // override the getParams method to pass the params that should be sent with the request
            @Override
            protected Map<String, String> getParams() {
                // return the created params for the request
                return params;
            }
        };

        // add request to queue
        queue.add(request);
    }

}

