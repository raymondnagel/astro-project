/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package astro.graphics;

import java.awt.image.BufferedImage;

/**
 *
 * @author Raymond Nagel
 */
public class Tile
{
    private int myWidth = 0;
    private int myHeight = 0;
    private boolean myObstacle = false;

    private BufferedImage myImage = null;

    public int getWidth()
    {
        return myWidth;
    }
    public int getHeight()
    {
        return myHeight;
    }

    public BufferedImage getImage()
    {
        return myImage;
    }

    public boolean isObstacle()
    {
        return myObstacle;
    }
}
