package com.techeart.restapi.service;

import com.techeart.restapi.api.data.DataResponseDto;
import com.techeart.restapi.api.data.DataResponsePageDto;
import com.techeart.restapi.api.data.UserPatchDto;
import com.techeart.restapi.api.exception.ApiRequestException;
import com.techeart.restapi.api.exception.BadEmailException;
import com.techeart.restapi.api.model.PaginationInfo;
import com.techeart.restapi.api.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@SpringBootTest
class UsersServiceTests
{
	/**
	 * Used only for the simulation of a repository
	 * because of persistence layer is absent in this demo app.
	 * */
	private List<User> userRepo;

	private static ConfigurationService config;
	private UsersService testedObj;

	private static final String apiPath = "/api/v1/users";

	@BeforeAll
    static void setupAll()
	{
		config = Mockito.mock(ConfigurationService.class);
		Mockito.when(config.getUserMinAge()).thenReturn(18);
		Mockito.when(config.getPaginationDefaultOffset()).thenReturn(0);
		Mockito.when(config.getPaginationDefaultLimit()).thenReturn(2);
		Mockito.when(config.getPaginationMaxLimit()).thenReturn(10);
	}

	@BeforeEach
	void setupCurrent()
	{
		testedObj = new UsersService(config);

		userRepo = new ArrayList<>(Arrays.asList(
                new User(UUID.randomUUID(), "bob@gmail.com", "Bob", "Washington", LocalDate.of(1996, 6, 13)),
                new User(UUID.randomUUID(), "john@gmail.com", "John", "Warner", LocalDate.of(2008, 10, 2)),
                new User(UUID.randomUUID(), "mari@gmail.com", "Mari", "Swanson", LocalDate.of(2001, 3, 26))
        ));

		testedObj.initUsersList(userRepo);
	}

	@Test
	public void getOne_gettingUser_returnsUser()
	{
		// given
		User user = userRepo.getFirst();
		UUID id = user.getId();

		// when
		User result = testedObj.getOne(id);

		// then
		Assertions.assertEquals(result, user);
	}

	@Test
	public void getOne_userNotFound_throwsException()
	{
		// given
		UUID id = UUID.randomUUID();

		// then
		ApiRequestException thrown = Assertions.assertThrows(ApiRequestException.class, () -> testedObj.getOne(id));
		Assertions.assertEquals(thrown.getStatusCode(), HttpStatus.NOT_FOUND);
	}

	@Test
	public void get_gettingUsers_returnsPaginatedData()
	{
		// given
		int offset = 0;
		int limit = userRepo.size();
		int total = userRepo.size();

		// when
		DataResponseDto result = testedObj.get(offset, limit, apiPath+"?offset="+offset+"&limit="+limit);

		// then
        Assertions.assertInstanceOf(DataResponsePageDto.class, result);

		DataResponsePageDto dto = (DataResponsePageDto)result;
        Assertions.assertEquals(dto.getData().size(), total);
		Assertions.assertNotNull(dto.getLinks());

		PaginationInfo pag = dto.getPagination();
		Assertions.assertNotNull(pag);
		Assertions.assertEquals(pag.getOffset(), offset);
		Assertions.assertEquals(pag.getLimit(), limit);
		Assertions.assertEquals(pag.getTotal(), total);
	}

	@Test
	public void get_offsetTooLarge_returnsEmpty()
	{
		// given
		int offset = userRepo.size();
		int limit = userRepo.size();

		// when
		DataResponseDto result = testedObj.get(offset, limit, apiPath+"?offset="+offset+"&limit="+limit);

		// then
		Assertions.assertFalse(result instanceof DataResponsePageDto);
		Assertions.assertEquals(result.getData().size(), 0);
	}

	@Test
	public void get_negativeLimit_returnsEmpty()
	{
		// given
		int offset = 0;
		int limit = -1;

		// when
		DataResponseDto result = testedObj.get(offset, limit, apiPath+"?offset="+offset+"&limit="+limit);

		// then
		Assertions.assertFalse(result instanceof DataResponsePageDto);
		Assertions.assertEquals(result.getData().size(), 0);
	}

