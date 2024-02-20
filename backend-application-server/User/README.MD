# The Database SetUp Module
### Sets up the tables in utilitymanagementdb

user

utility_account

user_utility_link

meter_reading

Run the application with spring.jpa.hibernate.ddl-auto= create-drop for the first time. Next time onwards set it as update to not run the initialization scripts over and over again.
### Handles the user authentication and authorization + generation of access tokens

