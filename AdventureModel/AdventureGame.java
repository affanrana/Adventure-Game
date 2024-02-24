package AdventureModel;

import Trolls.NoteTroll;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Class AdventureGame.  Handles all the necessary tasks to run the Adventure game.
 * Inspired by assignments created by Eric Roberts
 * and John Estell. Course code tailored by the CSC207
 * instructional team at UTM, with special thanks to:
 *
 * @author anshag01
 * @author mustafassami
 * @author guninkakr03
 *  */
public class AdventureGame {
    private final boolean audible;
    private String introText; //An attribute to store the Introductory text of the game.
    private String helpText; //A variable to store the Help text of the game. This text is displayed when the user types "HELP" command.
    private HashMap<Integer, Room> rooms; //A list of all the rooms in the game.
    private HashMap<String,String> synonyms = new HashMap<>(); //A HashMap to store synonyms of commands.
    private String[] actionVerbs = {"QUIT","HELP","LOOK","INVENTORY","TAKE","DROP"}; //List of action verbs (other than motions) that exist in all games. Motion vary depending on the room and game.
    public Player player; //The Player of the game.

    /**
     * Adventure Game Constructor
     * __________________________
     * Initializes attributes
     */
    public AdventureGame(boolean audible){
        this.synonyms = new HashMap<>();
        this.rooms = new HashMap<>();
        this.audible = audible; //audible game?
        introText = "";
        helpText = "";
    }

    private void setBuff(int checker, String directoryName, BufferedReader currBuff) throws IOException, FormattingException {
        if (checker == 1) {
            currBuff.mark(1);
            String buffString = Integer.toString(checker);
            while (currBuff.readLine() != null) {
                currBuff.reset();
                Room currRoom = Room.readRoom(directoryName, currBuff);
                currBuff.mark(1);
                int currRoomNum = currRoom.getRoomNumber();
                buffString = Integer.toString(currRoomNum);
                this.rooms.put(currRoomNum, currRoom);
            }
        }
        else if (checker == 2) {
            currBuff.mark(1);
            int buffChecker = 1;
            String totalLine;
            while(currBuff.readLine() != null){
                currBuff.reset();
                if (buffChecker == 1) {
                    totalLine = "checker_1";
                }
                AdventureObject.readObject(currBuff, this.rooms);
                currBuff.mark(1);
            }
            currBuff.close();
        }
    }

    private void setScanner(int checker, Scanner currScan) {
        if (checker == 1) {
            while (currScan.hasNextLine()){
                String currLine = currScan.nextLine();
                String[] pieces = currLine.split("=");
                this.synonyms.put(pieces[0].toUpperCase(), pieces[1].toUpperCase());
            }
            currScan.close();
        }
        else if (checker == 2) {
            while (currScan.hasNextLine()){
                String currLine = currScan.nextLine();
                this.introText += currLine + "\n";
            }
        }
        else if (checker == 3) {
            while (currScan.hasNextLine()){
                String currLine = currScan.nextLine();
                String full = "";
                if (this.helpText.isEmpty()){
                    this.helpText = currLine;
                    full += this.helpText;
                } else {
                    this.helpText += "\n" + currLine;
                    full += currLine;
                }
            }
        }
    }

    /**
     * setUpGame
     * __________________________
     * This method will do a lot of File I/O!  It should:
     *
     * 1. Populate the synonyms HashMap with data in synonyms.txt
     * 2. Populate the rooms HashMap with data in rooms.txt
     * 3. Initialize the introduction attribute with the text in introduction.txt
     * 4. Initialize the help attribute with the text in help.txt
     * 5. Place the objects in objects.txt in their initial room locations
     * 6. Assign the position of the player to the first room listed in the rooms.txt file.
     *
     * Your code should utilize the methods Room.readRoom and Object.readObject
     * to read individual Objects and Rooms from the file
     *
     * @param directoryName name of directory containing input files (corresponds to the name of the adventure)
     *
     * @throws IOException in the case of a file I/O error
     * @throws FormattingException in the case of a file formatting error
     *
     * Formatting exceptions should be received by this method
     * from Room.readRoom and Object.readObject. We ask that you
     * throw in response to the following formatting errors:
     * A. Room Numbers in files that are NOT NUMBERS.
     * B. Room Descriptions in files longer than TEN LINES.
     * C. Destination rooms for passages that are NOT NUMBERS
     * D. Location rooms for objects that are NOT NUMBERS
     * E. Location rooms for objects that do NOT EXIST
     */
    public void setUpGame(String directoryName) throws IOException, FormattingException {
        FileReader currSynFile = new FileReader(directoryName +"\\synonyms.txt");
        FileReader currRoomFile = new FileReader(directoryName + "\\rooms.txt");
        FileReader currIntroFile = new FileReader(directoryName + "\\introduction.txt");
        FileReader currHelpFile = new FileReader(directoryName + "\\help.txt");
        FileReader currObjFile = new FileReader(directoryName + "\\objects.txt");

        Scanner currSynScan = new Scanner(currSynFile);
        Scanner currIntroScan = new Scanner((currIntroFile));
        Scanner currHelpScan = new Scanner(currHelpFile);

        BufferedReader currRoomBuff = new BufferedReader(currRoomFile);
        BufferedReader currObjBuff = new BufferedReader(currObjFile);

        setScanner(1, currSynScan);
        setScanner(2, currIntroScan);
        setScanner(3, currHelpScan);

        setBuff(1, directoryName, currRoomBuff);
        setBuff(2, directoryName, currObjBuff);

        player = new Player(this.rooms.get(1));
    }

