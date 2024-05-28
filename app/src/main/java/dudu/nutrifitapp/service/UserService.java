package dudu.nutrifitapp.service;

import java.util.List;

import dudu.nutrifitapp.repo.repoDB.UserRepository;
import dudu.nutrifitapp.repo.RepoInterface.DataStatus;
import dudu.nutrifitapp.model.User;

public class UserService {
    private final UserRepository userRepository;

    public UserService() {
        this.userRepository = new UserRepository();
    }

    public void addUser(User user, String userId, final DataStatus dataStatus) {
        userRepository.add(user, userId, new DataStatus() {
            @Override
            public void onSuccess(Object data) {
                dataStatus.onSuccess(data);
            }

            @Override
            public void onError(Exception error) {
                dataStatus.onError(error);
            }
        });
    }

    public void getUser(String userId, final DataStatus dataStatus) {
        userRepository.getOne(userId, new DataStatus() {
            @Override
            public void onSuccess(Object data) {
                dataStatus.onSuccess(data);
            }

            @Override
            public void onError(Exception error) {
                dataStatus.onError(error);
            }
        });
    }

    public void updateUser(User user, String userId, final DataStatus dataStatus) {
        userRepository.update(user, userId, new DataStatus() {
            @Override
            public void onSuccess(Object data) {
                dataStatus.onSuccess(data);
            }

            @Override
            public void onError(Exception error) {
                dataStatus.onError(error);
            }
        });
    }

    public void deleteUser(String userId, final DataStatus dataStatus) {
        userRepository.delete(userId, new DataStatus() {
            @Override
            public void onSuccess(Object data) {
                dataStatus.onSuccess(data);
            }

            @Override
            public void onError(Exception error) {
                dataStatus.onError(error);
            }
        });
    }

    public void getAllUsers(final DataStatus dataStatus) {
        userRepository.getAll(new DataStatus() {
            @Override
            public void onSuccess(Object data) {
                dataStatus.onSuccess(data);
            }

            @Override
            public void onError(Exception error) {
                dataStatus.onError(error);
            }
        });
    }
}
