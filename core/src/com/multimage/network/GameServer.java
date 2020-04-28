package com.multimage.network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.multimage.MultiMage;
import com.multimage.network.packets.*;
import com.multimage.sprites.Mage;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class GameServer {

    Server server;

    int tcpPort = 5200;
    int udpPort = 5201;
    int ServerPort, ServerPort1, ServerPort2;

    Mage[] playerArr = new Mage[150];

    public int onlinePlayer = 0;
    public int PlayerID = 0;

    public GameServer() {

        InetAddress serverIP = null;
        try {
            serverIP = InetAddress.getByName("193.40.255.37");
        } catch (UnknownHostException e){
            e.printStackTrace();
        }

        server = new Server();

        try {
            server.bind(tcpPort, udpPort);
        } catch (IOException e) {
            e.printStackTrace();
        }

        registerPackets();
        server.start();
        System.out.println("Server started");

        server.addListener(new Listener() {

            @Override
            public void connected(Connection connection) {
                onlinePlayer++;
                System.out.println(onlinePlayer + " Connected");
            }

            @Override
            public void disconnected(Connection connection) {
                onlinePlayer--;
                playerArr[connection.getID()] = null;
                Disconnect dc = new Disconnect();
                dc.playerID = connection.getID();
                server.sendToAllTCP(dc);
            }

            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof Request) {
                    RequestAnswer answer = new RequestAnswer();
                    System.out.println("received");
                    answer.accepted = onlinePlayer <= 3;
                    connection.sendTCP(answer);
                    onlinePlayer++;
                } else if (object instanceof Moving) {
                    server.sendToAllExceptTCP(connection.getID(), (Moving) object);
                } else if (object instanceof Position) {
                    server.sendToAllExceptTCP(connection.getID(), (Position) object);
                    playerArr[connection.getID()].setPosX(((Position) object).posX);
                    playerArr[connection.getID()].setPosY(((Position) object).posY);
                    for (int i = 0; i < PlayerID; i++) {
                        if (playerArr[i] != null && playerArr[i].id != ((Position) object).playerID) {
                            Position pos = new Position();
                            pos.playerID = playerArr[i].id;
                            pos.posX = (float) playerArr[i].getPosX();
                            pos.posY = (float) playerArr[i].getPosY();
                            server.sendToTCP(connection.getID(), pos);
                        }
                    }
                } else if (object instanceof FirstPacket) {
                    FirstPacket fp = (FirstPacket) object;
                    fp.id = connection.getID();
                    server.sendToTCP(connection.getID(), fp);

                    playerArr[connection.getID()] = new Mage(fp.id, fp.x, fp.y);
                    PlayerID++;
                }
            }

        });

    }

    private void registerPackets() {
        Kryo kryo = server.getKryo();

        kryo.register(FirstPacket.class);
        kryo.register(Request.class);
        kryo.register(RequestAnswer.class);
        kryo.register(Disconnect.class);
        kryo.register(Moving.class);
        kryo.register(Position.class);
        kryo.register(Mage.State.class);
    }

    public static void main(String[] args) {
        new GameServer();
    }

}
