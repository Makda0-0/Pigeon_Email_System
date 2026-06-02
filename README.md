# Pigeon Mail

Simple JavaFX email system merged from the login/registration, database, and mail screen parts.

## What Is Included

- Login page
- Registration page
- Main page after successful login or registration
- Compose page using email accounts instead of numeric user IDs
- Inbox page for received emails
- Sent Emails page for emails sent by the logged-in user
- Search for inbox and sent emails by subject or body keyword

## Database Setup

Create a MySQL database named `pigeon`.

The app creates these tables automatically when it starts:

- `users`
- `emails`
- `email_recipients`

Database credentials are in:

`src/Java/com/template/DatabaseConnection.java`

Current values:

```java
private static final String URL = "jdbc:mysql://localhost:3306/pigeon";
private static final String USER = "admin";
private static final String PASSWORD = "1234";
```

Change those values if your MySQL runs on another port or uses another username/password.

## Run

With Maven installed:

```bash
mvn javafx:run
```

In IntelliJ IDEA:

1. Open this folder as a Maven project.
2. Wait for Maven import to finish.
3. Select the run configuration named `Pigeon Mail JavaFX`.
4. Click Run.

Main class:

```text
com.template.Main
```

## Code Structure

The project now uses the same simple style as the AP Pigeon project:

- Java files are in `src/Java/com/template`
- Images are in `src/resourses/images`
- All classes use the same `com.template` package
- Main class is `com.template.Main`

All needed code is now together in the `com.template` package.
