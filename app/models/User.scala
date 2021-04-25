package models

import java.time.{LocalDate, LocalDateTime}

/**
 * This is the User model that defines the user structure in the database
 * @param unique_id a unique ID that all users possess
 * @param username the username associated with this user
 * @param pass the password for this user account
 * @param fullname the fullname of this user
 * @param dob the date of birth of the user
 * @param toc the time of creation of this user account accurate to milliseconds
 */
case class User(unique_id: String, username: String, pass: String, fullname: String, dob: LocalDate, toc: LocalDateTime)

