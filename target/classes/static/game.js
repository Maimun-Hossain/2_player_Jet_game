// --- Global Game Variables ---
let stompClient = null;         // STOMP client for WebSocket communication
let playerName = '';            // Name of the current player (set upon joining)
let player1 = null;             // Local representation of Player 1
let player2 = null;             // Local representation of Player 2
let players = [];               // Array to hold local player objects (player1, player2)
let bullets = [];               // Array to hold local bullet objects for rendering
let powerUps = [];              // Array to hold local power-up objects for rendering
let gameStartTime = 0;          // Timestamp when the current game started (for client-side timer)
let gameInterval = null;        // Interval ID for the main rendering game loop (requestAnimationFrame alternative)
let gameTimerInterval = null;   // Interval ID for the 60-second game countdown timer
let timeLeft = 60;              // Remaining time in the game, in seconds

// --- DOM Elements (will be assigned values once the DOM is loaded) ---
let menuScreen, playerNameScreen, waitingScreen, gameScreen, scoreboardScreen, gameOverScreen;
let playButton, joinButton, scoreboardButton, backToMenuButton, exitButton;
let playerNameInput, leaderboardTable, winnerDisplay, playerScoreDisplay;
let canvas, ctx; // HTML5 Canvas element and its 2D rendering context

// --- Image Assets ---
// Preload jet images for Player 1 and Player 2
const jet1Image = new Image();
jet1Image.src = 'images/image-removebg-preview (1).png'; // Asset for the left jet
const jet2Image = new Image();
jet2Image.src = 'images/image-removebg-preview (2).png'; // Asset for the right jet
// Preload background image for the game canvas
const cloudBgImage = new Image();
cloudBgImage.src = 'images/vecteezy_clouds-on-sunny-day-with-blue-sky-background_.jpg'; // Cloud background asset

// Image preloading logic to ensure all assets are loaded before game start
let imagesLoaded = 0;
const totalImages = 3; // Total number of image assets to load

/**
 * Tracks loaded images and logs when all are ready.
 * Potentially enables UI elements dependent on image loading.
 */
function imageLoaded() {
    imagesLoaded++;
    if (imagesLoaded === totalImages) {
        console.log("All images loaded.");
        // In a more complex game, you might enable a "Start Game" button here
    }
}

// Assign onload handlers for image assets
jet1Image.onload = imageLoaded;
jet2Image.onload = imageLoaded;
cloudBgImage.onload = imageLoaded;


// --- Player Class (Local Frontend Representation) ---
/**
 * Represents a player's jet on the frontend canvas.
 * This class stores visual properties and provides a draw method.
 * Note: The authoritative game state is managed by the backend.
 */
class Player {
    constructor(name, x, y, width = 50, height = 30, score = 0) {
        this.name = name;       // Player's name
        this.x = x;             // X-coordinate on canvas
        this.y = y;             // Y-coordinate on canvas
        this.width = width;     // Width of the jet
        this.height = height;   // Height of the jet
        this.score = score;     // Current score
    }

    /**
     * Draws the player's jet on the canvas.
     * Uses different image assets for Player 1 and Player 2.
     */
    draw() {
        if (ctx) { // Ensure canvas context is available
            if (player1 && this.name === player1.name) { // If this player is player1
                ctx.drawImage(jet1Image, this.x, this.y, this.width, this.height);
            } else if (player2 && this.name === player2.name) { // If this player is player2
                ctx.drawImage(jet2Image, this.x, this.y, this.width, this.height);
            }
        }
    }
}

// --- Bullet Class (Local Frontend Representation) ---
/**
 * Represents a bullet on the frontend canvas.
 * Stores visual properties and provides a draw method.
 * Note: The authoritative bullet physics are managed by the backend.
 */
class Bullet {
    constructor(x, y, width = 10, height = 5, speed = 10, shooterName) {
        this.x = x;             // X-coordinate on canvas
        this.y = y;             // Y-coordinate on canvas
        this.width = width;     // Width of the bullet
        this.height = height;   // Height of the bullet
        this.speed = speed;     // Speed (used for local rendering, but authoritative is from backend)
        this.shooterName = shooterName; // Name of the player who fired this bullet
    }

    /**
     * Draws the bullet on the canvas.
     * Bullet color depends on the shooter.
     */
    draw() {
        if (ctx) { // Ensure canvas context is available
            // Set bullet color based on which player shot it
            ctx.fillStyle = (player1 && this.shooterName === player1.name) ? 'red' : 'blue';
            ctx.fillRect(this.x, this.y, this.width, this.height); // Draw as a rectangle
        }
    }
}



