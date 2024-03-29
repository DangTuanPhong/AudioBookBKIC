package com.bkic.tuanphong.audiobookbkic.handleLists.listChapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bkic.tuanphong.audiobookbkic.checkInternet.ConnectivityReceiver;
import com.bkic.tuanphong.audiobookbkic.checkInternet.MyApplication;
import com.bkic.tuanphong.audiobookbkic.customizes.CustomActionBar;
import com.bkic.tuanphong.audiobookbkic.database.DBHelper;
import com.bkic.tuanphong.audiobookbkic.download.DownloadReceiver;
import com.bkic.tuanphong.audiobookbkic.handleLists.adapters.ChapterAdapter;
import com.bkic.tuanphong.audiobookbkic.handleLists.utils.Book;
import com.bkic.tuanphong.audiobookbkic.handleLists.utils.Chapter;
import com.bkic.tuanphong.audiobookbkic.handleLists.utils.PresenterShowList;
import com.bkic.tuanphong.audiobookbkic.R;
import com.bkic.tuanphong.audiobookbkic.utils.Const;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_DRAGGING;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_SETTLING;
import static com.bkic.tuanphong.audiobookbkic.utils.Const.DB_NAME;
import static com.bkic.tuanphong.audiobookbkic.utils.Const.DB_VERSION;
import static com.bkic.tuanphong.audiobookbkic.utils.Const.HttpURL_API;

public class ListChapter extends AppCompatActivity
        implements ListChapterImp, ConnectivityReceiver.ConnectivityReceiverListener, DownloadReceiver.DownloadReceiverListener{
    private static final String TAG = "ListChapter";
    PresenterShowList presenterShowList = new PresenterShowList(this);
    private RecyclerView listChapter;
    private ChapterAdapter chapterAdapter;
    private Activity activity = ListChapter.this;
    private DBHelper dbHelper;
    private ArrayList<Chapter> list;
    private ProgressBar pBarCenter, pBarBottom;
    private View imRefresh;
    private Book bookIntent;
    private int mPAGE = 1; // Default page load page 1 at the first time
    private int mLastPAGE;
    private Boolean isFinalPage = false;
    private boolean isLoadingData = false;
    private Toast mToast = null;
    private boolean isShowingToast = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list);
        initIntentFilter();
        initDataFromIntent();
        initView();
        setTitle(bookIntent.getTitle());
        initDatabase();
        initObject();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here
        if(requestCode == Const.REQUEST_CODE_BACK_HOME)
            if (data != null)
                if (data.getBooleanExtra(Const.STRING_BACK_HOME, false)) {
                    Intent intent = new Intent();
                    intent.putExtra(Const.STRING_BACK_HOME, true);
                    setResult(Const.REQUEST_CODE_BACK_HOME, intent);
                    finish();//finishing activity
                }
    }

    //region BroadCasting
    //connectionReceiver
    private IntentFilter intentFilter;
    private ConnectivityReceiver receiver;
    //downloadReceiver
    private IntentFilter filter;
    private DownloadReceiver downloadReceiver;

    private void initIntentFilter() {
        intentFilter = new IntentFilter();
        intentFilter.addAction(CONNECTIVITY_ACTION);
        receiver = new ConnectivityReceiver();
        //set filter to only when download is complete and register broadcast receiver
        filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        downloadReceiver = new DownloadReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register receiver
        registerReceiver(receiver, intentFilter);
        registerReceiver(downloadReceiver, filter);
        // register status listener
        MyApplication.getInstance().setConnectivityListener(this);
        MyApplication.getInstance().setDownloadListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //unregister receiver
        unregisterReceiver(receiver);
        unregisterReceiver(downloadReceiver);
        //Cancel Toast Notification
        if(isShowingToast&&mToast!=null) mToast.cancel();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }

    @Override
    public void onDownloadCompleted(long downloadId) {

    }
    //endregion

    /**
     * Lấy dữ liệu thông qua intent
     */
    private void initDataFromIntent() {
        bookIntent = new Book
                (
                        getIntent().getIntExtra("BookId", -1),
                        getIntent().getStringExtra("BookTitle"),
                        getIntent().getStringExtra("BookImage"),
                        getIntent().getIntExtra("BookLength", 0),
                        getIntent().getIntExtra("CategoryId", -1)
                );
    }

    /**
     * Khai báo các view và khởi tạo giá trị
     */
    private void initView() {
        CustomActionBar actionBar = new CustomActionBar();
        actionBar.eventToolbar(this, bookIntent.getTitle(), true);
        listChapter = findViewById(R.id.listView);
        pBarCenter = findViewById(R.id.progressBar);
        pBarBottom = findViewById(R.id.pb_bottom);
        imRefresh = findViewById(R.id.imRefresh);
        ViewCompat.setImportantForAccessibility(getWindow().findViewById(R.id.tvToolbar), ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO);
    }

    private void initDatabase() {
        dbHelper = new DBHelper(this,DB_NAME ,null,DB_VERSION);
    }

    private void initObject() {
        //Update BookDetail
        //set chapterAdapter to list view
        SetAdapterToListView();
        //update list
        GetCursorData();
        //region get data from json parsing
        if(list.isEmpty()&&ConnectivityReceiver.isConnected()){
            SetRequestUpdateBookDetail();
            RequestLoadList();
        } else {
            pBarCenter.setVisibility(View.GONE);
        }
        //endregion

        //To refresh list when click button refresh
        imRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLoadingData)
                    if(ConnectivityReceiver.isConnected()) {
                        SetRequestUpdateBookDetail();
                        RefreshChapterTable();
                        isFinalPage = false;
                        mPAGE = 1;
                        RequestLoadList();
                    } else
                        Toast.makeText(activity, getString(R.string.message_internet_not_connected), Toast.LENGTH_SHORT).show();
                else{
                    Toast mToast = Toast.makeText(activity, getString(R.string.loading_data), Toast.LENGTH_SHORT);
                    mToast.show();
                }
            }
        });
    }

