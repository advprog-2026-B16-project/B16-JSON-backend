package id.ac.ui.cs.advprog.jsonbackend.service;

import id.ac.ui.cs.advprog.jsonbackend.model.User;
import id.ac.ui.cs.advprog.jsonbackend.repository.UserRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("NullableProblems")
class StubUserRepository implements UserRepository {

    private List<User> users = new ArrayList<>();

    void setUsers(List<User> users) {
        this.users = users;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return users.stream().filter(u -> u.getUsername().equals(username)).findFirst();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return users.stream().filter(u -> u.getEmail().equals(email)).findFirst();
    }

    @Override public void flush() {}
    @Override public @NonNull <S extends User> S saveAndFlush(@NonNull S entity) { return entity; }
    @Override public @NonNull <S extends User> List<S> saveAllAndFlush(@NonNull Iterable<S> entities) { return new ArrayList<>(); }
    @Override public void deleteAllInBatch(@NonNull Iterable<User> entities) {}
    @Override public void deleteAllByIdInBatch(@NonNull Iterable<String> ids) {}
    @Override public void deleteAllInBatch() {}
    @Override public @NonNull User getOne(@NonNull String id) { throw new UnsupportedOperationException(); }
    @Override public @NonNull User getById(@NonNull String id) { throw new UnsupportedOperationException(); }
    @Override public @NonNull User getReferenceById(@NonNull String id) { throw new UnsupportedOperationException(); }
    @Override public @NonNull <S extends User> List<S> findAll(@NonNull Example<S> example) { return new ArrayList<>(); }
    @Override public @NonNull <S extends User> List<S> findAll(@NonNull Example<S> example, @NonNull Sort sort) { return new ArrayList<>(); }
    @Override public @NonNull <S extends User> List<S> saveAll(@NonNull Iterable<S> entities) { return new ArrayList<>(); }
    @Override public @NonNull List<User> findAll() { return users; }
    @Override public @NonNull List<User> findAllById(@NonNull Iterable<String> ids) { return new ArrayList<>(); }
    @Override public @NonNull <S extends User> S save(@NonNull S entity) { return entity; }
    @Override public @NonNull Optional<User> findById(@NonNull String id) { return findByUsername(id); }
    @Override public boolean existsById(@NonNull String id) { return findByUsername(id).isPresent(); }
    @Override public long count() { return users.size(); }
    @Override public void deleteById(@NonNull String id) {}
    @Override public void delete(@NonNull User entity) {}
    @Override public void deleteAllById(@NonNull Iterable<? extends String> ids) {}
    @Override public void deleteAll(@NonNull Iterable<? extends User> entities) {}
    @Override public void deleteAll() {}
    @Override public @NonNull List<User> findAll(@NonNull Sort sort) { return new ArrayList<>(); }
    @Override public @NonNull Page<User> findAll(@NonNull Pageable pageable) { return Page.empty(); }
    @Override public @NonNull <S extends User> Optional<S> findOne(@NonNull Example<S> example) { return Optional.empty(); }
    @Override public @NonNull <S extends User> Page<S> findAll(@NonNull Example<S> example, @NonNull Pageable pageable) { return Page.empty(); }
    @Override public <S extends User> long count(@NonNull Example<S> example) { return 0; }
    @Override public <S extends User> boolean exists(@NonNull Example<S> example) { return false; }
    @Override public @NonNull <S extends User, R> R findBy(@NonNull Example<S> example, @NonNull Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) { throw new UnsupportedOperationException(); }
}
