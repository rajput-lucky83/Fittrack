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

## Run it
1. Install JDK 11+, Maven, MySQL 8.
2. Create the database: `mysql -u root -p < sql/schema.sql`
3. Edit `src/main/resources/db.properties` with your MySQL username/password.
4. Start: `mvn clean package cargo:run`
5. Open http://localhost:8080/fittrack

Default admin (created automatically on first start): `admin@fittrack.com` / `admin123`
Register a normal user from the Register page.

## Push to GitHub
```
git init
git add .
git commit -m "Initial commit: FitTrack fitness tracking app"
git branch -M main
git remote add origin https://github.com/<your-username>/fittrack.git
git push -u origin main
```
