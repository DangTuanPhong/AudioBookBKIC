package com.bkic.tuanphong.audiobookbkic.handleLists.favorite;

import org.json.JSONException;
import org.json.JSONObject;

public interface ListFavoriteImp {

    void SetTableSelectedData(JSONObject jsonObject) throws JSONException;

    void ShowListFromSelected();

    void LoadListDataFailed(String jsonMessage);

    void RemoveFavoriteSuccess(String message);

    void RemoveFavoriteFailed(String message);

    void RemoveAllFavoriteSuccess(String message);

    void RemoveAllFavoriteFailed(String message);
}
