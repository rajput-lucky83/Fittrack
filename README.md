# FitTrack - Online Fitness Tracking Application

Java web project (Servlets + JSP + JDBC + MySQL) with two roles: **Admin** and **User**.

## Tech stack
- Java 11, Servlet 4.0 / JSP 2.3 / JSTL 1.2 (Tomcat 9)
- JDBC with MySQL 8 (PreparedStatement, transactions, DAO pattern)
- Maven (WAR packaging)
- Chart.js (loaded from CDN) for graphs

## Features
**User:** register/login, workout log (add/edit/delete, calorie estimate), progress charts, weekly goals,
join/leave challenges, challenge history, personalised tips, guidance library (submit content), profile + password.

**Admin:** dashboard with statistics charts, live activity feed (polls every 5 s), user management
(create/edit/deactivate/delete), approve/reject user content, create challenges, system settings.

## Project structure
```
sql/schema.sql                  database tables + default settings
src/main/java/com/fittrack/
    model/      Person (abstract) -> User, Workout, Goal, Challenge, FitnessContent, enums
    dao/        CrudDao<T> interface + JDBC DAOs
    service/    GoalService, RecommendationService
    servlet/    controllers (login, workouts, admin ...)
    filter/     AuthFilter (role based access), EncodingFilter
    exception/  ValidationException, DuplicateEmailException
    util/       DBConnection, PasswordUtil, Validator, CalorieCalculator, JsonUtil
src/main/resources/db.properties   DB url / user / password
src/main/webapp/WEB-INF/views/     JSP pages (user/, admin/, common/)
```

## Run the project

## Prerequisites:

- JDK 11 or later
- Apache Maven
- MySQL 8
- Apache Tomcat 9

## Setup

1. Clone the repository:
   
   git clone https://github.com/rajput-lucky83/Fittrack.git
cd Fittrack

2. Create the database:
   
   mysql -u root -p < sql/schema.sql

3. Configure your local MySQL credentials in "src/main/resources/db.properties". Never commit this file or share your database password.

4. Build the project:
   
   mvn clean package

5. Copy "target/fittrack.war" into the Apache Tomcat 9 "webapps" directory.

6. Start Tomcat and open:
   "http://localhost:8080/fittrack"

Note: MySQL must be running, and the database credentials must be configured correctly.
