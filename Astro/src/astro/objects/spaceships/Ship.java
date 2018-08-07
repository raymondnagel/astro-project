/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package astro.objects.spaceships;

import astro.objects.*;
import astro.graphics.Explosion;
import astro.Astro;
import astro.Controllable;
import astro.graphics.AnimFrame;
import astro.graphics.Animation;
import astro.objects.projectiles.Laser.COLORS;
import astro.objects.projectiles.Laser.SIZES;
import astro.shipweapons.BeamLaser;
import astro.shipweapons.IonCannon;
import astro.shipweapons.LaserCannon;
import astro.shipweapons.PhotonTorpedoLauncher;
import astro.shipweapons.RocketLauncher;
import astro.shipweapons.ShipWeapon;
import astro.sound.Sound;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.util.Vector;
import javax.sound.sampled.Clip;

/**
 *
 * @author Raymond Nagel
 */
public class Ship extends Sprite implements Controllable
{
    protected static int SPEED_UP_POWER = 1;
    protected static int FIRE_POWER = 1;
    protected static int HYPERDRIVE_POWER = 5;
    protected static double HYPERDRIVE_SPEED = 5000;
    protected static int HYPERDRIVE_WAIT = 80;
    protected static int WARP_POINT_FRAMES = 5;
    protected static double TURN_AMOUNT = .07;


    protected boolean myLaserSightOn = false;
    protected boolean myHyperdriveOn = false;
    protected boolean myDescending = false;
    protected boolean myControlled = false;
    protected double myRemainingHorz = 0;
    protected double myRemainingVert = 0;
    protected double myMaxSpeed = 500;
    protected double mySpeedFactor = 0;
    protected long myLastUpdate = 0;
    private double myZ = 1;

    protected Point myLastWarpPoint = null;
    protected int myHyperdriveRevs = 0;
    protected int myMaxPower = 5000;
    protected int myPower = 5000;
    protected int myMaxHull = 1000;
    protected int myHull = 1000;
    protected double myShieldProtection = .8;

    protected static Sound ourHyperdriveRunning;
    protected static Sound ourEngineRunning;
    protected static Sound ourTurnSound;
    protected static Sound ourShieldUpSound;
    protected static Sound ourShieldDownSound;
    protected static BufferedImage ourShieldImage;

    protected Animation myIdleAnimation = null;
    protected Animation myThrustAnimation = null;
    protected double myShieldPct = 0;
    protected double myShieldInc = 0;

    protected Vector<ShipWeapon> myWeapons = new Vector<ShipWeapon>();
    protected int myCurrentWeaponIndex = 0;

    static
    {
        AnimFrame.loadFrame("sprites/ship.png", 1);
        AnimFrame.loadFrame("sprites/ship_1.png", 2);
        AnimFrame.loadFrame("sprites/ship_2.png", 2);
        ourShieldImage = Astro.getImageFromFile("sprites/energy_shield.png");
        ourEngineRunning = new Sound(Astro.getFile("sound_fx/engine_cycle_med.wav"));
        ourHyperdriveRunning = new Sound(Astro.getFile("sound_fx/hyperdrive_cycle.wav"));
        //ourTurnSound = new Sound(Astro.getFile("sound_fx/drive.wav"));
    }

    public Ship()
    {
        super();
        setImage(Astro.getImageFromFile("sprites/ship.png"));

        String[] shipIdle = {"sprites/ship.png"};
        myIdleAnimation = new Animation("ship_idle", true, shipIdle);
        setAnimation(myIdleAnimation);

        String[] shipThrust = {"sprites/ship_1.png", "sprites/ship_2.png"};
        myThrustAnimation = new Animation("ship_thrust", true, shipThrust);

        setWeapon(new LaserCannon(COLORS.GREEN, SIZES.SM));
        installWeapon(new BeamLaser(Color.RED, SIZES.SM));
        installWeapon(new PhotonTorpedoLauncher());
        installWeapon(new RocketLauncher(6));
        installWeapon(new IonCannon());

        myLastUpdate = System.nanoTime();
    }

    public int getPower()
    {
        return myPower;
    }
    public int getMaxPower()
    {
        return myMaxPower;
    }
    public int getHull()
    {
        return myHull;
    }
    public int getMaxHull()
    {
        return myMaxHull;
    }

    public double getPowerPct()
    {
        return (double)myPower/(double)myMaxPower;
    }
    public double getHullPct()
    {
        return (double)myHull/(double)myMaxHull;
    }
    public double getWeaponReadyPct()
    {
        return getCurrentWeapon().getReadyPct();
    }

