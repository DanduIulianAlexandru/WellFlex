package dudu.nutrifitapp.model;

import java.util.ArrayList;
import java.util.List;

public class SocialProfile {
    public String username;
    public String profilePictureUrl;
    public String name;
    public String biography;
    public List<String> friends;
    public List<String> posts;

    public SocialProfile() {
        // Default constructor required for calls to DataSnapshot.getValue(SocialProfile.class)
    }

    public SocialProfile(String username, String profilePictureUrl, String name, String biography) {
        this.username = username;
        this.profilePictureUrl = profilePictureUrl;
        this.name = name;
        this.biography = biography;
        this.friends = new ArrayList<>();
        this.posts = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public List<String> getFriends() {
        return friends;
    }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }

    public List<String> getPosts() {
        return posts;
    }

    public void setPosts(List<String> posts) {
        this.posts = posts;
    }

    @Override
    public String toString() {
        return "SocialProfile{" +
                "username='" + username + '\'' +
                ", profilePictureUrl='" + profilePictureUrl + '\'' +
                ", name='" + name + '\'' +
                ", biography='" + biography + '\'' +
                ", friends=" + friends +
                ", posts=" + posts +
                '}';
    }
}
