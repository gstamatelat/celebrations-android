package gr.james.celebrations;

public class Contact {

    public String id;
    public String firstname;
    public boolean giortazei;
    public String displayname;
    public boolean events;

    public Contact(String id, String FirstName, String displayname) {
        this.id = id;
        this.firstname = FirstName;
        this.giortazei = false;
        this.displayname = displayname;
        this.events = false;
    }

}