    public ShipWeapon getCurrentWeapon()
    {
        return myWeapons.get(myCurrentWeaponIndex);
    }
    public Vector<ShipWeapon> getInstalledWeapons()
    {
        return myWeapons;
    }
    public boolean isWeaponInstalled(ShipWeapon weapon)
    {
        return myWeapons.contains(weapon);
    }
    public boolean installWeapon(ShipWeapon weapon)
    {
        if (!myWeapons.contains(weapon))
        {
            myWeapons.add(weapon);
            weapon.setHost(this);
            return true;
        }
        else return false;
    }
    public int setWeapon(ShipWeapon weapon)
    {
        if (!isWeaponInstalled(weapon))
            installWeapon(weapon);
        myCurrentWeaponIndex = myWeapons.indexOf(weapon);
        return myCurrentWeaponIndex;
    }
    public void setWeaponIndex(int index)
    {
        if (index >= 0 && index < myWeapons.size())
        {
            myCurrentWeaponIndex = index;
        }
    }
    public int getWeaponIndex()
    {
        return myCurrentWeaponIndex;
    }
    public void cycleCurrentWeapon()
    {
        myCurrentWeaponIndex++;
        if (myCurrentWeaponIndex >= myWeapons.size())
            myCurrentWeaponIndex = 0;
    }


    public void decreasePower(int decrement)
    {
        myPower -= decrement;
        if (myPower <= 0)
        {
            myPower = 0;
            if (myShieldPct > 0)
                shieldsDown();
            if (myHyperdriveOn)
                hyperdriveOff();
        }
    }
    public void hurt(int damage)
    {
        int netDamage = damage;
        if (myShieldPct == 1)
        {
            netDamage -= (int)(((double)damage)*myShieldProtection);
            decreasePower(damage-netDamage);
        }
        myHull -= netDamage;
        if (myHull <= 0)
        {
            myHull = 0;
            this.explode();
        }
    }
    public void explode()
    {
        this.destroy();
        Astro.addSprite(new Explosion(getCenterPoint().x, getCenterPoint().y));
    }


    public double getZ()
    {
        return myZ;
    }

    public void speedUp()
    {
        if (mySpeedFactor < myMaxSpeed && myPower >= SPEED_UP_POWER)
        {
            if (mySpeedFactor==0)
            {
                Astro.getSoundManager().makeNoise(this, new Sound("extern/sound_fx/lift.wav"));
                if (Astro.getSubject() == this) Astro.getSoundManager().loopSound(ourEngineRunning, Clip.LOOP_CONTINUOUSLY);
                setAnimation(myThrustAnimation);
            }
            mySpeedFactor+=5;
            decreasePower(SPEED_UP_POWER);
        }
        if (mySpeedFactor > myMaxSpeed)
            mySpeedFactor = myMaxSpeed;
    }

    public void speedDown()
    {
        if (mySpeedFactor > 0)
        {
            mySpeedFactor-=5;
            if (mySpeedFactor <= 0)
            {
                if (Astro.getSubject() == this) Astro.getSoundManager().makeNoise(this, new Sound("extern/sound_fx/engine_off.wav"));
                ourEngineRunning.stop();
                setAnimation(myIdleAnimation);
                mySpeedFactor = 0;
            }
        }
    }

    public void descend()
    {
        myDescending = true;
        mySpeedFactor = 0;
    }

    public void revHyperdrive()
    {
        if (mySpeedFactor == 0)
        {
            Astro.getSoundManager().loopSound(ourEngineRunning, Clip.LOOP_CONTINUOUSLY);
            setAnimation(myThrustAnimation);
            mySpeedFactor = .1;
        }
        decreasePower(HYPERDRIVE_POWER);
        if (!myHyperdriveOn)
            hyperdriveOff();
        myHyperdriveRevs++;                
        speedUp();
        if (myHyperdriveRevs >= HYPERDRIVE_WAIT)
            hyperdriveOn();
    }
    public void startHyperdrive()
    {
        shieldsDown();
        myHyperdriveRevs = 0;
        myHyperdriveOn = true;
        Astro.getSoundManager().makeNoise(this, new Sound("extern/sound_fx/hyperdrive_start.wav"));
    }
    private void hyperdriveOn()
    {
        if (ourEngineRunning.isPlaying())
            ourEngineRunning.stop();
        myLastWarpPoint = getCenterPoint();
        Astro.getSoundManager().makeNoise(this, new Sound("extern/sound_fx/hyperdrive_takeoff.wav"));
        Astro.getSoundManager().loopSound(ourHyperdriveRunning, Clip.LOOP_CONTINUOUSLY);
    }
    public void hyperdriveOff()
    {
        myHyperdriveOn = false;
        myHyperdriveRevs = 0;
        setAnimation(myIdleAnimation);
        mySpeedFactor = 0;
        if (ourHyperdriveRunning.isPlaying())
            ourHyperdriveRunning.stop();
        if (ourEngineRunning.isPlaying())
            ourEngineRunning.stop();
        myLastWarpPoint = null;
        Astro.getSoundManager().makeNoise(this, new Sound("extern/sound_fx/engine_off.wav"));
    }
    public boolean isHyperdriveOn()
    {
        return myHyperdriveOn && myHyperdriveRevs >= HYPERDRIVE_WAIT;
    }

