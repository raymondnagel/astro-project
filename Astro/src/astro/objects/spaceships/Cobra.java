/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package astro.objects.spaceships;

import astro.objects.projectiles.Laser;
import astro.objects.*;
import astro.Astro;
import astro.graphics.AnimFrame;
import astro.graphics.Animation;
import astro.shipweapons.LaserCannon;
import java.awt.Color;

/**
 *
 * @author Raymond
 */
public class Cobra extends RoboShip
{
    static
    {
        AnimFrame.loadFrame("sprites/cobra.png", 1);
        AnimFrame.loadFrame("sprites/cobra_1.png", 2);
        AnimFrame.loadFrame("sprites/cobra_2.png", 2);
    }

    public Cobra()
    {
        myMaxHull = 1400;
        myHull = myMaxHull;
        setImage(Astro.getImageFromFile("sprites/cobra.png"));

        String[] shipIdle = {"sprites/cobra.png"};
        myIdleAnimation = new Animation("cobra_idle", true, shipIdle);
        setAnimation(myIdleAnimation);

        String[] shipThrusting = {"sprites/cobra_1.png", "sprites/cobra_2.png"};
        myThrustAnimation = new Animation("cobra_thrusting", true, shipThrusting);

        setWeapon(new LaserCannon(Laser.COLORS.RED, Laser.SIZES.MED));
        myLastUpdate = System.nanoTime();
    }

    @Override
    public Color getRadarColor()
    {
        return Color.GREEN;
    }

    


}
