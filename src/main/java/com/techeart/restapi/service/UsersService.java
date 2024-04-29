package com.techeart.restapi.service;

import com.techeart.restapi.api.data.DataResponseDto;
import com.techeart.restapi.api.data.DataResponsePageDto;
import com.techeart.restapi.api.data.UserPatchDto;
import com.techeart.restapi.api.exception.ApiRequestException;
import com.techeart.restapi.api.exception.BadEmailException;
import com.techeart.restapi.api.model.PaginationInfo;
import com.techeart.restapi.api.model.PaginationLinks;
import com.techeart.restapi.api.model.User;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;

@Service
public class UsersService
{
    private final ConfigurationService config;

    @Autowired
    public UsersService(ConfigurationService config)
    {
        this.config = config;
    }

    public DataResponseDto get(@Nullable Integer offset, @Nullable Integer limit, String url)
    {
        int ofs = offset == null ? config.getPaginationDefaultOffset() : offset;
        int lim = limit == null ? config.getPaginationDefaultLimit() : limit;

        return createResponsePage(USERS, ofs, lim, url);
    }

    public User getOne(UUID userId) throws ApiRequestException
    {
        for (User u : USERS)
        {
            if (userId.equals(u.getId()))
                return u;
        }

        throw ApiRequestException.notFound("No user found on given id.");
    }

    public List<User> getByBirthDate(@Nonnull LocalDate minBirthDate, @Nullable LocalDate maxBirthDate)
            throws ApiRequestException
    {
        LocalDate maxDate = maxBirthDate == null ? LocalDate.now() : maxBirthDate;

        if (minBirthDate.isAfter(maxDate))
            throw ApiRequestException.badRequest("Argument 'minBirthDate' cannot be less than 'maxBirthDate'.");

        List<User> result = new ArrayList<>();

        USERS.forEach(u -> {
            LocalDate bd = u.getBirthDate();
            if (bd.isAfter(minBirthDate) && bd.isBefore(maxDate))
                result.add(u);
        });

        return result;
    }

    public User add(@Nullable User user) throws ApiRequestException
    {
        if (user == null)
            throw ApiRequestException.badRequest("Can't create new user: no valid user data provided.");

        int userAge = getUserAgeInYears(user);
        if (!isUserAgeValid(userAge))
        {
            if (userAge == -1)
                throw ApiRequestException.badRequest("Wrong user birth date: " + user.getBirthDate());
            else
                throw ApiRequestException.forbidden("Illegal user age: " + userAge);
        }

        for (User u : USERS)
        {
            if (u.getEmail().equals(user.getEmail()))
                throw new BadEmailException("Email address is already in use.");
        }

        UUID id = UUID.randomUUID();
        User result = new User(
                id,
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getBirthDate(),
                user.getAddress(),
                user.getPhoneNumber()
        );
        USERS.add(result);

        return result;
    }

    public void update(@Nullable User user) throws ApiRequestException
    {
        if (user == null)
            throw ApiRequestException.badRequest("Can't update user: no valid user data provided.");

        int index = getUpdateIndex(user);

        if (index == -2)
            throw new BadEmailException("Email address is already in use.");
        else if (index < 0)
            throw ApiRequestException.notFound("No user found on given id.");

        USERS.set(index, user);
    }

    public void patch(@Nullable UUID userId, @Nullable UserPatchDto data) throws ApiRequestException
    {
        if (userId == null)
            throw ApiRequestException.badRequest("User id can not be null.");

        if (data == null)
            throw ApiRequestException.badRequest("Can't update user: no valid user data provided.");

        User original = getOne(userId);

        User user = data.patchUser(original);
        update(user);
    }

    public void delete(UUID userId) throws ApiRequestException
    {
        for (int i = 0; i < USERS.size(); i++)
        {
            if (USERS.get(i).getId().equals(userId))
            {
                USERS.remove(i);
                return;
            }
        }

        throw ApiRequestException.notFound("No user found on given id.");
    }

    private boolean isUserAgeValid(int ageYears)
    {
        return ageYears >= config.getUserMinAge();
    }

    private int getUserAgeInYears(User user)
    {
        if (user.getBirthDate().isAfter(LocalDate.now()))
            return -1;

        return Period.between(user.getBirthDate(), LocalDate.now()).getYears();
    }

    /**
     * Searches for the index of an updatable user in 'USERS' list.
     * <p>
     * Returns -2 if user email is already in use.
     * Returns -1 if user with specified id has not been found.
     * Otherwise, returns the index of a user with the same id in list.
     * */
    private int getUpdateIndex(User user)
    {
        /*
         * searching for first id match
         * and for any email match
         * in the same loop
         *
         * updating value in list only if
         * specified user id is found
         * and there is no users with the same email and different id
         * */

        int idMatchIndex = -1;
        int emailMatchIndex = -1;

        for (int i = 0; i < USERS.size(); i++)
        {
            User u = USERS.get(i);

            if (idMatchIndex < 0 && user.getId().equals(u.getId()))
            {
                idMatchIndex = i;
            }

            if (user.getEmail().equals(u.getEmail()))
            {
                emailMatchIndex = i;
                break;
            }
        }

        if (emailMatchIndex >= 0 && idMatchIndex != emailMatchIndex)
            return -2;

        return idMatchIndex;
    }

    private DataResponseDto createResponsePage(List<User> from, int offset, int limit, String url)
    {
        int total = from.size();
        if (total == 0)
            return new DataResponseDto();

        if (offset >= total)
            return new DataResponseDto();

        limit = Math.min(limit, config.getPaginationMaxLimit());
        limit = Math.min(limit, total - offset);

        if (limit <= 0)
            return new DataResponseDto();

        int lastIndex = offset + limit;

        List<User> data = from.subList(offset, lastIndex);
        PaginationInfo pagination = new PaginationInfo(offset, limit, total);
        PaginationLinks links = new PaginationLinks();

        if (lastIndex < total)
        {
            String nextPageLink = createPageUrl(url, lastIndex, limit);
            links.setNext(nextPageLink);
        }

        if (offset > 0)
        {
            int prevOfs = Math.max(offset - limit, 0);
            String prevPageLink = createPageUrl(url, prevOfs, limit);
            links.setPrev(prevPageLink);
        }

        return new DataResponsePageDto(pagination, links, data.toArray());
    }

    private String createPageUrl(String baseUrl, int offset, int limit)
    {
        return baseUrl + "?offset=" + offset + "&limit=" + limit;
    }

    /**
     * Used only for the simulation of a repository
     * because of persistence layer is absent in this demo app.
     * */
    private List<User> USERS = new ArrayList<>();

    /**
     * Used only for testing purposes
     * because of persistence layer is absent in this demo app.
     * */
    public void initUsersList(List<User> data)
    {
        USERS = data;
    }
}
