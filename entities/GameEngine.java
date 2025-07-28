package entities;

import constants.AttackConstants;
import constants.GameConstants;
import constants.PrefixConstants;
import networking.Server;
import networking.Server.GamePhases;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.List;

import static networking.Server.currGamePhase;

public class GameEngine implements Runnable, GameConstants, AttackConstants, PrefixConstants {
    private Point playerRespawnPoint;
    private List<Point> enemySpawnPoints;
    private List<Avatar> syncAvatars;
    private List<Enemy> syncEnemies;
    private List<Attacks> syncAttacks;
    private List<Interactable> syncInteractables;

    public GameEngine() {
         this.enemySpawnPoints = new ArrayList<>(4);
         this.syncAvatars = Collections.synchronizedList( new ArrayList<>(4));
         this.syncEnemies = Collections.synchronizedList(new ArrayList<>(8));
         this.syncAttacks  = Collections.synchronizedList(new ArrayList<>());
         this. syncInteractables = Collections.synchronizedList(new ArrayList<>(30));
     }


    public void initiateMap() {
//        Random random = new Random();
//        int randomInt = random.nextInt(3) + 1;
        String mapSave = "Save1"; //"Save" + randomInt;
        Scanner fileReader = null;
        try {
            fileReader = new Scanner(new File("src/saving/" + mapSave + ".txt"));
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }

        playerRespawnPoint = new Point(fileReader.nextInt(), fileReader.nextInt());
        fileReader.nextLine();

        String[] enemySpawnPoints =  fileReader.nextLine().split(" ");
        for (int i = 0; i < enemySpawnPoints.length; i += 2) {
            this.enemySpawnPoints.add(new Point(Integer.parseInt(enemySpawnPoints[i]), Integer.parseInt(enemySpawnPoints[i + 1])));
        }

        while (fileReader.hasNext()) {
            String objectType = fileReader.next();
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


    @Override
    public void run() {
        while (currGamePhase.equals(GamePhases.IN_GAME)) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("=================================================");
            System.out.println("AVATARS IN GAME");
            for (Avatar avatar : syncAvatars) {
                System.out.println("AVATARCLASS " + avatar.getClass()  + "avatar ID " + avatar.getID());
            }
            System.out.println("=================================================");

            for (Avatar avatar : syncAvatars) {
                avatar.move();
            }

            for (Attacks attack : syncAttacks) {
                if (attack instanceof Melee) {
                    ((Melee) attack).checkTimer();
                } else if (attack instanceof Explosion) {
                    ((Explosion) attack).update();
                } else if (attack instanceof Projectile) {
                    ((Projectile) attack).update();
                }
            }

            for (Enemy enemy : syncEnemies) {
                if (enemy instanceof ThunderGuard) {
                    ((ThunderGuard) enemy).move(getProximity(enemy, true, false, false,true));
                }
            }

            // handle all the collisions
            for (Avatar avatar : syncAvatars) {
                for (Attacks attack : attacksInProximity(avatar)) {
                    if (avatar.getHitbox().intersects(attack.getHitbox())) {
                        if (!(avatar.getIDWithoutPrefix().equals(attack.getCreatorID()))) {
                            avatar.handleCollision(attack);
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

            List<Attacks> attacksToRemove = new ArrayList<>();
            for (Attacks attack : syncAttacks) {
                for (Interactable interactable : interactablesInProximity(attack)) {
                    if (attack.getHitbox().intersects(interactable.getHitbox())) {
                        if (attack instanceof Rocket) {
                            syncAttacks.add(((Rocket) attack).startExplosion());
                            attacksToRemove.add(attack);
                        } else if (attack instanceof Bullet) {
                            attacksToRemove.add(attack);
                        }
                    }
                }
            }
            syncAttacks.removeAll(attacksToRemove);
            syncEnemies.removeIf(enemy -> (enemy.getHealth() < 0));
            for (Avatar avatar : syncAvatars ) {
                if (avatar.getHealth() < 0) {
                    avatar.respawn(this.playerRespawnPoint);
                }
            }



        }

    }


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

    private ArrayList<Interactable> interactablesInProximity(GameObject object) {
        ArrayList<Interactable> proximity = new ArrayList<>();
        int aliveObjX = (int) object.getHitbox().getLocation().getX();
        int aliveObjY = (int) object.getHitbox().getLocation().getY();
        double distanceX, distanceY;
        int distance;

        for (Interactable interactable : syncInteractables) {
            Point objectLocation = interactable.getHitbox().getLocation();
            distanceX = Math.abs(aliveObjX - objectLocation.getX());
            distanceY = Math.abs(aliveObjY - objectLocation.getY());
            distance = (int) Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));

            if (distance <= PROXIMITY_RADIUS) {
                proximity.add(interactable);
            }
        }
        return proximity;
    }


    public void wipeMap() {
        this.playerRespawnPoint = null;
        this.enemySpawnPoints.clear();
        this.syncAvatars.clear();
        this.syncEnemies.clear();
        this.syncInteractables.clear();
        this.syncAttacks.clear();
    }


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

    public String newInteractableID() {
        int lowestUnusedId = 0;

        while (true) {
            String potentialId = String.valueOf(lowestUnusedId);
            boolean isTaken = false;

            for (Interactable interactable : syncInteractables) {
                if (interactable.getIDWithoutPrefix().equals(potentialId)) {
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









    public void addEnemySpawnPoint(Point point) {
        this.enemySpawnPoints.add(point);
    }
    public void addAvatar(Avatar avatar) {
        this.syncAvatars.add(avatar);
    }
    public void addEnemy(Enemy enemy) {
        this.syncEnemies.add(enemy);
    }
    public void addInteractable(Interactable interactable) {
        this.syncInteractables.add(interactable);
    }
    public void addAttack(Attacks attack) {
        this.syncAttacks.add(attack);
    }

    public List<Point> getEnemySpawnPoint() {
        return this.enemySpawnPoints;
    }
    public Point getPlayerRespawnPoint() {
        return playerRespawnPoint;
    }

    public void setPlayerRespawnPoint(Point playerRespawnPoint) {
        this.playerRespawnPoint = playerRespawnPoint;
    }

    public List<Avatar> getSyncAvatars() {
        return syncAvatars;
    }

    public void setSyncAvatars(List<Avatar> syncAvatars) {
        this.syncAvatars = syncAvatars;
    }

    public List<Enemy> getSyncEnemies() {
        return syncEnemies;
    }

    public void setSyncEnemies(List<Enemy> syncEnemies) {
        this.syncEnemies = syncEnemies;
    }

    public List<Attacks> getSyncAttacks() {
        return syncAttacks;
    }

    public void setSyncAttacks(List<Attacks> syncAttacks) {
        this.syncAttacks = syncAttacks;
    }

    public List<Interactable> getSyncInteractables() {
        return syncInteractables;
    }

    public void setSyncInteractables(List<Interactable> syncInteractables) {
        this.syncInteractables = syncInteractables;
    }

}
