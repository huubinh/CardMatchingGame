package com.project.cardmatchingclient.models;

public class HistoryModel {
    private String userName;
    private String opponentName;
    private String userScore;
    private String opponentScore;
    private String date;

    public HistoryModel(String userName, String opponentName, String userScore, String opponentScore, String date) {
        this.userName = userName;
        this.opponentName = opponentName;
        this.userScore = userScore;
        this.opponentScore = opponentScore;
        this.date = date;
    }

    public String getUserName() {
        return userName;
    }

    public String getOpponentName() {
        return opponentName;
    }

    public String getUserScore() {
        return userScore;
    }

    public String getOpponentScore() {
        return opponentScore;
    }

    public String getDate() {
        return date;
    }
}
