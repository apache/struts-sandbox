/*
 * Copyright 2004-2005 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shale.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;
import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.faces.el.PropertyResolver;
import org.apache.shale.faces.ShaleConstants;

/**
 * <p>Utility methods supporting the generation and validation of transaction
 * tokens, used to avoid duplicate form submits.</p>
 *
 * $Id$
 */
public class TokenProcessor {
    
    
    // ------------------------------------------------------ Instance Variables


    /**
     * <p>Timestamp most recently used to generate a transaction token value.</p>
     */
    private long previous;


    // ----------------------------------------------------------- Pubic Methods


    /**
     * <p>Generate and return the next transaction token value, and store it
     * so that it may be verified on a subsequent form submit.</p>
     *
     * @param context <code>FacesContext</code> for the current request
     */
    public synchronized String generate(FacesContext context) {

        // Acquire the session identifier for this request
        // (creating the session if necessary)
        Object session = context.getExternalContext().getSession(true);
        PropertyResolver pr = context.getApplication().getPropertyResolver();
        byte id[] = ((String) pr.getValue(session, "id")).getBytes();

        // Acquire the timestamp we will use for this request
        long current = System.currentTimeMillis();
        if (current == previous) {
            current++;
        }
        byte now[] = new Long(current).toString().getBytes();

        // Calculate the new transaction token value
        String token = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(id);
            md.update(now);
            token = toHex(md.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new FacesException(e);
        }

        // Store the generated value for later verification
        Set set = (Set)
          context.getExternalContext().getSessionMap().get(ShaleConstants.TOKENS);
        if (set == null) {
            set = new HashSet();
            context.getExternalContext().getSessionMap().put(ShaleConstants.TOKENS, set);
        }
        set.add(token);

        // Return the generated and cached value
        return token;

    }


    /**
     * <p>Verify that the specified transaction token value (retrieved from an
     * incoming request) is a valid transaaction token.  In addition, remove it
     * from any stored cache of tokens, so that it may not be reused.</p>
     *
     * @param context <code>FacesContext</code> for the current request
     * @param token Transaction token to be verified
     *
     * @return <code>True</code> if this token has been verified,
     *  else <code>false</code>
     */
    public synchronized boolean verify(FacesContext context, String token) {

        Set set = (Set)
          context.getExternalContext().getSessionMap().get(ShaleConstants.TOKENS);
        if (set == null) {
            return false;
        }
        if (set.contains(token)) {
            set.remove(token);
            if (set.size() < 1) {
                context.getExternalContext().getSessionMap().remove(ShaleConstants.TOKENS);
            }
            return true;
        }
        return false;

    }


    // --------------------------------------------------------- Private Methods


    /**
     * <p>Convert the specified byte array into a String of hexadecimal
     * digit characters.</p>
     *
     * @param buffer Byte array to be converted
     */
    private String toHex(byte buffer[]) {

        StringBuffer sb = new StringBuffer(buffer.length * 2);
        for (int i = 0; i < buffer.length; i++) {
            sb.append(Character.forDigit((buffer[i] & 0xf0) >> 4, 16));
            sb.append(Character.forDigit((buffer[i] & 0x0f), 16));
        }
        return sb.toString();

    }


}
