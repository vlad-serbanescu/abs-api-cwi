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

				if (AbstractActor.machineOutputStreams == null) {
					AbstractActor.machineOutputStreams = new ConcurrentHashMap<String, ObjectOutputStream>();
				}

				if (AbstractActor.machineOutputStreams.containsKey(
						s.getInetAddress().getHostAddress()) == false)
					AbstractActor.machineOutputStreams
							.put(s.getInetAddress().getHostAddress(), oos);

				if (AbstractActor.machineInputStreams == null) {
					AbstractActor.machineInputStreams = new ConcurrentHashMap<String, ObjectInputStream>();
				}

				if (AbstractActor.machineInputStreams.containsKey(
						s.getInetAddress().getHostAddress()) == false) {
					AbstractActor.machineInputStreams
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
