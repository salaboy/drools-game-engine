/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.game.tests;

import java.util.ArrayList;
import java.util.List;
import org.drools.game.model.impl.base.BasePlayerImpl;
import org.drools.game.escape.room.model.house.Door;
import org.drools.game.escape.room.model.house.House;
import org.drools.game.escape.room.model.house.Room;
import org.drools.game.escape.room.model.items.Chest;
import org.drools.game.model.api.Item;
import org.drools.game.model.api.Player;
import org.drools.game.escape.room.model.items.Key;
import org.drools.game.escape.room.model.items.MagicStone;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author salaboy
 */
public class HouseConformanceTest {


    @Test
    public void helloHouse() {
        Player player = new BasePlayerImpl("salaboy");

        House house = new House("My Test Mansion!");

        /* Room A:  
        *  - 1 Door
        *  - 1 chest with a key 
         */
        Room roomA = new Room("Room A");
        List<Door> doorsA = new ArrayList<Door>();
        Door doorA = new Door("Door A");
        doorA.setLeadsTo("Room B");
        doorsA.add(doorA);
        roomA.setDoors(doorsA);

        List<Item> itemsRoomA = new ArrayList<Item>();
        Chest chestA = new Chest("Chest A");
        List<Item> itemsChestA = new ArrayList<Item>();
        Key keyA = new Key("Door A");
        itemsChestA.add(keyA);
        chestA.setItems(itemsChestA);

        itemsRoomA.add(chestA);
        itemsRoomA.add(new MagicStone());

        roomA.setItems(itemsRoomA);

        /* Room B:  
        *  - 1 Door
        *  - 1 chest with a key 
         */
        Room roomB = new Room("Room B");
        List<Door> doorsB = new ArrayList<Door>();
        Door doorB = new Door("Door B");
        doorB.setLeadsTo("Garden");
        doorsB.add(doorB);
        roomB.setDoors(doorsB);

        /* Room Garden:  
        *  - 1 Fence Door, unlocked
         */
        Room roomGarden = new Room("Garden");
        List<Door> doorsGarden = new ArrayList<Door>();
        Door doorFence = new Door("Door Fence");
        doorFence.setLeadsTo("Street");
        doorFence.setLocked(false);
        doorsGarden.add(doorFence);
        roomGarden.setDoors(doorsGarden);

        List<Room> rooms = new ArrayList<Room>();
        rooms.add(roomA);
        rooms.add(roomB);
        rooms.add(roomGarden);
        house.setRooms(rooms);

        List<String> initPlayer = new ArrayList<String>();
        initPlayer.add(player.getName());
        roomA.setPeopleInTheRoom(initPlayer);

        Assert.assertEquals(3, house.getRooms().size());
        Assert.assertTrue(roomA.getPeopleInTheRoom().contains(player.getName()));
        Assert.assertTrue(player.getInventory().getItems().isEmpty());

    }
}
