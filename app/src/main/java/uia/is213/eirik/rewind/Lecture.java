package uia.is213.eirik.rewind;


import im.delight.android.ddp.MeteorSingleton;

/**
 * Created by Eirik on 28.09.2015.
 */
public class Lecture {
    public String name;
    public String owner; // author id
    public String code; // lectureCode

    private String subIdQ;
    private String subIdV;

    public Lecture(String code){
        //this.name = Name;
        //this.owner = Owner;
        this.code = code;

        Enter();
    }


    public void Enter(){
        // We need to keep the Subscription Ids returned so we can unsubscribe later.
        subIdQ = MeteorSingleton.getInstance().subscribe("questions", new Object[]{code});
        subIdV = MeteorSingleton.getInstance().subscribe("votes", new Object[]{code});

        //Tell others we joined
        MeteorSingleton.getInstance().call("memberInsert", new Object[]{code});

        //Save lectureCode so that next time we launch the app, we automatically connect to same lecture.
        KeyValueDB.setKeyValue("lectureCode", this.code);
    }

    public void Leave(){
        //Unsubscribe from Lecture
        MeteorSingleton.getInstance().unsubscribe(subIdQ);
        MeteorSingleton.getInstance().unsubscribe(subIdV);

        //Tell others we left -- Meteor method doesn't exists
        //MeteorSingleton.getInstance().call("memberInsert", new Object[]{code});
    }

    @Override
    public String toString(){
        return code ;
    }
}
