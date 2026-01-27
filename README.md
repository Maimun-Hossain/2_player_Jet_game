# JetGame

JetGame is an exhilarating 2-player jet fighter game where players engage in aerial combat. Built with Java and the Spring Boot framework, this project showcases real-time multiplayer gameplay using WebSockets and concurrent programming for a smooth, interactive experience.

## Glimpse of the Game

https://github.com/user-attachments/assets/1cf55118-2e3b-4e12-986c-e1d533b3e64c

![Screenshot (396)](IMG/Screenshot%20(396).png)
![Screenshot (397)](IMG/Screenshot%20(397).png)
![Screenshot (398)](IMG/Screenshot%20(398).png)
![Screenshot (399)](IMG/Screenshot%20(399).png)
![Screenshot (400)](IMG/Screenshot%20(400).png)
![Screenshot (401)](IMG/Screenshot%20(401).png)
![Screenshot (403)](IMG/Screenshot%20(403).png)

## Features

*   **Real-time Multiplayer:** Engage in fast-paced 2-player aerial combat.
*   **WebSocket Communication:** Low-latency communication between clients and the server for responsive gameplay.
*   **Leaderboard:** Track scores and compete for the top spot.
*   **Power-ups:** Collect power-ups to gain an advantage in battle.
*   **Concurrent Gameplay:** The backend uses Java's threading capabilities to manage game state and player actions concurrently.

## Technologies Used

*   **Backend:**
    *   Java 21
    *   Spring Boot 3
    *   Spring WebSocket for real-time communication
    *   Spring Data JPA for database interaction
    *   Java Concurrency (Threading)
*   **Database:**
    *   MySQL
*   **Frontend:**
    *   HTML5
    *   CSS3
    *   JavaScript

## Setup and Installation

### Prerequisites

*   Java 21
*   Apache Maven
*   MySQL Server

### Installation

1.  **Clone the repository:**
    ```bash
    git clone <repository-url>
    cd jetgame
    ```

2.  **Database Setup:**
    *   Make sure you have MySQL server running.
    *   Create a database named `jetgame`.
    *   The application is configured to connect to the database with the username `root` and no password. If your configuration is different, please update the `src/main/resources/application.properties` file:
        ```properties
        spring.datasource.url=jdbc:mysql://localhost:3306/jetgame
        spring.datasource.username=<your-username>
        spring.datasource.password=<your-password>
        ```
    *   The tables will be created automatically on application startup (`spring.jpa.hibernate.ddl-auto=update`).

3.  **Build the project:**
    Use Maven to build the project.
    ```bash
    mvn clean install
    ```

4.  **Run the application:**
    You can run the application using the Spring Boot Maven plugin:
    ```bash
    mvn spring-boot:run
    ```
    Alternatively, you can run the generated JAR file:
    ```bash
    java -jar target/jetgame-0.0.1-SNAPSHOT.jar
    ```

5.  **Play the game:**
    Open your web browser and navigate to `http://localhost:8080`.
