package com.adw1n.hubtraffic.controllers;

import com.adw1n.hubtraffic.config.WithOAuth2Authentication;
import com.adw1n.hubtraffic.config.WithOAuth2AuthenticationSecurityContextFactory;
import com.adw1n.hubtraffic.models.GithubUser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest
@WithOAuth2Authentication
public class HomeControllerTest {
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webapp;

    @Before
    public void setup() {
        mockMvc = webAppContextSetup(webapp).build();
    }

    @Test
    public void testIndex() throws Exception{
        this.mockMvc.perform(
                get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }
}
