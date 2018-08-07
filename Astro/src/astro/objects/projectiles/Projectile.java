/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package astro.objects.projectiles;

import astro.objects.*;
import astro.objects.spaceships.Ship;
import astro.Astro;
import astro.sound.Sound;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.Vector;

/**
 *
 * @author Raymond Nagel
 */
public abstract class Projectile extends Sprite
{
    protected double mySpeedFactor = 100;
    protected double myRemainingHorz = 0;
    protected double myRemainingVert = 0;
    protected long myLastUpdate = 0;
    protected int myMaxDistance = 1200;
    protected int myTraveledDistance = 0;
    protected Sprite myOwner = null;

    protected void setSpeedFactor(double speedFactor)
    {
        mySpeedFactor = speedFactor;
    }


    public Projectile(Sprite owner)
    {
        super();
        myOwner = owner;
        myLastUpdate = System.nanoTime();
    }

    protected abstract int getDamagePower();
   
    public void impact(Ship ship)
    {
        Astro.getSoundManager().makeNoise(this, new Sound("extern/sound_fx/bang.wav"));
        this.destroy();
        Astro.addSprite(new Bang(ship, getCenterPoint().x, getCenterPoint().y));
        ship.hurt(getDamagePower());
    }

    @Override
    public void update()
    {
        double elapsedTime = (double)(System.nanoTime()-myLastUpdate)/1000000000;
        Point2D dirPoint = getAdjustedDir();
        double xT = (mySpeedFactor*dirPoint.getX() * elapsedTime) + myRemainingHorz;
        double yT = (mySpeedFactor*dirPoint.getY() * elapsedTime) + myRemainingVert;
        int xMov = (int)xT;
        int yMov = (int)yT;
        myRemainingHorz = xT-xMov;
        myRemainingVert = yT-yMov;
        setLocation(getX()+xMov, getY()+yMov);
        myLastUpdate = System.nanoTime();
        myTraveledDistance += (Math.abs(xMov) + Math.abs(yMov));

        Vector<Ship> ships = Astro.getShips();
        for (int s = 0; s < ships.size(); s++)
        {
            if (ships.get(s) != myOwner && ships.get(s).containsPoint(getCenterPoint()))
            {
                impact(ships.get(s));
            }
        }

        if (myTraveledDistance >= myMaxDistance)
        {
            this.destroy();
        }
    }

    @Override
    public Color getRadarColor()
    {
        return Color.ORANGE;
    }


}
