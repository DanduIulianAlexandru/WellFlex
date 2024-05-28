package dudu.nutrifitapp.model;

public class NutritiveProfile {
    public int age;
    public float height;
    public float weight;
    public String sex;
    public float objective;

    public NutritiveProfile() {
        // Default constructor required for calls to DataSnapshot.getValue(NutritiveProfile.class)
    }

    public NutritiveProfile(int age, float height, float weight, String sex, float objective) {
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.sex = sex;
        this.objective = objective;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public float getObjective() {
        return objective;
    }

    public void setObjective(float objective) {
        this.objective = objective;
    }

    @Override
    public String toString() {
        return "NutritiveProfile{" +
                "age=" + age +
                ", height=" + height +
                ", weight=" + weight +
                ", sex='" + sex + '\'' +
                ", objective=" + objective +
                '}';
    }
}

