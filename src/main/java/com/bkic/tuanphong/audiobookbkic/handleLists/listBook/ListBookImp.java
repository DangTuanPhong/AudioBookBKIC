package com.bkic.tuanphong.audiobookbkic.handleLists.listBook;

import org.json.JSONArray;

public interface ListBookImp {

    void ShowListFromSelected();

    void LoadListDataFailed(String jsonMessage);

    void SetTableSelectedData(JSONArray jsonArrayResult);
}
