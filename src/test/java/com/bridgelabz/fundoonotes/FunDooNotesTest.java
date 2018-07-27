package com.bridgelabz.fundoonotes;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = FunDooNotesApplication.class)
@SpringBootTest
public class FunDooNotesTest {

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext wac;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();

	}

	// @Test
	public void registerTest() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/register").contentType(MediaType.APPLICATION_JSON).content(
				"{\"emailId\" : \"simranbodra@gmail.com\", \"password\" : \"Simran@222\",\"confirmPassword\":\"Simran@222\",\"userName\":\"Simran Bodra\",\"phoneNumber\":\"7751886716\" }")
				.accept(MediaType.APPLICATION_JSON)).andExpect(jsonPath("$.message").value("Registration Successful"))
				.andExpect(jsonPath("$.status").value(10));
	}

	// @Test
	public void loginTest() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/login").contentType(MediaType.APPLICATION_JSON)
				.content("{ \"email\" :\"simranbodra6@gmail.com\",\"password\":\"Simran@4\"}")
				.accept(MediaType.APPLICATION_JSON)).andExpect(jsonPath("$.message").value("Login Successful"))
				.andExpect(jsonPath("$.status").value(20));
	}

	// @Test
	public void activateTest() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/activateaccount").header("token",
				"eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI1YjUxODRkZTgzMzQ2YzM2NjBiZTY0MWYiLCJpYXQiOjE1MzIyMjE0ODAsInN1YiI6IjViNTE4NGRlODMzNDZjMzY2MGJlNjQxZiJ9.zXU77YE94edlk3XNIzvSfLmhtHqnvhIlj3dIf6-3Wdc")
				.accept(MediaType.TEXT_PLAIN_VALUE))
				.andExpect(jsonPath("$.message").value("Account activated successfully"))
				.andExpect(jsonPath("$.status").value(12));
	}

	// @Test
	public void forgetPasswordTest() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/resetPasswordLink").param("email", "simranbodra6@gmail.com")
				.content(MediaType.TEXT_PLAIN_VALUE)).andExpect(jsonPath("$.message").value("Successfully sent mail"))
				.andExpect(jsonPath("$.status").value(31));
	}

	// @Test
	public void resetPasswordTest() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.put("/resetPassword").contentType(MediaType.APPLICATION_JSON).header(
				"token",
				"eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI1YjUxODRkZTgzMzQ2YzM2NjBiZTY0MWYiLCJpYXQiOjE1MzIyMjE0ODAsInN1YiI6IjViNTE4NGRlODMzNDZjMzY2MGJlNjQxZiJ9.zXU77YE94edlk3XNIzvSfLmhtHqnvhIlj3dIf6-3Wdc")
				.content("{ \"password\" :\"Simran@8\",\"confirmpassword\":\"Simran@8\"}"))
				.andExpect(jsonPath("$.message").value("Password reset successful"))
				.andExpect(jsonPath("$.status").value(32));
	}

	// @Test
	public void createNewNoteTest() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/note/create").contentType(MediaType.APPLICATION_JSON).header(
				"token",
				"eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI1YjUxODRkZTgzMzQ2YzM2NjBiZTY0MWYiLCJpYXQiOjE1MzIzOTU3NDUsInN1YiI6IjViNTE4NGRlODMzNDZjMzY2MGJlNjQxZiJ9._HPw5aCnfzaqT3O2Fu-cYJaT5sFKWOVPctmtcnIY5C4")
				.content(
						"{ \"title\" :\"Title Hello\",\"description\":\"Something\",\"colour\":\"Red\",\"reminder\":\"2018-07-25T04:56:14.154Z\"}"))
				.andExpect(jsonPath("$.title").value("Title Hello"))
				.andExpect(jsonPath("$.description").value("Something"));
	}

	// @Test
	public void updateNoteTest() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.put("/note/update/{noteId}", "5b53276c83346c234525d65d")
				.contentType(MediaType.APPLICATION_JSON).requestAttr("token", "5b5184de83346c3660be641f").content(
						"{ \"title\" :\"testing\",\"description\":\"today\",\"colour\" :\"blue\",\"reminder\" :\"2018-07-28T10:02:27.547Z\"}"))
				.andExpect(jsonPath("$.message").value("Note Successfully updated"))
				.andExpect(jsonPath("$.status").value(91));

	}

	// @Test
	public void deleteNoteTest() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.put("/note/delete/{noteId}", "5b53276c83346c234525d65d")
				.contentType(MediaType.APPLICATION_JSON)
				.header("token",
						"eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI1YjUxODRkZTgzMzQ2YzM2NjBiZTY0MWYiLCJpYXQiOjE1MzIzOTU3NDUsInN1YiI6IjViNTE4NGRlODMzNDZjMzY2MGJlNjQxZiJ9._HPw5aCnfzaqT3O2Fu-cYJaT5sFKWOVPctmtcnIY5C4")
				.accept(MediaType.TEXT_PLAIN_VALUE))
				.andExpect(jsonPath("$.message").value("Note Successfully moved to trash"))
				.andExpect(jsonPath("$.status").value(92));
	}

	//@Test
	/*public void permanentDeleteNoteTest() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/note/permanentDelete/{noteId}"), "5b53276c83346c234525d65d")
				.contentType(MediaType.APPLICATION_JSON)
				.header("token",
						"eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI1YjUxODRkZTgzMzQ2YzM2NjBiZTY0MWYiLCJpYXQiOjE1MzIzOTU3NDUsInN1YiI6IjViNTE4NGRlODMzNDZjMzY2MGJlNjQxZiJ9._HPw5aCnfzaqT3O2Fu-cYJaT5sFKWOVPctmtcnIY5C4")
				.accept(MediaType.TEXT_PLAIN).andExpect(jsonPath("$.message").value("Note Successfully moved to trash"))
				.andExpect(jsonPath("$.status").value(92));
	}*/
}
