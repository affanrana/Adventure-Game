import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.*;

import AdventureModel.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class AdventureTest.
 * Course code from the CSC207 instructional
 * team at UTM, contributors to tests include:
 *
 * @author iselein
 * @author anshag01
 *  */
public class AdventureTest {
    @Test
    void roomPopulatedTest() throws IOException, FormattingException {
        AdventureGame game = new AdventureGame(true);
        game.setUpGame("SmallGame");
        assertEquals(12, game.getRooms().size());
    }

    @Test
    void introPopulatedTest() throws IOException, FormattingException {
        AdventureGame game = new AdventureGame(true);
        game.setUpGame("SmallGame");
        assertTrue(game.getIntroText().length() > 0);
    }

    @Test
    void helpPopulatedTest() throws IOException, FormattingException {
        AdventureGame game = new AdventureGame(true);
        game.setUpGame("SmallGame");
        assertTrue(game.getHelpText().length() > 0);
    }

    @Test
    void createCommandTest() throws IOException, FormattingException {
        AdventureGame game = new AdventureGame(true);
        game.setUpGame("SmallGame");
        String[] command = game.convertCommand("Q");
        assertEquals("QUIT", command[0]);

        String[] command2 = game.convertCommand("F J");
        assertEquals("FRIENDLY", command2[0]);
        assertEquals("JAVA", command2[1]);
    }

    @Test
    void basicSynonymTest() throws IOException, FormattingException {
        HashMap<String, String> expectedSynonyms = new HashMap<String, String>();
        expectedSynonyms.put("F", "FRIENDLY");
        expectedSynonyms.put("T", "TINY");
        expectedSynonyms.put("B", "BIG");
        expectedSynonyms.put("J", "JAVA");
        expectedSynonyms.put("W", "WEST");
        expectedSynonyms.put("E", "EAST");
        expectedSynonyms.put("N", "NORTH");
        expectedSynonyms.put("S", "SOUTH");
        expectedSynonyms.put("U", "UP");
        expectedSynonyms.put("D", "DOWN");
        expectedSynonyms.put("Q", "QUIT");
        expectedSynonyms.put("I", "INVENTORY");
        expectedSynonyms.put("L", "LOOK");
        AdventureGame game = new AdventureGame(true);
        game.setUpGame("SmallGame");
        assertEquals(expectedSynonyms, game.getSynonyms());
    }

    @Test
    void initialPlayerRoomSet() throws IOException, FormattingException {
        AdventureGame game = new AdventureGame(true);
        game.setUpGame("SmallGame");
        assertEquals("Outside building", game.player.getCurrentRoom().getRoomName());
    }

    @Test
    void testConvertCommand() throws IOException, FormattingException {
        AdventureGame game = new AdventureGame(true);
        game.setUpGame("SmallGame");
        String[] expected = {"TAKE", "KEYS"};
        assertArrayEquals(expected, game.convertCommand("TAKE KEYS"));
    }

    @Test
    void testConvertCommandWithSynonyms() throws IOException, FormattingException {
        AdventureGame game = new AdventureGame(true);
        game.setUpGame("SmallGame");
        String[] expected = {"TINY"};
        assertArrayEquals(expected, game.convertCommand("t"));
    }

    @Test
    void testMovePlayerValidMove() throws IOException, FormattingException {
        AdventureGame game = new AdventureGame(true);
        game.setUpGame("SmallGame");
        boolean result = game.movePlayer("in");
        assertTrue(result);
        assertEquals(3, game.player.getCurrentRoom().getRoomNumber());
    }

    @Test
    void testMovePlayerLockedPassage() throws IOException, FormattingException {
        AdventureGame game = new AdventureGame(true);
        game.setUpGame("SmallGame");
        boolean result = game.movePlayer("south");
        assertEquals(4, game.player.getCurrentRoom().getRoomNumber());
    }

