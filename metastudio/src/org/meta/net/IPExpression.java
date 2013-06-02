/*
 * IPExpression.java
 *
 * Created on July 16, 2006, 7:21 AM
 */

package org.meta.net;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Understands IP regular expression, say A:B:C:* and provides a mechanism
 * to iterate through all the IP addresses satisfying the wild-card notation
 * of the IP. Also provides a way to check if a arbitary IP satisfies 
 * the IP regular expression rule.
 * <pre>
 * Limitations: 
 * a) In its current form, the algorithm only works for a local 
 *    IPV4 based network. 
 * b) You can only provide one wild-card search. Two stars are not allowed.
 *    eg. A:B:*:* is not allowed.
 * c) However, you may provide a part of the IP with two '?'s as in 
 *    A:B:C:1??, but A:2?:C:1? is illegal.
 * d) You can not include both '?' and '*' at the same time in the ip 
 *    expression
 * </pre>
 *
 * Note: You need to provide IP addresses with ':' instead of '.' (IPV4). 
 * IPV6 is not currently accepted. (but the algo. will try to convert...)
 *
 * @author V.Ganesh
 * @version 2.0 (Part of MeTA v2.0)
 */
public class IPExpression {
    
    private boolean isLoacalAddress;
    private String [] words;
    private int wildCardIndex;
    
    private ArrayList<InetAddress> ipList;
    private Iterator<InetAddress> ips;
    
    /** Creates a new instance of IPExpression 
     *
     * @throws UnsupportedOperationException if invalid expression
     */
    public IPExpression(String ipExpression) {
        this.ipExpression = ipExpression;
        
        checkExpression();
        generateAddress();
    }

    /**
     * Check if this IP address satisfies the IP expression?
     *
     * @return a boolean true / false indicating if the IP address matches the
     *         criterion specified in the IP expression.
     */
    public boolean matches(InetAddress ip) {
        return ipList.contains(ip);
    }
    
    /**
     * Generate IP addresses
     */
    private void generateAddress() {
        ipList = new ArrayList<InetAddress>();
        
        if (isLoacalAddress) {
            ips = ipList.iterator();
            return;
        } // end if
            
        // next depending on * of ? as wild-cards we generate the IP address
        // and search for MeTA services at these addresses
        if (words[wildCardIndex].indexOf('*') >= 0)
            multiCardIP(words, wildCardIndex);
        else if (words[wildCardIndex].indexOf('?') >= 0)
            singleCardIP(words, wildCardIndex);
        else {
            try {
                // try to siliently convert all ':' to '.'s
                ipExpression = ipExpression.replace(':', '.');
                
                InetAddress newAddress = InetAddress.getByName(ipExpression);
                ipList.add(newAddress);
            } catch (Exception e) {
                System.err.println("Unexpected error in " +
                        "IPExpression.generateAddress(): " + e);
                e.printStackTrace();                
            } // end of try .. catch block 
        } // end if
        
        ips = ipList.iterator();
    }
    
    /**
     * '*' based IP search
     * 
     * @param words components of IP address
     * @param wildCardIndex the index in the component containing the wild-card
     */    
    private void multiCardIP(String [] words, int wildCardIndex) {
        for(int i=1; i<=Byte.MAX_VALUE*2; i++) {
            words[wildCardIndex] =  "" + i;

            String addr = "";
            for(int j=0; j<words.length-1; j++) addr += words[j]+".";
            addr += words[words.length-1];
            
            try {
                InetAddress newAddress = InetAddress.getByName(addr);
                ipList.add(newAddress);
            } catch (Exception e) {
                System.err.println("Unexpected error in " +
                        "IPExpression.generateAddress(): " + e);
                e.printStackTrace();
                continue;
            } // end of try .. catch block                       
        } // end for
    }
    
    /**
     * '?' based IP search
     * 
     * @param words components of IP address
     * @param wildCardIndex the index in the component containing the wild-card
     */  
    private void singleCardIP(String [] words, int wildCardIndex) {
        // count number of '?'s, if more than three, then do a full search
        int qCount = 0;
        for(int i=0; i<words[wildCardIndex].length(); i++) {
            if (words[wildCardIndex].charAt(i) == '?') qCount++;
        } // end for
        
        if (qCount >= 3) {
            multiCardIP(words, wildCardIndex);
            return;
        } // end if 
        
        int indx = words[wildCardIndex].indexOf('?');
        int lastIndx = words[wildCardIndex].lastIndexOf('?');        
        
        if (indx == lastIndx) singleCardIPSub(words, wildCardIndex, indx);
        else {
            for(int i=0; i<10; i++) {
                words[wildCardIndex] = words[wildCardIndex].substring(0, indx) 
                                    + i + words[wildCardIndex].substring(indx+1, 
                                                words[wildCardIndex].length());
                singleCardIPSub(words, wildCardIndex, lastIndx);
            } // end if
        } // end if
    }
    
