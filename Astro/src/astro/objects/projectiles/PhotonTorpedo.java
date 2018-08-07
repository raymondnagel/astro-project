/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package astro.objects.projectiles;

import astro.objects.*;
import astro.Astro;
import astro.graphics.AnimFrame;
import astro.graphics.Animation;
import java.util.Random;

/**
 *
 * @author Raymond
 */
public class PhotonTorpedo extends Projectile
{
    public static final double SPEED = 1000;

    static
    {
        AnimFrame.loadFrame("sprites/photon_1.png", 2);
        AnimFrame.loadFrame("sprites/photon_2.png", 2);
        AnimFrame.loadFrame("sprites/photon_3.png", 2);
        AnimFrame.loadFrame("sprites/photon_4.png", 2);
    }

    public PhotonTorpedo(Sprite owner)
    {
        super(owner);
        setImage(Astro.getImageFromFile("sprites/photon_1.png"));
        String frames[] = {"sprites/photon_1.png", "sprites/photon_2.png", "sprites/photon_3.png", "sprites/photon_4.png"};
        Animation photon = new Animation("photon", true, frames);
        this.setAnimation(photon);
        setSpeedFactor(SPEED);        
        myMaxDistance = 1200;
        setAngle(owner.getAngle());
    }

    @Override
    public void update()
    {
        getCurrentAnimation().advance();
        super.update();
    }



    @Override
    protected int getDamagePower()
    {
        int base = 500, mult = 1, die = 500;
        return base + (mult * (new Random().nextInt(die)));
    }

}
