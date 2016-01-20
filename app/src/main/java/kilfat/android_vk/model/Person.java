package kilfat.android_vk.model;
public class Person {
    private String firstName;
    private String lastName;
    private String photoID;
    private boolean statusOnline;
    private int id;
    public Person(String firstName, String lastName,String photoID, boolean statusOnline, int id){
        this.firstName=firstName;
        this.lastName=lastName;
        this.photoID=photoID;
        this.statusOnline=statusOnline;
        this.id=id;
    }
    public int getID() {
        return id;
    }


    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getPhotoID() {
        return photoID;
    }

    public void setPhotoID(String photoID) {
        this.photoID = photoID;
    }

    public boolean isOnline(){
        return statusOnline;
    }

    public void setStatusOnline(boolean status){
        this.statusOnline=status;
    }
}
