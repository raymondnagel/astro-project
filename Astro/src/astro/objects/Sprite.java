/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package astro.objects;

import astro.Astro;
import astro.graphics.Animation;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Vector;

/**
 *
 * @author Raymond Nagel
 */
public abstract class Sprite extends Rotatable
{
    private Vector<Animation>        myAnimations = new Vector<Animation>();
    private Animation                myAnimation = null;

    public void addAnimation(Animation animation)
    {
        if (getAnimationByName(animation.getName()) == null)
        {
            myAnimations.add(animation);
        }
    }

    public void setAnimation(Animation animation)
    {
        myAnimation = animation;
    }
    public boolean setAnimation(String name)
    {
        myAnimation = getAnimationByName(name);
        return myAnimation != null;
    }

    public Animation getAnimationByName(String name)
    {
        for (int a = 0; a < myAnimations.size(); a++)
        {
            if (myAnimations.get(a).getName().equalsIgnoreCase(name))
            {
                return myAnimations.get(a);
            }
        }
        return null;
    }

    public Animation getCurrentAnimation()
    {
        return myAnimation;
    }

    public Sprite()
    {
        super(null, 0, 0);
    }

    public void update()
    {
        getCurrentAnimation().advance();
    }

    @Override
    public BufferedImage getImage()
    {
        if (myAnimation != null)
            return myAnimation.getImage();
        else
            return super.getImage();
    }

    public void destroy()
    {
        Astro.removeSprite(this);
    }

    public Color getRadarColor()
    {
        return Color.WHITE;
    }
}
