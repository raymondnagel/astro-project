/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package astro.graphics;

import astro.Astro;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author Raymond
 */
public class AnimFrame
{
    private static        Vector<AnimFrame> ourFrames = new Vector<AnimFrame>();
    private BufferedImage myImage = null;
    private int           myDuration = 0;
    private String        myName = null;

    public int getDuration()
    {
        return myDuration;
    }
    public BufferedImage getImage()
    {
        return myImage;
    }
    public String getName()
    {
        return myName;
    }

    private AnimFrame(String filename, int duration)
    {
        myName = filename;
        myDuration = duration;
        myImage = Astro.getImageFromFile(filename);
    }


    public static AnimFrame loadFrame(String filename, int duration)
    {
        AnimFrame af = getFrameByFilename(filename);
        if (af == null)
        {
            af = new AnimFrame(filename, duration);
            ourFrames.add(af);
        }
        return af;
    }

    public static AnimFrame getFrameByFilename(String filename)
    {
        for (int af = 0; af < ourFrames.size(); af++)
        {
            if (ourFrames.get(af).getName().equalsIgnoreCase(filename))
            {
                return ourFrames.get(af);
            }
        }
        return null;
    }
}
