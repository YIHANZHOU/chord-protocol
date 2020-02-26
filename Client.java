import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransportFactory;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSSLTransportFactory.TSSLTransportParameters;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TTransportException;
import java.io.*;
import java.util.*;
import java.net.*;
import java.security.*;
import java.math.BigInteger;

class Client {
  public static void main(String [] args) {
    // take the paramenters from the command line
    if(args.length != 2) {
      System.out.println("Parameters are not correct: in the format [SuperIP] [SuperPort]");
      return;
    }
    int port = Integer.parseInt(args[1]);
    // connect to super node to get a random node
    SuperNodeThrift.Client superNode = connectSuperNode(args[0], port);
    // get a random node address
    NodeInfo node = null;
    try {
      node = superNode.nodeInfo();
    } catch(TException e) {
      System.out.println("call nodeInfo() fail");
      System.exit(0);
    }

    // abort if the DHT is not ready
    if (node.id.equals("NOTREADY")) {
      System.out.println("=====================================================");
      System.out.println("  DHT Not ready, Please Connect Later");
      System.exit(0);
    }


    // main loop
    while(true) {
      // for each seperate operation, get a different random node
      try {
        node = superNode.nodeInfo();
      } catch(TException e) {
        System.out.println("call nodeInfo() fail");
        System.exit(0);
      }
      // build the connection to random node
      NodeThrift.Client randomNode = connectNode(node.ip, node.port);
      System.out.println("======================================================");
      System.out.println(" Please select an operation,only type in the a number:");
      System.out.println("   1: set a single pair");
      System.out.println("   2: set pairs through file");
      System.out.println("   3: get genre by title");
      System.out.println("   4: exit");
      Scanner sc = new Scanner(System.in);
      String operation =sc.nextLine();
      if (operation.equals("1")) {  // set a single pair
        setSinglePair(randomNode);
      } else if (operation.equals("2")) {  // set pairs through a file
        setByFile(randomNode);
      } else if (operation.equals("3")) {  // get a single pair
        getSinglePair(randomNode);
      } else if (operation.equals("4")) {  // exit the system
        System.out.println("Exit!");
        break;
      } else {  // incorrect input
        System.out.println("Invalid input!!");
      }
    }

  }


  // helper function
  // 1: set a single pair
  public static void setSinglePair(NodeThrift.Client node) {
    String title = null;
    String genre = null;
    String track = null;
    System.out.println("1:Set a single pair! Type in the title:");
    Scanner sc = new Scanner(System.in);
    title = sc.nextLine();
    System.out.println("Type in the genre:");
    genre = sc.nextLine();
    // TODO: check the title and genre format: empty?new line?...

    System.out.println("Type in 'yes' if you want node tracking.Anything else means no:");
    track = sc.nextLine();
    List<String> trackInfo = null;
    try {  // call setPair() to the node
      trackInfo = node.setPair(title, genre);
    } catch(TException e) {
      System.out.println("call setPair() fail");
      System.exit(0);
    }
    System.out.println("Pair set success!");
    // based on the input, print out different feedback
    if (track.equals("yes")) {  // print out the node tracking information
      printNodeTrack(trackInfo);
    }
  }
  // 2: set pairs through a file
  public static void setByFile(NodeThrift.Client node) {
    System.out.println("2:Set by a file! Type in the file name:");
    Scanner sc = new Scanner(System.in);
    String fileName = sc.nextLine();
    File file = new File(fileName);
    System.out.println("Type in 'yes' if you want node tracking.Anything else means no:");
    String track = sc.nextLine();
    Scanner scFile = null;
    try {
      scFile = new Scanner(file);
      while(scFile.hasNext()) {
        String words = scFile.nextLine();
        String[] word = words.split(":");
        List<String> trackInfo = node.setPair(word[0],word[1]);
        if (track.equals("yes")) {  // print out the node tracking information
          System.out.println("Pair <"+word[0]+","+word[1]+"> ");
          printNodeTrack(trackInfo);
        }
      }
      System.out.println("already set every pair in the book!");
    } catch(Exception e) {
      // e.printStackTrace();
      System.out.println("File not exist. Check your parameter");
    }
  }

  // 3: get a single pair
  public static void getSinglePair(NodeThrift.Client node) {
    String title = null;
    String track = null;
    System.out.println("3:Get a single pair! Type in the title:");
    Scanner sc = new Scanner(System.in);
    title = sc.nextLine();
    // TODO: check the title format: empty?new line?...

    System.out.println("Type in 'yes' if you want node tracking.Anything else means no:");
    track = sc.nextLine();
    ResultGet resultGet = null;
    try {  // call getPair() to the node
      resultGet = node.getPair(title);
    } catch(TException e) {
      System.out.println("call getPair() fail");
      System.exit(0);
    }
    // based on the feedback, give information
    if (resultGet.type == 2) {  // 1:has this value in the DHT; 2:no this value
      System.out.println("There is no such title in the DHT");
    } else {  // this pair it is in the system
      System.out.println("The genre is: "+resultGet.genre);
      if (track.equals("yes")) {
        printNodeTrack(resultGet.trackInfo);
      }
    }
  }

  // print out the track info
  public static void printNodeTrack(List<String> trackInfo) {
    System.out.println("Node Track Info:");
    for (int i=0; i<trackInfo.size(); i++) {
      System.out.println(" "+(i+1)+": "+trackInfo.get(i));
    }
  }
  // build connection to super node
  public static SuperNodeThrift.Client connectSuperNode(String ip, int port) {
    SuperNodeThrift.Client client = null;
    try {
      TTransport transport = new TSocket(ip, port);
      TProtocol protocol = new TBinaryProtocol(new TFramedTransport(transport));
      client = new SuperNodeThrift.Client(protocol);
      //Try to connect
      transport.open();
    } catch(TException e) {
      e.printStackTrace();
      System.exit(0);
    }
    return client;
  }
  // build connection to node
  public static NodeThrift.Client connectNode(String ip, int port) {
    NodeThrift.Client client = null;
    try {
      TTransport transport = new TSocket(ip, port);
      TProtocol protocol = new TBinaryProtocol(new TFramedTransport(transport));
      client = new NodeThrift.Client(protocol);
      //Try to connect
      transport.open();
    } catch(TException e) {
      e.printStackTrace();
      System.exit(0);
    }
    return client;
  }
}
