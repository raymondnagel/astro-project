/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package astro;

import java.io.File;

/**
 *
 * @author Raymond
 */
public class Universe
{
    private File[][] mySectors = null;
    private int myWidth = 0;
    private int myHeight = 0;

    public Universe(int widthInSectors, int heightInSectors)
    {
        myWidth = widthInSectors;
        myHeight = heightInSectors;
        mySectors = new File[myWidth][myHeight];
        for (int x = 0; x < myWidth; x++)
            for (int y = 0; y < myHeight; y++)
            {
                mySectors[x][y] = SpaceSector.randomToFile();
            }
    }

    public static Universe loadFromFile(File universeFile)
    {
        throw new UnsupportedOperationException("This method not yet supported!");
    }


}
