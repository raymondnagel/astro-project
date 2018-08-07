/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package astro;

import astro.graphics.SectorLayer;
import java.awt.Point;
import java.util.Random;
import java.util.Vector;

/**
 *
 * @author Raymond
 */
public abstract class Sector
{
    private int myWidth = 0;
    private int myHeight = 0;
    private Vector<SectorLayer> myLayers = new Vector<SectorLayer>();

    public void setWidth(int width)
    {
        myWidth = width;
    }
    public int getWidth()
    {
        return myWidth;
    }

    public void setHeight(int height)
    {
        myHeight = height;
    }
    public int getHeight()
    {
        return myHeight;
    }

    public Vector<SectorLayer> getLayers()
    {
        return myLayers;
    }

    public int getNumLayers()
    {
        return myLayers.size();
    }

    public void addLayer(SectorLayer layer)
    {
        myLayers.add(layer);
    }

    public Point getRandomPoint()
    {
        Random rand = new Random();
        return new Point(rand.nextInt(myWidth), rand.nextInt(myHeight));
    }
}
