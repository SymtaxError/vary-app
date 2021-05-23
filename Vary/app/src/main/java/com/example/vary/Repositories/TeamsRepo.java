package com.example.vary.Repositories;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.vary.Database.DbManager;
import com.example.vary.Models.TeamModel;

import java.util.ArrayList;
import java.util.List;


public class TeamsRepo {
    private MutableLiveData<List<TeamModel>> mTeams = new MutableLiveData<>();
    private static TeamsRepo sInstance;
    private static DbManager dbManager = null;

    public TeamsRepo() {
        mTeams.setValue(new ArrayList<>());
    }

    public void setDbManager(Context context) {
        dbManager = DbManager.getInstance(context);
    }

    public void changeOrder(int newStartIndex) {
        List<TeamModel> teams = mTeams.getValue();

        if (teams != null) {
            for (int i = 0; i < newStartIndex; i++) {
                TeamModel team = teams.remove(0);
                teams.add(team);
            }

            mTeams.postValue(teams);
        }
    }

    public LiveData<List<TeamModel>> getTeams() {
        return mTeams;
    }

    public void removeTeams() {
        List<TeamModel> listTeams = mTeams.getValue();
        if (listTeams != null) {
            listTeams.removeAll(listTeams);
            mTeams.postValue(listTeams);
        }
    }

    public void addTeam(String teamName) {
        List<TeamModel> listTeams = mTeams.getValue();
        if (listTeams == null) {
            listTeams = new ArrayList<>();
        }
        listTeams.add(new TeamModel(teamName, mTeams.getValue().size()));
        mTeams.postValue(listTeams);
    }

    public synchronized static TeamsRepo getInstance() {
        if (sInstance == null) {
            sInstance = new TeamsRepo();
        }

        return sInstance;
    }

    public String getTeamName(int position) {
        if (mTeams.getValue() != null) {
            return mTeams.getValue()
                    .get(position)
                    .getName();
        }
        return null;
    }

    public int getTeamPoints(int position) {
        if (mTeams.getValue() != null) {
            return mTeams.getValue()
                    .get(position)
                    .getPoints();
        }
        return 0;
    }

    public void removeTeam(int pos) {
        List<TeamModel> listTeams = mTeams.getValue();
        if (listTeams != null) {
            listTeams.remove(pos);
            mTeams.postValue(listTeams);
        }
    }

    public void increasePoints(int pos, int newPoints) {
        List<TeamModel> listTeams = mTeams.getValue();
        if (listTeams != null) {
            listTeams
                    .get(pos)
                    .increasePoints(newPoints);
            mTeams.postValue(listTeams);
        }
    }

    public void renameTeam(String name, int pos) {
        List<TeamModel> listTeams = mTeams.getValue();
        if (listTeams != null) {
            listTeams
                    .get(pos)
                    .setName(name);
            mTeams.postValue(listTeams);
        }
    }

    public int getSize() {
        if (mTeams.getValue() != null) {
            return mTeams.getValue()
                    .size();
        }
        return 0;
    }

    public ArrayList<String> getTeamsNames() {
        ArrayList<String> names = new ArrayList<>();
        if (mTeams.getValue() != null) {
            for (int i = 0; i < getSize(); i++) {
                names.add(mTeams
                        .getValue()
                        .get(i)
                        .getName());
            }
        }

        return names;
    }

    /*
    Используется в конце игры, чтобы отобразить команды в порядке от победителя к проигравшему
     */
    public void sortTeamsByPoints() {
        List<TeamModel> teams = mTeams.getValue();
        if (teams == null) {
            return;
        }

        // Сортировка пузырьком :(
        for (int i = 0; i < teams.size(); i++) {
            for (int j = i; j < teams.size() - 1; j++) {
                if (teams.get(j).getPoints() < teams.get(j+1).getPoints()) {
                    TeamModel temp = teams.get(j + 1);
                    teams.set(j + 1, teams.get(j));
                    teams.set(j, temp);
                }
            }
        }

        mTeams.postValue(teams);
    }
}
