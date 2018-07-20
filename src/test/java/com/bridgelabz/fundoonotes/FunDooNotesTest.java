package com.bridgelabz.fundoonotes;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import org.junit.Before;
import org.junit.Test;
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

    //@Test
    public void registerTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/register").contentType(MediaType.APPLICATION_JSON).content(
                "{\"emailId\" : \"\", \"password\" : \"Simran@222\",\"confirmPassword\":\"Simran@222\",\"userName\":\"Simran Bodra\",\"phoneNumber\":\"7751886716\" }")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Registration Successful"))
                .andExpect(jsonPath("$.status").value(10));
    }
   
    //@Test
    public void login() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/login").contentType(MediaType.APPLICATION_JSON)
                .content("{ \"email\" :\"simranbodra6@gmail.com\",\"password\":\"Simran@4\"}")
                .accept(MediaType.APPLICATION_JSON))
                //.andExpect(jsonPath("$.email").exists())
                //.andExpect(jsonPath("$.password").exists())
                .andExpect(jsonPath("$.message").value("Login Successful"))
                .andExpect(jsonPath("$.status").value(20));
    }
}
