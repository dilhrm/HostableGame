package networking;
import constants.AttackConstants;
import constants.AvatarConstants;
import constants.GameConstants;
import constants.PrefixConstants;
import entities.Vector;
import entities.Vector.Direction;
import networking.ClientRequestTypes.*;
import entities.*;
import entities.Holster.WeaponHolster;
import networking.ClientRequestTypes.GameWindowType.GameWindowSubtype;
import networking.ClientRequestTypes.NetworkingType.NetworkingSubtype;
import networking.ClientRequestTypes.NewAbilityType.NewAbilitySubtype;
import networking.ClientRequestTypes.NewActionType.NewActionSubtype;
import networking.ClientRequestTypes.OtherType.OtherSubtype;
import networking.ClientRequestTypes.StartActionType.StartActionSubtype;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.net.Socket;
import java.util.List;

/**
 * [Server]
 * Manages the networking aspects of a multiplayer game, handling client connections, game phases, and game state updates.
 * @author Michael Khart
 * @version 1.0, 01/22/2024
 */
public class Server {
    private final int PORT = 5000;
    private ServerSocket serverSocket;
    private Map<ClientHandler, Thread> syncClientsMap = Collections.synchronizedMap(new HashMap<>(5));
    public enum GamePhases {
        IN_LOBBY, IN_GAME
    }
    private GameEngine gameEngine;
    private Thread gameEngineThread;
    public static GamePhases currGamePhase;
    private ConnectionListener connectionListener;
    private Thread connectionListenerThread;


    public static void main(String[] args) throws Exception {
        Server server = new Server();
        server.run();
    }

    /**
     * Constructs the Server object, initializing the server socket, game engine, and listener threads.
     * @throws IOException If an error occurs in opening the server socket.
     */
    public Server() throws IOException {
        serverSocket = new ServerSocket(PORT);
        currGamePhase = GamePhases.IN_LOBBY;
        connectionListener = new ConnectionListener();
        connectionListenerThread = new Thread(connectionListener);
        gameEngine = new GameEngine();
        gameEngineThread = new Thread(gameEngine);
    }

    public void run() {
        System.out.println("Starting the server...");
        connectionListenerThread.start(); // Start the connection listener thread
    }

    /**
     * BroadcastUpdated
     * Broadcasts updated game state to all connected clients.
     */
    private void broadcastUpdated() { // REPLACE WITH ACTUAL CLASSES
        this.gameEngine.broadcastUpdated();
    }

    /**
     * broadcast
     * sends a newly updated game object to all clients
     * @param updatedObj
     */
    private void broadcast(GameObject updatedObj) {
        for (ClientHandler client : syncClientsMap.keySet()) {
            StringBuilder broadcast = new StringBuilder();
            broadcast.append(updatedObj.getID() + " ");
            broadcast.append(updatedObj.getHitbox().x + " ");
            broadcast.append(updatedObj.getHitbox().y + " ");
            broadcast.append(updatedObj.getHitbox().width + " ");
            broadcast.append(updatedObj.getHitbox().height + " ");

            client.sendObject((broadcast).toString());
        }
    }

    private void broadcast(String str) {
        for (ClientHandler client : syncClientsMap.keySet()) {
            client.sendObject(str);
        }
    }

    /**
     * whisper
     * Sends a private message to a specific client identified by ID.
     * @param message The message to be sent.
     * @param ID The unique identifier of the client.
     */
    private void whisper(String message, String ID) {
        for (ClientHandler client : syncClientsMap.keySet()) {
            if (client.getID().equals(ID)) {
                client.sendObject(message);
                break; // since we know its taken no point continuing to loop
            }
        }
    }

    /**
     * initateGame
     * Initiates the game by setting up the map
     */
    private void initiateGame() {
        this.gameEngine.initiateMap();
        currGamePhase = GamePhases.IN_GAME;
        gameEngineThread.start();
        connectionListenerThread.interrupt();
    }

    /**
     * reserGame
     * used to reset the game for next matches
     */
    private void resetGame() {
        this.gameEngine.wipeMap();
    }

