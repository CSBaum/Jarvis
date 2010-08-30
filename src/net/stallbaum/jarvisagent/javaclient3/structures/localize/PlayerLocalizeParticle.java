/*
 *  Player Java Client 2 - PlayerLocalizeParticle.java
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
 * $Id: PlayerLocalizeParticle.java 34 2006-02-15 17:51:14Z veedee $
 *
 */

package net.stallbaum.jarvisagent.javaclient3.structures.localize;

import net.stallbaum.jarvisagent.javaclient3.structures.*;

/**
 * A particle 
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v2.0 - Player 2.0 supported
 * </ul>
 */
public class PlayerLocalizeParticle implements PlayerConstants {

    // The particle's pose (m,m,rad) 
    private PlayerPose pose;
    // The weight coefficient for linear combination (alpha) 
    private double alpha;


    /**
     * @return  The particle's pose (m,m,rad) 
     **/
    public synchronized PlayerPose getPose () {
        return this.pose;
    }

    /**
     * @param newPose  The particle's pose (m,m,rad) 
     *
     */
    public synchronized void setPose (PlayerPose newPose) {
        this.pose = newPose;
    }
    /**
     * @return  The weight coefficient for linear combination (alpha) 
     **/
    public synchronized double getAlpha () {
        return this.alpha;
    }

    /**
     * @param newAlpha  The weight coefficient for linear combination (alpha) 
     *
     */
    public synchronized void setAlpha (double newAlpha) {
        this.alpha = newAlpha;
    }

}