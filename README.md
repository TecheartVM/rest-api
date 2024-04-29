# REST API demo application
## Main features
1. Represents an API giving access to repository of Users.
2. User model has the following fields:
   
    - ID (generated)
    - Email (required). Has validation against email pattern
    - First name (required)
    - Last name (required)
    - Birthdate (required). Value must be earlier than current date
    - Address (optional)
    - Phone number (optional)
   
3. API has the following functionality:
    - Get User by ID.
    - Search for all Users with the birthdate in specified range.
    - Get list of users with pagination.
    - Create new user.
        Checks User age is less than 18 (value taken from properties file).
        Validates Email address.
        Checks the existence of specified Email.
        Validates phone number.
        Validates the length of First and Last name
    - Update one/some User fields.
    - Update all User fields.
    - Delete User. 
4. Code is covered by unit tests using Spring.
5. Code has error handling for REST.
6. API responses are in JSON format.
7. Data persistence layer is no included.

## Data format
#### User data
```json
{
  "id": "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx", 
  "email": "some_email123@mail.com", 
  "firstName": "John", 
  "lastName": "Smith", 
  "birthDate": "YEAR-MONTH-DAY",
  "phoneNumber": "380991231234",
  "address": "Some Address"
}
```

## API
### Create new user
```javascript
POST api/v1/users
```
##### Input
```json
{
  "data": {
    "email": "some_email123@mail.com",
    "firstName": "Bob",
    "lastName": "Smith",
    "birthDate": "2000-01-01"
  }
}
```
##### Output
```
Status code 201 if registration successful or 4XX otherwise
```

### Get list of users
```javascript
GET api/v1/users
GET api/v1/users?offset=0&limit=1
```
##### Output
```json
{
  "pagination": {
    "offset": 0,
    "limit": 1,
    "total": 10
  },
  "data": [
    {
      "id": "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx",
      "email": "some_email123@mail.com",
      "firstName": "John",
      "lastName": "Smith",
      "birthDate": "2000-01-01",
      "phoneNumber": null,
      "address": null
    }
  ],
  "links": {
    "next": null,
    "prev": "Some URL"
  }
}
```

### Get user by id
```javascript
GET api/v1/users/{id}
```
##### Output
```json
{
  "data": [
    {
      "id": "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx",
      "email": "some_email123@mail.com",
      "firstName": "John",
      "lastName": "Smith",
      "birthDate": "2000-01-01",
      "phoneNumber": null,
      "address": null
    }
  ]
}
```

### Get users by birthdate range
```javascript
GET api/v1/users/search?minDate=2000-01-01
GET api/v1/users/search?minDate=2000-01-01&maxDate=2001-01-01
```
##### Output
```json
{
  "data": [
    {
      "id": "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx",
      "email": "some_email123@mail.com",
      "firstName": "John",
      "lastName": "Smith",
      "birthDate": "2000-01-01",
      "phoneNumber": null,
      "address": null
    }
  ]
}
```

### Update one/some user fields
```javascript
PATCH api/v1/users/{id}
```
##### Input
```json
{
  "data": {
    "email": "some_email123@mail.com",
    "firstName": "John",
    "lastName": "Smith",
    "birthDate": "2000-01-01",
    "phoneNumber": "380990009900",
    "address": "Some Address"
  }
}
```
##### Output
```
Status code 200 if user fields successfully updated or 4XX otherwise
```

### Update all user fields
```javascript
PUT api/v1/users
```
##### Input
```json
{
  "data": {
    "id": "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx",
    "email": "some_email123@mail.com",
    "firstName": "John",
    "lastName": "Smith",
    "birthDate": "2000-01-01",
    "phoneNumber": "380990009900",
    "address": "Some Address"
  }
}
```
##### Output
```
Status code 200 if user successfully updated or 4XX otherwise
```

### Delete user
```javascript
DELETE api/v1/users/{id}
```
##### Output
```
Status code 200 if user successfully deleted or 4XX otherwise
```
