package com.example.demo.repositories;

import com.example.demo.model.MagicData;
import com.example.demo.model.User;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
/**
 * Репозиторий для работы с данными пользователей в базе данных.
 */
@Repository
@AllArgsConstructor
public class UserRepository {

    private final JdbcTemplate jdbc;
    private final MagicData magicData;
    /**
     * Получение списка всех пользователей из базы данных.
     *
     * @return Список пользователей.
     */
    public List<User> findAll() {
        String sql = magicData.getFindAllQuery();

        RowMapper<User> userRowMapper = (r, i) -> {
            User rowObject = new User();
            rowObject.setId(r.getInt("id"));
            rowObject.setFirstName(r.getString("firstName"));
            rowObject.setLastName(r.getString("lastName"));
            return rowObject;
        };

        return jdbc.query(sql, userRowMapper);
    }
    /**
     * Сохранение нового пользователя в базе данных.
     *
     * @param user Новый пользователь для сохранения.
     * @return Сохраненный пользователь.
     */
    public User save(User user) {
        String sql = magicData.getSaveQuery();
        jdbc.update(sql, user.getFirstName(), user.getLastName());
        return user;
    }
    /**
     * Удаление пользователя из базы данных по идентификатору.
     *
     * @param id Идентификатор пользователя для удаления.
     */
    public void deleteById(int id) {
        String sql = magicData.getDeleteByIdQuery();
        jdbc.update(sql, id);
    }
    /**
     * Обновление данных пользователя в базе данных.
     *
     * @param id   Идентификатор пользователя для обновления.
     * @param user Обновленные данные пользователя.
     */
    public void updateUser(int id, User user) {
        String sql = magicData.getUpdateUserQuery();
        jdbc.update(sql, user.getFirstName(), user.getLastName(), id);
    }
    /**
     * Получение пользователя из базы данных по идентификатору.
     *
     * @param id Идентификатор пользователя.
     * @return Найденный пользователь или null, если пользователь не найден.
     */
    public User getOne(int id) {
        String sql = magicData.getGetOneQuery();
        List<User> users = jdbc.query(sql, (rs, rowNum) -> {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setFirstName(rs.getString("firstName"));
            user.setLastName(rs.getString("lastName"));
            return user;
        }, id);

        return users.isEmpty() ? null : users.get(0);
    }
}
