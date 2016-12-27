package com.sapience.kiva.kivafinal;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.sapience.kiva.kivafinal.DeviceListFragment.AsyncListener;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class FileServerAsyncTask extends AsyncTask<Void, Void, String> {
	
	public static final String SEND_ADDRESS = "address";


    @Override
	protected void onPreExecute() {
		
		super.onPreExecute();
		
		Log.d("WiFiDirect", "Receiver Started.");
	}
	private Context context;

	private Activity activity;
	private boolean calledFromRobotActivity = false;
	private boolean calledFromClientActivity = false;

    public FileServerAsyncTask(RobotActivity activity) {
        this.context = activity.getApplicationContext();
        this.activity = activity;
        this.calledFromRobotActivity = true;
        
    }
    
    public FileServerAsyncTask(ClientActivity activity) {
        this.context = activity.getApplicationContext();
        this.activity = activity;
        this.calledFromClientActivity = true;
        
    }

    @Override
    protected String doInBackground(Void... params) {
        ServerSocket serverSocket = null;
        Socket client = null;
        DataInputStream inputstream = null;
        try {
            serverSocket = new ServerSocket(8988);
            client = serverSocket.accept();
            inputstream = new DataInputStream(client.getInputStream());
            String str = inputstream.readUTF();
            if(str.equals(SEND_ADDRESS)){
            	//retrieve ip of sender..
            	String clientIP = client.getInetAddress().getHostAddress();
            	return clientIP;
            }
            Log.d("WiFiDirect", str);
            serverSocket.close();
            return str;
        } catch (IOException e) {
            Log.e("Wifi", e.getMessage());
            return null;
        }finally{
            if(inputstream != null){
               try{
                  inputstream.close();
               } catch (IOException e) {
                  Log.e("Wifi", e.getMessage());
               }
            }
            if(client != null){
               try{
                  client.close();
               } catch (IOException e) {
                  Log.e("Wifi", e.getMessage());
               }
            }
             if(serverSocket != null){
               try{
                  serverSocket.close();
               } catch (IOException e) {
                  Log.e("Wifi", e.getMessage());
               }
            }
        }
    }
    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
        	Log.d("WiFiDirect", result);
        	if(calledFromRobotActivity)
        		((RobotActivity)activity).onDataReceived(result);
        	else
        		((ClientActivity)activity).onDataReceived(result);
        }

    }
}