package bomberman;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

public class Server {

    private static final int PORT = 9002;

    private static HashSet<String> names = new HashSet<String>();//nomes dos usuários

    private static HashSet<PrintWriter> writers = new HashSet<PrintWriter>();//buffers de saida

    public static void main(String[] args) throws Exception {
        System.out.println("Servidor Bomberman rodando!.");
        ServerSocket listener = new ServerSocket(PORT);//socket
        try {
            while (true) {
                new Handler(listener.accept()).start();//aceita a conexão
            }
        } finally {
            listener.close();
        }
    }

    /**
     * Interage com um cliente
     */
    private static class Handler extends Thread {
        private String name;
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        /**
         * recebe o nome e as demais mensagens do cliente 
         */
        public void run() {
            try {

                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                while (true) {
                    out.println("SUBMITNAME");
                    name = in.readLine();
                    if (name == null) {
                        return;
                    }
                    synchronized (names) {
                        if (!names.contains(name)) {
                            names.add(name);
                            break;
                        }
                    }
                }

                if(names.size()>2){
                	//System.out.println("manda desconectar");
                	names.remove(name);
                	out.println("MESSAGE "+name+":"+27);
                }
                else
                	out.println("NAMEACCEPTED");
                writers.add(out);
                
                if(names.size()==2){
                	for (PrintWriter writer : writers) {
                		int p = 1;  
                    	for(String n: names){
                    		writer.println("INIT "+n+":"+p);
                    		p++;
                    	}
                    }
                	
                }

                // Envia as mensagens do cliente em broadcast
                while (true) {
                    String input = in.readLine();
                    if (input == null) {
                        return;
                    }
                    if(Integer.parseInt(input)==27)
                    	names.remove(name);
                    for (PrintWriter writer : writers) {
                        writer.println("MESSAGE " + name + ":" + input);
                    }
                }
            } catch (IOException e) {
                System.out.println(e);
            } finally {
                // Cliente desconectado
                if (name != null) {
                    names.remove(name);
                }
                if (out != null) {
                    writers.remove(out);
                }
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
    }
}