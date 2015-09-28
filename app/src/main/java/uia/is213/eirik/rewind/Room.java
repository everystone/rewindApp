package uia.is213.eirik.rewind;

/**
 * Created by Eirik on 28.09.2015.
 */
public class Room {
    public String Name;
    public String Owner;

    public Room(String Name, String Owner){
        this.Name = Name;
        this.Owner = Owner;
    }

    @Override
    public String toString(){
        return Name;
    }
}