    /**
     * checkGameState
     * checks if all client have told that they are ready, if so then they will
     */
    private void checkGameState() {
        boolean everyoneReady = true;

        for (ClientHandler currClient : syncClientsMap.keySet()) {
            if (!currClient.getReady()) {
                everyoneReady = false;
                break; // break since someone isnt ready no point in looping
            }
        }

        if (everyoneReady) {
            initiateGame();
        }
    }

    /**
     * fetchClientAvatar
     * Returns the clients avatar.
     * @param ID Id of the client
     * @return The avatar of the client.
     */
    private Avatar fetchClientAvatar(String ID) {
        Avatar clientsAvatar = null;
        for (Avatar avatar : gameEngine.getSyncAvatars()) { // finds the clients avatar
            if (avatar.getIDWithoutPrefix().equals(ID)) {
                clientsAvatar = avatar;
            }
        }
        return clientsAvatar;
    }

    /**
     * newClientid
     * Creates a unique id for connecting clients
     * @return new ID
     */
    private String newClientID() {
        int lowestUnusedId = 0;

        while (true) {
            String potentialId = "C-" + lowestUnusedId;
            boolean isTaken = false;

            for (ClientHandler handler : syncClientsMap.keySet()) {
                if (handler.getID().equals(potentialId)) {
                    isTaken = true;
                    break; // since we know its taken no point continuing to loop
                }
            }

            if (!isTaken) {
                return potentialId;
            }

            lowestUnusedId++;
        }
    }

