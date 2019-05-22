package com.bkic.lymenglong.audiobookbkic.feedback;

import android.app.DownloadManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import com.bkic.lymenglong.audiobookbkic.R;
import com.bkic.lymenglong.audiobookbkic.checkInternet.ConnectivityReceiver;
import com.bkic.lymenglong.audiobookbkic.customizes.CustomActionBar;
import com.bkic.lymenglong.audiobookbkic.download.DownloadReceiver;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;

public class FeedBack extends AppCompatActivity {
    private String menuTitle;
    private EditText mSubject;
    private EditText mMessage;
    private IntentFilter intentFilter;
    private ConnectivityReceiver receiver;
    private IntentFilter filter;
    private DownloadReceiver downloadReceiver;
    final String  maillab = "audiobookbkic@gmail.com";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        initIntentFilter();
        getDataFromIntent();
        initView();
        mSubject = findViewById(R.id.edt_fb_subject);
        mMessage = findViewById(R.id.edt_content);
        Button btnSend = findViewById(R.id.btn_send_email);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
                onBackPressed();
            }
        });
    }

    private void sendEmail(){

        String subS = mSubject.getText().toString();
        String messS = mMessage.getText().toString();

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL,new String[]{maillab});
        intent.putExtra(Intent.EXTRA_SUBJECT, subS);
        intent.putExtra(Intent.EXTRA_TEXT, messS);
        intent.setType("message/rfc822");
        startActivity(Intent.createChooser(intent, "Chọn ứng dụng để gửi"));

    }
    private void initIntentFilter() {
        intentFilter = new IntentFilter();
        intentFilter.addAction(CONNECTIVITY_ACTION);
        receiver = new ConnectivityReceiver();
        //set filter to only when download is complete and register broadcast receiver
        filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        downloadReceiver = new DownloadReceiver();
    }
    private void getDataFromIntent() {
        menuTitle = getIntent().getStringExtra("MenuTitle");
//        int idHome = getIntent().getIntExtra("idHome", -1);
    }
    private void initView() {
        CustomActionBar actionBar = new CustomActionBar();
        actionBar.eventToolbar(this, menuTitle, false );

    }
}
