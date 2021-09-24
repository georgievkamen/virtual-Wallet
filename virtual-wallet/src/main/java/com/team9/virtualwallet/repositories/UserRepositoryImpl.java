package com.team9.virtualwallet.repositories;

import com.team9.virtualwallet.exceptions.EntityNotFoundException;
import com.team9.virtualwallet.exceptions.FailedToUploadFileException;
import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.repositories.contracts.UserRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class UserRepositoryImpl extends BaseRepositoryImpl<User> implements UserRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public UserRepositoryImpl(SessionFactory sessionFactory) {
        super(sessionFactory, User.class);
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void updateProfilePhoto(User user, MultipartFile multipartFile) {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        user.setUserPhoto(fileName);
        Path uploadPath = Paths.get("./images/users/" + user.getId());

        try (InputStream inputStream = multipartFile.getInputStream()) {
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new FailedToUploadFileException(e.getMessage());
        }
        super.update(user);
    }

    @Override
    public List<User> getAll() {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("from User where isDeleted = false ", User.class);
            return query.list();
        }
    }

    @Override
    public User getById(int id) {
        try (Session session = sessionFactory.openSession()) {
            User user = session.get(User.class, id);
            if (user == null || user.isDeleted()) {
                throw new EntityNotFoundException("User", id);
            }
            return user;
        }
    }

    @Override
    public void delete(User user) {
        user.setDeleted(true);
        user.setEmail("0");
        user.setPhoneNumber("0");
        user.setBlocked(true);

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.update(user);
            session.createSQLQuery("delete from contact_list where contact_id = :id or user_id = :id ")
                    .setParameter("id", user.getId())
                    .setParameter("id", user.getId())
                    .executeUpdate();
            session.getTransaction().commit();
        }
    }

    @Override
    public User getByFieldNotDeleted(String fieldName, String searchTerm, int userId) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery(String.format("from User where %s = :value and isDeleted = false and id != :userId", fieldName), User.class)
                    .setParameter("value", searchTerm)
                    .setParameter("userId", userId);
            if (query.list().isEmpty()) {
                throw new EntityNotFoundException("User", fieldName, searchTerm);
            }
            return query.list().get(0);
        }
    }

    @Override
    public List<User> search(String searchTerm, int userId) {
        try (Session session = sessionFactory.openSession()) {
            String baseQuery = "from User where isDeleted = false and (username like :term or phoneNumber like :term or email like :term) and id != :id";
            Query<User> query = session.createQuery(baseQuery, User.class)
                    .setParameter("term", searchTerm)
                    .setParameter("term", searchTerm)
                    .setParameter("term", searchTerm)
                    .setParameter("id", userId);
            return query.list();
        }
    }

    @Override
    public List<User> filter(Optional<String> userName,
                             Optional<String> phoneNumber,
                             Optional<String> email,
                             int userId) {

        try (Session session = sessionFactory.openSession()) {
            var baseQuery = "select u from User u where u.id != :id";
            List<String> filters = new ArrayList<>();

            if (userName.isPresent()) {
                filters.add(" u.username like concat('%',:username,'%')");
            }

            if (phoneNumber.isPresent()) {
                filters.add(" u.phoneNumber like concat('%',:phoneNumber,'%')");
            }

            if (email.isPresent()) {
                filters.add(" u.email like concat('%',:email,'%')");
            }

            if (!filters.isEmpty()) {
                baseQuery += " and " + String.join(" and ", filters);
            }

            Query<User> query = session.createQuery(baseQuery, User.class);
            query.setParameter("id", userId);

            userName.ifPresent(s -> query.setParameter("username", s));
            phoneNumber.ifPresent(s -> query.setParameter("phoneNumber", s));
            email.ifPresent(s -> query.setParameter("email", s));

            return query.list();
        }

    }
}