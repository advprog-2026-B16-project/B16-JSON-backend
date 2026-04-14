package id.ac.ui.cs.advprog.jsonbackend.authprofile.service;

import id.ac.ui.cs.advprog.jsonbackend.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.repository.UserRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

class StubUserRepository implements UserRepository {
    @Override public Optional<User> findByUsername(String username) { return Optional.empty(); }
    @Override public Optional<User> findByEmail(String email) { return Optional.empty(); }

    @Override public @NonNull List<User> findAll() { return new ArrayList<>(); }
    @Override public @NonNull List<User> findAll(@NonNull Sort sort) { return new ArrayList<>(); }
    @Override public @NonNull Page<User> findAll(@NonNull Pageable pageable) { return Page.empty(); }
    @Override public @NonNull List<User> findAllById(@NonNull Iterable<UUID> ids) { return new ArrayList<>(); }
    @Override public long count() { return 0; }
    @Override public void delete(@NonNull User entity) {}
    @Override public void deleteAll(@NonNull Iterable<? extends User> entities) {}
    @Override public void deleteAll() {}
    @Override public void deleteById(@NonNull UUID id) {}
    @Override public void deleteAllById(@NonNull Iterable<? extends UUID> ids) {}
    @Override public boolean existsById(@NonNull UUID id) { return false; }
    @Override public @NonNull Optional<User> findById(@NonNull UUID id) { return Optional.empty(); }
    @Override public <S extends User> @NonNull S save(@NonNull S entity) { return entity; }
    @Override public <S extends User> @NonNull List<S> saveAll(@NonNull Iterable<S> entities) { return new ArrayList<>(); }
    @Override public void flush() {}
    @Override public <S extends User> @NonNull S saveAndFlush(@NonNull S entity) { return entity; }
    @Override public <S extends User> @NonNull List<S> saveAllAndFlush(@NonNull Iterable<S> entities) { return new ArrayList<>(); }
    @Override public void deleteAllInBatch(@NonNull Iterable<User> entities) {}
    @Override public void deleteAllByIdInBatch(@NonNull Iterable<UUID> ids) {}
    @Override public void deleteAllInBatch() {}
    @Override public @NonNull User getOne(@NonNull UUID id) { throw new UnsupportedOperationException(); }
    @Override public @NonNull User getById(@NonNull UUID id) { throw new UnsupportedOperationException(); }
    @Override public @NonNull User getReferenceById(@NonNull UUID id) { throw new UnsupportedOperationException(); }
    @Override public <S extends User> @NonNull List<S> findAll(@NonNull Example<S> example) { return new ArrayList<>(); }
    @Override public <S extends User> @NonNull List<S> findAll(@NonNull Example<S> example, @NonNull Sort sort) { return new ArrayList<>(); }
    @Override public <S extends User> @NonNull Page<S> findAll(@NonNull Example<S> example, @NonNull Pageable pageable) { return Page.empty(); }
    @Override public <S extends User> long count(@NonNull Example<S> example) { return 0; }
    @Override public <S extends User> boolean exists(@NonNull Example<S> example) { return false; }
    @Override public <S extends User, R> @NonNull R findBy(@NonNull Example<S> example, @NonNull Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) { return queryFunction.apply(null); }
    @Override public <S extends User> @NonNull Optional<S> findOne(@NonNull Example<S> example) { return Optional.empty(); }
}
