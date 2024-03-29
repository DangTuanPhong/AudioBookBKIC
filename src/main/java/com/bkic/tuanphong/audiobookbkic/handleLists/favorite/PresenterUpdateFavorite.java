package com.bkic.tuanphong.audiobookbkic.handleLists.favorite;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bkic.tuanphong.audiobookbkic.R;
import com.bkic.tuanphong.audiobookbkic.player.PlayControl;
import com.bkic.tuanphong.audiobookbkic.utils.Const;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.bkic.tuanphong.audiobookbkic.utils.Const.HttpURL_API;

public class PresenterUpdateFavorite implements PresenterUpdateFavoriteImp {
    private PlayControl playControlActivity;
    private ListFavorite listFavoriteActivity;
    private static final String TAG = "PreUpdateFavorite";

    public PresenterUpdateFavorite(PlayControl playControlActivity) {
        this.playControlActivity = playControlActivity;
    }

    PresenterUpdateFavorite(ListFavorite listFavoriteActivity) {
        this.listFavoriteActivity = listFavoriteActivity;
    }

    //region Method to Update Record
    /*private String FinalJSonObject;
    private String finalResult ;
    private HttpParse httpParse = new HttpParse();
    private void UpdateRecordData(final Activity activity, final HashMap<String, String> ResultHash, final String HttpUrl){

        @SuppressLint("StaticFieldLeak")
        class UpdateRecordDataClass extends AsyncTask<Void,Void,String> {
            @Override
            protected String  doInBackground(Void... voids) {

                finalResult = httpParse.postRequest(ResultHash, HttpUrl);

                return finalResult;
            }
            @Override
            protected void onPostExecute(String httpResponseMsg) {
                super.onPostExecute(httpResponseMsg);
                FinalJSonObject = httpResponseMsg;
                new GetHttpResponseFromHttpWebCall(activity).execute();
            }
        }
        UpdateRecordDataClass updateRecordDataClass = new UpdateRecordDataClass();
        updateRecordDataClass.execute();
    }

    //region Parsing Complete JSON Object.
    @SuppressLint("StaticFieldLeak")
    private class GetHttpResponseFromHttpWebCall extends AsyncTask<Void, Void, Void>
    {
        public Activity activity;

        String jsonAction = null;

        Boolean LogSuccess = false;

        String jsonResult;

        String jsonMessage;

        String jsonLog;

        GetHttpResponseFromHttpWebCall(Activity activity)
        {
            this.activity = activity;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0)
        {
            try
            {
                if(FinalJSonObject != null )
                {
                    JSONObject jsonObject;

                    try {
                        jsonObject = new JSONObject(FinalJSonObject);

                        jsonAction = jsonObject.getString("Action");

                        jsonResult = jsonObject.getString("Result");

                        jsonLog = jsonObject.getString("Log");

                        LogSuccess = jsonObject.getString("Log").equals("Success");

                        jsonMessage = jsonObject.getString("Message");

                    }
                    catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            switch (jsonAction){
                case "addFavourite":
                    if (LogSuccess) playControlActivity.UpdateFavoriteSuccess(jsonMessage.isEmpty()?jsonLog:jsonMessage);
                    else playControlActivity.UpdateFavoriteFailed(jsonMessage.isEmpty()?jsonLog:jsonMessage);
                    break;
                case "removeFavourite":
                    if (LogSuccess) listFavoriteActivity.RemoveFavoriteSuccess(jsonMessage.isEmpty()?jsonLog:jsonMessage);
                    else listFavoriteActivity.RemoveFavoriteFailed(jsonMessage.isEmpty()?jsonLog:jsonMessage);
                    break;
                case "removeAllFavorite":
                    if (LogSuccess) listFavoriteActivity.RemoveAllFavoriteSuccess(jsonMessage.isEmpty()?jsonLog:jsonMessage);
                    else listFavoriteActivity.RemoveAllFavoriteFailed(jsonMessage.isEmpty()?jsonLog:jsonMessage);

            }

        }
    }
 */   //endregion

