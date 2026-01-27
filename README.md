<div align="center">
  <img src="IMG/UIU.png" alt="UIU Logo" width="150"/>
</div>

<h1 align="center">🚀 JetGame: A 2-Player Aerial Combat Game 🚀</h1>

<h4 align="center">UIU AOOP PROJECT</h4>

<p align="center">
  Welcome to JetGame! An exciting real-time 2-player jet fighter game built with Java, Spring Boot, and WebSockets. 
  This project is a demonstration of full-stack web development and concurrent programming. 
  Dive in, take flight, and may the best pilot win!
</p>

---

## 🎬 Gameplay Preview

Check out this video to see the game in action!

https://github.com/Maimun-Hossain/2_player_Jet_game/raw/refs/heads/main/IMG/jetgame.mp4

---

## ✨ Features

*   **🎮 Real-time 2-Player Gameplay:** Challenge a friend and engage in fast-paced aerial combat.
*   **📡 WebSocket Communication:** Ultra-low latency for a smooth and responsive gaming experience.
*   **🏆 Live Leaderboard:** Keep track of scores and see who's dominating the skies.
*   **💥 Power-Ups:** Grab special items during the game to get an edge over your opponent.
*   **🔧 Concurrent Backend:** Built with Java's powerful threading capabilities to handle multiple game sessions flawlessly.
*   **🖥️ Simple Web Interface:** Easy to use UI built with HTML, CSS, and JavaScript.

---

## 🛠️ Technologies Used

| Category      | Technology                                                                                             |
|---------------|--------------------------------------------------------------------------------------------------------|
| **🚀 Backend**  | `Java 21`, `Spring Boot 3`, `Spring WebSocket`, `Spring Data JPA`, `Maven`                               |
| **💾 Database** | `MySQL`                                                                                                |
| **🌐 Frontend** | `HTML5`, `CSS3`, `JavaScript`                                                                          |

---

## 📖 Step-by-Step Installation Guide (for Beginners!)

Follow these steps carefully to get the game running on your local machine.

### 1. ✅ Prerequisites

Make sure you have these tools installed on your computer.

*   **Java Development Kit (JDK) 21:** This is the programming language we use.
    *   *How to check if you have it:* Open your terminal/command prompt and type `java -version`.
*   **Apache Maven:** This is a tool that helps build the project.
    *   *How to check if you have it:* Type `mvn -version`.
*   **MySQL Server:** This is the database where scores are stored.
    *   *How to check if you have it:* You'll typically use a tool like MySQL Workbench or check if the service is running.

### 2. 📂 Clone the Repository

First, you need to get the game's code onto your computer.

```bash
# Clone the project from GitHub
git clone <your-repository-url>

# Navigate into the project directory
cd jetgame
```

### 3. 🗄️ Database Setup

Next, let's prepare the database.

*   **Start your MySQL Server.**
*   **Create a new database.** The game needs a dedicated database to store data. Name it `jetgame`.
    You can do this with a command or a GUI tool like MySQL Workbench.
    ```sql
    CREATE DATABASE jetgame;
    ```
*   **Configure the connection (if needed).** The game is set to connect with the username `root` and an empty password. If your MySQL setup is different, you MUST edit this file:
    `src/main/resources/application.properties`

    Update these lines with your details:
    ```properties
    spring.datasource.username=<your-mysql-username>
    spring.datasource.password=<your-mysql-password>
    ```
    *The database tables will be created automatically when the game starts for the first time!*

### 4. 🏗️ Build the Project

Now we use Maven to compile and package the game. This might take a few minutes.

```bash
# This command cleans previous builds and creates a new one
mvn clean install
```
Look for a `BUILD SUCCESS` message at the end.

### 5. 🚀 Run the Game!

You're all set! It's time to launch the game server.

```bash
# Run the application using Spring Boot's Maven plugin
mvn spring-boot:run
```
You should see a lot of logs in your terminal. Look for a line that says `Started JetgameApplication in ... seconds`.

### 6. 🎉 Play!

The game is now running on your local server.

*   Open your web browser (like Chrome or Firefox).
*   Go to this address: `http://localhost:8080`
*   The game will load. You'll need two players on two different browser tabs (or computers on the same network) to play!

---

## 🕹️ How to Play

*   **Player 1:** Uses the `W`, `A`, `S`, `D` keys to move and `Spacebar` to shoot.
*   **Player 2:** Uses the `Arrow Keys` to move and `Enter` to shoot.
*   **Objective:** Shoot down your opponent to score points. The player with the highest score wins!

---

## 📸 Screenshots

![Screenshot (396)](IMG/Screenshot%20(396).png)
![Screenshot (397)](IMG/Screenshot%20(397).png)
![Screenshot (398)](IMG/Screenshot%20(398).png)
![Screenshot (399)](IMG/Screenshot%20(399).png)
![Screenshot (400)](IMG/Screenshot%20(400).png)
![Screenshot (401)](IMG/Screenshot%20(401).png)
![Screenshot (403)](IMG/Screenshot%20(403).png)

---

## 📂 Project Structure

Here's a simplified look at the project's layout to help you find your way around:

```
jetgame/
├── pom.xml                 # Maven configuration file
├── src/
│   ├── main/
│   │   ├── java/           # Backend Java code
│   │   │   └── .../jetgame/
│   │   │       ├── controller/ # Handles HTTP and WebSocket requests
│   │   │       ├── model/      # Defines game objects (Player, Bullet, etc.)
│   │   │       ├── service/    # Contains the core game logic
│   │   │       └── repository/ # For database interactions
│   │   └── resources/
│   │       ├── application.properties # Spring Boot configuration
│   │       └── static/         # Frontend files (HTML, CSS, JS)
│   └── test/                 # Test code
└── IMG/                      # Contains images and videos for the README
```
