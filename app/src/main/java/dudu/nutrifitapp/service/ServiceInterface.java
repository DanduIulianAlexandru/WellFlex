package dudu.nutrifitapp.service;

import java.util.List;

import dudu.nutrifitapp.repo.RepoInterface;

public interface ServiceInterface<T, ID> {
    void add(T entity, ID id);
    T getOne(ID id);
    T update(T entity, ID id);
    T delete(ID id);
    List<T> getAll();
}