    /**
     * convertCommand
     * __________________________
     * This method should translate strings from the user into an array of string tokens.
     * Tokens in the array should be in a standardized format (i.e. with synonyms applied).
     * For example, assume GRAB is a synonym of TAKE as indicated in the synonyms file.
     * In this case, the string command "GRAB KEYS" should be converted to the following
     * array of command tokens: {"TAKE", "KEYS"}
     *
     * Note that this method should strip white space from the beginning and end of the
     * user input string before tokenization.
     *
     * @param command use input string from the command line
     * @return a string array of tokens that represents the command.
     */
    public String[] convertCommand(String command){
        String[] currentLine = command.split(" ");
        int counter = 0;
        String currentLineString;
        while(counter < currentLine.length){
            String checkery = currentLine[counter].toUpperCase();
            if (this.synonyms.containsKey(checkery)){
                String checkeryAgain = currentLine[counter].toUpperCase();
                currentLineString = checkeryAgain;
                currentLine[counter] = this.synonyms.get(checkeryAgain);
            }
            counter++;
        }
        return currentLine;
    }

    //checks and does stuff for FORCED
    private boolean forcedCheck(Passage passy) {
        if (!this.rooms.containsKey(passy.getDestinationRoom())) {
            executeAction("QUIT");
            return false;
        }
        String descrip = this.rooms.get(passy.getDestinationRoom()).getDescription();
        System.out.println(descrip);
        this.rooms.get(passy.getDestinationRoom()).setVisited();
        if (passy.getDestinationRoom() != 0) {
            Room currRooms = this.rooms.get(passy.getDestinationRoom());
            this.player.setCurrentRoom(currRooms);
            return true;
        }
        else if (passy.getDestinationRoom() == 0) {
            return false;
        }
        else {
            Room currRooms = this.rooms.get(passy.getDestinationRoom());
            this.player.setCurrentRoom(currRooms);
            return true;
        }
    }

    //reads rooms
    private boolean roomAudio(int checker, Passage currPass) {
        if (checker == 1) {
            Room currRooms = this.rooms.get(currPass.getDestinationRoom());
            this.player.setCurrentRoom(currRooms);
            return true;
        }
        else if (checker == 2) {
            NoteTroll nT = new NoteTroll();
            if (nT.playGame()){
                Room currRooms = this.rooms.get(currPass.getDestinationRoom());
                this.player.setCurrentRoom(currRooms);
            }
            else {
                ArrayList<Integer> total = new ArrayList<Integer>();
                total.add(checker);
            }
            return true;
        }
        else if (checker == 3) {
            String nameKey = currPass.getKeyName();
            ArrayList<AdventureObject> inv = this.player.inventory;
            int counter = 0;
            while(counter < inv.size()){
                if (inv.get(counter).getName().equals(nameKey)){
                    Room currRooms = this.rooms.get(currPass.getDestinationRoom());
                    this.player.setCurrentRoom(currRooms);
                    return true;
                }
                counter++;
            }
            return false;
        }
        return true;
    }

