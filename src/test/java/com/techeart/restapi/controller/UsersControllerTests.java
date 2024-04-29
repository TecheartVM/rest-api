package com.techeart.restapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.techeart.restapi.api.controller.UsersController;
import com.techeart.restapi.api.data.DataRequestDto;
import com.techeart.restapi.api.data.DataResponseDto;
import com.techeart.restapi.api.data.DataResponsePageDto;
import com.techeart.restapi.api.data.UserPatchDto;
import com.techeart.restapi.api.model.PaginationInfo;
import com.techeart.restapi.api.model.PaginationLinks;
import com.techeart.restapi.api.model.User;
import com.techeart.restapi.service.UsersService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsersController.class)
public class UsersControllerTests
{
    private static final String apiPath = "/api/v1/users";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UsersService service;

    private final ObjectMapper MAPPER;

    public UsersControllerTests()
    {
        MAPPER = new ObjectMapper();
        MAPPER.registerModule(new JavaTimeModule());
        MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    @Test
    public void getOne_gettingUser_returnsDataArrayWithUser() throws Exception
    {
        User user = new User(UUID.randomUUID(), "bob@gmail.com", "Bob", "Washington", LocalDate.of(1996, 6, 13));

        given(service.getOne(user.getId())).willReturn(user);

        mvc.perform(get(apiPath+"/{userId}", user.getId()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].id", is(user.getId().toString())))
                .andExpect(jsonPath("$.data[0].email", is(user.getEmail())))
                .andExpect(jsonPath("$.data[0].firstName", is(user.getFirstName())))
                .andExpect(jsonPath("$.data[0].lastName", is(user.getLastName())))
                .andExpect(jsonPath("$.data[0].birthDate", is(user.getBirthDate().toString())))
                .andExpect(jsonPath("$.data[0].address", is(user.getAddress())))
                .andExpect(jsonPath("$.data[0].phoneNumber", is(user.getPhoneNumber())));
    }

    @Test
    public void getOne_gettingUser_returnsError() throws Exception
    {
        UUID id = UUID.randomUUID();

        given(service.getOne(id)).willThrow(new RuntimeException("Error occurred."));

        mvc.perform(get(apiPath+"/{userId}", id).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].status").exists())
                .andExpect(jsonPath("$.errors[0].detail").exists());
    }

    @Test
    public void getPage_gettingUsersNoParams_returnsPaginatedData() throws Exception
    {
        User user = new User(UUID.randomUUID(), "bob@gmail.com", "Bob", "Washington", LocalDate.of(1996, 6, 13));
        int offset = 0;
        int limit = 1;
        int total = 1;
        DataResponsePageDto result = new DataResponsePageDto(
                new PaginationInfo(offset, limit, total),
                new PaginationLinks(),
                List.of(user).toArray()
        );

        given(service.get(any(), any(), any())).willReturn(result);

        mvc.perform(get(apiPath).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].id", is(user.getId().toString())))
                .andExpect(jsonPath("$.data[0].email", is(user.getEmail())))
                .andExpect(jsonPath("$.data[0].firstName", is(user.getFirstName())))
                .andExpect(jsonPath("$.data[0].lastName", is(user.getLastName())))
                .andExpect(jsonPath("$.data[0].birthDate", is(user.getBirthDate().toString())))
                .andExpect(jsonPath("$.data[0].address", is(user.getAddress())))
                .andExpect(jsonPath("$.data[0].phoneNumber", is(user.getPhoneNumber())))
                .andExpect(jsonPath("$.pagination.offset", is(offset)))
                .andExpect(jsonPath("$.pagination.limit", is(limit)))
                .andExpect(jsonPath("$.pagination.total", is(total)))
                .andExpect(jsonPath("$.links.next", equalTo(null)))
                .andExpect(jsonPath("$.links.prev", equalTo(null)));
    }

    @Test
    public void getPage_gettingUsersNoParams_returnsEmptyArray() throws Exception
    {
        given(service.get(any(), any(), any())).willReturn(new DataResponseDto());

        mvc.perform(get(apiPath).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(0)))
                .andExpect(jsonPath("$.pagination").doesNotExist())
                .andExpect(jsonPath("$.links").doesNotExist());
    }

    @Test
    public void getPage_gettingUsersNoParams_returnsError() throws Exception
    {
        given(service.get(any(), any(), any())).willThrow(new RuntimeException("Error occurred."));

        mvc.perform(get(apiPath).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].status").exists())
                .andExpect(jsonPath("$.errors[0].detail").exists());
    }

    @Test
    public void findByBirthDate_gettingUsers_returnsDataArray() throws Exception
    {
        User user = new User(UUID.randomUUID(), "bob@gmail.com", "Bob", "Washington", LocalDate.of(2001, 6, 13));
        List<User> result = List.of(user);

        given(service.getByBirthDate(any(), any())).willReturn(result);

        mvc.perform(get(apiPath+"/search").param("minDate", "2000-01-01").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].id", is(user.getId().toString())))
                .andExpect(jsonPath("$.data[0].email", is(user.getEmail())))
                .andExpect(jsonPath("$.data[0].firstName", is(user.getFirstName())))
                .andExpect(jsonPath("$.data[0].lastName", is(user.getLastName())))
                .andExpect(jsonPath("$.data[0].birthDate", is(user.getBirthDate().toString())))
                .andExpect(jsonPath("$.data[0].address", is(user.getAddress())))
                .andExpect(jsonPath("$.data[0].phoneNumber", is(user.getPhoneNumber())));
    }

    @Test
    public void findByBirthDate_gettingUsers_returnsEmptyArray() throws Exception
    {
        given(service.getByBirthDate(any(), any())).willReturn(List.of());

        mvc.perform(get(apiPath+"/search").param("minDate", "2020-01-01").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(0)));
    }

