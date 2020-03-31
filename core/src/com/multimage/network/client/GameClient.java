package com.multimage.network.client;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.multimage.network.packets.*;
import com.multimage.sprites.Mage;

import java.io.IOException;

public class GameClient {

    String serverIP = "193.40.255.37";
    int tcpPort = 5200;
    int udpPort = 5201;
    int ServerPort, ServerPort1, ServerPort2;

    Mage player;
    Client GameClient;
    Mage[] otherPlayer = new Mage[3];
    int index = 0;

    public GameClient() {
        GameClient = new Client();
        registerPackets();
        GameClient.addListener(new Listener() {

            @Override
            public void connected(Connection connection) {
                GameClient.sendTCP(new Request());
            }

            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof RequestAnswer) {
                    boolean serverAnswer = ((RequestAnswer) object).accepted;
                    if (serverAnswer) {
                        GameClient.sendTCP(new FirstPacket());
                        System.out.println("Joined server");
                    } else {
                        System.out.println("Rejected");
                    }
                } else if (object instanceof FirstPacket) {
                    FirstPacket firstPacket = (FirstPacket) object;
                    player.id = firstPacket.id;
                    Position pos = new Position();
                    pos.playerID = firstPacket.id;
                    pos.posX = (int) player.getPosX();
                    pos.posY = (int) player.getPosY();
                    GameClient.sendTCP(pos);
                } else if (object instanceof Position) {
                    otherPlayer[index].id = ((Position) object).playerID;
                    otherPlayer[index].PosX = ((Position) object).posX;
                    otherPlayer[index].PosY = ((Position) object).posY;
                    index++;
                } else if (object instanceof Moving) {
                    for (int i = 0; i < index; i++) {
                        if (otherPlayer[i].id == ((Moving) object).post.playerID) {
                            otherPlayer[i].PosX = ((Moving) object).post.posX;
                            otherPlayer[i].PosY = ((Moving) object).post.posY;
                            otherPlayer[i].setCurrentState(((Moving) object).state);
                            otherPlayer[i].setDirection(((Moving) object).walkingRight);
                            break;
                        }
                    }
                } else if (object instanceof Disconnect) {
                    otherPlayer[((Disconnect) object).playerID] = null;
                    index--;
                }
            }
        });

        GameClient.start();
        try {
            GameClient.connect(9999, serverIP, tcpPort, udpPort);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void registerPackets() {
        Kryo kryo = GameClient.getKryo();

        kryo.register(Request.class);
        kryo.register(Request.class);
        kryo.register(Disconnect.class);
        kryo.register(Moving.class);
        kryo.register(Position.class);
        kryo.register(Mage.State.class);
    }

    public static void main(String[] args) {
        new GameClient();
    }

}
