package uia.is213.eirik.rewind;

import im.delight.android.ddp.MeteorSingleton;

/**
 * Created by Eirik on 28.09.2015.
 */
public class Room {
    public String name;
    public String owner; // author id
    public String code; // lectureCode

    public Room(String code){
        //this.name = Name;
        //this.owner = Owner;
        this.code = code;

        Enter();
    }


    public void Enter(){
        // Subscribe to Lecture Room
       // String id = MeteorSingleton.getInstance().subscribe("lectures", new Object[]{code});
        //Subscribe to questions and votes
        MeteorSingleton.getInstance().subscribe("questions", new Object[]{code});
        MeteorSingleton.getInstance().subscribe("votes", new Object[]{code});

        //Tell others we joined
        MeteorSingleton.getInstance().call("memberInsert", new Object[]{ code });

    }

    @Override
    public String toString(){
        return code ;
    }
}
