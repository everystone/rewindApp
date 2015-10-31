package uia.is213.eirik.rewind.Models;

import android.util.Log;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;

import im.delight.android.ddp.MeteorSingleton;
import im.delight.android.ddp.ResultListener;
import uia.is213.eirik.rewind.KeyValueDB;
import uia.is213.eirik.rewind.MainActivity;

/**
 * Created by Eirik on 13.10.2015.
 */
public class User {
    public String username;
    public String password;
    public String email;
    private String userId;  //Gets returned from Meteor server when Authenticated
    private String token;
    private String tokenExpires;
    public boolean isLoggedIn=false;

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    void Log(String msg){
        Log.d("SARA", msg);
        Toast.makeText(MainActivity.getAppContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public String getId(){
        return this.userId;
    }


    private void saveId(String json){
        //{"id":"kHH3m6jMwABuD6CY4","token":"3LB5e2YD-fiOQ_YhVSOB49_LtopfpYYoyY6jkpFXp2U","tokenExpires":{"$date":1452530864152}}
        try {
            JSONObject data = new JSONObject(json);
            this.userId = data.getString("id");
            this.token = data.getString("token");
            this.tokenExpires = data.getString("tokenExpires");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Debug
        Log("id: "+userId+", token:" +token+", tokenExpires:"+tokenExpires);
        KeyValueDB.SaveUserData(username, password, email);
    }

    public void Authenticate(){
        MeteorSingleton.getInstance().loginWithUsername(this.username, this.password, new ResultListener() {
            @Override
            public void onSuccess(String s) {
                Log("Authenticated: " + s);
                saveId(s);
                isLoggedIn=true;
            }
            @Override
            public void onError(String s, String s1, String s2) {
                Log(String.format("Auth failed: %s, %s, %s", s, s1, s2));
                if (s1.equals("User not found")) {
                    MeteorSingleton.getInstance().registerAndLogin(username, email, password, new ResultListener() {
                        @Override
                        public void onSuccess(String s) {
                            Log("Registererd " + s);
                            saveId(s);
                            isLoggedIn = true;
                        }

                        @Override
                        public void onError(String s, String s1, String s2) {
                            Log(String.format("Auth failed: %s, %s, %s", s, s1, s2));
                            isLoggedIn = false;
                        }
                    });
                }
            }

        });
    }


}
