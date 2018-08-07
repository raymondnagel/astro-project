/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package astro.shipweapons;

import astro.Astro;
import astro.objects.projectiles.Laser;
import astro.objects.projectiles.Laser.COLORS;
import astro.objects.projectiles.Laser.SIZES;
import astro.sound.Sound;
import java.awt.Point;
import java.awt.geom.Point2D;

/**
 *
 * @author Raymond
 */
public class LaserCannon extends ShipWeapon
{
    protected COLORS myColor = COLORS.BLUE;
    protected SIZES  mySize  = SIZES.MED;

    public LaserCannon(COLORS color, SIZES size)
    {
        myName = "Laser Cannon - " + color.name() + ", " + size.name();
        myColor = color;
        mySize = size;
        myFireDelay = 100;
        switch(mySize)
        {
            case SM: myRequiredPower = 5;
            break;
            case MED: myRequiredPower = 10;
            break;
            case LG: myRequiredPower = 20;
            break;
        }
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
            Laser laser = new Laser(myHost, mySize, myColor);
            laser.setCenterPoint(pp);
            Astro.addSprite(laser);
            myHost.decreasePower(getRequiredPower());
            String sizeText = mySize.name().toLowerCase();
            Astro.getSoundManager().makeNoise(myHost, new Sound("extern/sound_fx/laser_" + sizeText + ".wav"));
        }
    }


}