// --- DOMContentLoaded: Setup UI and Event Listeners ---
/**
 * Executes when the HTML document is fully loaded and parsed.
 * This is where DOM elements are assigned, event listeners are set up,
 * and initial game configurations are made.
 */
document.addEventListener('DOMContentLoaded', () => {
    console.log("DOM Content Loaded - Initializing game...");

    /**
     * Helper function to safely get a DOM element by its ID.
     * Logs an error if the element is not found, which is crucial for debugging UI issues.
     * @param {string} id The ID of the element to retrieve.
     * @returns {HTMLElement|null} The element, or null if not found.
     */
    function getElementByIdOrError(id) {
        const element = document.getElementById(id);
        if (!element) {
            console.error(`Error: Element with ID '${id}' not found in the DOM. This element is crucial for game functionality.`);
        }
        return element;
    }

    // Assign DOM elements to global variables for easy access
    menuScreen = getElementByIdOrError('menu-screen');
    playerNameScreen = getElementByIdOrError('player-name-screen');
    waitingScreen = getElementByIdOrError('waiting-screen');
    gameScreen = getElementByIdOrError('game-screen');
    scoreboardScreen = getElementByIdOrError('scoreboard-screen');
    gameOverScreen = getElementByIdOrError('game-over-screen');

    playButton = getElementByIdOrError('play-button');
    joinButton = getElementByIdOrError('join-button');
    scoreboardButton = getElementByIdOrError('scoreboard-button');
    backToMenuButton = getElementByIdOrError('back-to-menu-button');
    exitButton = getElementByIdOrError('exit-button');

    playerNameInput = getElementByIdOrError('player-name-input');
    leaderboardTable = getElementByIdOrError('leaderboard-table').getElementsByTagName('tbody')[0]; // Get tbody for inserting rows
    winnerDisplay = getElementByIdOrError('winner-display');
    playerScoreDisplay = getElementByIdOrError('player-score-display');
    
    // Initialize Canvas and its 2D rendering context
    canvas = getElementByIdOrError('game-canvas');
    if (canvas) {
        ctx = canvas.getContext('2d');
        canvas.width = 800; // Set canvas width to match game dimensions
        canvas.height = 600; // Set canvas height to match game dimensions
    } else {
        console.error("Canvas element not found, cannot initialize game graphics.");
    }

    // --- Event Listeners for UI Buttons ---
    // Handle 'PLAY' button click on the main menu
    playButton.addEventListener('click', () => {
        menuScreen.style.display = 'none'; // Hide menu
        playerNameScreen.style.display = 'block'; // Show player name input screen
    });

    // Handle 'JOIN' button click on the player name screen
    joinButton.addEventListener('click', () => {
        playerName = playerNameInput.value; // Get player name from input field
        if (playerName) {
            connect(); // Establish WebSocket connection
            playerNameScreen.style.display = 'none'; // Hide player name input
            waitingScreen.style.display = 'block'; // Show waiting screen
        } else {
            alert("Please enter your player name."); // Prompt if name is empty
        }
    });

    // Handle 'SCOREBOARD' button click on the main menu
    scoreboardButton.addEventListener('click', () => {
        menuScreen.style.display = 'none'; // Hide menu
        scoreboardScreen.style.display = 'block'; // Show scoreboard
        fetchLeaderboard(); // Fetch and display leaderboard data
    });
    
    // Handle 'Back to Menu' button click on the scoreboard screen
    backToMenuButton.addEventListener('click', () => {
        scoreboardScreen.style.display = 'none'; // Hide scoreboard
        menuScreen.style.display = 'block'; // Show main menu
    });

    // Handle 'EXIT' button click on the game over screen
    exitButton.addEventListener('click', () => {
        gameOverScreen.style.display = 'none'; // Hide game over screen
        menuScreen.style.display = 'block'; // Show main menu
        // Disconnect WebSocket to end the current game session cleanly
        if (stompClient && stompClient.connected) {
            stompClient.disconnect(() => {
                console.log("Disconnected from WebSocket.");
                stompClient = null; // Clear stompClient reference
            });
        }
    });

    // --- Keyboard Controls for Player Actions ---
    document.addEventListener('keydown', (event) => {
        // Only process key presses if WebSocket is connected and both players are initialized
        if (!stompClient || !stompClient.connected || !player1 || !player2) return;

        // Player 1 controls (W, S, Space)
        if (playerName === player1.name) {
            if (event.key === 'w') {
                sendAction('UP');
            } else if (event.key === 's') {
                sendAction('DOWN');
            } else if (event.key === ' ') {
                event.preventDefault(); // Prevent spacebar from scrolling the page
                sendAction('SHOOT');
            }
        // Player 2 controls (ArrowUp, ArrowDown, Enter)
        } else if (playerName === player2.name) {
            if (event.key === 'ArrowUp') {
                sendAction('UP');
            } else if (event.key === 'ArrowDown') {
                sendAction('DOWN');
            } else if (event.key === 'Enter') {
                event.preventDefault(); // Prevent Enter key from triggering default browser action
                sendAction('SHOOT');
            }
        }
    });
}); // End DOMContentLoaded event listener