	@Test
	public void getByBirthDate_searchSuccessful_returnsUsers()
	{
		// given
		LocalDate minDate = LocalDate.of(2000, 1, 1);
		LocalDate maxDate = LocalDate.MAX;

		// when
		List<User> result = testedObj.getByBirthDate(minDate, maxDate);

		// then
		Assertions.assertEquals(2, result.size());
	}

	@Test
	public void getByBirthDate_notFound_returnsEmpty()
	{
		// given
		LocalDate minDate = LocalDate.MAX.minusYears(1);
		LocalDate maxDate = LocalDate.MAX;

		// when
		List<User> result = testedObj.getByBirthDate(minDate, maxDate);

		// then
		Assertions.assertTrue(result.isEmpty());
	}

	@Test
	public void getByBirthDate_illegalDateRange_throwsException()
	{
		// given
		LocalDate minDate = LocalDate.MAX;
		LocalDate maxDate = LocalDate.MIN;

		// then
		ApiRequestException thrown = Assertions.assertThrows(
				ApiRequestException.class, () -> testedObj.getByBirthDate(minDate, maxDate));
		Assertions.assertEquals(thrown.getStatusCode(), HttpStatus.BAD_REQUEST);
	}

	@Test
	public void add_addedSuccessfully_returnsUser()
	{
		// given
		User toAdd = new User(UUID.randomUUID(), "test@gmail.com", "test", "test", LocalDate.of(1996, 6, 13));

		// when
		User result = testedObj.add(toAdd);

		// then
		Assertions.assertNotNull(result);
		/*id must be changed because it has to be generated by database (for demo it is generated in method itself)*/
		Assertions.assertNotEquals(toAdd.getId(), result.getId());
	}

	@Test
	public void add_dataIsNull_throwsException()
	{
		// then
		ApiRequestException thrown = Assertions.assertThrows(ApiRequestException.class, () -> testedObj.add(null));
		Assertions.assertEquals(thrown.getStatusCode(), HttpStatus.BAD_REQUEST);
	}

	@Test
	public void add_wrongBirthDate_throwsException()
	{
		// given
		User toAdd = new User(UUID.randomUUID(), "test@gmail.com", "test", "test", LocalDate.MAX);

		// then
		ApiRequestException thrown = Assertions.assertThrows(ApiRequestException.class, () -> testedObj.add(toAdd));
		Assertions.assertEquals(thrown.getStatusCode(), HttpStatus.BAD_REQUEST);
	}

	@Test
	public void add_illegalBirthDate_throwsException()
	{
		// given
		int illegalAge = config.getUserMinAge() - 1;
		LocalDate birthDate = LocalDate.now().minusYears(illegalAge);
		User toAdd = new User(UUID.randomUUID(), "test@gmail.com", "test", "test", birthDate);

		// then
		ApiRequestException thrown = Assertions.assertThrows(ApiRequestException.class, () -> testedObj.add(toAdd));
		Assertions.assertEquals(thrown.getStatusCode(), HttpStatus.FORBIDDEN);
	}

	@Test
	public void add_emailIsInUse_throwsException()
	{
		// given
		String email = userRepo.getFirst().getEmail();
		User toAdd = new User(UUID.randomUUID(), email, "test", "test", LocalDate.of(1996, 6, 13));

		// then
		Assertions.assertThrows(BadEmailException.class, () -> testedObj.add(toAdd));
	}

	@Test
	public void update_updatedSuccessfully()
	{
		// given
		UUID id = userRepo.getFirst().getId();
		User toUpdate = new User(id, "test@test.com", "test", "test", LocalDate.of(1996, 6, 13));

		// when
		testedObj.update(toUpdate);

		// then
		Assertions.assertEquals(toUpdate, userRepo.getFirst());
	}

