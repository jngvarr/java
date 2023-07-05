public class Human {
    String firstname;
    String lastname;
    String sirname;
    String dateOfBirth;
    String phoneNumber;
    char sex;

    public Human(String lastname, String firstname, String sirname, String dateOfBirth, String phoneNumber, char sex) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.sirname = sirname;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.sex = sex;
    }

    @Override
    public String toString() {
        return "<" + lastname + "><" + firstname + "><" + sirname + "><" + dateOfBirth + "><"
                + phoneNumber + "><" + sex + ">\n";
    }
}
