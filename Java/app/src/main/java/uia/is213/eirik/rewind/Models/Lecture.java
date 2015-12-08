package uia.is213.eirik.rewind.Models;


import org.json.JSONArray;

import java.util.HashMap;

import im.delight.android.ddp.MeteorSingleton;
import uia.is213.eirik.rewind.KeyValueDB;

/**
 * Created by Eirik on 28.09.2015.
 */
public class Lecture {
    private String name;
    private String owner; // author id

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
    public String getOwner(){
        return owner;
    }
    private String code; // lectureCode
    private String title;
    public String getTitle(){ return this.title; }
    public void setTitle(String title){ this.title = title;}
    private String subIdQ; // subscription id questions
    private String subIdV; // subscription id VOtes
    private String subIdL; // subscription Id LEctures

    class Member{
        String id;
        public Member(String id){
            this.id=id;
        }
    }
    private JSONArray members;

    public Lecture(String code){
        //this.name = Name;
        //this.owner = Owner;
        this.code = code;

        Enter();
    }


    public void setMembers(JSONArray members){
        this.members = members;
    }
    public int getNumbeOfMembers(){
        if(members == null) return 0;
        return members.length();
    }


    public void Enter(){
        // We need to keep the Subscription Ids returned so we can unsubscribe later.
        subIdQ = MeteorSingleton.getInstance().subscribe("questions", new Object[]{code});
        subIdV = MeteorSingleton.getInstance().subscribe("votes", new Object[]{code});
        subIdL = MeteorSingleton.getInstance().subscribe("lecture", new Object[]{code});

        //Tell others we joined
        MeteorSingleton.getInstance().call("insertMember", new Object[]{code});

        //Save lectureCode so that next time we launch the app, we automatically connect to same lecture.
        KeyValueDB.setKeyValue("lectureCode", this.code);
    }

    public void Leave(){
        //Unsubscribe from Lecture
        MeteorSingleton.getInstance().call("deleteMember", new Object[]{code});
        MeteorSingleton.getInstance().unsubscribe(subIdQ);
        MeteorSingleton.getInstance().unsubscribe(subIdV);
        MeteorSingleton.getInstance().unsubscribe(subIdL);

        //Tell others we left -- Meteor method doesn't exists
        //MeteorSingleton.getInstance().call("memberInsert", new Object[]{code});
    }

    @Override
    public String toString(){
        return code ;
    }
}
