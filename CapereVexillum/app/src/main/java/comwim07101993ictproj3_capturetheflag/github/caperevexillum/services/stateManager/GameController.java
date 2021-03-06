package comwim07101993ictproj3_capturetheflag.github.caperevexillum.services.stateManager;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.EmptyStackException;
import java.util.Observable;
import java.util.Observer;

import comwim07101993ictproj3_capturetheflag.github.caperevexillum.activities.LobbyActivity;
import comwim07101993ictproj3_capturetheflag.github.caperevexillum.activities.bases.AActivityWithStateManager;
import comwim07101993ictproj3_capturetheflag.github.caperevexillum.models.LobbySettings;
import comwim07101993ictproj3_capturetheflag.github.caperevexillum.models.Player;
import comwim07101993ictproj3_capturetheflag.github.caperevexillum.models.Team;
import comwim07101993ictproj3_capturetheflag.github.caperevexillum.services.socketService.ESocketEmitKey;
import comwim07101993ictproj3_capturetheflag.github.caperevexillum.services.socketService.ESocketOnKey;
import comwim07101993ictproj3_capturetheflag.github.caperevexillum.services.socketService.SocketValueChangedArgs;
import comwim07101993ictproj3_capturetheflag.github.caperevexillum.services.stateManager.interfaces.IGameController;
import comwim07101993ictproj3_capturetheflag.github.caperevexillum.services.stateManager.interfaces.IStateManagerKey;

/**
 * Created by wimva on 4/12/2017.
 */

public class GameController extends StateManagerWithSocket implements IGameController {

    private final static String TAG = GameController.class.getSimpleName();

    private AActivityWithStateManager context;
    private static Gson gson = new Gson();

    private String team;
    /**
     * StateManagerWithSocket is the constructor for the class StateManagerWithSocket.
     */
    GameController() {
        super();
    }

    /**
     * StateManagerWithSocket is the constructor for the class StateManagerWithSocket.
     *
     * @param sharedPreferences the shared preferences to load and save the current state from and to.
     */
    GameController(SharedPreferences sharedPreferences) {
        super(sharedPreferences);
    }


    @Override
    public void createLobby(LobbySettings lobbySettings) {
        socketService.addObserver(new Observer() {
            @Override
            public synchronized void update(Observable observable, Object arg) {
                if (!(arg instanceof SocketValueChangedArgs)) {
                    return;
                }

                SocketValueChangedArgs socketArg = (SocketValueChangedArgs) arg;
                if (socketArg.getKey() != ESocketOnKey.WAS_LOBBY_CREATED) {
                    return;
                }
                try{
                    socketService.deleteObserver(this);
                    LobbySettings lobbySettings = gson.fromJson((String) socketArg.getArgs(), LobbySettings.class);
                    GameController.this.checkLobbyJoin(lobbySettings,true);
                }
                catch(Exception ex){
                    Log.d(TAG, "error =>update: "+socketArg.getKey());
                    throw new RuntimeException();
                }
            }
        });

        socketService.send(ESocketEmitKey.CREATE_LOBBY, gson.toJson(lobbySettings));
    }
    private void checkLobbyJoin( LobbySettings lobbySettings ,Boolean host){
        if (lobbySettings.getId() != -1) {
            setInt(EStateManagerKey.LOBBY_ID, lobbySettings.getId());
            setString(EStateManagerKey.PLAYER_NAME, lobbySettings.getHostName());


            showToast("navigating to lobby activity");
            context.startActivity(LobbyActivity.class);
            //context.startActivity(new Intent(context, LobbyActivity.class));

        } else if (lobbySettings.getHostName() == null) {
            showToast("Playername already exists");
        } else if (lobbySettings.getName() == null) {
            showToast("Lobbyname already exists");
        } else {
            showToast("Could not create lobby");
        }
    }

    @Override
    public void leaveLobby() {
        showToast("Host left, leaving lobby...");
        socketService.addObserver(new Observer() {
            @Override
            public synchronized void update(Observable observable, Object arg) {
                if (!(arg instanceof SocketValueChangedArgs)) {
                    return;
                }
                if(((SocketValueChangedArgs) arg).getKey().equals(ESocketOnKey.LEAVE_LOBBY)) {
                    socketService.deleteObserver(this);
                    StateChangedArgs stateChangedArgs= new StateChangedArgs(
                            null,
                            ((SocketValueChangedArgs) arg).getArgs(),
                            EStateManagerKey.LEAVE_LOBBY
                    );
                    notifyObservers(stateChangedArgs);
                }
            }});
        if (getBoolean(EStateManagerKey.IS_HOST)){
            socketService.send(ESocketEmitKey.HOST_LEFT,getInt(EStateManagerKey.LOBBY_ID ));
        } else {
            socketService.send(ESocketEmitKey.LEAVE_LOBBY, getInt(EStateManagerKey.LOBBY_ID ));
        }

    }

