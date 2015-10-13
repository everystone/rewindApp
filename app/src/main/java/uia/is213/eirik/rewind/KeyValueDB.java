package uia.is213.eirik.rewind;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Eirik on 13.10.2015.
 */
public class KeyValueDB {

    private SharedPreferences sharedPreferences;
    private static String PREF_NAME = "rewindAppPrefs";

    public KeyValueDB(){
    }

    private static SharedPreferences getPrefs() {
        return MainActivity.getAppContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static String getUsername(Context context) {
        return getPrefs().getString("username_key", "default_username");
    }

    public static void SaveUserData(String usr, String pw, String mail){
        SharedPreferences.Editor editor = getPrefs().edit();
        editor.putString("username", usr);
        editor.putString("password", pw);
        editor.putString("email", mail);
        editor.commit();
    }

    public static void setKeyValue(String key, String value) {
        SharedPreferences.Editor editor = getPrefs().edit();
        editor.putString(key, value);
        editor.commit();
    }
    public static String getValue(String key, String def){
        return getPrefs().getString(key, def);
    }
}
