package at.mci.bugtracker.dao;

import at.mci.bugtracker.db.Database;
import at.mci.bugtracker.model.User;
import at.mci.bugtracker.model.UserRole;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDaoTest {

    @Mock private Database db;
    @Mock private ResultSet rs;
    @InjectMocks private UserDao dao;

    private static final OffsetDateTime CREATED_AT = OffsetDateTime.parse("2026-01-01T00:00:00Z");

    @BeforeAll
    static void setUp() throws Exception {
        System.out.println("Testing UserDao.java");
    }

    // --- findById ---

    @Test
    void findById_returnsUser_whenFound() throws SQLException {
        setupUserResultSet();
        stubQueryOneWithMapper();

        User user = dao.findById(1);

        assertThat(user.id()).isEqualTo(1);
        assertThat(user.username()).isEqualTo("marie");
        assertThat(user.email()).isEqualTo("marie@example.com");
        assertThat(user.role()).isEqualTo(UserRole.TESTER);
        assertThat(user.active()).isTrue();
    }

    @Test
    void findById_returnsNull_whenNotFound() {
        when(db.queryOne(anyString(), any(Database.RowMapper.class), any())).thenReturn(null);

        assertThat(dao.findById(999)).isNull();
    }

    // --- findByUsername ---

    @Test
    void findByUsername_returnsUser_whenFound() throws SQLException {
        setupUserResultSet();
        stubQueryOneWithMapper();

        User user = dao.findByUsername("marie");

        assertThat(user.username()).isEqualTo("marie");
        assertThat(user.passwordHash()).isEqualTo("$2a$10$hashedpassword");
    }

    @Test
    void findByUsername_returnsNull_whenNotFound() {
        when(db.queryOne(anyString(), any(Database.RowMapper.class), any())).thenReturn(null);

        assertThat(dao.findByUsername("nobody")).isNull();
    }

    // --- findByEmail ---

    @Test
    void findByEmail_returnsUser_whenFound() throws SQLException {
        setupUserResultSet();
        stubQueryOneWithMapper();

        User user = dao.findByEmail("marie@example.com");

        assertThat(user.email()).isEqualTo("marie@example.com");
    }

    @Test
    void findByEmail_returnsNull_whenNotFound() {
        when(db.queryOne(anyString(), any(Database.RowMapper.class), any())).thenReturn(null);

        assertThat(dao.findByEmail("nobody@example.com")).isNull();
    }

    // --- existsByUsernameOrEmail ---

    @Test
    void existsByUsernameOrEmail_returnsTrue_whenExists() throws SQLException {
        when(rs.getInt("n")).thenReturn(1);
        when(db.queryOne(anyString(), any(Database.RowMapper.class), any(), any()))
                .thenAnswer(inv -> ((Database.RowMapper<?>) inv.getArgument(1)).map(rs));

        assertThat(dao.existsByUsernameOrEmail("marie", "marie@example.com")).isTrue();
    }

    @Test
    void existsByUsernameOrEmail_returnsFalse_whenNotExists() throws SQLException {
        when(rs.getInt("n")).thenReturn(0);
        when(db.queryOne(anyString(), any(Database.RowMapper.class), any(), any()))
                .thenAnswer(inv -> ((Database.RowMapper<?>) inv.getArgument(1)).map(rs));

        assertThat(dao.existsByUsernameOrEmail("nobody", "nobody@example.com")).isFalse();
    }

    @Test
    void existsByUsernameOrEmail_returnsFalse_whenDbReturnsNull() {
        when(db.queryOne(anyString(), any(Database.RowMapper.class), any(), any())).thenReturn(null);

        assertThat(dao.existsByUsernameOrEmail("x", "x@example.com")).isFalse();
    }

    // --- save ---

    @Test
    void save_insertsAndReturnsNewUser() throws SQLException {
        when(db.insertReturningId(anyString(), any(), any(), any(), any())).thenReturn(1L);
        setupUserResultSet();
        stubQueryOneWithMapper();

        User user = dao.save("marie", "marie@example.com", "$2a$10$hashedpassword", UserRole.TESTER);

        assertThat(user.id()).isEqualTo(1);
        assertThat(user.username()).isEqualTo("marie");
        verify(db).insertReturningId(anyString(), any(), any(), any(), any());
    }

    // --- updatePassword ---

    @Test
    void updatePassword_callsDbUpdate() {
        dao.updatePassword(1, "$2a$10$newHash");

        verify(db, times(1)).update(anyString(), any(), any());
    }

    // --- updateRole ---

    @Test
    void updateRole_callsDbUpdate() {
        dao.updateRole(1, UserRole.DEVELOPER);

        verify(db, times(1)).update(anyString(), any(), any());
    }

    // --- findAll ---

    @Test
    void findAll_returnsAllUsers() {
        User marie = new User(1, "marie", "marie@example.com", "$2a$10$hash", UserRole.TESTER, true, CREATED_AT);
        when(db.query(anyString(), any(Database.RowMapper.class))).thenReturn(List.of(marie));

        List<User> result = dao.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).username()).isEqualTo("marie");
    }

    @Test
    void findAll_returnsEmptyList_whenNoUsers() {
        when(db.query(anyString(), any(Database.RowMapper.class))).thenReturn(List.of());

        assertThat(dao.findAll()).isEmpty();
    }

    // --- Helpers ---

    @SuppressWarnings("unchecked")
    private void stubQueryOneWithMapper() throws SQLException {
        when(db.queryOne(anyString(), any(Database.RowMapper.class), any()))
                .thenAnswer(inv -> ((Database.RowMapper<?>) inv.getArgument(1)).map(rs));
    }

    private void setupUserResultSet() throws SQLException {
        when(rs.getLong("id")).thenReturn(1L);
        when(rs.getString("username")).thenReturn("marie");
        when(rs.getString("email")).thenReturn("marie@example.com");
        when(rs.getString("password_hash")).thenReturn("$2a$10$hashedpassword");
        when(rs.getString("role")).thenReturn("TESTER");
        when(rs.getBoolean("active")).thenReturn(true);
        when(rs.getObject("created_at", OffsetDateTime.class)).thenReturn(CREATED_AT);
    }
}