    public void turnLeft()
    {
        this.rotate(-TURN_AMOUNT);
    }
    public void turnRight()
    {
        this.rotate(TURN_AMOUNT);
    }

    public void fire()
    {
        if (getCurrentWeapon()!=null)
            getCurrentWeapon().tryFire();
    }

    public void shieldsUp()
    {
        if (myShieldPct == 0 && !isHyperdriveOn() && myPower > 0)
        {
            myShieldInc = .05;
            Astro.getSoundManager().makeNoise(this, new Sound(Astro.getFile("sound_fx/energize_shields.wav")));
        }
    }
    public void shieldsDown()
    {
        if (myShieldPct == 1)
        {
            myShieldInc = -.05;
            Astro.getSoundManager().makeNoise(this, new Sound(Astro.getFile("sound_fx/energize_shields.wav")));
        }
    }
    public boolean isShieldsUp()
    {
        return myShieldPct == 1;
    }

    @Override
    public void update()
    {
        // Get time elapsed since my last update via this method.
        double elapsedTime = (double)(System.nanoTime()-myLastUpdate)/1000000000;

        // Update location.
        Point2D dirPoint = getAdjustedDir();
        double xT = (mySpeedFactor*dirPoint.getX() * elapsedTime) + myRemainingHorz;
        double yT = (mySpeedFactor*dirPoint.getY() * elapsedTime) + myRemainingVert;
        if (myHyperdriveOn)
        {
            if (myHyperdriveRevs < HYPERDRIVE_WAIT)
                revHyperdrive();
            else
            {
                xT = (HYPERDRIVE_SPEED*getXDir() * elapsedTime) + myRemainingHorz;
                yT = (HYPERDRIVE_SPEED*getYDir() * elapsedTime) + myRemainingVert;
            }
        }
        int xMov = (int)xT;
        int yMov = (int)yT;
        myRemainingHorz = xT-xMov;
        myRemainingVert = yT-yMov;
        setLocation(getX()+xMov, getY()+yMov);
        double traveledDistance = Math.sqrt((xT*xT)+(yT*yT));

        // Update animation.
        getCurrentAnimation().advance();

        // Update height if ascending or descending.
        if (myDescending && myZ >= .02)
        {
            myZ -= .02;
        }

        myShieldPct+=myShieldInc;
        if (myShieldPct <= 0)
        {
            myShieldPct = 0;
            myShieldInc = 0;
        }
        else if (myShieldPct > 1)
        {
            myShieldPct = 1;
            myShieldInc = 0;
        }


        // Record the time that this update finished.
        myLastUpdate = System.nanoTime();
    }

