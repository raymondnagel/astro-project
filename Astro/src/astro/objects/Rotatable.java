/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package astro.objects;

import astro.Astro;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 *
 * @author rnagel
 */
public class Rotatable
{
    private BufferedImage   myImage = null;
    private Point           myLocation = null;
    private Dimension       mySize = null;
    private double          myAngle = 0.0;
    protected final static double RADIANS = 2 * Math.PI;

    public void setImage(BufferedImage image)
    {
        myImage = image;
        if (myImage != null)
            mySize = new Dimension(myImage.getWidth(), myImage.getHeight());
    }
    public BufferedImage getImage()
    {
        return myImage;
    }

    public void setLocation(int x, int y)
    {
        myLocation.x = x;
        myLocation.y = y;
    }
    public void setLocation(Point location)
    {
        myLocation = location;
    }
    public Point getLocation()
    {
        return myLocation;
    }

    public void setSize(Dimension size)
    {
        mySize = size;
    }
    public Dimension getSize()
    {
        return mySize;
    }

    public int getX()
    {
        return myLocation.x;
    }
    public int getY()
    {
        return myLocation.y;
    }

    public int getWidth()
    {
        return mySize.width;
    }
    public int getHeight()
    {
        return mySize.height;
    }

    public int getLeft()
    {
        return getX();
    }
    public int getTop()
    {
        return getY();
    }
    public int getRight()
    {
        return getX()+getWidth();
    }
    public int getBottom()
    {
        return getY()+getHeight();
    }

    public boolean containsPoint(Point p)
    {
        return p.x >= myLocation.x && p.x <= myLocation.x + mySize.width && p.y >= myLocation.y && p.y <= myLocation.y + mySize.height;
    }

    public Point getCenterPoint()
    {
        return new Point(getX()+getWidth()/2, getY()+getHeight()/2);
    }
    public void setCenterPoint(Point ctr)
    {
        setLocation(ctr.x - (getWidth()/2), ctr.y - (getHeight()/2));
    }

    public void setAngle(double theta)
    {
        myAngle = theta;
        while (myAngle > RADIANS)
        {
            myAngle -= RADIANS;
        }
        while (myAngle < 0)
        {
            myAngle += RADIANS;
        }
    }
    public double getAngle()
    {
        return myAngle;
    }

    private Point2D.Double getDir()
    {
        double pct = myAngle / RADIANS;
        double horz = 0;
        double vert = 0;
        int xMod = pct >= .5 ? -1 : 1;
        int yMod = pct >= .25 && pct < .75 ? 1 : -1;
        double m = 0;
        if (xMod == 1)
            m = pct;
        else
            m = pct-.5;

        horz = .25 - (Math.abs(.25-m));
        vert = Math.abs(.25 - horz);
        horz *= xMod;
        vert *= yMod;
        return new Point2D.Double(horz, vert);
    }

    public double getXDir()
    {
        Point2D.Double dir = getDir();
        return ( Math.abs(dir.x) / (Math.abs(dir.x)+Math.abs(dir.y)) ) * Math.signum(dir.x);
    }
    public double getYDir()
    {
        Point2D.Double dir = getDir();
        return ( Math.abs(dir.y) / (Math.abs(dir.y)+Math.abs(dir.x)) ) * Math.signum(dir.y);
    }

    public Point2D.Double getAdjustedDir()
    {
        Point2D.Double dir = getDir();
        double xDir = Math.abs(dir.x) / (Math.abs(dir.x)+Math.abs(dir.y));
        double yDir = Math.abs(dir.y) / (Math.abs(dir.y)+Math.abs(dir.x));
        double dist = Math.sqrt((xDir*xDir)+(yDir*yDir));
        xDir = xDir/dist;
        yDir = yDir/dist;
        return new Point2D.Double(xDir*Math.signum(dir.x), yDir*Math.signum(dir.y));
    }

    public double getDistanceFromPoint(Point target)
    {
        Point ctrPoint = getCenterPoint();
        int xDiff = target.x-ctrPoint.x;
        int yDiff = target.y-ctrPoint.y;
        return Math.sqrt(Math.abs(xDiff*xDiff)+Math.abs(yDiff*yDiff));
    }

    public Point getRandomPointWithinRange(int range)
    {
        Random rand = new Random();
        Point p = new Point();
        do
        {
            p.x = rand.nextInt(range*2)+(getCenterPoint().x-range);
            p.y = rand.nextInt(range*2)+(getCenterPoint().y-range);
        } while (getDistanceFromPoint(p) > range);
        return p;
    }

    public void rotate(double rotation)
    {
        setAngle(myAngle+rotation);
    }
    


    public Rotatable(BufferedImage image, int x, int y)
    {
        setImage(image);
        myLocation = new Point(x, y);
    }

    public void render(Graphics2D g)
    {
        int sx = Astro.getScrollX();
        int sy = Astro.getScrollY();
        Point p = getCenterPoint();
        g.rotate(myAngle, p.x-sx, p.y-sy);
        paint(g);
        g.rotate(-myAngle, p.x-sx, p.y-sy);
    }

    protected void paint(Graphics2D g)
    {
        int sx = Astro.getScrollX();
        int sy = Astro.getScrollY();
        g.drawImage(getImage(), getX()-sx, getY()-sy, null);
    }


}
