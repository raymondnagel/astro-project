/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package astro;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.awt.image.VolatileImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author Raymond
 */
public class PlanetMaker
{
    public static class RGB
    {
        double r, g, b;

        public RGB(double r, double g, double b)
        {
            this.r = r;
            this.g = g;
            this.b = b;
        }

        public RGB(int rgb)
        {
            r = (double) (rgb >> 16 & 0xff) / 255;
            g = (double) (rgb >> 8 & 0xff) / 255;
            b = (double) (rgb >> 0 & 0xff) / 255;
        }

        public void scale(double scale)
        {
            r *= scale;
            g *= scale;
            b *= scale;
        }

        public void add(RGB texel)
        {
            r += texel.r;
            g += texel.g;
            b += texel.b;
        }

        public int toRGB()
        {
            return 0xff000000 | (int) (r * 255.99) << 16 |
                    (int) (g * 255.99) << 8 | (int) (b * 255.99) << 0;
        }
    }

    public static interface Texture
    {
        public RGB getTexel(double i, double j);
    }

    public static class ImageTexture implements Texture
    {
        int[] imagePixels;
        int imageWidth, imageHeight;

        public ImageTexture(Image image, int width, int height) throws InterruptedException
        {
            PixelGrabber grabber = new PixelGrabber(image, 0, 0, width, height, true);
            if (!grabber.grabPixels())
            {
                throw new IllegalArgumentException("Invalid image; pixel grab failed.");
            }
            imagePixels = (int[]) grabber.getPixels();
            imageWidth = grabber.getWidth();
            imageHeight = grabber.getHeight();
        }

        public RGB getTexel(double i, double j)
        {
            return new RGB(imagePixels[(int) (i * imageWidth % imageWidth) +
                    imageWidth * (int) (j * imageHeight % imageHeight)]);
        }
    }

    public static interface Obj
    {
        public RGB getIntersection(Vec ray);
    }

    public static class Vec
    {
        double x, y, z;
        public Vec(double x, double y, double z)
        {
            this.x = x;
            this.y = y;
            this.z = z;
        }
        double getLength()
        {
            return Math.sqrt(x * x + y * y + z * z);
        }
        void scale(double scale)
        {
            x *= scale;
            y *= scale;
            z *= scale;
        }
        double dot (Vec v)
        {
            return x * v.x + y * v.y + z * v.z;
        }
    }

    public static class Sphere implements Obj
    {
        Texture texture;
        Light light;
        double sphereZ, sphereR;
        double c;
        public Sphere(double sphereZ, double sphereR, Texture texture, Light light)
        {
            this.sphereZ = sphereZ;
            this.sphereR = sphereR;
            this.texture = texture;
            this.light = light;
            c = sphereZ * sphereZ - sphereR * sphereR;
        }
        public RGB getIntersection(Vec ray)
        {
            double a = ray.x * ray.x + ray.y * ray.y + ray.z * ray.z;
            double b = -2.0 * sphereZ * ray.z;
            double tmp = b * b - 4.0 * a * c;
            if (tmp < 0)
            {
                return new RGB(1.0, 1.0, 1.0);
            } else
            {
                double t = (-b - Math.sqrt(tmp)) / 2.0 / a;
                Loc intersect = new Loc(ray.x * t, ray.y * t, ray.z * t);
                double j = Math.asin(intersect.y / sphereR);
                double i = Math.acos(intersect.x / sphereR / Math.cos(j));
                RGB rgb = texture.getTexel(i / Math.PI, j / Math.PI + 0.5);

                Vec norm = new Vec(intersect.x, intersect.y, intersect.z - sphereZ);
                double length = norm.getLength();
                norm.scale(1.0 / length);
                Vec dir = light.getDirection(intersect);
                double coincidence = -norm.dot(dir);
                double lighting = 0.25 + 0.75 * ((coincidence >= 0) ? coincidence : 0.0);
                rgb.scale(lighting);
                return rgb;
            }
        }
    }

    public static class Loc
    {
        double x, y, z;

        public Loc(double x, double y, double z)
        {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    public static interface Light
    {
        public Vec getDirection(Loc position);
    }

    public static class DirectionalLight implements Light
    {
        Vec dir;
        public DirectionalLight(double x, double y, double z)
        {
            dir = new Vec(x, y, z);
            double length = dir.getLength();
            dir.scale(1.0 / length);
        }
        public Vec getDirection(Loc loc)
        {
            return dir;
        }
    }

    public static BufferedImage getPlanetGraphic(int width, BufferedImage txImg) throws InterruptedException
    {
        Texture texture = new ImageTexture(txImg, txImg.getWidth(), txImg.getHeight());
        Light light = new DirectionalLight (.6, .4, .6);
        Obj obj = new Sphere (width, width, texture, light);
        int[] imageData = new int[width * width];

        int sup = 2;
        double supInv = 1.0 / sup;
        for (int j = 0; j < width; ++ j) {
          for (int i = 0; i < width; ++ i) {
            RGB pixel = new RGB (0.0, 0.0, 0.0);
            for (int k = 0; k < sup; ++ k) {
              for (int l = 0; l < sup; ++ l) {
                Vec ray = new Vec ((i * 2. - width) / 2. + k * supInv,
                                   (j * 2. - width) / 2. + l * supInv, 150.0);
                RGB rgb = obj.getIntersection (ray);
                pixel.add (rgb);
              }
            }
            pixel.scale (supInv * supInv);
            imageData[i + width * j] = pixel.toRGB ();
          }
        }

        MemoryImageSource source = new MemoryImageSource (width, width, imageData, 0, width);
        source.setAnimated (true);
        source.setFullBufferUpdates(true);
        source.newPixels();
        Image planetImage = Toolkit.getDefaultToolkit().createImage(source);

        BufferedImage pbi = toBufferedImage(planetImage, BufferedImage.TYPE_4BYTE_ABGR);

        return pbi;

    }





    public static BufferedImage toBufferedImage(final Image image, final int type)
    {
        if (image instanceof BufferedImage)
        {
            return (BufferedImage) image;
        }
        if (image instanceof VolatileImage)
        {
            return ((VolatileImage) image).getSnapshot();
        }
        loadImage(image);
        final BufferedImage buffImg = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
        final Graphics2D g2 = buffImg.createGraphics();
        g2.drawImage(image, null, null);
        g2.dispose();
        return buffImg;
    }

    private static void loadImage(final Image image)
    {
        class StatusObserver implements ImageObserver
        {
            boolean imageLoaded = false;

            public boolean imageUpdate(final Image img, final int infoflags,
                    final int x, final int y, final int width, final int height)
            {
                if (infoflags == ALLBITS)
                {
                    synchronized (this)
                    {
                        imageLoaded = true;
                        notify();
                    }
                    return true;
                }
                return false;
            }
        }
        final StatusObserver imageStatus = new StatusObserver();
        synchronized (imageStatus)
        {
            if (image.getWidth(imageStatus) == -1 || image.getHeight(imageStatus) == -1)
            {
                while (!imageStatus.imageLoaded)
                {
                    try
                    {
                        imageStatus.wait();
                    } catch (InterruptedException ex)
                    {
                    }
                }
            }
        }
    }
}