// --- WebSocket and Game Logic Functions ---

/**
 * Establishes a WebSocket connection to the backend game server using SockJS and STOMP.
 * Subscribes to game updates and sends the player's join request.
 */
function connect() {
    // Create a new SockJS connection, which provides fallback options for WebSocket
    const socket = new SockJS('/jet-game');
    // Wrap the SockJS socket with STOMP for message handling
    stompClient = Stomp.over(socket);
    
    // Establish the STOMP connection
    stompClient.connect({}, (frame) => {
        console.log('Connected to WebSocket: ' + frame);
        
        // Subscribe to the public game topic where all game state updates are broadcast
        stompClient.subscribe('/topic/game', (message) => {
            handleGameMessage(JSON.parse(message.body)); // Process incoming game messages
        });
        
        // Send a join message to the server with the player's name
        stompClient.send("/app/join", {}, playerName);
    }, (error) => {
        console.error("STOMP Connection Error: ", error);
        alert("Failed to connect to game server. Please ensure the backend is running. " + error);
        // On error, revert to player name input screen
        if (waitingScreen) waitingScreen.style.display = 'none';
        if (playerNameScreen) playerNameScreen.style.display = 'block';
    });
}

/**
 * Sends a player action to the backend game server via WebSocket.
 * @param {string} actionType The type of action to send (e.g., "UP", "DOWN", "SHOOT").
 */
function sendAction(actionType) {
    if (stompClient && stompClient.connected) {
        // Construct and send a JSON message containing the player's name and action
        stompClient.send("/app/action", {}, JSON.stringify({ player: playerName, action: actionType }));
    } else {
        console.warn("STOMP client not connected, cannot send action.");
    }
}

/**
 * Handles incoming game messages from the WebSocket server.
 * Updates the frontend state and UI based on the message type.
 * @param {object} message The parsed JSON game message object.
 */
function handleGameMessage(message) {
    switch (message.type) {
        case 'WAITING_FOR_PLAYER':
            console.log(message.payload + ' joined. Waiting for second player...');
            // UI already shows waiting screen, no explicit action needed here unless specific text update
            break;
        case 'GAME_START':
            console.log('Game starting with state: ', message.payload);
            if (waitingScreen) waitingScreen.style.display = 'none'; // Hide waiting screen
            if (gameScreen) gameScreen.style.display = 'block'; // Show game canvas
            startGame(message.payload); // Initialize and start the game
            break;
        case 'SCORE_UPDATE':
            // This message provides the authoritative game state update from the server
            updateGameState(message.payload);
            break;
        case 'GAME_OVER':
            gameOver(message.payload); // Handle game over sequence
            break;
        default:
            console.log("Unknown message type received:", message.type);
    }
}

/**
 * Updates the local frontend game state based on authoritative data received from the backend.
 * This function is called frequently with SCORE_UPDATE messages.
 * @param {object} gameState The GameState object received from the server.
 */
