package at.mci.bugtracker.controller;

import at.mci.bugtracker.auth.SessionStore;
import at.mci.bugtracker.dao.UserDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserDao users;

    @MockBean
    private SessionStore sessions;

    @Test
    void loginReturnsFieldValidationMessages() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "",
                                  "password": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.username").value("Username darf nicht leer sein"))
                .andExpect(jsonPath("$.password").value("Passwort darf nicht leer sein"));
    }

    @Test
    void registerReturnsFieldValidationMessages() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "ab",
                                  "email": "invalid",
                                  "password": "short",
                                  "passwordConfirm": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.username").value("Username muss zwischen 3 und 50 Zeichen lang sein"))
                .andExpect(jsonPath("$.email").value("Ungueltiges E-Mail-Format"))
                .andExpect(jsonPath("$.password").value("Passwort muss zwischen 8 und 255 Zeichen lang sein"))
                .andExpect(jsonPath("$.passwordConfirm").exists());
    }

    @Test
    void changePasswordReturnsFieldValidationMessages() throws Exception {
        mockMvc.perform(post("/api/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "currentPassword": "",
                                  "newPassword": "short"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.currentPassword").value("Aktuelles Passwort darf nicht leer sein"))
                .andExpect(jsonPath("$.newPassword").value("Neues Passwort muss zwischen 6 und 255 Zeichen lang sein"));
    }
}
