/*
 *  Player Java Client 2 - PlayerLogSetWriteState.java
 *  Copyright (C) 2006 Radu Bogdan Rusu
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * $Id: PlayerLogSetWriteState.java 34 2006-02-15 17:51:14Z veedee $
 *
 */

package net.stallbaum.jarvisagent.javaclient3.structures.log;

import net.stallbaum.jarvisagent.javaclient3.structures.*;

/**
 * Request/reply: Set write state
 * To start or stop data logging, send a PLAYER_LOG_REQ_SET_WRITE_STATE request.
 *  Null response. 
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerLogSetWriteState implements PlayerConstants {

    // State: FALSE=disabled, TRUE=enabled 
    private byte state;


    /**
     * @return  State: FALSE=disabled, TRUE=enabled 
     **/
    public synchronized byte getState () {
        return this.state;
    }

    /**
     * @param newState  State: FALSE=disabled, TRUE=enabled 
     *
     */
    public synchronized void setState (byte newState) {
        this.state = newState;
    }

}