    /**
     * movePlayer
     * __________________________
     * Moves the player in the given direction, if possible.
     * Return false if the player wins or dies as a result of the move.
     * Else, return true (so the game can continue).
     *
     * To implement this method you will need to:
     *
     * 1. Determine the moves that are possible from a given room.
     *    If the user's move is not in the list of possible moves,
     *    print an error message and return TRUE.
     *    The player will then remain in the same room and the game will continue.
     * 3. If the move is in the list of possible moves, and the path is BLOCKED
     *    by a missing object, determine if the user has an OBJECT required to
     *    remove the BLOCK:
     *    -- If YES, set the current room to the destination made accessible by the OBJECT.
     *    -- If NO, check for an UNBLOCKED path and continue.
     * 3. If the move is in the list of possible moves, and the path is BLOCKED
     *    by a TROLL:
     *    -- If the game is "audible" Instantiate a SoundTroll; if not Instantiate the Troll
     *       of your choice (a NoteTroll or GameTroll).
     *    -- Call the TROLL's "playGame" method.
     *    -- If "playGame" returns FALSE, the player has LOST. This method should return TRUE.
     *       The player will remain in the same room and the game will continue.
     *    -- If "playGame" returns TRUE, set the current room to the destination made accessible by the WIN.
     * 4. If the move is in the list of possible moves, and the path is NOT BLOCKED,
     *    set the current room to the destination associated with the move.
     * 5. Finally, If the current room has changed, check to see if the move from this new room is "FORCED".
     *    -- If yes, print the description of the room and mark it as "visited"
     *    -- Set the current room to the destination that is FORCED
     *    -- If this destination == 0, return FALSE (meaning the player has died or won).
     *    -- Otherwise, return TRUE  (the player will proceed from the new current room).
     *
     * @param direction the move command
     * @return false, if move results in death or a win (and game is over).  Else, true.
     */
    public boolean movePlayer(String direction) {
        List<Passage> allPass = this.player.getCurrentRoom().getPassageTable().getPassages();
        int counter = 0;
        int checker = 0;
        while (counter < allPass.size()){
            Passage currPass = allPass.get(counter);
            String uppy = direction.toUpperCase();
            String newCurrUppy = "";
            if (currPass.getDirection().equals(uppy)){
                if (currPass.getKeyName() == null){
                    return roomAudio(1, currPass);
                }
                else if (currPass.getKeyName().equals("TROLL")){
                    if (!this.audible){
                        return roomAudio(2, currPass);
                    }
                }
                else if (checker == 1) {
                    newCurrUppy = Integer.toString(checker);
                }
                else{
                    if (roomAudio(3, currPass)) {
                        return true;
                    }
                }
            }
            counter++;
        }
        Room current = player.getCurrentRoom();
        Passage passy = current.getPassageTable().getPassages().get(0);
        String directy = passy.getDirection();
        checker++;
        if (directy.equals("FORCED")){
            checker++;
            return forcedCheck(passy);
        }
        return true;
    }

