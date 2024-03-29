package com.bkic.tuanphong.audiobookbkic.handleLists.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bkic.tuanphong.audiobookbkic.R;
import com.bkic.tuanphong.audiobookbkic.database.DBHelper;
import com.bkic.tuanphong.audiobookbkic.handleLists.listOffline.ListOfflineChapter;
import com.bkic.tuanphong.audiobookbkic.handleLists.utils.Book;
import com.bkic.tuanphong.audiobookbkic.overrideTalkBack.PresenterOverrideTalkBack;
import com.bkic.tuanphong.audiobookbkic.utils.Const;

import java.util.ArrayList;


public class BookOfflineAdapter extends RecyclerView.Adapter {
    private ArrayList<Book> books;
    private Activity activity;
    private Book bookModel;
    private int adapterPosition;
    /*
private int getIdChapter;
private String getTitleChapter, getContentChapter, getfileUrlChapter;
*/

    public BookOfflineAdapter(Activity activity, ArrayList<Book> books) {
        this.books = books;
        this.activity = activity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        return new ChapterHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ChapterHolder) {
            ChapterHolder chapterHolder = (ChapterHolder) holder;
            chapterHolder.name.setText(books.get(position).getTitle());
            String sTitle = books.get(position).getTitle();
            String sAuthor = books.get(position).getAuthor();
            String cdLength = null;
            String sContentDescription;
            int iLength = books.get(position).getLength()*1000; // response in sec convert to millisecond
            //check book author
            if (sAuthor != null&& !sAuthor.toLowerCase().trim().equals("null")
                    && !sAuthor.toLowerCase().trim().equals("undefined")) {
                //check book length
                if(iLength!=0) {
                    String sLength = chapterHolder.presenterOverrideTalkBack.getConvertedDuration(iLength);
                    chapterHolder.sLength.setVisibility(View.VISIBLE);
                    chapterHolder.sLength.setText(String.valueOf(sLength));
                    cdLength = chapterHolder.presenterOverrideTalkBack.DurationContentDescription(iLength);
                }else chapterHolder.sLength.setVisibility(View.GONE);

                chapterHolder.subTitle.setText(sAuthor);
                chapterHolder.subTitle.setVisibility(View.VISIBLE);

                //fix content description for item list
                if(cdLength != null) sContentDescription = activity.getResources().getString(
                        R.string.item_book_cd_title_author_length, sTitle, sAuthor,cdLength);
                else sContentDescription = activity.getResources().getString(
                        R.string.item_book_cd_title_author, sTitle, sAuthor);
            } else {
                chapterHolder.subTitle.setVisibility(View.GONE);
                //fix content description for item list
                sContentDescription = activity.getResources().getString(
                        R.string.item_book_cd_title_only,sTitle
                );
            }
            chapterHolder.layoutItem.setContentDescription(sContentDescription);
        }

    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    class ChapterHolder extends RecyclerView.ViewHolder implements View.OnClickListener ,View.OnLongClickListener{

        private TextView name;
//        private ImageView imgNext;
        private PresenterOverrideTalkBack presenterOverrideTalkBack = new PresenterOverrideTalkBack(activity);
        private View layoutItem;
        private TextView subTitle;
        private TextView sLength;


        ChapterHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.title_item);
            layoutItem = itemView.findViewById(R.id.layout_item_list);
//            imgNext = itemView.findViewById(R.id.imgNext);
            subTitle = itemView.findViewById(R.id.sub_title_item);
            sLength = itemView.findViewById(R.id.item_length);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            //Do allow talk back to read content when user touch screen
            presenterOverrideTalkBack.DisableTouchForTalkBack(itemView);
            presenterOverrideTalkBack.DisableTouchForTalkBack(itemView.findViewById(R.id.title_item));
            presenterOverrideTalkBack.DisableTouchForTalkBack(itemView.findViewById(R.id.imgNext));
        }

        @Override
        public boolean onLongClick(View v) {
            bookModel = new Book(
                    books.get(getAdapterPosition()).getId(),
                    books.get(getAdapterPosition()).getTitle(),
                    books.get(getAdapterPosition()).getUrlImage(),
                    books.get(getAdapterPosition()).getLength(),
                    books.get(getAdapterPosition()).getAuthor()
            );
            adapterPosition = getAdapterPosition();
            showAlertDialog();
            return true;
        }

        @Override
        public void onClick(View view) {
            if(view == itemView) {
                Intent intent = new Intent(activity, ListOfflineChapter.class);
                intent.putExtra("BookId", books.get(getAdapterPosition()).getId());
                intent.putExtra("BookTitle", books.get(getAdapterPosition()).getTitle());
                intent.putExtra("BookImage", books.get(getAdapterPosition()).getUrlImage());
                intent.putExtra("BookLength", books.get(getAdapterPosition()).getLength());
                intent.putExtra("CategoryId", books.get(getAdapterPosition()).getCategoryId());
                activity.startActivityForResult(intent, Const.REQUEST_CODE_BACK_HOME);
            }
        }
    }

    private void showAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//        builder.setTitle("Chọn Dạng Sách");
        builder.setMessage("Bạn muốn xóa khỏi danh sách không?");
        builder.setCancelable(false);
        builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                books.remove(adapterPosition);
                notifyDataSetChanged();
                Toast.makeText(activity, bookModel.getTitle()+" Đã Xóa", Toast.LENGTH_SHORT).show();
                RemoveOfflineData(bookModel.getId());
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(activity, "Đã Kích Không", Toast.LENGTH_SHORT).show();
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void RemoveOfflineData(int bookId){
        UpdateBookStatus(bookId);
        UpdateChapterStatus(bookId);
        DeleteDownloadStatusByBookId(bookId);
    }

    private void UpdateChapterStatus(int bookId) {
        DBHelper dbHelper = new DBHelper(activity, Const.DB_NAME, null, Const.DB_VERSION);
        Cursor cursor = dbHelper.GetData(
                "SELECT ChapterId " +
                        "FROM chapter " +
                        "WHERE BookId = '"+bookId+"' AND ChapterStatus = '1'" +
                        ";"
        );
        if(cursor.moveToFirst()){
            do{
                if (cursor.getCount() != 0) {
                    dbHelper.QueryData(
                            "UPDATE chapter " +
                                    "SET ChapterStatus = '0' " +
                                    "WHERE ChapterId = '"+cursor.getInt(0)+"'" +
                                    ";"
                    );
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        dbHelper.close();
    }

    private void DeleteDownloadStatusByBookId(int bookId) {
        DBHelper dbHelper = new DBHelper(activity, Const.DB_NAME, null, Const.DB_VERSION);
        dbHelper.QueryData("DELETE FROM downloadStatus WHERE BookId = '"+bookId+"'");
        dbHelper.close();
    }

    private void UpdateBookStatus(int bookId) {
        DBHelper dbHelper = new DBHelper(activity, Const.DB_NAME, null, Const.DB_VERSION);
        Cursor cursor = dbHelper.GetData("SELECT BookId FROM book WHERE BookId = '"+bookId+"' AND BookStatus = '1';");
        if(cursor.moveToFirst()){
            if(cursor.getCount()==0) return;
        }
        String UPDATE_DATA =
                "UPDATE book " +
                        "SET BookStatus = '0' " +
                        "WHERE BookId = '"+bookId+"'";
        dbHelper.QueryData(UPDATE_DATA);
        dbHelper.close();
    }
}
