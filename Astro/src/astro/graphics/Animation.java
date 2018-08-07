/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package astro.graphics;

import java.awt.image.BufferedImage;
import java.util.Vector;

/**
 *
 * @author Raymond
 */
public class Animation
{
    private Vector<AnimFrame>       myFrames = new Vector<AnimFrame>();
    private boolean                 myRepeat = false;
    private boolean                 myDone = false;
    private String                  myName = null;
    private int                     myIndex = 0;
    private int                     myCycleCount = 0;

    public Animation(String name, boolean repeating, Vector<AnimFrame> frames)
    {
        myName = name;
        myRepeat = repeating;
        myFrames.addAll(frames);
        reset();
    }
    public Animation(String name, boolean repeating, String[] filenames)
    {
        myName = name;
        myRepeat = repeating;
        for (int f = 0; f < filenames.length; f++)
            myFrames.add(AnimFrame.getFrameByFilename(filenames[f]));
        reset();
    }

    public BufferedImage getImage()
    {
        return myFrames.get(myIndex).getImage();
    }

    public boolean isRepeating()
    {
        return myRepeat;
    }

    public boolean isDone()
    {
        return myDone;
    }

    public String getName()
    {
        return myName;
    }

    public int getCurrentIndex()
    {
        return myIndex;
    }

    public void advance()
    {
        if (!myDone)
        {
            myCycleCount++;
            if (myCycleCount >= myFrames.get(myIndex).getDuration())
            {
                myCycleCount = 0;
                myIndex++;
                if (myIndex >= myFrames.size())
                {
                    if (myRepeat)
                    {
                        myIndex = 0;
                    }
                    else
                    {
                        myDone = true;
                        myIndex--;
                        done();
                    }
                }
            }
        }
    }

    public void reset()
    {
        myIndex = 0;
        myCycleCount = 0;
    }

    public void done()
    {
        
    }
}
