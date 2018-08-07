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
import java.awt.Point;
import java.util.Random;

/**
 *
 * @author Raymond
 */

public class RoboShip extends Ship implements AutoShip
{
    public static enum AUTO_MODES {WANDER, EXPLORE, PATROL, HUNT};
    public static int ourFireInterval = 2;

    protected Point myGoalPoint = null;
    protected int myTurnsSinceFire = 0;
    protected AUTO_MODES myAutoMode = AUTO_MODES.WANDER;
    protected boolean myChasing = false;

    static
    {
        AnimFrame.loadFrame("sprites/enemy_ship.png", 1);
        AnimFrame.loadFrame("sprites/enemy_ship_1.png", 2);
        AnimFrame.loadFrame("sprites/enemy_ship_2.png", 2);
    }

    public RoboShip()
    {
        myMaxHull = 300;
        myHull = myMaxHull;
        setImage(Astro.getImageFromFile("sprites/enemy_ship.png"));

        String[] shipIdle = {"sprites/enemy_ship.png"};
        myIdleAnimation = new Animation("enemy_ship_idle", true, shipIdle);
        setAnimation(myIdleAnimation);

        String[] shipThrusting = {"sprites/enemy_ship_1.png", "sprites/enemy_ship_2.png"};
        myThrustAnimation = new Animation("enemy_ship_thrusting", true, shipThrusting);

        setWeapon(new LaserCannon(Laser.COLORS.RED, Laser.SIZES.SM));
        myLastUpdate = System.nanoTime();
    }

    @Override
    public void update()
    {
        if (myTurnsSinceFire < ourFireInterval)
            myTurnsSinceFire++;

        automate();
        super.update();

        
        
    }

    @Override
    public void fire() {
        if (myTurnsSinceFire >= ourFireInterval)
        {
            super.fire();
            myTurnsSinceFire = 0;
        }
    }




    public void automate()
    {
        Point subjectCtr = Astro.getSubject().getCenterPoint();
        double distToSubject = getDistanceFromPoint(subjectCtr);
        boolean facingSubject = isFacing(subjectCtr);
        double targetSpeed = 0;
        if (myAutoMode != AUTO_MODES.HUNT && distToSubject < 1000)
        {
            myAutoMode = AUTO_MODES.HUNT;
            myGoalPoint = null;
        }
        if (myAutoMode != AUTO_MODES.WANDER && distToSubject > 1000)
        {
            myAutoMode = AUTO_MODES.WANDER;
            myGoalPoint = null;
            shieldsDown();
        }

        switch (myAutoMode)
        {
            case WANDER:
                if (myGoalPoint == null)
                    setNewRandomGoal();
                else if (getDistanceFromPoint(myGoalPoint)<100)
                    setNewRandomGoal();
                setCourseFor(myGoalPoint);
                targetSpeed = getDistanceFromPoint(myGoalPoint);
                if (targetSpeed < mySpeedFactor)
                    speedDown();
                else if (targetSpeed > mySpeedFactor)
                    speedUp();
                break;
            case HUNT:
                if (myShieldPct == 0 && getHullPct() < .5)
                    shieldsUp();
                Random rand = new Random();
                if (facingSubject && getDistanceFromPoint(subjectCtr) < 1000)
                {
                    fire();
                    myGoalPoint = Astro.getSubject().getRandomPointWithinRange(500);
                    myChasing = false;
                }
                if (myGoalPoint == null)
                {
                    myGoalPoint = Astro.getSubject().getRandomPointWithinRange(50);
                    myChasing = true;
                }
                else if (getDistanceFromPoint(myGoalPoint)<100)
                {
                    myChasing = !myChasing;
                    if (myChasing)
                        myGoalPoint = Astro.getSubject().getRandomPointWithinRange(50);
                    else
                        myGoalPoint = getRandomPointWithinRange(500);
                }
                    
                
                setCourseFor(myGoalPoint);
                targetSpeed = getDistanceFromPoint(myGoalPoint);
                if (targetSpeed < mySpeedFactor)
                    speedDown();
                else
                if (targetSpeed > mySpeedFactor)
                    speedUp();
                break;
        }

    }