    /**
     * Helper for singleCardIP()
     *
     * @param words components of IP address
     * @param wildCardIndex the index in the component containing the wild-card
     * @param indx index of '?' under consideration
     */
    private void singleCardIPSub(String [] words, int wildCardIndex, int indx) {
        for(int i=0; i<10; i++) {
            words[wildCardIndex] = words[wildCardIndex].substring(0, indx) + i + 
                                    words[wildCardIndex].substring(indx+1, 
                                                words[wildCardIndex].length());
            
            if (Integer.parseInt(words[wildCardIndex]) > 254) break;
                        
            String addr = "";
            for(int j=0; j<words.length-1; j++) addr += words[j]+".";
            addr += words[words.length-1];
            
            try {
                InetAddress newAddress = InetAddress.getByName(addr);
                ipList.add(newAddress);
            } catch (Exception e) {
                System.err.println("Unexpected error in " +
                        "IPExpression.generateAddress(): " + e);
                e.printStackTrace();
                continue;
            } // end of try .. catch block
        } // end for
    }
    
    /**
     * check the IP expression
     *
     * @throws UnsupportedOperationException if invalid expression
     */
    private void checkExpression() {
        // try to siliently convert all dots to ':'
        ipExpression = ipExpression.replace('.', ':');
        
        // check the expression for any violations
        // first check for double '*' s
        if (ipExpression.indexOf('*') != ipExpression.lastIndexOf('*')) {
            throw new UnsupportedOperationException("Illegal expression, " +
                    " * can be used only once in expression: " + ipExpression);
        } // end if
        
        // then check if '*' and '?' occur together 
        if ((ipExpression.indexOf('*') >= 0) 
            && (ipExpression.indexOf('?') >= 0)) {
            throw new UnsupportedOperationException("Illegal expression, " +
                    " * and ? can not be used together in expression: " 
                    + ipExpression);
        } // end if
        
        // next check if more than one '?'s are present
        // if so, all of them should be part of only one part of
        // the dotted notation:
        // A:B:C:1?? is valid, so is A:2?:C:D but not A:2?:C:1?
        if (ipExpression.indexOf('?') != ipExpression.lastIndexOf('?')) {
            String [] words = ipExpression.split(":");
            int qFound = 0;
            
            for(int i=0; i<words.length; i++) {
                if (words[i].indexOf('?') >= 0) qFound++;
            } // end for
            
            if (qFound > 1) {
                throw new UnsupportedOperationException("Illegal expression, " +
                    " ? can be used in only one sub-part of the expression: " 
                    + ipExpression);
            } // end if
        } // end if
        
        // if we reach here, probably the expession is a valid one
        // but still needs to be checked if each part is a number
        words = ipExpression.split(":");
        wildCardIndex = 0;
        
        for(int i=0; i<words.length; i++) {
            if ((words[i].indexOf('?') >= 0) || ((words[i].indexOf('*') >= 0))){
                wildCardIndex = i;
                continue;
            } // end if
            
            try {
                // check if it is an integer?
                Integer.parseInt(words[i]);
                
                if (i==0) {
                    // check if a local address is given for probing
                    // if so we return back!!
                    if (Integer.parseInt(words[i]) == 127) {                        
                        isLoacalAddress = true;
                        return;
                    } // end if
                } // end if
            } catch (Exception e) {
                throw new UnsupportedOperationException("Illegal expression, " +
                    " sub-parts of expression not containing ? and * should " +
                    "be integers: " + ipExpression);
            } // end of try .. catch block
        } // end for
    }
    
    /**
     * Holds value of property ipExpression.
     */
    private String ipExpression;

    /**
     * Getter for property ipExpression.
     * @return Value of property ipExpression.
     */
    public String getIpExpression() {
        return this.ipExpression;
    }
    
    /**
     * Return the next IP address in the list, or a null if none is available.
     */
    public InetAddress getNextAddress() {
        if (ips.hasNext()) return ips.next();
        else return null;
    }
} // end of class IPExpression
