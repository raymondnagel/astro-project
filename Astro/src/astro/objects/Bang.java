/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package astro.objects;

import astro.Astro;
import astro.graphics.AnimFrame;
import astro.graphics.Animation;
import astro.sound.Sound;
import java.awt.Point;

/**
 *
 * @author Raymond
 */
public class Bang extends Sprite
{
    private Animation myBang = null;
    private Sprite myHost = null;
    private int myRelX = 0;
    private int myRelY = 0;

    static
    {
        AnimFrame.loadFrame("sprites/bang_1.png", 2);
        AnimFrame.loadFrame("sprites/bang_2.png", 2);
        AnimFrame.loadFrame("sprites/bang_3.png", 2);
        AnimFrame.loadFrame("sprites/bang_4.png", 2);
        AnimFrame.loadFrame("sprites/bang_5.png", 2);
        AnimFrame.loadFrame("sprites/bang_6.png", 2);
        AnimFrame.loadFrame("sprites/bang_7.png", 2);
    }

    public Bang(Sprite host, int x, int y)
    {
        setImage(Astro.getImageFromFile("sprites/bang_1.png"));
        myHost = host;
        String[] frames = {"sprites/bang_1.png", "sprites/bang_2.png", "sprites/bang_3.png", "sprites/bang_4.png", "sprites/bang_5.png", "sprites/bang_6.png", "sprites/bang_7.png",};
        myBang = new Animation("Bang", false, frames);
        setAnimation(myBang);
        myRelX = x - myHost.getX();
        myRelY = y - myHost.getY();
        setCenterPoint(new Point(myHost.getX()+myRelX, myHost.getY()+myRelY));
    }

    @Override
    public void update() {
        if (myHost != null)
        {
            setCenterPoint(new Point(myHost.getX()+myRelX, myHost.getY()+myRelY));
            super.update();
            if (myBang.isDone())
            {
                this.destroy();
            }
        }
        else
            this.destroy();
    }

}