/*    private  Thread thread = new Thread(){
        @Override
        public void run() {
            try {
                Thread.sleep(2000); // 3500 millisecond As I am using LENGTH_LONG in Toast
                //Do something
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };*/
    private void RefreshChapterTable() {
        String DELETE_DATA =
                "DELETE FROM chapter "+
                "WHERE BookId = '"+bookIntent.getId()+"'";
        dbHelper.QueryData(DELETE_DATA);
        dbHelper.close();
    }

    private void SetRequestUpdateBookDetail() {
        HashMap<String, String> ResultHash = new HashMap<>();
        String keyPost = "json";
        String valuePost =
                "{" +
                        "\"Action\":\"getBookDetail\", " +
                        "\"BookId\":"+ bookIntent.getId() +"" +
                "}";
        ResultHash.put(keyPost,valuePost);
        presenterShowList.GetSelectedResponse(activity, ResultHash, HttpURL_API);
    }

    private void RequestLoadList() {
        isLoadingData = true;
        pBarBottom.setVisibility(View.VISIBLE);
        HashMap<String, String> ResultHash = new HashMap<>();
        int BookId = bookIntent.getId();
        String keyPost = "json";
        String postValue =
                "{" +
                        "\"Action\":\"getChapterList\", " +
                        "\"BookId\":\""+BookId+"\", " +
                        "\"Page\":\""+ mPAGE +"\"" +
                        "}";
        ResultHash.put(keyPost,postValue);
        presenterShowList.GetSelectedResponse(activity, ResultHash, HttpURL_API);
    }


    private void SetAdapterToListView() {
        list = new ArrayList<>();
        chapterAdapter = new ChapterAdapter(ListChapter.this, list);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        listChapter.setLayoutManager(mLinearLayoutManager);
        listChapter.setAdapter(chapterAdapter);
        listChapter.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.d(TAG, "onScrollStateChanged: " +
                        "\nnewState = "+newState+"" +
                        "\nSCROLL_STATE_IDLE = "+SCROLL_STATE_IDLE+" " +
                        "\nSCROLL_STATE_DRAGGING ="+SCROLL_STATE_DRAGGING+" " +
                        "\nSCROLL_STATE_SETTLING = "+SCROLL_STATE_SETTLING+""
                );
                if (!isLoadingData) {
                    if(ConnectivityReceiver.isConnected()) {
                        if (newState == SCROLL_STATE_DRAGGING && !isFinalPage) {
                            mPAGE++;
                            Cursor cursor = dbHelper.GetData
                                    (
                                            "SELECT MAX(Page) AS LastPage " +
                                                    "FROM chapter " +
                                                    "WHERE BookId = '" + bookIntent.getId() + "';"
                                    );
                            if (cursor.moveToFirst()) mLastPAGE = cursor.getInt(0);
                            if (mPAGE < mLastPAGE) mPAGE = mLastPAGE;
                            RequestLoadList();
                        }
                    }/*else Toast.makeText(activity, getString(R.string.message_internet_not_connected), Toast.LENGTH_SHORT).show();*/
                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.d(TAG, "onScrolled: \ndx ="+dx+" \ndy = "+dy+"");
            }
        });
    }

    //region Method to get data for database
    private void GetCursorData() {
        list.clear();
        Cursor cursor = dbHelper.GetData(
                "SELECT ChapterId, ChapterTitle, ChapterUrl, ChapterLength, BookId " +
                        "FROM chapter " +
                        "WHERE BookId = '"+ bookIntent.getId() +"' " +
                        "ORDER BY MetaInsertTime ASC"); //ORDER BY ChapterTitle COLLATE NOCASE to ignore case
        while (cursor.moveToNext()){
            Chapter chapterModel = new Chapter();
            chapterModel.setId(cursor.getInt(0));
            chapterModel.setTitle(cursor.getString(1));
            chapterModel.setFileUrl(cursor.getString(2));
            chapterModel.setLength(cursor.getInt(3));
            chapterModel.setBookId(cursor.getInt(4));
            list.add(chapterModel);
        }
        cursor.close();
        chapterAdapter.notifyDataSetChanged();
        dbHelper.close();
        pBarCenter.setVisibility(View.GONE);
        isLoadingData = false;
        pBarBottom.setVisibility(View.GONE);
    }
    //endregion

    @Override
    public void SetUpdateBookDetail(JSONObject jsonObject) throws JSONException {
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpledateformat =
                new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String insertTime = simpledateformat.format(calendar.getTime());
        Book bookModel = new Book();
        bookModel.setId(Integer.parseInt(jsonObject.getString("BookId")));
        bookModel.setTitle(jsonObject.getString("BookTitle"));
        bookModel.setAuthor(jsonObject.getString("Author"));
        bookModel.setPublishDate(jsonObject.getString("PublishDate"));
        bookModel.setUrlImage(jsonObject.getString("BookImage"));
        bookModel.setContent(jsonObject.getString("BookContent"));
        bookModel.setLength(Integer.parseInt(jsonObject.getString("BookLength")));
        bookModel.setFileUrl(jsonObject.getString("BookURL"));
        bookModel.setCategoryList(jsonObject.getString("CategoryList"));
        bookModel.setNumOfChapter(Integer.parseInt(jsonObject.getString("NumOfChapter")));
        Cursor cursor = dbHelper.GetData("SELECT BookId FROM book WHERE BookId = '"+bookModel.getId()+"'");
        cursor.moveToFirst();
        if(cursor.getCount()==0){
            String INSERT_DATA =
                    "INSERT INTO book(" +
                            "BookId, BookTitle, BookAuthor, BookPublishDate, " +
                            "BookImage, BookContent, BookLength, BookURL, CategoryId, " +
                            "NumOfChapter, BookStatus, Page, InsertTime" +
                            ") " +
                            "VALUES" +
                            "(" +
                            "'"+bookModel.getId()+"', " +
                            "'"+bookModel.getTitle()+"', " +
                            "'"+bookModel.getAuthor()+"', " +
                            "'"+bookModel.getPublishDate()+"', " +
                            "'"+bookModel.getUrlImage()+"', " +
                            "'"+bookModel.getContent()+"', " +
                            "'"+bookModel.getLength()+"', " +
                            "'"+bookModel.getFileUrl()+"', " +
                            "'"+bookModel.getCategoryId()+"', " +
                            "'"+bookModel.getNumOfChapter()+"', " +
                            "'"+0+"', " + //book Status default = 0
                            "'null', " +
                            "'"+insertTime+"'"+ //page
                            ");";
            dbHelper.QueryData(INSERT_DATA);
        } else{
            String UPDATE_DATA =
                        "UPDATE book SET " +
                                "BookTitle = '"+bookModel.getTitle()+"', " +
                                "BookAuthor = '"+bookModel.getAuthor()+"', " +
                                "BookPublishDate = '"+bookModel.getPublishDate()+"', " +
                                "BookImage = '"+bookModel.getUrlImage()+"', " +
                                "BookContent = '"+bookModel.getContent()+"', " +
                                "BookLength = '"+bookModel.getLength()+"', " +
                                "BookURL = '"+bookModel.getFileUrl()+"', " +
//                            "CategoryId = '"+bookModel.getCategoryId()+"', " +
                                "NumOfChapter = '"+bookModel.getNumOfChapter()+"', " +
                                "InsertTime = '"+insertTime+"'" +
                                "WHERE " +
                                "BookId = '"+bookModel.getId()+"'" +
                                ";";
                dbHelper.QueryData(UPDATE_DATA);
            }
        dbHelper.close();
    }

    @Override
    public void SetTableSelectedData(JSONArray jsonArrayChapter) {
        for (int i = 0; i < jsonArrayChapter.length(); i++) {
            try {
                JSONObject jsonObject = jsonArrayChapter.getJSONObject(i);
                Chapter chapterModel = new Chapter();
                String ChapterId = jsonObject.getString("ChapterId");
                if(!ChapterId.toLowerCase().equals("null")) {
                    chapterModel.setId(Integer.parseInt(ChapterId));
                    chapterModel.setTitle(jsonObject.getString("ChapterTitle"));
                    chapterModel.setFileUrl(jsonObject.getString("ChapterURL"));
                    try {
                    chapterModel.setLength(Integer.parseInt(jsonObject.getString("ChapterLength")));
                    }catch (NumberFormatException e){
                        Log.d(TAG, "NumberFormatException: " + e);
                    }
                    int BookId = bookIntent.getId();
                    Calendar calendar = Calendar.getInstance();
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat simpledateformat =
                            new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    String insertTime = simpledateformat.format(calendar.getTime());
                    String metaInsertTime = String.valueOf(calendar.getTimeInMillis());
                    Log.e(TAG, "SetTableSelectedData: "+ metaInsertTime);
                    String INSERT_DATA;
                    try {
                        INSERT_DATA =
                                "INSERT INTO chapter" +
                                        "(" +
                                        "ChapterId, " +
                                        "ChapterTitle, " +
                                        "ChapterUrl, " +
                                        "ChapterLength, " +
                                        "BookId, " +
                                        "Page, " +
                                        "InsertTime, " +
                                        "MetaInsertTime" +
                                        ") " +
                                        "VALUES" +
                                        "(" +
                                        "'"+chapterModel.getId()+"', " +
                                        "'"+chapterModel.getTitle()+"', " +
                                        "'"+chapterModel.getFileUrl() +"', " +
                                        "'"+chapterModel.getLength() +"', " +
                                        "'"+BookId+"', " + //BookId
//                                        "'"+0+"', " + // Status Chapter is equal 0 which mean chapter have not downloaded yet
                                        "'"+mPAGE+"', " +
                                        "'"+insertTime+"', " +
                                        "'"+metaInsertTime+"'"+
                                        ")";
                        dbHelper.QueryData(INSERT_DATA);
                    } catch (Exception e) {
                        String UPDATE_DATA = "UPDATE chapter SET " +
                                "ChapterTitle = '"+chapterModel.getTitle()+"', " +
                                "ChapterUrl = '"+chapterModel.getFileUrl()+"', " +
                                "ChapterLength = '"+chapterModel.getLength()+"', " +
                                "BookId = '"+BookId+"' , " +//BookId
                                "Page = '"+mPAGE+"', " +
                                "InsertTime = '"+insertTime+"' " +
                                "WHERE ChapterId = '"+chapterModel.getId()+"'";
                        dbHelper.QueryData(UPDATE_DATA);
                    }
                }
            } catch (JSONException ignored) {
                Log.e(TAG, "onPostExecute: " + jsonArrayChapter);
            }
        }
        if(jsonArrayChapter.length()<10) isFinalPage = true;
    }

    //Now I Don't Use It Any More
    @Override
    public void SetTableSelectedData(JSONObject jsonObject) throws JSONException {
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpledateformat =
                new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String insertTime = simpledateformat.format(calendar.getTime());
        Chapter chapterModel = new Chapter();
        chapterModel.setId(Integer.parseInt(jsonObject.getString("ChapterId")));
        chapterModel.setTitle(jsonObject.getString("ChapterTitle"));
        chapterModel.setFileUrl(jsonObject.getString("ChapterURL"));
        chapterModel.setLength(Integer.parseInt(jsonObject.getString("ChapterLength")));
        int BookId = bookIntent.getId();
        String INSERT_DATA;
        try {
            INSERT_DATA =
                    "INSERT INTO chapter(" +
                            "ChapterId, ChapterTitle, ChapterUrl, ChapterLength, BookId, " +
                            "ChapterStatus, Page, InsertTime" +
                            ") " +
                            "VALUES" +
                            "(" +
                            "'"+chapterModel.getId()+"', " +
                            "'"+chapterModel.getTitle()+"', " +
                            "'"+chapterModel.getFileUrl() +"', " +
                            "'"+chapterModel.getLength() +"', " +
                            "'"+BookId+"', " + //BookId
                            "'"+0+"', " + // Status Chapter is equal 0 which mean chapter have not downloaded yet
                            "'"+mPAGE+"', " +
                            "'"+insertTime+"'"+
                            ")";
            dbHelper.QueryData(INSERT_DATA);
        } catch (Exception e) {
            String UPDATE_DATA = "UPDATE chapter SET " +
                    "ChapterTitle = '"+chapterModel.getTitle()+"', " +
                    "ChapterUrl = '"+chapterModel.getFileUrl()+"', " +
                    "ChapterLength = '"+chapterModel.getLength()+"', " +
                    "BookId = '"+BookId+"', " +
                    "InsertTime = '"+insertTime+"' " + //BookId
                    "WHERE ChapterId = '"+chapterModel.getId()+"'";
            dbHelper.QueryData(UPDATE_DATA);
        }
    }

    @Override
    public void ShowListFromSelected() {
        GetCursorData();
        Log.d(TAG, "onPostExecute: "+ bookIntent.getTitle());
    }

    @Override
    public void LoadListDataFailed(String jsonMessage) {
        mPAGE--;
        isFinalPage = true;
        String ms = getString(R.string.message_no_more_chapter);
        isShowingToast = isShowingToastNotification(ms);
//        isShowingToast = isShowingToastNotification(jsonMessage);
    }

    private boolean isShowingToastNotification(String jsonMessage){
        mToast = Toast.makeText(activity, jsonMessage, Toast.LENGTH_SHORT);
        mToast.show();
        return true;
    }
}
