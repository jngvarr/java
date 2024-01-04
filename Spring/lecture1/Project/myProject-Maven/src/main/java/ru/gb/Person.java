package ru.gb;

import com.google.gson.Gson;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Person {
    private String firstname;
    private String lastname;
    private int age;

    public Person(String firstname, String lastname, int age) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.age = age;
    }

    public Person() {

    }

    // region getters and setters
    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
// endregion

    public void writePersonToFile(Person person, String filePath) {
        Gson gson = new Gson();
        String json = gson.toJson(person);

        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(json);
            System.out.println("Объект Person успешно сериализован в файл: '" + filePath + "'");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Метод для чтения объекта Person из файла JSON
    public Person readPersonFromFile(String filePath) {
        Gson gson = new Gson();

        try (FileReader reader = new FileReader(filePath)) {
            System.out.println("Объект Person успешно десериализован: ");
            return gson.fromJson(reader, Person.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Person)) return false;

        Person person = (Person) o;

        return new EqualsBuilder().append(age, person.age).append(firstname, person.firstname).append(lastname, person.lastname).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(firstname).append(lastname).append(age).toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("firstname", firstname)
                .append("lastname", lastname)
                .append("age", age)
                .toString();
    }
}
