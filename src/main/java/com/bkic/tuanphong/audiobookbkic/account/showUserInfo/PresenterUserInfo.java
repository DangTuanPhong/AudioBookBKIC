package com.bkic.tuanphong.audiobookbkic.account.showUserInfo;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bkic.tuanphong.audiobookbkic.R;
import com.bkic.tuanphong.audiobookbkic.account.login.Session;
import com.bkic.tuanphong.audiobookbkic.account.utils.User;
import com.bkic.tuanphong.audiobookbkic.checkInternet.ConnectivityReceiver;
import com.bkic.tuanphong.audiobookbkic.utils.Const;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.bkic.tuanphong.audiobookbkic.utils.Const.HttpURL_API;


public class PresenterUserInfo implements PresenterUserInfoImp {
    private UserInfoActivity userInfoActivity;
    private static final String TAG = "PresenterUserInfo";
    private User userUpdated;

    PresenterUserInfo(UserInfoActivity userInfoActivity) {
        this.userInfoActivity = userInfoActivity;
    }

    //region Method to show current record Current Selected Record
    /*private String FinalJSonObject;
    private String ParseResult;
    private HttpParse httpParse = new HttpParse();
    private void HttpWebCall(final Activity activity, final HashMap<String,String> ResultHash, final String httpHolder){

        @SuppressLint("StaticFieldLeak")
        class HttpWebCallFunction extends AsyncTask<Void,Void,String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(Void... voids) {
                ParseResult = httpParse.postRequest(ResultHash, httpHolder);
                return ParseResult;
            }
            @Override
            protected void onPostExecute(String httpResponseMsg) {
                super.onPostExecute(httpResponseMsg);
                //Storing Complete JSon Object into String Variable.
                FinalJSonObject = httpResponseMsg ;
                //Parsing the Stored JSOn String to GetHttpResponse Method.
                new GetHttpResponseFromHttpWebCall(activity).execute();
            }

        }

        HttpWebCallFunction httpWebCallFunction = new HttpWebCallFunction();

        httpWebCallFunction.execute();
    }

    //region Parsing Complete JSON Object.
    @SuppressLint("StaticFieldLeak")
    private class GetHttpResponseFromHttpWebCall extends AsyncTask<Void, Void, Void>
    {
        public Activity activity;

        String jsonAction = null;

        Boolean LogSuccess = false;

        String ResultJsonObject;

        String message;

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

                        ResultJsonObject = jsonObject.getString("Result");

                        LogSuccess = jsonObject.getString("Log").equals("Success");

                        message = jsonObject.getString("Message");

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
                case "checkPassword":
                    if (LogSuccess) {
                        ShowDialogUpdateUserInfo(activity);
                    } else {
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case "updateUserDetail":
                    if (LogSuccess){
                        userInfoActivity.UpdateDetailSuccess(message);
                        Session session = new Session(activity);
                        session.setUpdateUserDetail(userUpdated);
                        userInfoActivity.DisplayUserDetail();
                    } else {
                        userInfoActivity.UpdateDetailFailed(message);
                    }
                    break;
                case "updatePassword" :
                    if (LogSuccess){
                        userInfoActivity.UpdatePasswordSuccess(message);
                    } else {
                        userInfoActivity.UpdatePasswordFailed(message);
                    }
                    break;
            }
        }
    }
    //endregion
*/    //endregion

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
                        case "checkPassword":
                            if (LogSuccess) {
                                ShowDialogUpdateUserInfo(context);
                            } else {
                                Toast.makeText(context, jsonMessage, Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case "updateUserDetail":
                            if (LogSuccess){
                                userInfoActivity.UpdateDetailSuccess(jsonMessage);
                                Session session = new Session(context);
                                session.setUpdateUserDetail(userUpdated);
                                userInfoActivity.DisplayUserDetail();
                            } else {
                                userInfoActivity.UpdateDetailFailed(jsonMessage);
                            }
                            break;
                        case "updatePassword" :
                            if (LogSuccess){
                                userInfoActivity.UpdatePasswordSuccess(jsonMessage);
                            } else {
                                userInfoActivity.UpdatePasswordFailed(jsonMessage);
                            }
                            break;
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



    @Override
    public void ShowAlertLogoutDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setTitle("Đãng Xuất Tài Khoản");
        builder.setMessage("Bạn có muốn đăng xuất không?");
        builder.setCancelable(false);
        builder.setPositiveButton("Thoát", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                userInfoActivity.LogoutFailed();
            }
        });
        builder.setNegativeButton("Đăng xuất", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                userInfoActivity.LogoutSuccess();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void ShowDialogUpdateUserInfo(Context context){
        final Dialog dialog = new Dialog(context,android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_update_user);
        final AppCompatEditText editTextFirstName = dialog.findViewById(R.id.textInputEditTextFirstName);
        final AppCompatEditText editTextLastName = dialog.findViewById(R.id.textInputEditTextLastName);
        final AppCompatEditText editTextUserName = dialog.findViewById(R.id.textInputEditTextUserName);
        final AppCompatEditText editTextEmail = dialog.findViewById(R.id.textInputEditTextEmail);
        final AppCompatEditText editTextPhone = dialog.findViewById(R.id.textInputEditTextPhoneNumber);
        final AppCompatEditText editTextAddress = dialog.findViewById(R.id.textInputEditTextAddress);
        AppCompatButton appCompatButtonEdit = dialog.findViewById(R.id.appCompatButtonEdit);
        AppCompatButton appCompatButtonCancel = dialog.findViewById(R.id.appCompatButtonCancel);
        editTextFirstName.setText(userInfoActivity.GetCurrentUserDetail().get(0).getFirstName());
        editTextLastName.setText(userInfoActivity.GetCurrentUserDetail().get(0).getLastName());
        editTextUserName.setText(userInfoActivity.GetCurrentUserDetail().get(0).getUsername());
        editTextEmail.setText(userInfoActivity.GetCurrentUserDetail().get(0).getEmail());
        editTextPhone.setText(userInfoActivity.GetCurrentUserDetail().get(0).getPhonenumber());
        editTextAddress.setText(userInfoActivity.GetCurrentUserDetail().get(0).getAddress());
        appCompatButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        appCompatButtonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userUpdated = new User
                        (
                               editTextFirstName.getText().toString().trim(),
                               editTextLastName.getText().toString().trim(),
                               editTextUserName.getText().toString().trim(),
                               editTextEmail.getText().toString().trim(),
                               editTextPhone.getText().toString().trim(),
                               editTextAddress.getText().toString().trim()
                        );
                if(CheckValidityDetail(userUpdated)) {
                    RequestUpdateUserDetail(userUpdated);
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    private Boolean CheckValidityDetail(User userUpdated) {
        if(userUpdated.getFirstName().isEmpty()) {
            Toast.makeText(userInfoActivity, R.string.error_message_first_name, Toast.LENGTH_SHORT).show();
            return false;
        }
        if(userUpdated.getLastName().isEmpty()){
            Toast.makeText(userInfoActivity, R.string.error_message_last_name, Toast.LENGTH_SHORT).show();
            return false;
        }
        if(userUpdated.getUsername().isEmpty()){
            Toast.makeText(userInfoActivity, R.string.error_message_username, Toast.LENGTH_SHORT).show();
            return false;
        }
         if(userUpdated.getEmail().isEmpty()){
            Toast.makeText(userInfoActivity, R.string.error_message_email, Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(userUpdated.getEmail()).matches()){
            Toast.makeText(userInfoActivity, R.string.error_invalid_email, Toast.LENGTH_SHORT).show();
            return false;
        }
        if(userUpdated.getPhonenumber().isEmpty()){
            Toast.makeText(userInfoActivity, R.string.error_message_phone_number, Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!Patterns.PHONE.matcher(userUpdated.getPhonenumber()).matches()){
            Toast.makeText(userInfoActivity, R.string.error_invalid_phone, Toast.LENGTH_SHORT).show();
            return false;
        }
        if(userUpdated.getAddress().isEmpty()){
            Toast.makeText(userInfoActivity, R.string.error_message_address, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void RequestUpdateUserDetail(User user) {
        String keyPost = "json";
        String valuePost =
                "{\"Action\":\"updateUserDetail\", " +
                        "\"UserId\": \""+userInfoActivity.GetCurrentUserDetail().get(0).getId()+"\", " +
                        "\"UserName\": \""+user.getUsername()+"\"," +
                        "\"UserMail\": \""+user.getEmail()+"\", " +
                        "\"UserFirstName\": \""+user.getFirstName()+"\", " +
                        "\"UserLastName\": \""+user.getLastName()+"\", " +
                        "\"UserAddress\": \""+user.getAddress()+"\", " +
                        "\"UserPhone\": \""+user.getPhonenumber()+"\" " +
                "}";
        HashMap<String, String> ResultHash = new HashMap<>();
        ResultHash.put(keyPost, valuePost);
        RequestJSON(userInfoActivity, ResultHash);
//        HttpWebCall(userInfoActivity,ResultHash,Const.HttpURL_API);
    }

    @Override
    public void ShowDialogUpdatePassword(final Context context) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_update_password);
        final EditText editTextOldPassword = dialog.findViewById(R.id.editTextOldPassword);
        final EditText editTextNewPassword = dialog.findViewById(R.id.editTextNewPassword);
        final EditText editTextConfirmPassword = dialog.findViewById(R.id.editTextConfirmPassword);
        Button buttonCancel = dialog.findViewById(R.id.buttonCancel);
        Button buttonUpdate = dialog.findViewById(R.id.buttonUpdate);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editTextOldPassword.getText().toString().isEmpty()) {
                    Toast.makeText(context, R.string.message_please_complete_old_password, Toast.LENGTH_SHORT).show();
                    return;
                }
                if(editTextNewPassword.getText().toString().isEmpty()) {
                    Toast.makeText(context, R.string.message_please_complete_new_password, Toast.LENGTH_SHORT).show();
                    return;
                }
                if(editTextConfirmPassword.getText().toString().isEmpty()) {
                    Toast.makeText(context, R.string.message_please_complete_confirm_password, Toast.LENGTH_SHORT).show();
                    return;
                }
                if(editTextNewPassword.getText().toString().equals(editTextConfirmPassword.getText().toString())){
                    if (ConnectivityReceiver.isConnected())
                        RequestUpdateUserPassword(editTextOldPassword, editTextNewPassword);
                    else
                        Toast.makeText(context, context.getString(R.string.message_internet_not_connected), Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    Toast.makeText(context, R.string.error_message_confirm_password_not_match, Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }

    private void RequestUpdateUserPassword(EditText editTextOldPassword, EditText editTextNewPassword) {
        String keyPost = "json";
        String valuePost =
                "{" +
                        "\"Action\":\"updatePassword\", " +
                        "\"UserId\":\""+userInfoActivity.GetCurrentUserDetail().get(0).getId()+"\", " +
                        "\"UserOldPassword\":\""+editTextOldPassword.getText().toString()+"\", " +
                        "\"UserNewPassword\":\""+editTextNewPassword.getText().toString()+"\" " +
                "}";
        HashMap<String,String> ResultHash = new HashMap<>();
        ResultHash.put(keyPost, valuePost);
//        HttpWebCall(userInfoActivity,ResultHash,Const.HttpURL_API);
        RequestJSON(userInfoActivity, ResultHash);
    }

    @Override
    public void ShowDialogConfirmPassword(final Context context){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirm_password);
        final EditText editTextConfirmPassword = dialog.findViewById(R.id.editTextOldPassword);
        Button buttonUpdate = dialog.findViewById(R.id.buttonUpdate);
        Button buttonCancel = dialog.findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!editTextConfirmPassword.getText().toString().isEmpty())
                    if (ConnectivityReceiver.isConnected())
                        RequestConfirmPassword(editTextConfirmPassword);
                    else Toast.makeText(context, context.getString(R.string.message_internet_not_connected), Toast.LENGTH_SHORT).show();
                else {
                    Toast.makeText(context, R.string.message_please_complete_password, Toast.LENGTH_SHORT).show();
                    return;
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void RequestConfirmPassword(EditText editTextConfirmPassword) {
        String keyPost = "json";
        String valuePost =
                "{\"Action\":\"checkPassword\"," +
                        "\"UserId\":\""+userInfoActivity.GetCurrentUserDetail().get(0).getId()+"\"," +
                        "\"UserPassword\":\""+editTextConfirmPassword.getText().toString()+"\"" +
                "}";
        HashMap<String,String> ResultHash = new HashMap<>();
        ResultHash.put(keyPost, valuePost);
//        HttpWebCall(userInfoActivity,ResultHash, Const.HttpURL_API);
        RequestJSON(userInfoActivity, ResultHash);
    }


}