function updateGameState(gameState) {
    // Basic validation of the received game state
    if (!gameState || !gameState.players || gameState.players.length < 2) {
        console.error("Invalid game state received for update:", gameState);
        return;
    }

    // Initialize local player objects (player1, player2) if they haven't been set yet.
    // This typically happens once on GAME_START.
    if (!player1 || !player2) {
        player1 = new Player(gameState.players[0].name, gameState.players[0].x, gameState.players[0].y, gameState.players[0].width, gameState.players[0].height, gameState.players[0].score);
        player2 = new Player(gameState.players[1].name, gameState.players[1].x, gameState.players[1].y, gameState.players[1].width, gameState.players[1].height, gameState.players[1].score);
        players = [player1, player2]; // Keep a reference in the 'players' array
    } else {
        // For subsequent updates, find the corresponding local player objects and update their properties.
        const remotePlayer1 = gameState.players.find(p => player1 && p.name === player1.name);
        const remotePlayer2 = gameState.players.find(p => player2 && p.name === player2.name);

        if (remotePlayer1) {
            player1.x = remotePlayer1.x;
            player1.y = remotePlayer1.y;
            player1.score = remotePlayer1.score;
            player1.width = remotePlayer1.width;   // Update dimensions in case power-ups changed them
            player1.height = remotePlayer1.height; // Update dimensions in case power-ups changed them
        }
        if (remotePlayer2) {
            player2.x = remotePlayer2.x;
            player2.y = remotePlayer2.y;
            player2.score = remotePlayer2.score;
            player2.width = remotePlayer2.width;   // Update dimensions in case power-ups changed them
            player2.height = remotePlayer2.height; // Update dimensions in case power-ups changed them
        }
    }

    // Update bullet positions and states. Clear all local bullets and re-add based on server state.
    bullets.length = 0; // Clear existing local bullets
    gameState.bullets.forEach(b => {
        bullets.push(new Bullet(b.x, b.y, b.width, b.height, b.speed, b.shooterName));
    });

    // Update power-up positions and states. Clear all local power-ups and re-add based on server state.
    powerUps.length = 0; // Clear existing local power-ups
    gameState.powerUps.forEach(pu => {
        powerUps.push(pu); // Simply copy power-up data (x, y, type)
    });

    // Update the client-side timer to reflect game time progress
    const elapsedTime = Math.floor((Date.now() - gameStartTime) / 1000);
    timeLeft = Math.max(0, 60 - elapsedTime); // Ensure time doesn't go negative
}

/**
 * Handles the end-of-game sequence.
 * Stops game loops, displays the game over screen, determines the winner,
 * and shows final scores. Score saving is handled by the backend.
 * @param {object} finalGameState The final GameState object received from the server.
 */
function gameOver(finalGameState) {
    if (gameInterval) clearInterval(gameInterval);           // Stop the main rendering loop
    if (gameTimerInterval) clearInterval(gameTimerInterval); // Stop the client-side game timer

    if (gameScreen) gameScreen.style.display = 'none';           // Hide the game canvas
    if (gameOverScreen) gameOverScreen.style.display = 'block'; // Show the game over screen

    // Retrieve final player states from the backend's final game state
    const p1State = finalGameState.players.find(p => player1 && p.name === player1.name);
    const p2State = finalGameState.players.find(p => player2 && p.name === player2.name);

    let winnerText = '';
    let winnerPlayerName = '';
    let winnerScore = 0;

    // Determine the winner based on final scores
    if (p1State && p2State) { // Ensure both player states are valid
        if (p1State.score > p2State.score) {
            winnerText = `Winner: ${p1State.name}!`;
            winnerPlayerName = p1State.name;
            winnerScore = p1State.score;
        } else if (p2State.score > p1State.score) {
            winnerText = `Winner: ${p2State.name}!`;
            winnerPlayerName = p2State.name;
            winnerScore = p2State.score;
        } else {
            winnerText = "It's a Draw!"; // Handle draw scenario
            winnerPlayerName = "Draw";
            winnerScore = p1State.score; // In a draw, both scores are equal
        }
    } else {
        winnerText = "Game ended unexpectedly.";
    }

    // Update the game over screen with winner information and player's score
    if (winnerDisplay) winnerDisplay.innerText = winnerText;
    let currentPlayerFinalScore = (p1State && playerName === p1State.name) ? p1State.score : (p2State && playerName === p2State.name ? p2State.score : 0);
    if (playerScoreDisplay) playerScoreDisplay.innerText = `Your Score: ${currentPlayerFinalScore}`;

    // Score saving to the leaderboard is now entirely handled by the backend (GameService)
}

/**
 * Initializes a new game session on the frontend.
 * Resets local game entities, sets up the game timer, and starts the rendering loop.
 * @param {object} initialGameState The initial GameState object received from the server.
 */