    /**
     * disconnectClient
     * Disconnects a client and removes their handler from the client map.
     * @param client The ClientHandler of the client to disconnect.
     */
    private void disconnectClient(ClientHandler client) {
        try {
            Thread threadToRemove = syncClientsMap.get(client);
            threadToRemove.interrupt();
            client.getSocket().close();
            syncClientsMap.remove(client);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (this.syncClientsMap.isEmpty()) {
            System.exit(0);
        }
    }


// =============================================================================================================

    /**
     * [ConnectionListener]
     * Class is responsible for receiving and then connecting clients to the server.
     * @author Michael Khart
     * @version 1.0, 01,22,2024A
     */
    class ConnectionListener implements Runnable {
        public void run() {
            try {
                System.out.println("Waiting for a connection request from a client...");
                while (!Thread.currentThread().isInterrupted() && currGamePhase.equals(GamePhases.IN_LOBBY)) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        System.out.println("New client");

                        // sets up all of the connections and gives a new id to the new client
                        ClientHandler handler = new ClientHandler(clientSocket);
                        String newID = newClientID();
                        handler.setID(newID);
                        handler.sendObject(new ServerMessage(newID, null, ServerMessage.MessageType.WHISPER));
                        Thread connectionThread = new Thread(handler);

                        syncClientsMap.put(handler, connectionThread);
                        connectionThread.start();
                    } catch (SocketException e) {
                        System.out.println("Server Socket is closed.");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

// =============================================================================================================

    /**
     * ClientHandler
     * The clientHandler class is responsible for helping smooth the communication and request handling between
     * the server and several clients.
     * @author Michael Khart
     * @version 1.0, 01/22/2024
     */
    class ClientHandler implements Runnable {
        private Socket socket;
        private ObjectOutputStream output;
        private ObjectInputStream input;
        private String ID;
        private boolean ready;

        /**
         * ClientHandler
         * Constructor for the ClientHandler
         * @param socket The socket that connects this and the Client.
         */
        public ClientHandler(Socket socket) {
            this.socket = socket;
            ready = false;
            try {
                output = new ObjectOutputStream(socket.getOutputStream());
                input = new ObjectInputStream(socket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        /**
         * run
         * This method will handle any requests that the clients send to the server.
         */
        @Override
        public void run() {
            ClientRequest request;

            try {
                while (!socket.isClosed()) {
                    request = (ClientRequest) input.readObject();
                    if (request != null ) {
                        handleRequest(request);
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        /**
         * handleRequest
         * This method will see what type of action the client is trying to do and act accordingly.
         * @param request The request object the clients have sent
         */
        private void handleRequest(ClientRequest request) {
            RequestType requestType = request.getRequestType();

            // if we are in lobby, we only take in some requests/actions
            if (currGamePhase.equals(GamePhases.IN_LOBBY)) {

                if (requestType instanceof GameWindowType) {
                    GameWindowType requestCast = ((GameWindowType) requestType);
                    GameWindowSubtype requestCastSubtype = (GameWindowSubtype) requestCast.getSubtype();

                    if (requestCastSubtype.equals(GameWindowSubtype.CHARACTER_CHOSEN)) {
                        // will create a empty class object
                        Class<?> clazz;
                        Avatar requestAvatarInstance = null;
                        try {
                            clazz = requestCast.getAvatarClass();
                            requestAvatarInstance = (Avatar) clazz.newInstance();
                        } catch (java.lang.InstantiationException | java.lang.IllegalAccessException e) {
                            e.printStackTrace();
                        }

                        // depending on the requests chosen avatar type, we create that avatar instance
                        if (requestAvatarInstance != null) {
                            Avatar clientAvatar;
                            if (requestAvatarInstance instanceof Norman) {
                                clientAvatar = new Norman(request.getIDWithoutPrefix());
                                gameEngine.addAvatar(clientAvatar);
                            } else if (requestAvatarInstance instanceof Titan) {
                                clientAvatar = new Titan(request.getIDWithoutPrefix());
                                gameEngine.addAvatar(clientAvatar);
                            } else {
                                clientAvatar = new Goblino(request.getIDWithoutPrefix());
                                gameEngine.addAvatar(clientAvatar);
                            }

                        }
                    // if the client has entered ready then make that client ready (used in seeing if we initate game)
                    } else if (requestCastSubtype.equals(GameWindowSubtype.READY)) {
                        if (fetchClientAvatar(request.getIDWithoutPrefix()) != null) { // if they have chosen an avatar
                            this.ready = true;
                            checkGameState();
                        }
                    }
                } else if (requestType instanceof NetworkingType) {
                    // if the client wants to disconnect, let them
                    NetworkingType requestCast = ((NetworkingType) requestType);
                    NetworkingSubtype requestCastSubtype = (NetworkingSubtype) requestCast.getSubtype();

                    if (requestCastSubtype.equals(NetworkingSubtype.DISCONNECT)) {
                        disconnect();
                    }
                }
            } else if (currGamePhase.equals(GamePhases.IN_GAME)) {
                // now that we are in game different things happen when different request come in
                Avatar clientsAvatar = fetchClientAvatar(request.getIDWithoutPrefix());

                // since the client is trying to create a new attack, create an attack based on their current weapon
                if (requestType instanceof NewActionType) {
                    NewActionType requestCast = ((NewActionType) requestType);
                    NewActionSubtype requestCastSubtype = (NewActionSubtype) requestCast.getSubtype();
                    Vector mousePoint = requestCast.getMousePoint();
                    if (requestCastSubtype.equals(NewActionSubtype.NEW_ATTACK )) {
                        gameEngine.syncAttacks.add(clientsAvatar.attack(mousePoint));
                    } else if (requestCastSubtype.equals(NewActionSubtype.ULTIMATE_ABILITY)) {
                        gameEngine.syncAttacks.add(clientsAvatar.ultimateAbility());
                    }

                // if we want to start a movement ability
                } else if (requestType instanceof NewAbilityType) {
                    NewAbilityType requestCast = ((NewAbilityType) requestType);
                    NewAbilitySubtype requestCastSubtype = (NewAbilitySubtype) requestCast.getSubtype();
                    if (requestCastSubtype.equals(NewAbilitySubtype.JUMP)) {
                        clientsAvatar.jump();
                    } else if (requestCastSubtype.equals(NewAbilitySubtype.DASH)) {
                        clientsAvatar.dash();
                    } else if (requestCastSubtype.equals(NewAbilitySubtype.LAUNCH)) {
                        clientsAvatar.launch();
                    }

                // if the client is starting to move in a certain direction
                } else if (requestType instanceof StartActionType) {
                    StartActionType requestCast = ((StartActionType) requestType);
                    StartActionSubtype requestCastSubtype = (StartActionSubtype) requestCast.getSubtype();
                    if (requestCastSubtype.equals(StartActionSubtype.START_MOVING_RIGHT)) {
                        if (clientsAvatar.getSpeed().getIntX() < AvatarConstants.MAX_PLAYER_SPEED) {
                            clientsAvatar.speedUp(Direction.RIGHT);
                        }
                    } else if (requestCastSubtype.equals(StartActionSubtype.START_MOVING_LEFT)) {
                        if (clientsAvatar.getSpeed().getIntX() < AvatarConstants.MAX_PLAYER_SPEED) {
                            clientsAvatar.speedUp(Direction.LEFT);
                        }
                    }

                // if the client is trying to disconnect,
                } else if (requestType instanceof NetworkingType) {
                    NetworkingType requestCast = ((NetworkingType) requestType);
                    NetworkingSubtype requestCastSubtype = (NetworkingSubtype) requestCast.getSubtype();
                    if (requestCastSubtype.equals(NetworkingSubtype.UPDATE_REQUESTED)) {
                        broadcastUpdated();
                    } else if (requestCastSubtype.equals(NetworkingSubtype.DISCONNECT)) {
                        disconnect();
                    }

                // switch their current weapon
                } else if (requestType instanceof OtherType) {
                    OtherType requestCast = ((OtherType) requestType);
                    OtherSubtype requestCastSubtype = (OtherSubtype) requestCast.getSubtype();
                    if (requestCastSubtype.equals(OtherSubtype.KEY_1)) {
                        if (clientsAvatar.getWeaponHolster().getCurrentWeapon().canHolsterWeapon(WeaponHolster.MELEE)) {
                            clientsAvatar.switchWeapon(WeaponHolster.MELEE);
                        }
                    } else if (requestCastSubtype.equals(OtherSubtype.KEY_2)) {
                        if (clientsAvatar.getWeaponHolster().getCurrentWeapon().canHolsterWeapon(WeaponHolster.GUN)) {
                            clientsAvatar.switchWeapon(WeaponHolster.GUN);
                        }
                    } else {
                        if (clientsAvatar.getWeaponHolster().getCurrentWeapon().canHolsterWeapon(WeaponHolster.RPG)) {
                            clientsAvatar.switchWeapon(WeaponHolster.RPG);
                        }
                    }
                }
            }
        }

        /**
         * sendObj
         * sends an object through the streams to the client.
         * @param updatedObj The object we are sending
         */
        public void sendObject(Object updatedObj) {
            try {
                output.writeObject(updatedObj);
            } catch (IOException e) {
                System.out.println("There are no clients to broadcast to.");
            }
        }

        /**
         * disconnect
         * Disconnects this client and handler from the server
         */
        private void disconnect() {
            try {
                disconnectClient(this);
                output.close();
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /*
        ============================
        getters and setters
        ============================
         */

        public Socket getSocket() {
            return socket;
        }

        public String getID() {
            return ID;
        }
        public void setID(String ID) {
            this.ID = ID;
        }
        public boolean getReady() {
            return ready;
        }
    }

// =============================================================================================================

    /**
     * [GameEngine]
     * This class is responsible for doing in game caluclations like moving all entities, calculating and
     * handling collisions, etc.
     * @author Michael Khart
     * @version 1.0  01,22,2024
     */
    class GameEngine implements Runnable, GameConstants, AttackConstants, PrefixConstants {
        private Point playerRespawnPoint;
        private List<Point> enemySpawnPoints;
        private List<Avatar> syncAvatars;
        private List<Enemy> syncEnemies;
        private List<Attacks> syncAttacks;
        private List<Interactable> syncInteractables;

        /**
         * Constructor for GameEngine
         * Initiates needed collections
         */
        public GameEngine() {
            this.enemySpawnPoints = new ArrayList<>(4);
            this.syncAvatars = Collections.synchronizedList( new ArrayList<>(4));
            this.syncEnemies = Collections.synchronizedList(new ArrayList<>(8));
            this.syncAttacks  = Collections.synchronizedList(new ArrayList<>());
            this. syncInteractables = Collections.synchronizedList(new ArrayList<>(30));
        }


        /**
         * initiateMap
         * Reads a save file and uploads saved data into data collections
         */
        public void initiateMap() {
            String mapSave = "Save1";
            Scanner fileReader = null;
            try {
                fileReader = new Scanner(new File("src/saving/" + mapSave + ".txt"));
            } catch(FileNotFoundException e) {
                e.printStackTrace();
            }
            // first line in save is the player respawn point
            playerRespawnPoint = new Point(fileReader.nextInt(), fileReader.nextInt());

            for (Avatar avatar : syncAvatars) {
                avatar.getHitbox().setIntX(playerRespawnPoint.x);
                avatar.getHitbox().setIntY(playerRespawnPoint.y);
            }
            fileReader.nextLine();

            // sets enemy respawnPoints
            String[] enemySpawnPoints =  fileReader.nextLine().split(" ");
            for (int i = 0; i < enemySpawnPoints.length; i += 2) {
                this.enemySpawnPoints.add(new Point(Integer.parseInt(enemySpawnPoints[i]), Integer.parseInt(enemySpawnPoints[i + 1])));
            }
            while (fileReader.hasNext()) {
                String objectType = fileReader.next();
                // turns saved data into objects and adds them to collections
                switch (objectType) {
                    case "Wall" :
                        this.syncInteractables.add(new Wall(fileReader.next(), new Hitbox(fileReader.nextInt(), fileReader.nextInt(), fileReader.nextInt(), fileReader.nextInt())));
                        break;
                    case "Hazard" :
                        this.syncInteractables.add(new Hazard(fileReader.next(), new Hitbox(fileReader.nextInt(), fileReader.nextInt(), fileReader.nextInt(), fileReader.nextInt()), fileReader.nextInt()));
                        break;
                    case "PowerUp":
                        this.syncInteractables.add(new PowerUp(fileReader.next(), new Hitbox(fileReader.nextInt(), fileReader.nextInt(), fileReader.nextInt(), fileReader.nextInt()), PowerUp.BoostType.valueOf(fileReader.next()), fileReader.nextDouble()));
                        break;

                }
            }
        }


        /**
         * Run
         * This method acts as the main game loop, where objects are moved, collisions handled, etc
         */
        @Override
        public void run() {
            while (currGamePhase.equals(GamePhases.IN_GAME)) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                // Moves all the objects that can move
                for (Avatar avatar : syncAvatars) {
                    avatar.move();
                }
                List<Attacks> attacksToRemove = new ArrayList<>();
                for (Attacks attack : syncAttacks) {
                    if (attack instanceof Melee) {
                        if (((Melee) attack).checkTimer()) {
                            attacksToRemove.add(attack);
                        }
                    } else if (attack instanceof Explosion) {
                        ((Explosion) attack).update();
                        if (((Explosion) attack).checkSize()) {
                            attacksToRemove.add(attack);
                        }
                    } else if (attack instanceof Projectile) {
                        ((Projectile) attack).update();
                    }
                }


                for (Enemy enemy : syncEnemies) {
                    if (enemy instanceof ThunderGuard) {
                        ((ThunderGuard) enemy).move(getProximity(enemy, true, false, false,true));
                    }
                }

                // handle all the collisions first for avatar and then enemeis and attacks
                // uses proximity (only objects close to it) for caluclations
                for (Avatar avatar : syncAvatars) {
                    for (Attacks attack : attacksInProximity(avatar)) {
                        if (avatar.getHitbox().intersects(attack.getHitbox())) {
                            if (!(avatar.getIDWithoutPrefix().equals(attack.getCreatorID()))) {
                                avatar.handleCollision(attack);
                                attacksToRemove.add(attack);
                                System.out.println("avatar has collided with attack");
                            }
                        }
                    }
                    for (Enemy enemy : enemiesInProximity(avatar)) {
                        if (avatar.getHitbox().intersects(enemy.getHitbox())) {
                            avatar.handleCollision(enemy);
                        }
                    }
                    for (Interactable interactable : interactablesInProximity(avatar)) {
                        if (avatar.getHitbox().intersects(interactable.getHitbox())) {
                            avatar.handleCollision(interactable);
                        }
                    }
                }
                for (Enemy enemy : syncEnemies) {
                    for (Attacks attack : attacksInProximity(enemy)) {
                        if (enemy.getHitbox().intersects(attack.getHitbox())) {
                            enemy.handleCollision(attack);
                        }
                    }
                    for (Interactable interactable : interactablesInProximity(enemy)) {
                        if (enemy.getHitbox().intersects(interactable.getHitbox())) {
                            enemy.handleCollision(interactable);
                        }
                    }
                }
                List<Attacks> newAttacks = new ArrayList<>();
                for (Attacks attack : syncAttacks) {
                    for (Interactable interactable : interactablesInProximity(attack)) {
                        if (attack.getHitbox().intersects(interactable.getHitbox())) {
                            if (attack instanceof Rocket) {
                                newAttacks.add(((Rocket) attack).startExplosion());
                                attacksToRemove.add(attack);
                            } else if (attack instanceof Bullet) {
                                attacksToRemove.add(attack);
                            }
                        }
                    }
                }
                syncAttacks.addAll(newAttacks);

                // removes unneeded things and respawns avatars/enemies if they died
                for (Attacks attack : attacksToRemove) {
                    broadcast("INFO_UPDATE REMOVE " + attack.getID());
                }
                syncAttacks.removeAll(attacksToRemove);
                syncEnemies.removeIf(enemy -> (enemy.getHealth() < 0));
                for (Avatar avatar : syncAvatars ) {
                    if (avatar.getHealth() < 0) {
                        avatar.respawn(this.playerRespawnPoint);
                    }
                }

                this.broadcastUpdated();
            }

        }


        /**
         * broadcastUpdated
         * Will send updates of all the objects within the game to clients.
         */
        private void broadcastUpdated() {
            for (Avatar avatar : syncAvatars) {
                broadcast(avatar);
            }
            for (Attacks attack : syncAttacks) {
                broadcast(attack);
            }
            for (Enemy enemy : syncEnemies) {
                broadcast(enemy);
            }
            for (Interactable interactable : syncInteractables) {
                broadcast(interactable);
            }
        }

        /**
         * getProximity
         * Will return an Arraylist of GameObjects. This is used to optomise the game/calculations as we only take
         * objects that are within a small radius into account when doing things like calculating collisions and movement.
         * Takes in 4 booleans which will be used to determine what objects we would like to add.
         * @param aliveObj The object that we are looking for a proximity of.
         * @param includeAvatars Boolean of if we want to include
         * @param includeEnemies Boolean of if we want to include
         * @param includeAttacks Boolean of if we want to include
         * @param includeInteractables Boolean of if we want to include
         * @return The collection of the object types within a raidus
         */
        public ArrayList<GameObject> getProximity(Alive aliveObj, boolean includeAvatars, boolean includeEnemies, boolean includeAttacks, boolean includeInteractables) {
            ArrayList<GameObject> proximity = new ArrayList<>();
            if (includeAvatars) {
                proximity.addAll(avatarsInProximity(aliveObj));
            }
            if (includeAttacks) {
                proximity.addAll(attacksInProximity(aliveObj));
            }
            if (includeEnemies) {
                proximity.addAll(enemiesInProximity(aliveObj));
            }
            if (includeInteractables) {
                proximity.addAll(interactablesInProximity(aliveObj));
            }
            return proximity;
        }

        /**
         * avatarsInProximity
         * This method will return each instance of an Avatar object that is within the PROXIMITY threshold
         * @param aliveObj The object whos surroundings will be taken and analysed.
         * @return The collection of nearby avatars
         */
        private ArrayList<Avatar> avatarsInProximity(Alive aliveObj) {
            ArrayList<Avatar> proximity = new ArrayList<>();
            int aliveObjX = (int) aliveObj.getHitbox().getLocation().getX();
            int aliveObjY = (int) aliveObj.getHitbox().getLocation().getY();
            double distanceX, distanceY;
            int distance;

            for (Avatar avatar : syncAvatars) {
                Point objectLocation = avatar.getHitbox().getLocation();
                distanceX = Math.abs(aliveObjX - objectLocation.getX());
                distanceY = Math.abs(aliveObjY - objectLocation.getY());
                distance = (int) Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));

                if (distance <= PROXIMITY_RADIUS) {
                    proximity.add(avatar);
                }
            }
            return proximity;
        }

        /**
         * enemiesInProximity
         * This method will return each instance of an enemy object that is within the PROXIMITY threshold.
         * @param aliveObj The object whose proximity is being looked at
         * @return The nearby enemies
         */
        private ArrayList<Enemy> enemiesInProximity(Alive aliveObj) {
            ArrayList<Enemy> proximity = new ArrayList<>();
            int aliveObjX = (int) aliveObj.getHitbox().getLocation().getX();
            int aliveObjY = (int) aliveObj.getHitbox().getLocation().getY();
            double distanceX, distanceY;
            int distance;

            for (Enemy enemy : syncEnemies) {
                Point objectLocation = enemy.getHitbox().getLocation();
                distanceX = Math.abs(aliveObjX - objectLocation.getX());
                distanceY = Math.abs(aliveObjY - objectLocation.getY());
                distance = (int) Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));

                if (distance <= PROXIMITY_RADIUS) {
                    proximity.add(enemy);
                }
            }
            return proximity;
        }

        /**
         * attacksInProximity
         * This method will return each instance of an Attack object that is within the PROXIMITY threshold.
         * @param aliveObj The object whose proximity is being looked at
         * @return The nearby attacks
         */

        private ArrayList<Attacks> attacksInProximity(Alive aliveObj) {
            ArrayList<Attacks> proximity = new ArrayList<>();
            int aliveObjX = (int) aliveObj.getHitbox().getLocation().getX();
            int aliveObjY = (int) aliveObj.getHitbox().getLocation().getY();
            double distanceX, distanceY;
            int distance;

            for (Attacks attack : syncAttacks) {
                Point objectLocation = attack.getHitbox().getLocation();
                distanceX = Math.abs(aliveObjX - objectLocation.getX());
                distanceY = Math.abs(aliveObjY - objectLocation.getY());
                distance = (int) Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));

                if (distance <= PROXIMITY_RADIUS) {
                    proximity.add(attack);
                }
            }
            return proximity;
        }

        /**
         * interatablesInProximity
         * This method will return each instance of an Interactable object that is within the PROXIMITY threshold.
         * @param object The object whose proximity is being looked at
         * @return The nearby interactables
         */
        private ArrayList<Interactable> interactablesInProximity(GameObject object) {
            ArrayList<Interactable> interactables = new ArrayList<Interactable>();
            interactables.addAll(syncInteractables);
            return (interactables);
        }

        /**
         * wipeMap
         * Used to restart the game and wipe all info for the purpose of enabling replayability
         */
        public void wipeMap() {
            this.playerRespawnPoint = null;
            this.enemySpawnPoints.clear();
            this.syncAvatars.clear();
            this.syncEnemies.clear();
            this.syncInteractables.clear();
            this.syncAttacks.clear();
        }

        /**
         * newEnemyID
         * Finds a unique unused enemyID within the game to assign to new Enemies
         * @return The unique ID
         */
        public String newEnemyID() {
            int lowestUnusedId = 0;

            while (true) {
                String potentialId = String.valueOf(lowestUnusedId);
                boolean isTaken = false;

                for (Enemy enemy : syncEnemies) {
                    if (enemy.getIDWithoutPrefix().equals(potentialId)) {
                        isTaken = true;
                        break; // since it's taken, no point continuing to loop
                    }
                }

                if (!isTaken) {
                    return potentialId;
                }

                lowestUnusedId++;
            }
        }

        // getters and setters

        public void addAvatar(Avatar avatar) {
            this.syncAvatars.add(avatar);
        }
        public List<Avatar> getSyncAvatars() {
            return syncAvatars;
        }
    }
}