    private String jsonAction, /*jsonResult,*/ jsonMessage, jsonLog;
    private Boolean LogSuccess;
    private void RequestJSON(final Context context, final HashMap<String,String> hashMap){
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.POST, HttpURL_API, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    jsonAction = jsonObject.getString(Const.JSON_KEY_ACTION);
//                    jsonResult = jsonObject.getString(Const.JSON_KEY_RESULT);
                    jsonMessage = jsonObject.getString(Const.JSON_KEY_MESSAGE);
                    jsonLog = jsonObject.getString(Const.JSON_KEY_LOG);
                    LogSuccess = jsonLog.equals(Const.JSON_KEY_LOG_SUCCESS);
                    switch (jsonAction){
                        case "addFavourite":
                            if (LogSuccess) playControlActivity.UpdateFavoriteSuccess(jsonMessage.isEmpty()?jsonLog:jsonMessage);
                            else playControlActivity.UpdateFavoriteFailed(jsonMessage.isEmpty()?jsonLog:jsonMessage);
                            break;
                        case "removeFavourite":
                            if (LogSuccess) listFavoriteActivity.RemoveFavoriteSuccess(jsonMessage.isEmpty()?jsonLog:jsonMessage);
                            else listFavoriteActivity.RemoveFavoriteFailed(jsonMessage.isEmpty()?jsonLog:jsonMessage);
                            break;
                        case "removeAllFavorite":
                            if (LogSuccess) listFavoriteActivity.RemoveAllFavoriteSuccess(jsonMessage.isEmpty()?jsonLog:jsonMessage);
                            else listFavoriteActivity.RemoveAllFavoriteFailed(jsonMessage.isEmpty()?jsonLog:jsonMessage);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String ms = context.getString(R.string.error_message_not_stable_internet);
                Toast.makeText(context, ms, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onErrorResponse:" +error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                return hashMap;
            }
        };

        requestQueue.add(request);
    }

    //Update Favorite Or History To Server (addHistory, addFavorite)
    @Override
    public void RequestUpdateToServer(String actionRequest, String userId, String chapterId, String insertTime) {
        HashMap<String,String> ResultHash = new HashMap<>();
        String keyPost = "json";
        String valuePost =
                "{" +
                        "\"Action\":\""+actionRequest+"\", " +
                        "\"UserId\":\""+userId+"\", " +
                        "\"BookId\":\""+chapterId+"\", " +
                        "\"InsertTime\":\""+insertTime+"\"" +
                        "}";
        ResultHash.put(keyPost, valuePost);
//        UpdateRecordData(playControlActivity, ResultHash, HttpURL_API);
        RequestJSON(playControlActivity, ResultHash);
    }
    //endregion
    @Override
    public void RequestToRemoveBookById(Context context, String userId, String chapterId) {
        HashMap<String, String> ResultHash = new HashMap<>();
        String keyPost = "json";
        String valuePost =
                "{" +
                        "\"Action\":\"removeFavourite\", " +
                        "\"UserId\":\""+userId+"\", " +
                        "\"BookId\":\""+chapterId+"\"" +
                        "}";
        ResultHash.put(keyPost, valuePost);
//        UpdateRecordData(playControlActivity, ResultHash, HttpURL_API);
        RequestJSON(context, ResultHash);
    }

    @Override
    public void RequestToRemoveAllBook(String userId) {
        HashMap<String, String> ResultHash = new HashMap<>();
        String keyPost = "json";
        String valuePost =
                "{" +
                        "\"Action\":\"removeAllFavorite\", " +
                        "\"UserId\":\""+userId+"\"" +
                        "}";
        ResultHash.put(keyPost, valuePost);
//        UpdateRecordData(playControlActivity, ResultHash, HttpURL_API);
        RequestJSON(playControlActivity, ResultHash);
    }
}
