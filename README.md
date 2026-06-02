# Pigeon Mail

Pigeon Mail is a simple email system . It allows users to create an account, log in, send emails, view received emails, view sent emails, and search emails using keywords.



## What Is Included

- Login page
- Registration page
- Main page after successful login or registration
- Compose page using email accounts instead of numeric user IDs
- Inbox page for received emails
- Sent Emails page for emails sent by the logged-in user
- Search for inbox and sent emails by subject or body keyword
- Transparent Pigeon Mail logo used in the UI

## Main Features

### Login and Registration

Users can register with:

- Full name
- Email address
- Password

The registration page checks that:

- All fields are filled
- Name only contains letters and spaces
- Email has a valid format
- Email is not already registered

After successful login or registration, the user is taken to the main page.

### Main Page

The main page has buttons for:

- Compose
- Inbox
- Sent
- Logout

### Compose Email

The compose page lets the logged-in user send an email by entering:

- Receiver email
- Subject
- Body

The system uses email accounts instead of user IDs.

### Inbox and Sent Emails

The inbox shows emails received by the logged-in user.

The sent page shows emails sent by the logged-in user.

Both pages allow searching by keyword in the subject or body.

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

## Tables Used

### users

Stores registered users.

Important columns:

- `user_id`
- `full_name`
- `email`
- `password_hash`
- `created_at`
- `last_login`

### emails

Stores the email message.

Important columns:

- `email_id`
- `sender_id`
- `subject`
- `body`
- `sent_at`

### email_recipients

Stores the receiver of each email.

Important columns:

- `recipient_id`
- `email_id`
- `receiver_id`
- `is_read`

## Run

With Maven:

```bash
mvn javafx:run
```


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

## Important Files

| File | Purpose |
| --- | --- |
| `Main.java` | Starts the JavaFX application |
| `LoginPage.java` | Shows the login screen |
| `RegistrationPage.java` | Shows the registration screen and checks user input |
| `MainPage.java` | Shows the main menu after login |
| `ComposePage.java` | Allows the user to send an email |
| `MailPage.java` | Shows inbox and sent emails |
| `DatabaseConnection.java` | Handles database connection and email queries |
| `UserManagement.java` | Handles login, registration, and password hashing |
| `UserSession.java` | Stores the logged-in user's information |
| `EmailRecord.java` | Stores email information for display |
| `AppStyle.java` | Contains common UI styling and logo loading |

## Notes

- The app must connect to MySQL before login or registration can work.
- The receiver email must already be registered before an email can be sent to it.
- The logo file is stored at `src/resourses/images/pigeon-mail-logo-transparent.png`.
- The project is intentionally simple and does not use separate controller, model, and view folders.
