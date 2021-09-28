package com.team9.virtualwallet.repositories;

import com.team9.virtualwallet.exceptions.EntityNotFoundException;
import com.team9.virtualwallet.exceptions.FailedToUploadFileException;
import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.repositories.contracts.UserRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
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
    public List<User> filter(Optional<String> userName,
                             Optional<String> phoneNumber,
                             Optional<String> email,
                             Pageable pageable) {

        try (Session session = sessionFactory.openSession()) {
            var baseQuery = "select u from User u ";
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
                baseQuery += " where " + String.join(" and ", filters);
            }

            Query<User> query = session.createQuery(baseQuery, User.class);

            userName.ifPresent(s -> query.setParameter("username", s));
            phoneNumber.ifPresent(s -> query.setParameter("phoneNumber", s));
            email.ifPresent(s -> query.setParameter("email", s));

            query.setFirstResult((pageable.getPageSize() * pageable.getPageNumber()) - pageable.getPageSize());
            query.setMaxResults(pageable.getPageSize());

            return query.list();
        }

    }
}