    @Override
    public void joinLobby(LobbySettings lobbySettings) {
        socketService.addObserver(new Observer() {
            @Override
            public synchronized void update(Observable observable, Object arg) {
                if (!(arg instanceof SocketValueChangedArgs)) {
                    return;
                }

                SocketValueChangedArgs socketArg = (SocketValueChangedArgs) arg;
                if (socketArg.getKey() != ESocketOnKey.WAS_LOBBY_CREATED) {
                    return;
                }
                try{
                    socketService.deleteObserver(this);
                    LobbySettings lobbySettings = gson.fromJson((String) socketArg.getArgs(), LobbySettings.class);
                    GameController.this.checkLobbyJoin(lobbySettings,false);
                }
                catch(Exception ex){
                    Log.d(TAG, "error =>update: "+socketArg.getKey());
                    throw new RuntimeException();
                }
            }
        });
        socketService.send(ESocketEmitKey.JOIN_LOBBY, gson.toJson(lobbySettings));
    }

    @Override
    public void joinTeam(String team) {

        int lobbyId=getInt(EStateManagerKey.LOBBY_ID);
        Team newTeam=new Team(team,0);
        String playername = getString(EStateManagerKey.PLAYER_NAME);
        Player player = new Player(playername,newTeam);
        player.setLobbyId(lobbyId);
        Gson gson = new Gson();
        String jsonPlayer =gson.toJson(player);

        this.team=team;
        socketService.send(ESocketEmitKey.JOIN_TEAM, jsonPlayer);
        //Player player = new Player()

        socketService.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object arg) {
                setString(EStateManagerKey.MY_TEAM,GameController.this.team);
                notifyObservers(arg);

            }
        });
    }

    @Override
    public void leaveTeam() {
        joinTeam(Team.NO_TEAM);
    }

    @Override
    public void startGame() {
        socketService.send(ESocketEmitKey.START_GAME,getInt(EStateManagerKey.LOBBY_ID));


    }

    @Override
    public void askPlayers() {
        socketService.send(ESocketEmitKey.ASK_PLAYERS,getInt(EStateManagerKey.LOBBY_ID));
    }

    @Override
    public void askFlags() {
        socketService.send(ESocketEmitKey.ASK_FLAGS,getInt(EStateManagerKey.LOBBY_ID));
    }

    @Override
    public void startGameListeners() {
        this.syncFlags();
        this.listenToEnd();
    }

    public void listenToEnd(){
        socketService.addObserver(new Observer() {
            @Override
            public synchronized void update(Observable observable, Object arg) {
                if (!(arg instanceof SocketValueChangedArgs)) {
                    return;
                }
                if(((SocketValueChangedArgs) arg).getKey().equals(ESocketOnKey.END_SCREEN)) {

                    StateChangedArgs stateChangedArgs= new StateChangedArgs(
                            null,
                            ((SocketValueChangedArgs) arg).getArgs(),
                            EStateManagerKey.END_SCREEN
                    );
                    notifyObservers(stateChangedArgs);
                }
            }});
    }
    public void syncFlags() {
        socketService.addObserver(new Observer() {
            @Override
            public synchronized void update(Observable observable, Object arg) {
                if (!(arg instanceof SocketValueChangedArgs)) {
                    return;
                }
                if(((SocketValueChangedArgs) arg).getKey().equals(ESocketOnKey.SYNC_SCORE)) {

                    StateChangedArgs stateChangedArgs= new StateChangedArgs(
                            null,
                            ((SocketValueChangedArgs) arg).getArgs(),
                            EStateManagerKey.SYNC_SCORE
                    );
                    notifyObservers(stateChangedArgs);
                }
            }});
    }


    @Override
    public synchronized void setContext(AActivityWithStateManager context) {
        this.context = context;
    }

    @Override
    public synchronized AActivityWithStateManager getContext() {
        return context;
    }

    private void showToast(final String message) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.w(getTAG(), "Error while making toast" + e.getMessage());
                }
            }
        });
    }
}
