/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package astro.objects.projectiles;

import astro.objects.*;
import astro.objects.spaceships.Ship;
import astro.Astro;
import astro.graphics.AnimFrame;
import astro.graphics.Animation;
import astro.sound.Sound;
import java.util.Random;

/**
 *
 * @author Raymond
 */
public class IonRay extends Projectile
{
    public static final double SPEED = 1200;

    static
    {
        AnimFrame.loadFrame("sprites/ion_ray.png", 2);
    }

    public IonRay(Sprite owner)
    {
        super(owner);
        setImage(Astro.getImageFromFile("sprites/ion_ray.png"));
        String frames[] = {"sprites/ion_ray.png"};
        Animation ion = new Animation("ion ray", true, frames);
        this.setAnimation(ion);
        setSpeedFactor(SPEED);        
        myMaxDistance = 800;
        setAngle(owner.getAngle());
    }


    @Override
    protected int getDamagePower()
    {
        int base = 10, mult = 1, die = 50;
        return base + (mult * (new Random().nextInt(die)));
    }

    protected int getShieldReduction()
    {
        int base = 100, mult = 4, die = 400;
        return base + (mult * (new Random().nextInt(die)));
    }

    @Override
    public void impact(Ship ship)
    {
        Astro.getSoundManager().makeNoise(this, new Sound("extern/sound_fx/bang.wav"));
        this.destroy();
        Astro.addSprite(new Bang(ship, getCenterPoint().x, getCenterPoint().y));
        if (ship.isShieldsUp())
            ship.decreasePower(getShieldReduction());
       
        ship.hurt(getDamagePower());
    }

}
