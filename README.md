# Stackoverflow Clone



## Overview

This is a fully functional clone of StackOverflow that replicates the core functionalities of the original platform. Users can ask questions, provide answers, comment on discussions, and upvote or downvote content. The project is built using modern web development technologies and adheres to best practices for design and functionality.

## Features

- **User Authentication:** Users can sign up, log in, and manage their profiles.
- **Ask Questions:** Authenticated users can post questions.
- **Answer Questions:** Users can provide answers to posted questions.
- **Upvote/Downvote:** Voting functionality on both questions and answers.
- **Tags:** Add the ability to tag questions with relevant topics.
- **User Profile Page:** Display a user's asked questions, provided answers, and reputation score.
- **Sorting and Filtering:** Provide sorting options for questions by date, popularity, etc.
- **Commenting System:** Users can comment on both questions and answers.
- **Search:** Search functionality for finding specific questions.




## Technologies Used

### Backend

- **Java:** The core backend language used for development.
- **Spring Boot/MVC:** Backend framework used to create MVC endpoints and handle business logic.
- **Spring Security:** For authentication and authorization.
- **Spring Data JPA/Hibernate:** Used for ORM to interact with the database.
- **MySQL:** The relational database used for data storage.

### Frontend
- **HTML/CSS:** Custom design for user interface and layout.
- **JavaScript (Vanilla):** Provides interactive features for the front end.
- **Thymeleaf:** Used for server-side rendering.

## Other Tools
- **ModelMapper:** For object-to-object mapping, used in DTOs.
- **Maven:** Dependency management.
- **Lombok:** Reduces boilerplate code in the project.

## Installation
### Prerequisites
- Java 17 or later
- Maven
- MySQL (or any preferred database)
- Git

### Steps
1. Clone the repository:
   ```
   git clone https://gitlab.com/mountblue/29.2-java/stack-overflow-clone/stackoverflow-clone
   ```
   
2. Navigate to the project directory:
   ```
   cd stackoverflow-clone
   ```

3. Set up the database:
   - Create a MySQL database named stackoverflow_clone.
   - Update the database credentials in application.properties or application.yml:
    ```
    spring.datasource.url=jdbc:mysql://localhost:3306/stackoverflow_clone
    spring.datasource.username=your-username
    spring.datasource.password=your-password
    spring.jpa.hibernate.ddl-auto=update
    ```
4. Install dependencies and build the project:
    ```
    mvn clean install
    ```

5. Run the application:
    ```
    mvn spring-boot:run
    ```

6. Access the application on http://localhost:8080.

## Database Schema
The application contains the following core entities:
1. **User:** Manages user information and reputation.
2. **Question:** Stores details about the questions posted by users.
3. **Answer:** Contains answers provided for questions.
4. **QuestionVote/AnswerVote:** Tracks upvotes and downvotes for questions and answers.
5. **Comment:** Stores comments made on questions. 







