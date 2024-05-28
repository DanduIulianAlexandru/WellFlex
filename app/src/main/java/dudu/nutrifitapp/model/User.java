package dudu.nutrifitapp.model;

public class User {
    private String email;
    private String password;
    private SocialProfile socialProfile;
    private NutritiveProfile nutritiveProfile;

    public User(String email, String password, SocialProfile socialProfile, NutritiveProfile nutritiveProfile) {
        this.email = email;
        this.password = password;
        this.socialProfile = socialProfile;
        this.nutritiveProfile = nutritiveProfile;
    }

    public User(){}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public SocialProfile getSocialProfile() {
        return socialProfile;
    }

    public void setSocialProfile(SocialProfile socialProfile) {
        this.socialProfile = socialProfile;
    }

    public NutritiveProfile getNutritiveProfile() {
        return nutritiveProfile;
    }

    public void setNutritiveProfile(NutritiveProfile nutritiveProfile) {
        this.nutritiveProfile = nutritiveProfile;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", socialProfile=" + socialProfile +
                ", nutritiveProfile=" + nutritiveProfile +
                '}';
    }
}
