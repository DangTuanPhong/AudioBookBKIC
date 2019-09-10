package com.bkic.tuanphong.audiobookbkic.account.showUserInfo;

import com.bkic.tuanphong.audiobookbkic.account.utils.User;

import java.util.List;

public interface UserInfoImp {

    void LogoutFailed();

    void LogoutSuccess();

    void DisplayUserDetail();

    List<User> GetCurrentUserDetail();

    void UpdateDetailSuccess(String message);

    void UpdateDetailFailed(String message);

    void UpdatePasswordSuccess(String message);

    void UpdatePasswordFailed(String message);
}
