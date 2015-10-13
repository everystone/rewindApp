package uia.is213.eirik.rewind;

import android.util.Log;

import im.delight.android.ddp.Meteor;
import im.delight.android.ddp.MeteorSingleton;
import im.delight.android.ddp.ResultListener;

/**
 * Created by Eirik on 13.10.2015.
 */
public class User {
    public String username;
    public String password;
    public String email;
    public boolean isLoggedIn=false;

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    void Log(String msg){
        Log.d("AUTH", msg);
    }

    public void Authenticate(){
        MeteorSingleton.getInstance().registerAndLogin(this.username, this.email, this.password, new ResultListener() {
            @Override
            public void onSuccess(String s) {
                Log("Authenticated: " + s);
                isLoggedIn=true;
            }
            @Override
            public void onError(String s, String s1, String s2) {
                Log(String.format("Auth failed: %s, %s, %s", s, s1, s2));
                if (s1.equals("Username already exists.")) {
                    MeteorSingleton.getInstance().loginWithUsername(username, password, new ResultListener() {
                        @Override
                        public void onSuccess(String s) {
                            Log("Authenticated: " + s);
                            isLoggedIn=true;
                        }

                        @Override
                        public void onError(String s, String s1, String s2) {
                            Log(String.format("Auth failed: %s, %s, %s", s, s1, s2));
                            isLoggedIn=false;
                        }
                    });
                }
            }

        });
    }


}
