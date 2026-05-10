package at.mci.bugtracker.dao;

import at.mci.bugtracker.db.Database;
import at.mci.bugtracker.model.User;
import at.mci.bugtracker.model.UserRole;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public class UserDao {
    private final Database db;
    public UserDao(Database db) {
        this.db = db;
    }

    public User findById(long id) {
        return db.queryOne(
                "SELECT id, username, email, password_hash, role, created_at FROM users WHERE id = ?",
                rs -> new User(
                        rs.getLong("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password_hash"),
                        UserRole.valueOf(rs.getString("role")),
                        rs.getBoolean("active"),
                        rs.getObject("created_at", OffsetDateTime.class)
                ),
                id
        );
    }

    public User findByUsername(String userName) {
        return db.queryOne(
                "SELECT id, username, email, password_hash, role, created_at FROM users WHERE username = ?",
                rs -> new User(
                        rs.getLong("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password_hash"),
                        UserRole.valueOf(rs.getString("role")),
                        rs.getBoolean("active"),
                        rs.getObject("created_at", OffsetDateTime.class)
                ),
                userName
        );
    }

    public User findByEmail(String email) {
        return db.queryOne(
                "SELECT id, username, email, password_hash, role, created_at FROM users WHERE email = ?",
                rs -> new User(
                        rs.getLong("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password_hash"),
                        UserRole.valueOf(rs.getString("role")),
                        rs.getBoolean("active"),
                        rs.getObject("created_at", OffsetDateTime.class)
                ),
                email
        );
    }

    public User save(String username, String email, String passwordHash, UserRole role) {
        long id = db.insertReturningId(
                "INSERT INTO users (username, email, password_hash, role) VALUES (?, ?, ?, ?) RETURNING id",
                username, email, passwordHash, role.name()
        );
        return findById(id);
    }

    public void updateRole(long id, UserRole role) {
        db.update("UPDATE users SET role = ? WHERE id = ?", role.name(), id);
    }

    public void updatePassword(long id, String passwordHash) {
        db.update("UPDATE users SET password_hash = ? WHERE id = ?", passwordHash, id);
    }

    public boolean existsByUsernameOrEmail(String username, String email) {
        Integer count = db.queryOne(
                "SELECT COUNT(*) AS n FROM users WHERE username = ? OR email = ?",
                rs -> rs.getInt("n"),
                username, email
        );
        return count != null && count > 0;
    }

    public List<User> findAll() {
        return db.query(
                "SELECT id, username, email, role, active, created_at FROM users ORDER BY username ASC",
                rs -> new User(
                        rs.getLong("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password_hash"),
                        UserRole.valueOf(rs.getString("role")),
                        rs.getBoolean("active"),
                        rs.getObject("created_at", OffsetDateTime.class)
                )
        );
    }
}
