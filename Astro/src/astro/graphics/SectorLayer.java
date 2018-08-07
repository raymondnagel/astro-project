/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package astro.graphics;

import java.awt.Graphics2D;

/**
 *
 * @author Raymond Nagel
 */
public interface SectorLayer
{
    public abstract int getWidth();
    public abstract int getHeight();
    public abstract boolean isVisible();
    public abstract void draw(Graphics2D g, int scrX, int scrY);
}
