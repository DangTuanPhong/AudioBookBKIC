package com.bkic.tuanphong.audiobookbkic.main;

import org.json.JSONException;
import org.json.JSONObject;

public interface MainImp {
    void ShowListMenu();
    void SetMenuData(JSONObject jsonObject) throws JSONException;
}