    @Test
    void testMovePlayerLockedPassageWithKey() throws IOException, FormattingException {
        AdventureGame game = new AdventureGame(true);
        game.setUpGame("SmallGame");
        game.movePlayer("IN"); // room 3
        game.player.takeObject("KEYS"); // pick keys
        game.movePlayer("OUT"); // room 1
        game.movePlayer("SOUTH"); // room 4
        game.movePlayer("SOUTH"); // room 5
        game.movePlayer("SOUTH"); // room 6
        game.movePlayer("DOWN"); // room (expected room 8)
        assertEquals(8, game.player.getCurrentRoom().getRoomNumber());
    }

    @Test
    void testReadRoom() throws IOException, FormattingException {
        BufferedReader buff = new BufferedReader(new FileReader("SmallGame/rooms.txt"));
        Room room = Room.readRoom("SmallGame", buff);
        assertEquals(1, room.getRoomNumber());
        assertEquals("Outside building", room.getRoomName());
        String intro = "You are standing at the end of a road before a small brick\n"
                + "building.  A small stream flows out of the building and\n"
                + "down a gully to the south.  A road runs up a small hill\n"
                + "to the west.";
        assertEquals(intro, room.getDescription().strip());
        assertEquals(7, room.getPassageTable().getPassages().size());
    }

    @Test
    void testReadObject() throws IOException, FormattingException {
        // populate the rooms array first
        BufferedReader roomsBuff = new BufferedReader(new FileReader("SmallGame/rooms.txt"));
        BufferedReader buff = new BufferedReader(new FileReader("SmallGame/objects.txt"));
        HashMap<Integer, Room> rooms = new HashMap<>();
        while (roomsBuff.ready()) {
            Room room = Room.readRoom("SmallGame", roomsBuff);
            rooms.put(room.getRoomNumber(), room);
        }
        AdventureObject.readObject(buff, rooms);
        Room room = rooms.get(3);
        assertEquals(1, room.objectsInRoom.size());
    }

    @Test
    void testTakeObject() throws IOException, FormattingException {
        AdventureGame game = new AdventureGame(true);
        game.setUpGame("SmallGame");
        game.movePlayer("IN");
        boolean result = game.player.takeObject("KEYS");
        assertTrue(result);
        assertEquals(1, game.player.inventory.size());
        assertEquals("KEYS", game.player.inventory.get(0).getName());
        assertFalse(game.player.getCurrentRoom().objectsInRoom.contains("KEYS"));
    }

    @Test
    void testDropObject() throws IOException, FormattingException {
        AdventureGame game = new AdventureGame(true);
        game.setUpGame("SmallGame");
        game.movePlayer("IN");
        game.player.takeObject("KEYS");
        game.movePlayer("OUT");
        game.player.dropObject("KEYS");
        assertEquals(0, game.player.inventory.size());
        assertFalse(game.player.getCurrentRoom().objectsInRoom.contains("KEYS"));
    }

    @Test
    void testTakeObject2() throws IOException, FormattingException {
        AdventureGame game = new AdventureGame(true);
        game.setUpGame("SmallGame");
        game.movePlayer("IN");
        game.player.takeObject("KEYS");
        game.movePlayer("OUT");
        assertEquals(1, game.player.inventory.size());
    }

    @Test
    void testDropObject2() throws IOException, FormattingException {
        AdventureGame game = new AdventureGame(true);
        game.setUpGame("SmallGame");
        game.movePlayer("IN");
        game.player.takeObject("TOKEN");
        game.movePlayer("OUT");
        game.player.dropObject("TOKEN");
        game.player.takeObject("SWORD");
        game.player.dropObject("SWORD");
        game.player.takeObject("KEYS");
        game.player.dropObject("KEYS");
        assertEquals(0, game.player.inventory.size());
        assertFalse(game.player.getCurrentRoom().objectsInRoom.contains("SWORD, KEYS"));
    }

