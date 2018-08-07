/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package astro.sound;

import astro.Astro;
import astro.objects.Sprite;
import java.util.Vector;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

/**
 *
 * @author rnagel
 */
public class SoundManager implements LineListener
{
    public static final int SOUND_DISTANCE = 1000;
    private Vector<Sound>   mySounds = null;
    private int             myMaxSimultaneousSounds = 0;

    public SoundManager(int maxSounds)
    {
        mySounds = new Vector<Sound>(maxSounds);
        myMaxSimultaneousSounds = maxSounds;
    }

    public void update(LineEvent event)
    {
        if (event.getType() == LineEvent.Type.STOP)
        {
            for (int s = 0; s < mySounds.size(); s++)
            {
                if (mySounds.get(s).getClip() == event.getLine())
                {
                    mySounds.removeElementAt(s);
                }
            }
        }
    }

    public boolean playSound(Sound sound)
    {
        if (mySounds.size() < myMaxSimultaneousSounds)
        {
            sound.getClip().addLineListener(this);
            mySounds.add(sound);
            sound.start();
            return true;
        }
        return false;
    }


    public boolean makeNoise(Sprite sprite, Sound sound)
    {
        if (sprite.getDistanceFromPoint(Astro.getSubject().getCenterPoint()) < SOUND_DISTANCE)
        {
            return playSound(sound);
        }
        else
        {
            return false;
        }
    }

    public boolean loopSound(Sound sound, int count)
    {
        if (mySounds.size() < myMaxSimultaneousSounds)
        {
            sound.getClip().addLineListener(this);
            mySounds.add(sound);
            sound.loop(count);
            return true;
        }
        return false;
    }
}
