package com.project.cardmatchingclient.models;

public class RoomListModel {
    private String name;
    private String host;
    private String state;

    public RoomListModel(String name, String host, String state) {
        this.name = name;
        this.host = host;
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public String getHost() {
        return host;
    }

    public String getState() {
        return state;
    }
}