    @Test
    void testReadObject2() throws IOException, FormattingException {
        BufferedReader roomsBuff = new BufferedReader(new FileReader("SmallGame/rooms.txt"));
        BufferedReader buff = new BufferedReader(new FileReader("SmallGame/objects.txt"));
        HashMap<Integer, Room> rooms = new HashMap<>();
        while (roomsBuff.ready()) {
            Room room = Room.readRoom("SmallGame", roomsBuff);
            rooms.put(room.getRoomNumber(), room);
        }
        AdventureObject.readObject(buff, rooms);
        Room room = rooms.get(2);
        assertEquals(0, room.objectsInRoom.size());
    }

    @Test
    void testReadRoom2() throws IOException, FormattingException {
        BufferedReader buff = new BufferedReader(new FileReader("SmallGame/rooms.txt"));
        int counter = 0;
        while (counter <= 14) {
            String currLine = buff.readLine();
            counter++;
        }
        Room room = Room.readRoom("SmallGame", buff);
        assertEquals(2, room.getRoomNumber());
        assertEquals("End of road", room.getRoomName());
        assertEquals(2, room.getPassageTable().getPassages().size());
    }

    @Test
    void testMovePlayerLockedPassageWithKey2() throws IOException, FormattingException {
        AdventureGame game = new AdventureGame(true);
        game.setUpGame("SmallGame");
        game.movePlayer("IN"); // room 3
        game.player.takeObject("KEYS"); // pick keys
        game.movePlayer("OUT"); // room 1
        game.movePlayer("SOUTH"); // room 4
        game.movePlayer("NORTH"); // room 5
        game.movePlayer("NORTH"); // room 6
        game.player.dropObject("KEYS"); // drop keys
        game.movePlayer("EAST"); // room (expected room 3)
        assertEquals(3, game.player.getCurrentRoom().getRoomNumber());
    }

    @Test
    void testMovePlayerLockedPassage2() throws IOException, FormattingException {
        AdventureGame game = new AdventureGame(true);
        game.setUpGame("SmallGame");
        boolean result = game.movePlayer("south");
        result = game.movePlayer("north");
        result = game.movePlayer("south");
        assertEquals(4, game.player.getCurrentRoom().getRoomNumber());
    }

    @Test
    void testMovePlayerValidMove2() throws IOException, FormattingException {
        AdventureGame game = new AdventureGame(true);
        game.setUpGame("SmallGame");
        boolean result = game.movePlayer("in");
        result = game.movePlayer("out");
        result = game.movePlayer("north");
        assertTrue(result);
        assertEquals(3, game.player.getCurrentRoom().getRoomNumber());
    }

    @Test
    void testConvertCommandWithSynonyms2() throws IOException, FormattingException {
        AdventureGame game = new AdventureGame(true);
        game.setUpGame("SmallGame");
        String[] expected = {"LOOK"};
        assertArrayEquals(expected, game.convertCommand("l"));
    }

    @Test
    void testConvertCommand2() throws IOException, FormattingException {
        AdventureGame game = new AdventureGame(true);
        game.setUpGame("SmallGame");
        String[] expected = {"TAKE", "TOKEN"};
        assertArrayEquals(expected, game.convertCommand("TAKE TOKEN"));
    }

    @Test
    void initialPlayerRoomSet2() throws IOException, FormattingException {
        AdventureGame game = new AdventureGame(true);
        game.setUpGame("SmallGame");
        game.movePlayer("north");
        assertEquals("Inside building", game.player.getCurrentRoom().getRoomName());
    }

    @Test
    void createCommandTest2() throws IOException, FormattingException {
        AdventureGame game = new AdventureGame(true);
        game.setUpGame("SmallGame");
        String[] command = game.convertCommand("U T I");
        assertEquals("UP", command[0]);
        assertEquals("TINY", command[1]);
        assertEquals("INVENTORY", command[2]);
    }
}

