package dudu.nutrifitapp.model;

public class Exercise implements android.os.Parcelable {
    private final String name;
    private final int duration;
    private final int animationResId;

    public Exercise(String name, int duration, int animationResId) {
        this.name = name;
        this.duration = duration;
        this.animationResId = animationResId;
    }

    protected Exercise(android.os.Parcel in) {
        name = in.readString();
        duration = in.readInt();
        animationResId = in.readInt();
    }

    public static final Creator<Exercise> CREATOR = new Creator<Exercise>() {
        @Override
        public Exercise createFromParcel(android.os.Parcel in) {
            return new Exercise(in);
        }

        @Override
        public Exercise[] newArray(int size) {
            return new Exercise[size];
        }
    };

    public String getName() {
        return name;
    }

    public int getDuration() {
        return duration;
    }

    public int getAnimationResId() {
        return animationResId;
    }

    @Override
    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(duration);
        dest.writeInt(animationResId);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
