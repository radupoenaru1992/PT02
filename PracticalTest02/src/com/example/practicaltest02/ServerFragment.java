package com.example.practicaltest02;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class ServerFragment extends Fragment {
	
	private EditText serverTextEditText;
	private Button bServer;
	
	private int open = 0;
	
	private ServerThread serverThread;
	
	private class CommunicationThread extends Thread {
		
		private Socket socket;
		
		public BufferedReader getReader(Socket socket) throws IOException {
			return new BufferedReader(new InputStreamReader(socket.getInputStream()));
		}
		
		public PrintWriter getWriter(Socket socket) throws IOException {
			return new PrintWriter(socket.getOutputStream(), true);
		}
		
		public CommunicationThread(Socket socket) {
			this.socket = socket;
		}
		
		@Override
		public void run() {
			try {
				Log.v("Connection", "Connection opened with "+socket.getInetAddress()+":"+socket.getLocalPort());
				BufferedReader br = getReader(socket);
				String city = br.readLine();
				String option = br.readLine();
				PrintWriter printWriter = getWriter(socket);
				printWriter.println(city+"!!!");
				printWriter.println(option+"!!!");
				socket.close();
				Log.v("Connection Closed", "Connection closed");
			} catch (IOException ioException) {
				Log.e("Exception", "An exception has occurred: "+ioException.getMessage());
				ioException.printStackTrace();
			}
		}
	}
	
	private class ServerThread extends Thread {
		
		private boolean isRunning;
		
		private ServerSocket serverSocket;
		
		public void startServer() {
			isRunning = true;
			start();
			Log.v("startServer()", "startServer() method invoked");
		}
		
		public void stopServer() {
			isRunning = false;
			try {
				serverSocket.close();
			} catch(IOException ioException) {
				Log.e("Exception", "An exception has occurred: "+ioException.getMessage());
				ioException.printStackTrace();
			}
			Log.v("stopServer", "stopServer() method invoked");
		}
		
		@Override
		public void run() {
			try {
				int port = Integer.parseInt(serverTextEditText.getText().toString());
				serverSocket = new ServerSocket(port);
				while (isRunning) {
					Socket socket = serverSocket.accept();
					new CommunicationThread(socket).start();
				}
			} catch (IOException ioException) {
				Log.e("Exception", "An exception has occurred: "+ioException.getMessage());
				ioException.printStackTrace();
			}
		}
	}	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle state) {
		return inflater.inflate(R.layout.fragment_server, parent, false);
	}
	
	public class Asculta implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			
			if (open == 0) {
				serverThread = new ServerThread();	
				serverThread.startServer();
				open = 1;
			}
			else {
				serverThread.stopServer();
				open = 0;
			}
			
		}
		
	}
	
	@Override
	public void onActivityCreated(Bundle state) {
		super.onActivityCreated(state);
		
		serverTextEditText = (EditText)getActivity().findViewById(R.id.etServer);
		bServer = (Button)getActivity().findViewById(R.id.bServer);
		
		Asculta asc = new Asculta();
		bServer.setOnClickListener(asc);
	}	
	
}


