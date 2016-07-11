package abs.api.cwi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class MainSocketConnection implements Runnable {

	ServerSocket thisMachine;

	public MainSocketConnection(ServerSocket machine) {
		thisMachine = machine;
	}

	public void run() {
		while (true) {
			try {
				Socket s = thisMachine.accept();

				ObjectOutputStream oos = new ObjectOutputStream(
						s.getOutputStream());
				ObjectInputStream ois = new ObjectInputStream(
						s.getInputStream());

				if (DeploymentComponent.machineOutputStreams == null) {
					DeploymentComponent.machineOutputStreams = new ConcurrentHashMap<String, ObjectOutputStream>();
				}

				if (DeploymentComponent.machineOutputStreams.containsKey(
						s.getInetAddress().getHostAddress()) == false)
					DeploymentComponent.machineOutputStreams
							.put(s.getInetAddress().getHostAddress(), oos);

				if (DeploymentComponent.machineInputStreams == null) {
					DeploymentComponent.machineInputStreams = new ConcurrentHashMap<String, ObjectInputStream>();
				}

				if (DeploymentComponent.machineInputStreams.containsKey(
						s.getInetAddress().getHostAddress()) == false) {
					DeploymentComponent.machineInputStreams
							.put(s.getInetAddress().getHostAddress(), ois);

					new Thread(new MachineListener(ois)).start();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

}
