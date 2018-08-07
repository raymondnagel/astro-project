/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package astro.graphics;

import astro.objects.*;
import astro.Astro;
import astro.sound.Sound;
import java.awt.Point;

/**
 *
 * @author Raymond
 */
public class Explosion extends Sprite
{
    private Animation myExplosion = null;

    static
    {
        AnimFrame.loadFrame("sprites/explode_1.png", 4);
        AnimFrame.loadFrame("sprites/explode_2.png", 4);
        AnimFrame.loadFrame("sprites/explode_3.png", 4);
        AnimFrame.loadFrame("sprites/explode_4.png", 4);
        AnimFrame.loadFrame("sprites/explode_5.png", 4);
        AnimFrame.loadFrame("sprites/explode_6.png", 4);
        AnimFrame.loadFrame("sprites/explode_7.png", 4);
    }

    public Explosion(int x, int y)
    {
        setImage(Astro.getImageFromFile("sprites/explode_1.png"));

        String[] frames = {"sprites/explode_1.png", "sprites/explode_2.png", "sprites/explode_3.png", "sprites/explode_4.png", "sprites/explode_5.png", "sprites/explode_6.png", "sprites/explode_7.png",};
        myExplosion = new Animation("explosion", false, frames);
        setAnimation(myExplosion);
        this.setCenterPoint(new Point(x, y));
        Astro.getSoundManager().makeNoise(this, new Sound(Astro.getFile("sound_fx/explode.wav")));
    }

    @Override
    public void update() {
        super.update();  
        if (myExplosion.isDone())
        {
            this.destroy();
        }
    }

}