    /**
     * executeAction
     * __________________________
     * Given a string, check if it is a valid action.
     * Then, perform the action.  Return true if the game continues, else false if it is over.
     *
     * To implement this method you will first convert the input to a standard vocabulary
     * by consulting the synonyms table.
     *
     * If the player's command is:
     * 1. QUIT: print "GAME OVER"
     * 2. HELP: print the help text
     * 3. LOOK: print the description of the room
     * 4. INVENTORY: print the items in the player's inventory items or "INVENTORY EMPTY",
     *    if the player has no objects
     * 5. TAKE <obj>:
     * If a player does not provide an object, print "THE TAKE COMMAND REQUIRES AN OBJECT".
     * If a player provides an object that exists in the room, print "<obj> HAS BEEN TAKEN"
     * If a player provides an object that does not exist in the room, print "<obj> IS NOT IN ROOM"
     * Replace "<obj>" with the name of the object in the strings above.
     * If an object is taken as a result of this command, add the object to the player's inventory.
     * 6. DROP <obj>:
     * If a player does not provide an object, print "THE DROP COMMAND REQUIRES AN OBJECT".
     * If a player provides an object that exists in their inventory, print "<obj> HAS BEEN DROPPED"
     * If a player provides an object that does not exist in their inventory, print "<obj> IS NOT IN INVENTORY"
     * Replace "<obj>" with the name of the object in the strings above.
     * If an object is dropped as a result of this command, remove it from the player's inventory and add it to the current room.
     * 7. Any other commands:
     * Delegate the command to the movePlayer method.
     * If the result is true, the move is valid and the player may proceed;
     * If the result is false, print "GAME OVER" (and return FALSE);
     *
     * @param command: String representation of the command.
     * @return true, if game can continue after the move is complete.  Else, false.
     */
    public boolean executeAction(String command){

        //first, look up synonyms and convert the user's input string to standard tokens
        String[] inputArray = convertCommand(command);

        //ADD YOUR CODE HERE
        if (Arrays.asList(inputArray).contains("QUIT")) {
            System.out.println("GAME OVER");
            return false;
        }
        else if (Arrays.asList(inputArray).contains("HELP")) {
            System.out.println(this.helpText);
        }
        else if (Arrays.asList(inputArray).contains("LOOK")) {
            System.out.println(this.rooms.get(this.player.getCurrentRoom().getRoomNumber()).descripFull());
        }
        else if (Arrays.asList(inputArray).contains("INVENTORY")) {
            if (this.player.inventory.isEmpty()) {
                System.out.println("INVENTORY EMPTY");
            }
            else {
                Iterator<AdventureObject> iterator = this.player.inventory.iterator();
                while (iterator.hasNext()) {
                    AdventureObject s = iterator.next();
                    System.out.println(s.getName());
                }
            }
        }
        else if (Arrays.asList(inputArray).contains("TAKE")) {
            if (Arrays.asList(inputArray).size() < 2) {
                System.out.println("THE TAKE COMMAND REQUIRES AN OBJECT");
            }
            else {
                for (AdventureObject obj : this.player.getCurrentRoom().objectsInRoom) {
                    if (obj.getName().equals(inputArray[1])) {
                        System.out.println(inputArray[1] + " HAS BEEN TAKEN");
                        this.player.takeObject(inputArray[1]);
                        break;
                    } else {
                        System.out.println(inputArray[1] + " IS NOT IN ROOM");
                    }
                }
            }
        }
        else if (Arrays.asList(inputArray).contains("DROP")) {
            if (Arrays.asList(inputArray).size() < 2) {
                System.out.println("THE DROP COMMAND REQUIRES AN OBJECT");
            }
            else {
                Iterator<AdventureObject> iterator = this.player.inventory.iterator();
                boolean found = false;
                while (iterator.hasNext() && !found) {
                    AdventureObject obj = iterator.next();
                    if (obj.getName().equals(inputArray[1])) {
                        System.out.println(inputArray[1] + " HAS BEEN DROPPED");
                        this.player.dropObject(inputArray[1]);
                        found = true;
                    } else {
                        System.out.println(inputArray[1] + " IS NOT IN INVENTORY");
                    }
                }
            }
        }
        else {
            if (movePlayer(inputArray[0])) {
                return true;
            }
            else {
                return false;
            }
        }
        return true; //replace this line as needed!!
    }

    /**
     * playGame
     * __________________________
     * This function is the Game Loop.
     *
     * It keeps track of the player's input and performs actions requested by the player.
     * The game loop ends when the player types "QUIT" or when the player dies.
     */
    public void playGame(){

        System.out.println(this.introText); //print out the introduction
        String input = "";
        while(true){
            if (!input.equals("LOOK") && !input.equals("L")) { //if the command is to LOOK, printing the description is redundant.
                System.out.println(this.player.getCurrentRoom().getDescription()); //print where we are to the console
                this.player.getCurrentRoom().articulateDescription(); //state where we are as well, audibly
            }
            this.player.getCurrentRoom().setVisited(); //mark the room as visited
            if (this.player.getCurrentRoom().hasObjects()) { //are there objects here?
                System.out.println("The following object(s) are here:");
                this.player.getCurrentRoom().printObjects();
            }
            System.out.print("> "); //prompt for the user
            Scanner scanner = new Scanner(System.in); //read input from the user
            input = scanner.nextLine().toUpperCase(); //convert input to upper case
            if (this.audible) this.player.getCurrentRoom().stopDescription(); //stop voice, we are moving on!
            if (!executeAction(input)) return; //execute the command, if possible.
        }
    }

    /**
     * Getters and Setters
     * __________________________
     * Some potentially useful getter and setter methods for class attributes are below.
     */
    public void setPlayer(Player player) {
        this.player = player;
    }
    public Player getPlayer() {
        return player;
    }
    public void setRooms(HashMap<Integer, Room> rooms) { this.rooms = rooms; }
    public HashMap<Integer, Room> getRooms() { return rooms;}
    public void setIntroText(String introText) {
        this.introText = introText;
    }
    public String getIntroText() {
        return this.introText;
    }
    public void setHelpText(String helpText) {
        this.helpText = helpText;
    }
    public String getHelpText() {
        return this.helpText;
    }
    public void setSynonyms(HashMap<String, String> synonyms) {
        this.synonyms = synonyms;
    }
    public HashMap<String, String> getSynonyms() {
        return this.synonyms;
    }

}