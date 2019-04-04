/*
 * Copyright (c) 2003-2005 The BISON Project
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 2 as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 */

package examples.task2;

import java.lang.Math; 
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;

/**
 * <p>
 * This intialization class collects the simulation parameters from the config
 * file and generates uniformly random 2d-coordindates for each node. The
 * coordianates are distributed on a unit (1.0) square.
 * </p>
 * <p>
 * The first node in the {@link Network} is considered as the root node and its
 * coordinate is set to the center of the square.
 * </p>
 * 
 * 
 * @author Gian Paolo Jesi
 */
public class Initializer implements Control {
	/**
	 * The protocol to operate on.
	 * 
	 * @config
	 */
	private static final String PAR_PROT = "protocol";

	/** Protocol identifier, obtained from config property {@link #PAR_PROT}. */
	public static int pid;

	/**
	 * Standard constructor that reads the configuration parameters. Invoked by
	 * the simulation engine.
	 * 
	 * @param prefix
	 *            the configuration prefix for this class.
	 */
	public Initializer(String prefix) {
		pid = Configuration.getPid(prefix + "." + PAR_PROT);
	}

	/**
	 * Initialize the node coordinates. The first node in the {@link Network} is
	 * the root node by default and it is located in the middle (the center of
	 * the square) of the surface area.
	 */
	public boolean execute() {
		// Set the root: the index 0 node by default.
		Node n = Network.get(0);
		Coordinates prot = (Coordinates) n.getProtocol(pid);
		prot.setX(0.5);
		prot.setY(0.5);
		
		NetworkInfo net_in = NetworkInfo.getInstance();
		String fileName = net_in.getTrace_file_name();
		
        // This will reference one line at a time
        String line = null;
        try {
        	
            //setting HLR Location
            n = Network.get(0);
			prot = (Coordinates) n.getProtocol(pid);
			prot.setX(net_in.getGraph_size()/2);
			prot.setY(net_in.getGraph_size()/2);
            
			//setting VLR Location
			int la_WH = net_in.getLa_width_height();
			int x = 0, y =0;
			int node_start_idx = (net_in.getTotal_vlr()+net_in.getTotal_hlr());
			int ad = 1;
			for ( ; ad<node_start_idx; ++ad )
			{
				n = Network.get(ad);
				prot = (Coordinates) n.getProtocol(pid);
				x = (ad-1)%net_in.getTotal_col_la();
				y = (ad-1)/net_in.getTotal_col_la();
				prot.setX(x*la_WH + (la_WH/2));
				prot.setY(y*la_WH + (la_WH/2));
//				System.out.println(prot.getX() + "HOLLAAAA" + prot.getY());
			}

       
			BufferedReader fileBFR = net_in.getFileBufferedReader();
			
            //skipping first hlr + vlr nodes
            for (int i = 0; i < ad; ++i)
            {
            	fileBFR.readLine();
            }
            
            while((line = fileBFR.readLine()) != null) {
//                System.out.println(line + " "+ ad);
                String[] val = line.split("\t");   	
                
		        n = Network.get(ad);
				prot = (Coordinates) n.getProtocol(pid);
				prot.setX(Integer.valueOf((val[2])));
				prot.setY(Integer.valueOf((val[3])));
//		        System.out.println("ad: " + ad +" Val: " +val[1] +" "+  val[2] +" -- "+val[3] );
                
		        ++ad;
		        if (ad >= Network.size())
		        	break;
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
