/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package astro.shipweapons;

import astro.Astro;
import astro.objects.projectiles.Rocket;
import astro.sound.Sound;
import java.awt.Point;
import java.awt.geom.Point2D;

/**
 *
 * @author Raymond
 */
public class RocketLauncher extends ShipWeapon
{
    public static final int POWER_PER_CORE = 5;

    protected int myCores = 0;

    public RocketLauncher(int cores)
    {
        myCores = cores;
        myName = "Rocket Launcher, " + myCores + "-core";
        myRequiredPower = myCores * POWER_PER_CORE;
        myFireDelay = 2000;
    }

    @Override
    public void fire()
    {
        if (myHost.getPower() >= getRequiredPower())
        {
            int dist = myHost.getWidth();
            Point sp = myHost.getCenterPoint();
            Point2D dirPoint = myHost.getAdjustedDir();
            Point pp = new Point((int)(sp.x + dirPoint.getX()*dist), (int)(sp.y + dirPoint.getY()*dist));
            Rocket rocket = new Rocket(myHost, myCores);
            rocket.setCenterPoint(pp);
            Astro.addSprite(rocket);
            myHost.decreasePower(getRequiredPower());
            Astro.getSoundManager().makeNoise(myHost, new Sound("extern/sound_fx/rocket_launch.wav"));
        }
    }


}