    @Test
    public void findByBirthDate_gettingUsersNoParams_returnsError() throws Exception
    {
        mvc.perform(get(apiPath+"/search").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].status").exists())
                .andExpect(jsonPath("$.errors[0].detail").exists());
    }

    @Test
    public void create_addingNewUser_returnsSuccessCode() throws Exception
    {
        User user = new User("bob@gmail.com", "Bob", "Washington", LocalDate.of(2001, 6, 13));
        DataRequestDto<User> data = new DataRequestDto<>(user);

        mvc.perform(post(apiPath).accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON).content(MAPPER.writeValueAsString(data)))
                .andExpect(status().isCreated());
    }

    @Test
    public void create_addingInvalidUser_returnsError() throws Exception
    {
        mvc.perform(post(apiPath).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].status").exists())
                .andExpect(jsonPath("$.errors[0].detail").exists());
    }

    @Test
    public void update_patchingUser_returnsSuccessCode() throws Exception
    {
        UserPatchDto user = new UserPatchDto(
                "bob@gmail.com", "Bob", "Washington", LocalDate.of(2001, 6, 13), null, null);
        DataRequestDto<UserPatchDto> data = new DataRequestDto<>(user);

        UUID id = UUID.randomUUID();

        mvc.perform(patch(apiPath+"/{userId}", id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON).content(MAPPER.writeValueAsString(data)))
                .andExpect(status().isOk());
    }

    @Test
    public void update_patchingUser_returnsError() throws Exception
    {
        UUID id = UUID.randomUUID();

        mvc.perform(patch(apiPath+"/{userId}", id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON).content(MAPPER.writeValueAsString(null)))
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].status").exists())
                .andExpect(jsonPath("$.errors[0].detail").exists());
    }

    @Test
    public void replace_puttingUser_returnsSuccessCode() throws Exception
    {
        User user = new User(UUID.randomUUID(), "bob@gmail.com", "Bob", "Washington", LocalDate.of(2001, 6, 13));
        DataRequestDto<User> data = new DataRequestDto<>(user);

        mvc.perform(put(apiPath)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON).content(MAPPER.writeValueAsString(data)))
                .andExpect(status().isOk());
    }

    @Test
    public void replace_puttingUser_returnsError() throws Exception
    {
        mvc.perform(put(apiPath)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON).content(MAPPER.writeValueAsString(null)))
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].status").exists())
                .andExpect(jsonPath("$.errors[0].detail").exists());
    }

    @Test
    public void delete_deletingUser_returnsSuccessCode() throws Exception
    {
        UUID id = UUID.randomUUID();

        mvc.perform(delete(apiPath+"/{userId}", id).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void delete_deletingUser_returnsError() throws Exception
    {
        UUID id = UUID.randomUUID();

        doThrow(new RuntimeException("Error occurred."));

        mvc.perform(delete(apiPath+"/{userId}", id).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].status").exists())
                .andExpect(jsonPath("$.errors[0].detail").exists());
    }
}
