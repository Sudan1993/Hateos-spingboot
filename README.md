# Hateos-spingboot

This project involves 
- spring security
- session management
- ability to login using email id & password.

Technical Details :

- The endpoint /cars/* is secured where in a person has to login with mailid and password and validate them.
- During the time of validation session-token is saved
- A filter is put in place where the cars request is handled and it checks for the session-token availability
- If not present the user is asked to validate the mail address.
- CRUD operation on CAR Pojo using HATEOS, the root url is /cars/
  > (No swagger link available)
