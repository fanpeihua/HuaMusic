package fanpeihua.huamusic.ui.my.user;

import android.content.Context;
import android.content.SharedPreferences;

import fanpeihua.huamusic.MusicApp;
import fanpeihua.huamusic.common.Constants;
import fanpeihua.huamusic.utils.SPUtils;

public class UserStatus {

    public static void saveUserInfo(User userInfo) {
        SharedPreferences sp = MusicApp.getAppContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(Constants.USER_ID, userInfo.getId());
        editor.putString(Constants.TOKEN, userInfo.getToken());
        editor.putString(Constants.USERNAME, userInfo.getName());
        editor.putString(Constants.USER_SEX, userInfo.getSex());


        editor.putString(Constants.USER_IMG, userInfo.getAvatar());
        editor.putString(Constants.USER_EMAIL, userInfo.getEmail());
        editor.putString(Constants.PHONE, userInfo.getPhone());
        editor.putString(Constants.NICK, userInfo.getNickname());
        editor.putInt(Constants.SECRET, userInfo.getSecret());
        editor.putLong(Constants.TOKEN_TIME, System.currentTimeMillis());

        editor.apply();
        saveLoginStatus(true);
    }

    public static User getUserInfo() {
        if (!getLoginStatus()) return null;
        SharedPreferences sp = MusicApp.getAppContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        User user = new User();
        user.setId(sp.getString(Constants.USER));
    }

    public static void clearUserInfo() {
        SharedPreferences sp = MusicApp.getAppContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply();
        saveLoginStatus(false);
    }

    public static User getUserInfo() {
        if (!getLoginStatus()) return null;
        SharedPreferences sp = MusicApp.getAppContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        User user = new User();
        return user;
    }

    public static boolean getLoginStatus() {
        return SPUtils.getAnyByKey(Constants.LOGIN_STATUS, false);
    }

    public static void saveLoginStatus(boolean status) {
        SPUtils.putAnyCommit(Constants.LOGIN_STATUS, status);
    }

    public static boolean getTokenStatus() {
        SharedPreferences sp = MusicApp.getAppContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        long time = sp.getLong(Constants.TOKEN_TIME, 0L);
        return (System.currentTimeMillis() - time < 7 * 24 * 60 * 60 * 1000);
    }
}
