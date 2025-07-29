package networking;
import entities.*;
import displaySystem.*;
import networking.ClientRequestTypes.*;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;

/**
 * [Client]
 * Handles client-side operations for a multiplayer game including networking and user input.
 * @author Dilen De Silva
 * @version 1.0, 01/22/2024
 */
public class Client {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 5000;
    private Socket socket;
    private ObjectOutputStream clientRequest;
    private ObjectInputStream serverBroadcast;
    Thread receiveMessageThread;
    Thread sendDisplayThread;
    private String ID;
    private KeyboardInput keyboardInput;
    private Mouse mouseInput;
    private GameWindow gameWindow;
    private LobbyWindow lobWindow;

    public static void main(String[] args) {
        new Client();
        new Client();
    }

    /**
     * Constructor
     * Will instantiate the Client and set up all the sockets, streams and listeners
     */
    public Client() {
        String serverIp = JOptionPane.showInputDialog("Enter the ip address of the host");

        try {
            socket = new Socket(serverIp, SERVER_PORT);
            clientRequest = new ObjectOutputStream(socket.getOutputStream());
            serverBroadcast = new ObjectInputStream(socket.getInputStream());
            keyboardInput = new KeyboardInput();
            mouseInput = new Mouse();
            lobWindow = new LobbyWindow();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // creates and starts the other threads
        receiveMessageThread = new Thread(new receiveFromServer());
        sendDisplayThread = new Thread(new sendToDisplay());
        receiveMessageThread.start();
        sendDisplayThread.start();
    }

    /**
     * disconnect
     * This method will disconnect the Client, remove streams and listeners, and close socket
     * @param request The notification of the want to disconnect from the game
     */
    public void disconnect(ClientRequest request) {
        try {
            if(gameWindow != null) { // closes inputs to avoid exceptions due to closing communication
                gameWindow.removeKeyListener(keyboardInput);
                gameWindow.removeMouseListener(mouseInput);
            }
            receiveMessageThread.interrupt();
            sendDisplayThread.interrupt();
            sendToServer(request);
            clientRequest.close();
            serverBroadcast.close();
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * sendToServer
     * Sands an Object to the server
     * @param request The Object
     */
    private void sendToServer(ClientRequest request) {
        try {
            clientRequest.writeObject(request);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * receiveFromServer
     * This class is responsible for the communication with the server. It will receive information from server and
     * handle the info.
     * @author Michael Khart & Dilen De Silva
     * @version 1.0, 01/22/2024
     */
    private class receiveFromServer implements Runnable {

        /**
         * run
         * Will receive info from the server and handle it accordingly
         */
        @Override
        public void run() {
            try {
                Object serverMessage;
                while (!socket.isClosed()) {
                    serverMessage = serverBroadcast.readObject();

                    if (serverMessage instanceof String) {
                        String[] ar = ((String) serverMessage).split(" ");

                        if(gameWindow == null){
                            gameWindow = new GameWindow();
                            gameWindow.addMouseListener(mouseInput);
                            gameWindow.addKeyListener(keyboardInput);
                        }

                        if (!ar[0].equals("INFO_UPDATE")) {
                            Hitbox hitbox = new Hitbox(Integer.parseInt(ar[1]), Integer.parseInt(ar[2]), Integer.parseInt(ar[3]), Integer.parseInt(ar[4]));
                            ServerMessage message = new ServerMessage(ar[0], hitbox, ServerMessage.MessageType.BROADCAST );
                            handleServerMessage(message);
                        } else {
                            if (ar[1].equals("GAMEOVER")) {
                                gameWindow.setGameOver();
                            } else if (ar[1].equals("REMOVE")) {
                                gameWindow.removeHitbox(ar[2]);
                            }
                        }


                    } else if (serverMessage instanceof ServerMessage) {
                        handleServerMessage((ServerMessage) serverMessage);
                    }

                }

            } catch (SocketException e ) {
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        private void handleServerMessage(ServerMessage serverMessage) {

            if (serverMessage.getMessageType().equals(ServerMessage.MessageType.BROADCAST)) {
                if (gameWindow != null) {
                    gameWindow.updateHitbox(serverMessage);
                }
            } else if (serverMessage.getMessageType().equals(ServerMessage.MessageType.WHISPER)){
                System.out.println("=============================");
                System.out.println("NEW ID ASSIGNED =" + serverMessage.getID());
                System.out.println("=============================");
                ID = serverMessage.getID();
            }
        }
    }

    /**
     * [SendToDisplay]
     * This class is responsible for the communication with the displaySystem package
     * @author Dilen de silva
     * @version 1, 01,22,2024
     */
    private class sendToDisplay implements Runnable {
        @Override
        public void run() {
            while (!socket.isClosed()) {
                while (lobWindow != null) {
                    if (lobWindow.isReady()){
                        if (lobWindow.getCharConfirmed()) {
                            if (lobWindow.getPermSelectedChar() == 0){
                                sendToServer(new ClientRequest(getID(), new GameWindowType(Norman.class)));
                            } else if (lobWindow.getPermSelectedChar() == 1){
                                sendToServer(new ClientRequest(getID(), new GameWindowType(Titan.class)));
                            } else if (lobWindow.getPermSelectedChar() == 2){
                                sendToServer(new ClientRequest(getID(), new GameWindowType(Goblino.class)));
                            } else {
                                System.out.println(lobWindow.getSelectedChar());
                            }
                            System.out.println("PLAYER IS READY");
                            sendToServer(new ClientRequest(getID(), new GameWindowType()));
                            lobWindow.setVisible(false);
                            lobWindow = null;
                        }
                    }
                }
            }
        }
    }

    /**
     * KeyboardInput
     * This class is responsible for taking in input from a client and turning that info to the server.
     * @author Michael Khart
     * @version 1.0, 01/22/2024
     */
    class KeyboardInput implements KeyListener {

        /**
         * KeyTYped
         * Int he event that a key way typed we send different requests to server
         * @param e
         */
        @Override
        public void keyTyped(KeyEvent e) {
            char keyChar = e.getKeyChar();

            switch (Character.toLowerCase(keyChar)) {
                case ' ':
                    sendToServer(new ClientRequest(getID(), new NewAbilityType(NewAbilityType.NewAbilitySubtype.JUMP)));
                    break;
                case '1':
                    sendToServer(new ClientRequest(getID(), new OtherType(OtherType.OtherSubtype.KEY_1)));
                    break;
                case '2':
                    sendToServer(new ClientRequest(getID(), new OtherType(OtherType.OtherSubtype.KEY_2)));
                    break;
                case '3':
                    sendToServer(new ClientRequest(getID(), new OtherType(OtherType.OtherSubtype.KEY_3)));
                    break;
                case '4':
                    sendToServer(new ClientRequest(getID(), new OtherType(OtherType.OtherSubtype.KEY_4)));
                    break;
                case 'q':
                    disconnect(new ClientRequest(getID(), new NetworkingType(NetworkingType.NetworkingSubtype.DISCONNECT)));
                    break;
                case 'x':
                    sendToServer(new ClientRequest(getID(), new NewActionType(NewActionType.NewActionSubtype.ULTIMATE_ABILITY, null)));
                    break;

            }
        }

        /**
         * KepPressed
         * In the case that a client is holding a key, send the corresponding info to the server.
         * @param e - The pressed key
         */
        @Override
        public void keyPressed(KeyEvent e) {
            switch (Character.toLowerCase(e.getKeyChar())) {
                case 'a':
                    sendToServer(new ClientRequest(getID(), new StartActionType(StartActionType.StartActionSubtype.START_MOVING_LEFT)));
                    break;
                case 'd':
                    sendToServer(new ClientRequest(getID(), new StartActionType(StartActionType.StartActionSubtype.START_MOVING_RIGHT)));
                    break;
            }
            if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                sendToServer(new ClientRequest(getID(), new NewAbilityType(NewAbilityType.NewAbilitySubtype.DASH)));

            } else if (e.getKeyCode() == KeyEvent.VK_CAPS_LOCK) {
                sendToServer(new ClientRequest(getID(), new NewAbilityType(NewAbilityType.NewAbilitySubtype.LAUNCH)));
            }


        }

        @Override
        public void keyReleased(KeyEvent e) {

        }
    }

    /**
     * Mouse
     * This class is responsible for taking in input from a client and turning that info to the server.
     * @author Michael Khart
     * @version 1.0, 01/22/2024
     */
    class Mouse extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            int buttonClicked = e.getButton();
            switch (buttonClicked) {
                case MouseEvent.BUTTON1:
                    Vector mousePoint = new Vector(e.getX(), e.getY());
                    sendToServer(new ClientRequest(getID(), new NewActionType(NewActionType.NewActionSubtype.NEW_ATTACK, mousePoint)));
                    break;
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {}

        @Override
        public void mouseReleased(MouseEvent e) {}
    }


    public String getID() {
        return ID;
    }
}
