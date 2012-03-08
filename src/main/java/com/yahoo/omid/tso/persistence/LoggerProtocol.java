/**
 * Copyright (c) 2011 Yahoo! Inc. All rights reserved. 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. See accompanying LICENSE file.
 */

package com.yahoo.omid.tso.persistence;

import java.nio.ByteBuffer;

import com.yahoo.omid.tso.TSOState;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LoggerProtocol extends TSOState{
    private static final Log LOG = LogFactory.getLog(LoggerProtocol.class);
    
    /*
     * Protocol flags. Used to identify fields of the logger records.
     */
    public final static byte TIMESTAMPORACLE = (byte) -1;
    public final static byte COMMIT = (byte) -2;
    public final static byte LARGESTDELETEDTIMESTAMP = (byte) -3;
    public final static byte ABORT = (byte) -4;
    public final static byte FULLABORT = (byte) -5;
    
    
    /**
     * Logger protocol constructor. Currently it only constructs the
     * super class, TSOState.
     * 
     * @param logger
     * @param largestDeletedTimestamp
     */
    LoggerProtocol(long largestDeletedTimestamp){
        super(largestDeletedTimestamp);
    }
    
    void execute(ByteBuffer bb){
        LOG.info("Executing...");
        boolean done = false;
        while(!done){
            byte op = bb.get();
            long timestamp, startTimestamp, commitTimestamp;
                
            switch(op){
            case TIMESTAMPORACLE:
                timestamp = bb.getLong();
                this.getSO().initialize(timestamp);
                break;
            case COMMIT:
                startTimestamp = bb.getLong();
                commitTimestamp = bb.getLong();
                processCommit(startTimestamp, commitTimestamp);
                
                break;
            case LARGESTDELETEDTIMESTAMP:
                timestamp = bb.getLong();
                processLargestDeletedTimestamp(timestamp);
                
                break;
            case ABORT:
                timestamp = bb.getLong();
                processAbort(timestamp);
                
                break;
            case FULLABORT:
                timestamp = bb.getLong();
                processFullAbort(timestamp);
                
                break;
            }
        }
    }
    
    /**
     * Returns a TSOState object based on this object.
     * 
     * @return
     */
    TSOState getState(){
        return ((TSOState) this);
    }

}
