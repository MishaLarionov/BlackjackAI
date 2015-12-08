class AI{
    
    private Socket server;
    private BufferedReader sRead;
    private PrintWriter sWrite;
    
    private ActionSelector decision;
    
    private final static String NAME = "VinceFelixIainAI";
    private int myPlayerNumber;
    
    private static final boolean DEBUG = true;
    
    public static void main(String[] args){
        System.out.println("===AI===");
        new AI();
    }
    
    public AI() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        
        System.out.println("What is the IP of the server?");
        String ip = br.readLine();
        while (!ip.matches("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}"))
            {
                System.out.println("This doesn't look like a valid IPv4 adddress. Try again.");
                ip = br.readLine();
            }
        
        System.out.println("\nWhat is the port number?");
        String port = br.readLine();
        while (!port.matches("[0-9]*"))
        {
            System.out.println("This doesn't look like a valid port number. Try again.");
            ip = br.readLine();
        }
        
        br.close();
        br = null;
        
            server = new Socket(ip, Integer.parseInt(port));
            
            sRead = new BufferedReader(new InputStreamReader(server.getInputStream()));
            sWrite = new PrintWriter(server.getOutputStream());
            
            decision = new ActionSelector();
        
        sendMessage(NAME + "\nPLAY");
        
        if (!getNextLine().equals("% ACCEPTED")){
            System.out.println("Server denied connection. AI quitting.");
            try{
                server.close();
            } catch (IOException e){
                e.printStackTrace();
            }
            System.exit(0);
        }
        
        try{
        myPlayerNumber = Integer.parseInt(getMessage("@").split(" ")[1].trim());}
        catch (NumberFormatException e){
            e.printStackTrace();
        }
        
        sendMessage("READY");
    }
    
    private void sendMessage(String message) {
        try{
            sWrite.println(message);
            sWrite.flush();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        
        if (DEBUG)
        System.out.println("Sent to server: " + message);
    }
    
    private String getNextLine() {
        String message = "";
        try{
            message = sRead.readLine();
        }catch (SocketException e) {
            System.err.println("Connection to server failed. Quitting.");
            System.exit(0);
        }
        catch (IOException e){e.printStackTrace();}
        if (DEBUG) System.out.println("Message received from server: " + message);
    }
}