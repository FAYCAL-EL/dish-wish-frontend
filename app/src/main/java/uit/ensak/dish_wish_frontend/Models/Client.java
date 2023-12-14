package uit.ensak.dish_wish_frontend.Models;

import java.time.Instant;
import java.util.List;

public class Client {

    private Long id;

    private String email;

    private String password;

    private String firstName;

    private String lastName;

    private String address;

    private String phoneNumber;

    private String photo;


    private Instant createdOn;


    private Instant lastUpdatedOn;


    private List<Notification> notifications;


    private List<Command> commands;

    private List<Rating> ratings;


    private List<Diet> diets;


    private List<Allergy> allergies;

    public String getRole() {
        return "CLIENT";
    }

    public void setRole(String client) {
    }

}