function startGame(initialGameState) {
    // Clear any previously running game intervals to prevent multiple loops
    if (gameInterval) clearInterval(gameInterval);
    if (gameTimerInterval) clearInterval(gameTimerInterval);

    // Reset local arrays for game entities
    bullets = [];
    powerUps = [];

    // Initialize local player objects (player1, player2) from the received initial game state.
    if (initialGameState.players && initialGameState.players.length === 2) {
        player1 = new Player(initialGameState.players[0].name, initialGameState.players[0].x, initialGameState.players[0].y, initialGameState.players[0].width, initialGameState.players[0].height, initialGameState.players[0].score);
        player2 = new Player(initialGameState.players[1].name, initialGameState.players[1].x, initialGameState.players[1].y, initialGameState.players[1].width, initialGameState.players[1].height, initialGameState.players[1].score);
        players = [player1, player2]; // Populate the 'players' array
    } else {
        console.error("Invalid initial game state for players:", initialGameState);
        return; // Cannot start game without valid player data
    }
    
    // Also update bullets and powerups lists from the initial game state
    bullets.length = 0;
    initialGameState.bullets.forEach(b => {
        bullets.push(new Bullet(b.x, b.y, b.width, b.height, b.speed, b.shooterName));
    });

    powerUps.length = 0;
    initialGameState.powerUps.forEach(pu => {
        powerUps.push(pu); // PowerUp data (x, y, type, duration)
    });

    gameStartTime = Date.now(); // Record the exact start time for client-side timer calculation
    timeLeft = 60; // Reset the countdown timer
    
    // Start the main game rendering loop, running at approximately 60 frames per second
    gameInterval = setInterval(gameLoop, 1000 / 60);

    // Start a separate interval for the 60-second game countdown, updating every second
    gameTimerInterval = setInterval(() => {
        timeLeft--; // Decrement time remaining
        if (timeLeft <= 0) {
            clearInterval(gameTimerInterval); // Stop the timer when it reaches zero
        }
    }, 1000);
}

/**
 * Fetches the leaderboard data from the backend REST API and displays it in the scoreboard table.
 */
function fetchLeaderboard() {
    fetch('/api/leaderboard') // Make a GET request to the leaderboard API
        .then(response => response.json()) // Parse the JSON response
        .then(data => {
            if (leaderboardTable) {
                leaderboardTable.innerHTML = ''; // Clear any previous entries in the table body
                // Iterate over the fetched scores and add them as rows to the table
                data.forEach(score => {
                    const row = leaderboardTable.insertRow();
                    row.insertCell(0).innerText = score.playerName;
                    row.insertCell(1).innerText = score.score;
                    // Format the match date for display
                    row.insertCell(2).innerText = new Date(score.matchDate).toLocaleDateString();
                });
            } else {
                console.error("Leaderboard table body (tbody) not found.");
            }
        })
        .catch(error => console.error("Error fetching leaderboard:", error)); // Log any errors during fetch
}

/**
 * The main rendering loop of the game.
 * Clears the canvas, draws all game entities (players, bullets, power-ups),
 * and displays scores and the timer.
 * This loop does NOT update game physics; it only renders the current state received from the backend.
 */
function gameLoop() {
    // Ensure the 2D rendering context is available
    if (!ctx) {
        console.error("Canvas context not available for drawing.");
        return;
    }
    
    // Draw the cloud background image across the entire canvas.
    // This implicitly clears the previous frame.
    ctx.drawImage(cloudBgImage, 0, 0, canvas.width, canvas.height);
    
    // Draw players, scores, and timer only if both player objects are initialized
    if (player1 && player2) {
        player1.draw(); // Draw Player 1's jet
        player2.draw(); // Draw Player 2's jet

        // Display Player 1's score on the left
        ctx.fillStyle = '#ff0'; // Yellow color for text
        ctx.font = '24px "Press Start 2P"'; // Retro font from CSS
        ctx.textAlign = 'left';
        ctx.fillText(`${player1.name}: ${player1.score}`, 20, 40);

        // Display Player 2's score on the right
        ctx.textAlign = 'right';
        ctx.fillText(`${player2.name}: ${player2.score}`, canvas.width - 20, 40);

        // Display the game timer in the center top
        ctx.textAlign = 'center';
        ctx.fillText(`Time: ${timeLeft}`, canvas.width / 2, 40);
    }

    // Draw all active power-ups
    powerUps.forEach(pu => {
        ctx.fillStyle = 'lime'; // Green color for power-ups
        ctx.beginPath();
        // Draw power-ups as circles for simplicity
        ctx.arc(pu.x + 10, pu.y + 10, 10, 0, Math.PI * 2); // Center of circle at pu.x+10, pu.y+10, radius 10
        ctx.fill();
        ctx.fillStyle = 'black'; // Black color for text on power-up
        ctx.font = '10px Arial';
        ctx.fillText(pu.type.charAt(0), pu.x + 10, pu.y + 14); // Display first letter of power-up type
    });

    // Draw all active bullets
    bullets.forEach((bullet) => {
        bullet.draw(); // Draw each bullet
    });

    // Important: Game physics updates (movement, collisions) are handled ONLY by the backend.
    // This frontend loop is solely for rendering the current authoritative state.
}