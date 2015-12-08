class AI{
    
    private Socket server;
    private BufferedReader sRead;
    private PrintWriter sWrite;
    
    private ActionSelector decision;
    
    private final static String NAME = "VinceFelixIainAI";
    private int myPlayerNumber;
    private int myCoins = 1000;
    
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
        
        waitUntilMatching("@");
        try{
        myPlayerNumber = Integer.parseInt(getNextLine().split(" ")[1].trim());}
        catch (NumberFormatException e){
            e.printStackTrace();
        }
        
        sendMessage("READY");
        waitUntilMatching("% START");
        
        while (true){
            actOnMessage();
        }
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
        return message;
    }
    
    private void actOnMessage() {
        String message = getNextLine();
        
        char firstCharacter = message.charAt(0);
        switch (firstCharacter){
            case #:
                // Card is dealt
                cardDealt(message);
                break;
                
            case $:
                // Do nothing
                break;
            
            case &:
                // Turn's result
                break;
                
            case !:
                // Time out, check if it's me.
                break;
                
            case *:
                // Bankruptcy... check if me?
                break;
                
            case @:
                // New player... safely ignore
                break;
                
            case %:
                // Server command
                switch (message){
                    case "% NEWROUND":
                        resetForNewRound();
                        break;
                    
                    case "% " + myPlayerNumber + " turn":
                        // do things for my own turn
                        runMyTurn();
                        break;
                        
                    case "% SHUFFLE":
                        decision.resetCardCounter();
                        break;
                        
                    case "% FORMATERROR":
                        System.err.println("Server returned error. Quitting.");
                        System.exit(0);
                }
                break;
                
            case +:
                // Updated number of coins at end of round
                break;
            
            default:
                break;
        }
    }
    
    private boolean waitUntilMatching(String start){
        String msg = "";
        while (true) {try{
            sRead.mark(100);}catch(IOException e){e.printStackTrace();}
            msg = getNextLine();
            if (msg.startsWith(start)){
                sRead.reset();
                return true;
            }
        }
    }
    
    private void resetForNewRound(){
        decision.resetHand();
        int betAmount = 1010 - myCoins;
        if (betAmount >= myCoins || betAmount < 10){
            betAmount = 10;
        }
        sendMessage(betAmount + "");
    }
    
    private void runMyTurn() {
        int move;		
		
		// If our number of coins is less than double the bet amount, don't		
		// allow doubling down.		
		if (myCoins > 2 * betAmount)		
			move = decision.decideMove(true);		
		else		
			move = decision.decideMove(false);		
		
		// Last move is never "hit", so gets all the hits out of the way		
		while (move == ActionSelector.HIT) {		
			sendMessage("hit");		
			// Gets the card and adds it to my hand		
            // This next one is guaranteed to be a card input (hopefully)
			actOnMessage();	
			move = decision.decideMove(false);		
		}		
		
		// Either a double down or a stand must be the last move.		
		if (move == ActionSelector.DOUBLE) {		
			sendMessage("doubledown");		
		} else if (move == ActionSelector.STAND) {		
			sendMessage("stand");		
		}
    }
    
    private void cardPlayed(String input){
        decision.cardPlayed(new Card(input.split(" ")[2].charAt(0)));
    }
}