    public void setCourseFor(Point p)
    {
        Point current = getCenterPoint();
        double xDiff = p.x - current.x;
        double yDiff = p.y - current.y;
        double total = Math.abs(xDiff)+Math.abs(yDiff);
        double xSign = Math.signum(xDiff);
        double ySign = Math.signum(yDiff);
        double xDir = (Math.abs(xDiff)/total);
        double yDir = 1-xDir;
        xDir*=xSign;
        yDir*=ySign;

        double acc = 0;
        if (xDir>=0 && yDir<0)
            acc+=xDir;
        else if (xDir>=0 && yDir>=0)
            acc+=1+yDir;
        else if (xDir<0 && yDir>=0)
            acc+=2+(-xDir);
        else if (xDir<0 && yDir<0)
            acc+=3+(-yDir);

        double pct = acc/4.0;
        double targetTheta = pct*RADIANS;
        double currentTheta = getAngle();

        double dir = targetTheta-currentTheta;
        if (Math.abs(dir)>Math.PI)
            dir = -dir;

        if (Math.abs(dir) > .05)
        {
            if (dir>=0)
               this.turnRight();
            else
                this.turnLeft();
        }
    }

    public boolean isFacing(Point p)
    {
        Point current = getCenterPoint();
        double xDiff = p.x - current.x;
        double yDiff = p.y - current.y;
        double total = Math.abs(xDiff)+Math.abs(yDiff);
        double xSign = Math.signum(xDiff);
        double ySign = Math.signum(yDiff);
        double xDir = (Math.abs(xDiff)/total);
        double yDir = 1-xDir;
        xDir*=xSign;
        yDir*=ySign;

        double acc = 0;
        if (xDir>=0 && yDir<0)
            acc+=xDir;
        else if (xDir>=0 && yDir>=0)
            acc+=1+yDir;
        else if (xDir<0 && yDir>=0)
            acc+=2+(-xDir);
        else if (xDir<0 && yDir<0)
            acc+=3+(-yDir);

        double pct = acc/4.0;
        double targetTheta = pct*RADIANS;
        double currentTheta = getAngle();

        double dir = targetTheta-currentTheta;
        if (Math.abs(dir)>Math.PI)
            dir = -dir;

        return (Math.abs(dir) < .05);
    }

    public boolean isFacing(Point p, double maxRadianVar)
    {
        Point current = getCenterPoint();
        double xDiff = p.x - current.x;
        double yDiff = p.y - current.y;
        double total = Math.abs(xDiff)+Math.abs(yDiff);
        double xSign = Math.signum(xDiff);
        double ySign = Math.signum(yDiff);
        double xDir = (Math.abs(xDiff)/total);
        double yDir = 1-xDir;
        xDir*=xSign;
        yDir*=ySign;

        double acc = 0;
        if (xDir>=0 && yDir<0)
            acc+=xDir;
        else if (xDir>=0 && yDir>=0)
            acc+=1+yDir;
        else if (xDir<0 && yDir>=0)
            acc+=2+(-xDir);
        else if (xDir<0 && yDir<0)
            acc+=3+(-yDir);

        double pct = acc/4.0;
        double targetTheta = pct*RADIANS;
        double currentTheta = getAngle();

        double dir = targetTheta-currentTheta;
        if (Math.abs(dir)>Math.PI)
            dir = -dir;

        return (Math.abs(dir) < maxRadianVar);
    }


    public void setNewRandomGoal()
    {
        myGoalPoint = Astro.getCurrentSector().getRandomPoint();
    }

    @Override
    public Color getRadarColor()
    {
        return Color.MAGENTA;
    }


}
