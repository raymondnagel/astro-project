/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package astro.objects.spaceships;

import astro.objects.projectiles.Laser;
import astro.Astro;
import astro.graphics.AnimFrame;
import astro.graphics.Animation;
import astro.shipweapons.LaserCannon;
import java.awt.Color;

/**
 *
 * @author Raymond
 */
public class Stingray extends RoboShip
{
    static
    {
        AnimFrame.loadFrame("sprites/stingray.png", 1);
        AnimFrame.loadFrame("sprites/stingray_1.png", 2);
        AnimFrame.loadFrame("sprites/stingray_2.png", 2);
    }

    public Stingray()
    {
        myMaxSpeed = 400;
        myMaxHull = 3000;
        myMaxPower = 9000;
        myHull = myMaxHull;
        myPower = myMaxPower;
        setImage(Astro.getImageFromFile("sprites/stingray.png"));

        String[] shipIdle = {"sprites/stingray.png"};
        myIdleAnimation = new Animation("stingray_idle", true, shipIdle);
        setAnimation(myIdleAnimation);

        String[] shipThrusting = {"sprites/stingray_1.png", "sprites/stingray_2.png"};
        myThrustAnimation = new Animation("stingray_thrusting", true, shipThrusting);
        
        setWeapon(new LaserCannon(Laser.COLORS.BLUE, Laser.SIZES.LG));
        myLastUpdate = System.nanoTime();
    }

    @Override
    public Color getRadarColor()
    {
        return Color.CYAN;
    }



}
