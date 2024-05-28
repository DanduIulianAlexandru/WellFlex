package dudu.nutrifitapp.repo;

public interface RepoInterface<T, ID> {
    void add(T entity, ID id, DataStatus dataStatus);
    void getOne(ID id, DataStatus dataStatus);
    void update(T entity, ID id, DataStatus dataStatus);
    void delete(ID id, DataStatus dataStatus);
    void getAll(DataStatus dataStatus);









    public interface DataStatus {
        void onSuccess(Object data);
        void onError(Exception e);
    }

}
