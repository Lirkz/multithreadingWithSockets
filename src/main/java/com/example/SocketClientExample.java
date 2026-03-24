//David Saiontz 3/24/38 client for basic chat server
package com.example;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;


public class SocketClientExample{
	static int textboxes =10;
	
	/*
	 * Modify this example so that it opens a dialogue window using java swing, 
	 * takes in a user message and sends it
	 * to the server. The server should output the message back to all connected clients
	 * (you should see your own message pop up in your client as well when you send it!).
	 *  We will build on this project in the future to make a full fledged server based game,
	 *  so make sure you can read your code later! Use good programming practices.
	 *  ****HINT**** you may wish to have a thread be in charge of sending information 
	 *  and another thread in charge of receiving information.
	*/
    public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException, InterruptedException{
        //get the localhost IP address, if server is running on some other IP, you need to use that
        InetAddress host = InetAddress.getLocalHost();
        Socket socket = new Socket(host.getHostName(), 9876);
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
        JFrame f = new JFrame("Chatter");
        JPanel history = new JPanel();
        history.setLayout(new GridLayout(0,1));
        ArrayList<JLabel> pastMessages = new ArrayList<JLabel>();
        JTextField input = new JTextField();
        input.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                oos.writeObject(input.getText());
				oos.flush();
                input.setText("");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }});
        for (int i =0;i<textboxes;i++){
            JLabel j = new JLabel();
            pastMessages.add(j);
            history.add(j);
        }
        f.setLayout(new GridBagLayout()); 
        GridBagConstraints g = new GridBagConstraints();
        g.weighty=0.1;
        g.weightx=0.5;
        g.ipady=1;
        g.fill=GridBagConstraints.HORIZONTAL;
        g.gridheight=1;
        g.gridx=0;
        g.gridy=5;
        f.add(input,g);
        g.anchor=GridBagConstraints.FIRST_LINE_START;
        g.gridy=0;
        g.gridheight=5;
        f.add(history,g);
        f.setVisible(true);
        f.setSize(300,300);
        while (true){
            try{
                String message = (String) ois.readObject();
                JLabel label = new JLabel(message);
                label.setAlignmentX(Component.CENTER_ALIGNMENT);
                history.remove(pastMessages.get(0));
                pastMessages.remove(0);
                pastMessages.add(label);
                history.add(label);
                history.revalidate();
                history.repaint();
            }
            catch(Exception e){
                System.out.println("Disconnected from server");
                oos.close();
                ois.close();
                socket.close();
                break;
            } 
        }
    }
}
