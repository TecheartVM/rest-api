package com.techeart.restapi.api.controller;

import com.techeart.restapi.api.data.DataRequestDto;
import com.techeart.restapi.api.data.DataResponseDto;
import com.techeart.restapi.api.data.UserPatchDto;
import com.techeart.restapi.api.model.User;
import com.techeart.restapi.service.UsersService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(path = "api/v1/users")
public class UsersController
{
    private final UsersService usersService;

    @Autowired
    public UsersController(UsersService usersService)
    {
        this.usersService = usersService;
    }

    @GetMapping
    public DataResponseDto getPage(@RequestParam(name = "offset", required = false) Integer offset,
                                   @RequestParam(name = "limit", required = false) Integer limit,
                                   HttpServletRequest request)
    {
        return usersService.get(offset, limit, request.getRequestURL().toString());
    }

    @GetMapping(path = "{userId}")
    public DataResponseDto getOne(@PathVariable UUID userId)
    {
        Optional<User> user = usersService.getOne(userId);
        return new DataResponseDto(user.isPresent() ? user : List.of());
    }

    @GetMapping(path = "search")
    public DataResponseDto findByBirthDate(
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam("minDate") LocalDate minDate,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(value = "maxDate", required = false) LocalDate maxDate)
    {
        return new DataResponseDto(usersService.getByBirthDate(minDate, maxDate).toArray());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@Valid @RequestBody DataRequestDto<User> user)
    {
        usersService.add(user.getData());
    }

    @PatchMapping(path = "{userId}")
    public void update(@PathVariable UUID userId, @RequestBody DataRequestDto<UserPatchDto> data)
    {
        usersService.patch(userId, data.getData());
    }

    @PutMapping
    public void replace(@RequestBody DataRequestDto<User> user)
    {
        usersService.update(user.getData());
    }

    @DeleteMapping(path = "{userId}")
    public void delete(@PathVariable UUID userId)
    {
        usersService.delete(userId);
    }
}