    @Override
    protected void paint(Graphics2D g)
    {
        if (myZ != 1)
        {
            int sx = Astro.getScrollX();
            int sy = Astro.getScrollY();
            int w = (int)(getWidth()*getZ());
            int h = (int)(getHeight()*getZ());
            int x = getCenterPoint().x-sx-(w/2);
            int y = getCenterPoint().y-sy-(h/2);
            g.drawImage(getImage(), x, y, x+w, y+h, 0, 0, getWidth(), getHeight(), null);
        }
        else
        {
            super.paint(g);
        }
    }
    protected void paintShield(Graphics2D g)
    {
        if (myShieldPct > 0)
        {
            int sW = (int)(ourShieldImage.getWidth()*myShieldPct);
            int sH = (int)(ourShieldImage.getHeight()*myShieldPct);
            int x = (getCenterPoint().x-Astro.getScrollX()) - (sW/2);
            int y = (getCenterPoint().y-Astro.getScrollY()) - (sH/2);
            g.drawImage(ourShieldImage, x, y, sW, sH, null);
        }
    }
    protected void paintHyperdriving(Graphics2D g)
    {
        if (isHyperdriveOn() && myLastWarpPoint != null)
        {
            Point cp = getCenterPoint();
            int sx = Astro.getScrollX();
            int sy = Astro.getScrollY();
            int x = myLastWarpPoint.x;
            int y = myLastWarpPoint.y;
            int xDiff = cp.x - x;
            int yDiff = cp.y - y;
            int xInc = xDiff/WARP_POINT_FRAMES;
            int yInc = yDiff/WARP_POINT_FRAMES;
            BufferedImage img = getImage();
            float[] offsets = new float[4];
            this.setCenterPoint(myLastWarpPoint);
            for (int wp = 0; wp < WARP_POINT_FRAMES; wp++)
            {
                float scale = .05f + (wp*.05f);
                float[] scales = {scale, scale, scale, scale};
                RescaleOp rop = new RescaleOp(scales, offsets, null);
                g.rotate(getAngle(), myLastWarpPoint.x-sx, myLastWarpPoint.y-sy);
                g.drawImage(img, rop, getX()-sx, getY()-sy);
                g.rotate(-getAngle(), myLastWarpPoint.x-sx, myLastWarpPoint.y-sy);
                myLastWarpPoint.x += xInc;
                myLastWarpPoint.y += yInc;
                this.setCenterPoint(myLastWarpPoint);
            }
            this.setCenterPoint(cp);
            myLastWarpPoint = cp;
        }
    }
    protected void paintStats(Graphics2D g)
    {
        int w = 50;
        int x = getCenterPoint().x - (w/2);
        int y = getTop() - 10;
        x-=Astro.getScrollX();
        y-=Astro.getScrollY();
        g.setColor(Color.DARK_GRAY);
        g.drawLine(x, y, x+w, y);
        g.setColor(Color.RED);
        g.drawLine(x, y, x+(int)(getHullPct()*w), y);
        y -=2;
        g.setColor(Color.DARK_GRAY);
        g.drawLine(x, y, x+w, y);
        g.setColor(Color.CYAN);
        g.drawLine(x, y, x+(int)(getPowerPct()*w), y);
    }
    protected void paintLaserSight(Graphics2D g)
    {
        Point2D dirPoint = getAdjustedDir();
        int x = getCenterPoint().x-Astro.getScrollX(), y = getCenterPoint().y-Astro.getScrollY();
        for (int dist = 0; dist < 10; dist++)
        {
            x = (int)(x+(dirPoint.getX()*100));
            y = (int)(y+(dirPoint.getY()*100));
            g.setColor(new Color(255, 0, 0, 200));
            g.fillOval(x-1, y-1, 3, 3);
        }
    }

    @Override
    public void render(Graphics2D g)
    {
        super.render(g);
        paintShield(g);
        paintHyperdriving(g);
        if (Astro.isStatBarsShown())
            paintStats(g);
        if (myLaserSightOn)
            paintLaserSight(g);
    }


    public void keysAreDown(boolean[] keyMap)
    {
        if (keyMap[KeyEvent.VK_LEFT])
        {
            this.turnLeft();
        }
        if (keyMap[KeyEvent.VK_RIGHT])
        {
            this.turnRight();
        }
        if (keyMap[KeyEvent.VK_UP])
        {
            this.speedUp();
        }
        if (keyMap[KeyEvent.VK_DOWN])
        {
            this.speedDown();
        }

    }

    public void keyPressed(int keyCode)
    {
        switch (keyCode)
        {
            case KeyEvent.VK_ENTER:
                this.descend();
                break;
            case KeyEvent.VK_END:
                startHyperdrive();
                break;
//            case KeyEvent.VK_LEFT:
//            case KeyEvent.VK_RIGHT:
//                if (mySpeedFactor > myMaxSpeed/2)
//                {
//                    if (!ourTurnSound.isPlaying())
//                    {
//                        ourTurnSound = new Sound(Astro.getFile("sound_fx/drive.wav"));
//                        Astro.getSoundManager().playSound(ourTurnSound);
//                    }
//                }
//                break;
        }
    }

    public void keyReleased(int keyCode)
    {
        switch (keyCode)
        {
            case KeyEvent.VK_SPACE:
                this.fire();
                break;
            case KeyEvent.VK_SHIFT:
                if (myShieldPct==0)
                    this.shieldsUp();
                else if (myShieldPct == 1)
                    this.shieldsDown();
                break;
            case KeyEvent.VK_END:
                hyperdriveOff();
                break;
            case KeyEvent.VK_CONTROL:
                cycleCurrentWeapon();
                break;
            case KeyEvent.VK_HOME:
                myLaserSightOn = !myLaserSightOn;
        }
    }

    public boolean isControlled()
    {
        return myControlled;
    }

    public void setControlled(boolean controlled)
    {
        myControlled = controlled;
    }

    @Override
    public Color getRadarColor()
    {
        return Color.GRAY;
    }


    
    public void unpause()
    {
        myLastUpdate = System.nanoTime();
    }
}
