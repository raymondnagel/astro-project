/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package astro.objects.spaceships;

import astro.objects.projectiles.Laser;
import astro.Astro;
import astro.graphics.AnimFrame;
import astro.graphics.Animation;
import astro.shipweapons.BeamLaser;
import java.awt.Color;

/**
 *
 * @author Raymond
 */
public class FireHawk extends RoboShip
{
    static
    {
        AnimFrame.loadFrame("sprites/firehawk.png", 1);
        AnimFrame.loadFrame("sprites/firehawk_1.png", 2);
        AnimFrame.loadFrame("sprites/firehawk_2.png", 2);
    }

    public FireHawk()
    {
        myMaxSpeed = 50;
        myMaxHull = 9000;
        myMaxPower = 20000;
        myHull = myMaxHull;
        myPower = myMaxPower;
        setImage(Astro.getImageFromFile("sprites/firehawk.png"));

        String[] shipIdle = {"sprites/firehawk.png"};
        myIdleAnimation = new Animation("firehawk_idle", true, shipIdle);
        setAnimation(myIdleAnimation);

        String[] shipThrusting = {"sprites/firehawk_1.png", "sprites/firehawk_2.png"};
        myThrustAnimation = new Animation("firehawk_thrusting", true, shipThrusting);
        
        setWeapon(new BeamLaser(Color.RED, Laser.SIZES.LG));
        myLastUpdate = System.nanoTime();
    }

    @Override
    public Color getRadarColor()
    {
        return Color.RED;
    }

    @Override
    public void turnLeft()
    {
        this.rotate(-.005);
    }

    @Override
    public void turnRight()
    {
        this.rotate(+.005);
    }




}
