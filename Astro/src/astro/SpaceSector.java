/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package astro;

import astro.graphics.ImageLayer;
import astro.graphics.ImageRepeatLayer;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;

/**
 *
 * @author Raymond
 */
public class SpaceSector extends Sector
{
    public static SpaceSector random(int width, int height, int layers)
    {
        SpaceSector ss = new SpaceSector();
        ss.setWidth(width);
        ss.setHeight(height);

        double closeness = 1.0;
        double proxDec = closeness/(double)layers;
        int density = 50;

//        layers--;
//        ImageRepeatLayer bg = new ImageRepeatLayer(BufferedImage.TYPE_4BYTE_ABGR, .05);
//        //File bgFile = Astro.randomFileFromFolder("space_bg");
//        File bgFile = Astro.getFile("space_bg/test.png");
//        BufferedImage tile = Astro.getImageFromFile(bgFile);
//        TexturePaint tp = new TexturePaint(tile, new Rectangle2D.Float(0,0,tile.getWidth(), tile.getHeight()));
//        Rectangle2D fillRect = new Rectangle2D.Float(0,0,bg.getWidth(),bg.getHeight());
//        Graphics2D g = bg.createGraphics();
//        g.setPaint(tp);
//        g.fill(fillRect);
//        ss.addLayer(bg);

        for (int sl = 0; sl < layers; sl++)
        {
            ImageRepeatLayer layer = new ImageRepeatLayer(BufferedImage.TYPE_4BYTE_ABGR, closeness);
            ss.addLayer(layer);
            ss.addRandomStarsToImageLayer(layer, density, closeness);
            closeness -= proxDec;
            density *= 2;
        }

        return ss;
    }

    private void addRandomStarsToImageLayer(ImageLayer layer, int perScreen, double closeness)
    {
        int w = layer.getWidth();
        int h = layer.getHeight();
        int screenPixels = Astro.SCREEN_W*Astro.SCREEN_H;
        int layerPixels = w*h;
        double numScreens = (double)layerPixels/(double)screenPixels;
        int total = (int)(numScreens*perScreen);
        int lum = (int)(closeness*245);
        Random rand = new Random();
        int x, y, r, g, b, all;
        Color color;
        for (int num = 0; num < total; num++)
        {
            x = rand.nextInt(w);
            y = rand.nextInt(h);
            if (rand.nextInt(5) == 3)
            {// Colored star
                r = lum + (rand.nextInt(20) - 10);
                g = lum + (rand.nextInt(20) - 10);
                b = lum + (rand.nextInt(20) - 10);
                color = new Color(r, g, b);
                layer.setRGB(x, y, color.getRGB());
            }
            else
            {// White star
                all = lum + (rand.nextInt(20) - 10);
                color = new Color(all, all, all);
                layer.setRGB(x, y, color.getRGB());
            }
        }
    }


    public static File randomToFile()
    {
        return null;
    }
}