	@Test
	public void update_dataIsNull_throwsException()
	{
		// then
		ApiRequestException thrown = Assertions.assertThrows(ApiRequestException.class, () -> testedObj.update(null));
		Assertions.assertEquals(thrown.getStatusCode(), HttpStatus.BAD_REQUEST);
	}

	@Test
	public void update_idNotFound_throwsException()
	{
		// given
		User toUpdate = new User(UUID.randomUUID(), "test@gmail.com", "test", "test", LocalDate.of(1996, 6, 13));

		// then
		ApiRequestException thrown = Assertions.assertThrows(ApiRequestException.class, () -> testedObj.update(toUpdate));
		Assertions.assertEquals(thrown.getStatusCode(), HttpStatus.NOT_FOUND);
	}

	@Test
	public void update_emailIsInUse_throwsException()
	{
		// given
		UUID id = userRepo.getFirst().getId();
		String email = userRepo.getLast().getEmail();
		User toUpdate = new User(id, email, "test", "test", LocalDate.of(1996, 6, 13));

		// then
		Assertions.assertThrows(BadEmailException.class, () -> testedObj.update(toUpdate));
	}

	@Test
	public void patch_patchedSuccessfully()
	{
		// given
		User toPatch = userRepo.getFirst();
		String email = String.valueOf(toPatch.getEmail());
		String lastName = String.valueOf(toPatch.getLastName());
		LocalDate birthDate = toPatch.getBirthDate();

		UUID id = toPatch.getId();
		UserPatchDto data = new UserPatchDto(null, "test", null, null, null, null);

		// when
		testedObj.patch(id, data);

		// then
		User result = userRepo.getFirst();

		/*check changed field*/
		Assertions.assertEquals(data.getFirstName(),result.getFirstName());

		/*other fields must remain the same*/
		Assertions.assertEquals(email, result.getEmail());
		Assertions.assertEquals(lastName, result.getLastName());
		Assertions.assertEquals(birthDate, result.getBirthDate());
	}

	@Test
	public void patch_idIsNull_throwsException()
	{
		// then
		ApiRequestException thrown = Assertions.assertThrows(ApiRequestException.class, () -> testedObj.patch(null, null));
		Assertions.assertEquals(thrown.getStatusCode(), HttpStatus.BAD_REQUEST);
	}

	@Test
	public void patch_dataIsNull_throwsException()
	{
		// given
		UUID id = userRepo.getFirst().getId();

		// then
		ApiRequestException thrown = Assertions.assertThrows(ApiRequestException.class, () -> testedObj.patch(id, null));
		Assertions.assertEquals(thrown.getStatusCode(), HttpStatus.BAD_REQUEST);
	}

	@Test
	public void patch_idNotFound_throwsException()
	{
		// given
		UUID id = UUID.randomUUID();
		UserPatchDto data = new UserPatchDto();

		// then
		ApiRequestException thrown = Assertions.assertThrows(ApiRequestException.class, () -> testedObj.patch(id, data));
		Assertions.assertEquals(thrown.getStatusCode(), HttpStatus.NOT_FOUND);
	}

	@Test
	public void patch_emailIsInUse_throwsException()
	{
		// given
		UUID id = userRepo.getFirst().getId();
		String email = userRepo.getLast().getEmail();
		UserPatchDto data = new UserPatchDto(email, "test", "test", LocalDate.of(1996, 6, 13), null, null);

		// then
		Assertions.assertThrows(BadEmailException.class, () -> testedObj.patch(id, data));
	}

	@Test
	public void delete_deletedSuccessfully()
	{
		// given
		UUID id = userRepo.getFirst().getId();
		int resultSize = userRepo.size() - 1;

		// when
		testedObj.delete(id);

		// then
		Assertions.assertEquals(resultSize, userRepo.size());
	}

	@Test
	public void delete_idNotFound_throwsException()
	{
		// given
		UUID id = UUID.randomUUID();

		// then
		ApiRequestException thrown = Assertions.assertThrows(ApiRequestException.class, () -> testedObj.delete(id));
		Assertions.assertEquals(thrown.getStatusCode(), HttpStatus.NOT_FOUND);
	}
}
