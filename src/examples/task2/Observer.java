package examples.task2;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javafx.util.Pair;
import peersim.config.FastConfig;
import peersim.core.Network;
import peersim.core.Node;
import peersim.reports.GraphObserver;
import peersim.transport.Transport;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;

public class Observer extends GraphObserver {

	int cycle = 0;
	
	public Observer(String prefix) {
		super(prefix);
	}

	public boolean execute() {
		// TODO: note that "4" is a constant here! this can be improved
		System.err.println("Observer called"+" at time "+CommonState.getTime());
		if (CommonState.getTime() != 0)
		{
			NetworkInfo.getInstance().setTOMeanCost();
		}
		
		NetworkInfo net_in = NetworkInfo.getInstance();
		String fileName = net_in.getTrace_file_name();

        // This will reference one line at a time
        String line = null;
        try {

            int total_VLR = net_in.getTotal_vlr();
            int total_HLR = net_in.getTotal_hlr();
            
            // Always wrap FileReader in BufferedReader.
            BufferedReader fileBFR = net_in.getFileBufferedReader();
            
            for (int i = 0; i <(total_HLR  + total_VLR); ++i)
            {
            	fileBFR.readLine();
            }
            
			for (int i = total_HLR  + total_VLR; i < Network.size(); i++) {
				line = fileBFR.readLine();
				if ( line != null )
				{
					Node current = (Node) Network.get(i);
					String[] val = line.split("\t");   	
//					System.out.println("Observer sent.");
					((Transport) current.getProtocol(FastConfig.getTransport(4))).send(
							current, current, new Message(current, Integer.parseInt(val[2]), Integer.parseInt(val[3])), 4);	
//					System.out.println("");
				}
			}
			
			/////
			//node, time
//			System.out.println("NOW ITS GONNA SEARCh");
			ArrayList<Pair<Integer, Integer>> lookupArr = net_in.getLookupArr();
			Node source = Network.get(net_in.getTotal_hlr() + net_in.getTotal_vlr()); //17
			
			for (int i = 0; i<lookupArr.size(); ++i)
			{
				if (lookupArr.get(i).getValue() == CommonState.getTime())
				{
					((Transport) source.getProtocol(FastConfig.getTransport(6)))
					.send(source, Network.get(0), new Message(source, lookupArr.get(i).getKey(), Message.MessageType.search), 6);
					System.err.println("Message sent From:" + source.getIndex() + " TO HLR");	
				}
			}
			
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                "Unable to open file '" + 
                fileName + "'");                
        }
        catch(IOException ex) {
            System.out.println(
                "Error reading file '" 
                + fileName + "'");                  
            // Or we could just do this: 
            // ex.printStackTrace();
        }

		return false;
	}

}
