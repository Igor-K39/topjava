package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class InMemoryUserRepository extends InMemoryBaseRepository<User> implements UserRepository {
    private static final Comparator<User> userComparator = Comparator.comparing(User::getName).thenComparing(User::getEmail);

    @Override
    public boolean delete(int id) {
        return super.delete(id);
    }

    @Override
    public User save(User user) {
        return super.save(user);
    }

    @Override
    public User get(int id) {
        return super.get(id);
    }

    @Override
    public List<User> getAll() {
        return super.getAll().stream()
                .sorted(userComparator)
                .collect(Collectors.toList());
    }

    @Override
    public User getByEmail(String email) {
        return super.getAll().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst()
                .orElse(null);
    }
}
