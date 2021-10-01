package com.team9.virtualwallet.repositories;

import com.team9.virtualwallet.exceptions.EntityNotFoundException;
import com.team9.virtualwallet.models.Pages;
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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.team9.virtualwallet.utils.FileUploadHelper.uploadFile;

@Repository
public class UserRepositoryImpl extends BaseRepositoryImpl<User> implements UserRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public UserRepositoryImpl(SessionFactory sessionFactory) {
        super(sessionFactory, User.class);
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Pages<User> getAll(User user, Pageable pageable) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("from User", User.class);
            query.setFirstResult((pageable.getPageSize() * pageable.getPageNumber()) - pageable.getPageSize());
            query.setMaxResults(pageable.getPageSize());

            Query countQuery = session.createQuery("select count (id) from User");
            Long countResults = (Long) countQuery.uniqueResult();

            return new Pages<>(query.list(), countResults, pageable);
        }
    }

    @Override
    public Pages<User> getAllUnverified(Pageable pageable) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("from User where idVerified = false and isDeleted = false", User.class);
            query.setFirstResult((pageable.getPageSize() * pageable.getPageNumber()) - pageable.getPageSize());
            query.setMaxResults(pageable.getPageSize());

            Query countQuery = session.createQuery("select count (id) from User where idVerified = false and isDeleted = false");
            Long countResults = (Long) countQuery.uniqueResult();

            return new Pages<>(query.list(), countResults, pageable);
        }
    }

    @Override
    public void updateProfilePhoto(User user, MultipartFile multipartFile) {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        user.setUserPhoto(fileName);
        Path uploadPath = Paths.get("./images/users/" + user.getId());

        uploadFile(uploadPath, fileName, multipartFile);
        super.update(user);
    }

    @Override
    public void updateIdAndSelfiePhoto(User user, MultipartFile idPhoto, MultipartFile selfiePhoto) {
        String idPhotoFileName = StringUtils.cleanPath(Objects.requireNonNull(idPhoto.getOriginalFilename()));
        user.setIdPhoto(idPhotoFileName);

        String selfiePhotoFileName = StringUtils.cleanPath(Objects.requireNonNull(selfiePhoto.getOriginalFilename()));
        user.setSelfie(selfiePhotoFileName);

        Path uploadPath = Paths.get("./images/users/" + user.getId() + "/id/");

        uploadFile(uploadPath, idPhotoFileName, idPhoto);
        uploadFile(uploadPath, selfiePhotoFileName, selfiePhoto);
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
    public Pages<User> filter(Optional<String> userName,
                              Optional<String> phoneNumber,
                              Optional<String> email,
                              Pageable pageable) {

        try (Session session = sessionFactory.openSession()) {
            var baseQuery = "from User ";
            var countBaseQuery = "select count (id) ";
            List<String> filters = new ArrayList<>();

            if (userName.isPresent()) {
                filters.add(" username like concat('%',:username,'%')");
            }

            if (phoneNumber.isPresent()) {
                filters.add(" phoneNumber like concat('%',:phoneNumber,'%')");
            }

            if (email.isPresent()) {
                filters.add(" email like concat('%',:email,'%')");
            }

            if (!filters.isEmpty()) {
                baseQuery += " where " + String.join(" and ", filters);
            }

            countBaseQuery += baseQuery;

            Query<User> query = session.createQuery(baseQuery, User.class);
            Query countQuery = session.createQuery(countBaseQuery);

            userName.ifPresent(value -> {
                query.setParameter("username", value);
                countQuery.setParameter("username", value);
            });

            phoneNumber.ifPresent(value -> {
                query.setParameter("phoneNumber", value);
                countQuery.setParameter("phoneNumber", value);
            });

            email.ifPresent(value -> {
                query.setParameter("email", value);
                countQuery.setParameter("email", value);
            });

            query.setFirstResult((pageable.getPageSize() * pageable.getPageNumber()) - pageable.getPageSize());
            query.setMaxResults(pageable.getPageSize());
            Long countResults = (Long) countQuery.uniqueResult();

            return new Pages<>(query.list(), countResults, pageable);
        }

    }
}