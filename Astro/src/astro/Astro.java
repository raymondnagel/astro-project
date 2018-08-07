/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package astro;

import astro.graphics.NullRepaintManager;
import astro.graphics.ScreenManager;
import astro.graphics.SectorLayer;
import astro.objects.spaceships.Cobra;
import astro.objects.Drawing;
import astro.objects.spaceships.RoboShip;
import astro.objects.spaceships.Ship;
import astro.objects.Sprite;
import astro.objects.spaceships.FireHawk;
import astro.objects.spaceships.Stingray;
import astro.sound.MidiPlayer;
import astro.sound.Sound;
import astro.sound.SoundManager;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.DisplayMode;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.TexturePaint;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

/**
 *
 * @author Raymond Nagel
 */
public class Astro
{
    public static final int SCREEN_W = 1024;
    public static final int SCREEN_H = 768;

    public static Sound LASER_SOUND = null;

    public static Robot                 ourRobot = null;

    private static BufferedImage        ourExitWest = null;
    private static BufferedImage        ourExitEast = null;
    private static BufferedImage        ourExitNorth = null;
    private static BufferedImage        ourExitSouth = null;
    private static BufferedImage        ourPowerMeter = null;
    private static BufferedImage        ourHullMeter = null;
    private static BufferedImage        ourWeaponPrepMeter = null;
    private static BufferedImage        ourWeaponReadyMeter = null;

    private static JFrame               ourMainFrame = null;
    private static ScreenManager        ourScreenManager = null;
    private static SoundManager         ourSoundManager = null;
    private static MidiPlayer           ourMidiPlayer = null;
    private static boolean              ourStop = false;
    private static Sector               ourCurrentSector = null;
    
    private final static Vector<Sprite> ourSprites = new Vector<Sprite>();
    private final static Vector<Drawing> ourDrawings = new Vector<Drawing>();
    private final static boolean[]      ourKeyMap = new boolean[600];
    private static Font                 ourFont = null;
    private static Cursor               ourCursor = null;
    private static Cursor               ourBlankCursor = null;

    private static int                  ourScrollX = 0;
    private static int                  ourScrollY = 0;
    private static int                  ourFadeFactor = 0;
    private static int                  ourFadeIncrement = 0;

    private static Sprite               ourSubject = null;
    private static BufferedImage        ourSectorMap = null;
    private static boolean              ourInterfaceShown = false;
    private static boolean              ourStatBarsShown = false;
    private static boolean              ourPaused = false;

    public static JFrame getMainFrame()
    {
        return ourMainFrame;
    }

    public static ScreenManager getScreenManager()
    {
        return ourScreenManager;
    }

    public static SoundManager getSoundManager()
    {
        return ourSoundManager;
    }

    public static BufferedImage getImageFromFile(File file)
    {
        try {
            return ImageIO.read(file);
        } catch (IOException ex) {
            Logger.getLogger(Astro.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public static BufferedImage getImageFromFile(String filename)
    {
        try
        {
            return ImageIO.read(getFile(filename));
        } catch (IOException ex)
        {
            Logger.getLogger(Astro.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public static Font getFontFile(String filename)
    {
        try {
            return Font.createFont(Font.PLAIN, getFile("fonts/" + filename));
        } catch (FontFormatException ex) {
            Logger.getLogger(Astro.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (IOException ex) {
            Logger.getLogger(Astro.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public static File getFile(String filename)
    {
        try {
            return new File("extern/" + filename);
        } catch (Exception ex){
            Logger.getLogger(Astro.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public static FileInputStream getFileInputStream(String filename)
    {
        try {
            return new FileInputStream(getFile(filename));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Astro.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public static void setScrollX(int scrollX)
    {
        ourScrollX = scrollX;
    }
    public static void setScrollY(int scrollY)
    {
        ourScrollY = scrollY;
    }
    public static int getScrollX()
    {
        return ourScrollX;
    }
    public static int getScrollY()
    {
        return ourScrollY;
    }

    public static Sector getCurrentSector()
    {
        return ourCurrentSector;
    }

    public static void addDrawing(Drawing d)
    {
        ourDrawings.add(d);
    }
    public static void removeDrawing(Drawing d)
    {
        ourDrawings.remove(d);
    }
    public static Vector<Drawing> getDrawings()
    {
        return ourDrawings;
    }

    public static void addSprite(Sprite s)
    {
        ourSprites.add(s);
    }
    public static void removeSprite(Sprite s)
    {
        ourSprites.remove(s);
    }
    public static Vector<Sprite> getSprites()
    {
        return ourSprites;
    }
    public static Vector<Ship> getShips()
    {
        Vector ships = new Vector<Ship>();
        for (int s = 0; s < ourSprites.size(); s++)
        {
            if (ourSprites.get(s) instanceof Ship)
                ships.add((Ship)ourSprites.get(s));
        }
        return ships;
    }

    public static Sprite getSubject()
    {
        return ourSubject;
    }

    public static void followSprite(Sprite s)
    {
        setScrollX(s.getCenterPoint().x-(SCREEN_W/2));
        setScrollY(s.getCenterPoint().y-(SCREEN_H/2));
        if (ourScrollX < 0)
        {
            setScrollX(0);
        } else if(ourScrollX+SCREEN_W > ourCurrentSector.getWidth())
        {
            setScrollX(ourCurrentSector.getWidth()-SCREEN_W);
        }
        if (ourScrollY < 0)
        {
            setScrollY(0);
        } else if(ourScrollY+SCREEN_H > ourCurrentSector.getHeight())
        {
            setScrollY(ourCurrentSector.getHeight()-SCREEN_H);
        }
    }

    public static void setShowInterface(boolean show)
    {
        ourInterfaceShown = show;
        if (!ourInterfaceShown)
            getMainFrame().setCursor(ourBlankCursor);
        else
        {
            getMainFrame().setCursor(ourCursor);
            if (ourRobot != null)
                ourRobot.mouseMove(SCREEN_W, SCREEN_H);
        }
    }

    public static void setShowStatBars(boolean show)
    {
        ourStatBarsShown = show;
    }

    public static boolean isStatBarsShown()
    {
        return ourStatBarsShown;
    }

    public static void setPaused(boolean paused)
    {
        ourPaused = paused;
        if (ourPaused)
        {
            getSoundManager().playSound(new Sound("extern/sound_fx/pause.wav"));
        }
        else
        {
            for (int s = 0; s < ourSprites.size(); s++)
            {
                if (ourSprites.get(s) instanceof Ship)
                {
                    ((Ship)ourSprites.get(s)).unpause();
                }
            }
        }
    }
    public static boolean isPaused()
    {
        return ourPaused;
    }

    public static void fadeOut()
    {
        ourFadeFactor = 0;
        ourFadeIncrement = 5;
    }
    public static void fadeIn()
    {
        ourFadeFactor = 255;
        ourFadeIncrement = -2;
    }

    public static void main(String[] args)
    {
        ourCurrentSector = SpaceSector.random(20000, 20000, 5);
        Random rand = new Random();
        for (int pls = 0; pls < 10; pls++)
        {
//            try
//            {
                int x = rand.nextInt(20000);
                int y = rand.nextInt(20000);
                BufferedImage planet = newRandomPlanetGraphic();
                //BufferedImage planet = PlanetMaker.getPlanetGraphic(200, getImageFromFile("planet_textures/craters_big.png"));
                //PlanetCreator.generate(100, getImageFromFile("planet_textures/craters_big.png"));
                Sprite planetSprite = new Sprite()
                {
                    @Override
                    public void update()
                    {
                    }

                    @Override
                    public Color getRadarColor()
                    {
                        return Color.YELLOW;
                    }
                };
                planetSprite.setImage(planet);
                planetSprite.setCenterPoint(new Point(x, y));
                addSprite(planetSprite);
//            } catch (InterruptedException ex)
//            {
//                Logger.getLogger(Astro.class.getName()).log(Level.SEVERE, null, ex);
//            }
        }

        ourSubject = new Ship(){
            @Override
            public Color getRadarColor()
            {
                return Color.BLUE;
            }
        };
        ourSubject.setCenterPoint(new Point(SCREEN_W/2, SCREEN_H/2));
        ((Ship)ourSubject).setControlled(true);
        ourSprites.add(ourSubject);

        Stingray sr = new Stingray();
        sr.setCenterPoint(getCurrentSector().getRandomPoint());
        sr.setControlled(false);
        ourSprites.add(sr);

//        FireHawk fh = new FireHawk();
//        fh.setCenterPoint(getCurrentSector().getRandomPoint());
//        fh.setControlled(false);
//        ourSprites.add(fh);

        for (int e = 0; e < 3; e++)
        {
            Cobra cobra = new Cobra();
            cobra.setCenterPoint(getCurrentSector().getRandomPoint());
            cobra.setControlled(false);
            ourSprites.add(cobra);
        }

        for (int e = 0; e < 6; e++)
        {
            RoboShip rb = new RoboShip();
            rb.setCenterPoint(getCurrentSector().getRandomPoint());
            rb.setControlled(false);
            ourSprites.add(rb);
        }


        //ourSubject = rb;

        try
        {
            init();
            mainLoop();
            ourScreenManager.restoreScreen();
            System.exit(0);
        } catch (Exception ex)
        {
            Logger.getLogger(Astro.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    private static void mainLoop() throws Exception
    {
        long startTime = 0;
        long maxTime = 10;
        long elpTime = 0;
        fadeIn();
        do
        {
            startTime = System.currentTimeMillis();
            mainCycle();
            elpTime = System.currentTimeMillis()-startTime;
            if (elpTime < maxTime)
                Thread.sleep(maxTime-elpTime);
        }while (!ourStop);
    }

    private static void mainCycle()
    {
        if (!ourPaused)
        {
            // Process keys currently pressed for continuous action:
            for (int r = 0; r < ourSprites.size(); r++)
            {
                if (ourSprites.get(r) instanceof Controllable)
                {
                    Controllable c = (Controllable)ourSprites.get(r);
                    if (c.isControlled())
                        c.keysAreDown(ourKeyMap);
                }
            }

            // Update all Sprites:
            for (int r = 0; r < ourSprites.size(); r++)
            {
                ourSprites.get(r).update();
            }

            // Follow the main character:
            followSprite(ourSubject);

            if (!ourSprites.contains(ourSubject) && ourFadeIncrement == 0)
                fadeOut();

            // Update fading:
            ourFadeFactor += ourFadeIncrement;
            if (ourFadeFactor <= 0)
            {
                ourFadeFactor = 0;
                ourFadeIncrement = 0;
            }
            if (ourFadeFactor >= 255)
            {
                ourFadeFactor = 255;
                ourFadeIncrement = 0;
            }
        }
        // Draw and update the screen:
        Graphics2D g = ourScreenManager.getGraphics();
        draw(g);
        g.dispose();
        ourScreenManager.update();
    }

    public static void draw(Graphics2D g)
    {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, SCREEN_W, SCREEN_H);

        Vector<SectorLayer> layers = ourCurrentSector.getLayers();
        for (int layer = 0; layer < layers.size(); layer++)
        {
            layers.get(layer).draw(g, ourScrollX, ourScrollY);
        }
        drawExits(g);
        drawSprites(g);
        drawDrawings(g);
        drawFade(g);
        if (ourInterfaceShown)
        {
            drawRadar(g);
            drawPowerMeter(g);
            drawHullMeter(g);
            drawCurrentWeapon(g);
            drawWeaponReadyMeter(g);
        }
        if (ourPaused)
            drawPaused(g);
    }
    public static void drawExits(Graphics2D g)
    {
        if (ourScrollX == 0)
        {
            double y = (SCREEN_H/2) - (ourExitWest.getHeight()/2);
            double x = 5;
            g.drawImage(ourExitWest, (int)x, (int)y, null);
            g.setColor(Color.WHITE);

            g.setFont(ourFont.deriveFont(Font.PLAIN, 14));
            int height = g.getFontMetrics().getHeight();
            g.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
            g.drawString("Milky Way", (int)x, (int)y+ourExitWest.getHeight()+height);
        } 
        else if (ourScrollX >= ourCurrentSector.getWidth()-SCREEN_W)
        {
            double y = (SCREEN_H/2) - (ourExitEast.getHeight()/2);
            double x = SCREEN_W-(ourExitEast.getWidth()+5);
            g.drawImage(ourExitEast, (int)x, (int)y, null);
            g.setColor(Color.WHITE);

            g.setFont(ourFont.deriveFont(Font.PLAIN, 14));
            int height = g.getFontMetrics().getHeight();
            int width = g.getFontMetrics().stringWidth("Milky Way");
            g.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
            g.drawString("Milky Way", (int)SCREEN_W-(width+5), (int)y+ourExitEast.getHeight()+height);
        }
        if (ourScrollY == 0)
        {
            double y = 5;
            double x = (SCREEN_W/2) - (ourExitNorth.getWidth()/2);
            g.drawImage(ourExitNorth, (int)x, (int)y, null);
            g.setColor(Color.WHITE);

            g.setFont(ourFont.deriveFont(Font.PLAIN, 14));
            int height = g.getFontMetrics().getHeight();
            int width = g.getFontMetrics().stringWidth("Milky Way");
            g.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
            g.drawString("Milky Way", (int)(SCREEN_W/2)-(width/2), (int)y+ourExitNorth.getHeight()+height);
        }
        else if (ourScrollY >= ourCurrentSector.getHeight()-SCREEN_H)
        {
            g.setFont(ourFont.deriveFont(Font.PLAIN, 14));
            int height = g.getFontMetrics().getHeight();
            int width = g.getFontMetrics().stringWidth("Milky Way");
            double y = SCREEN_H - (ourExitSouth.getHeight()+5+height);
            double x = (SCREEN_W/2) - (ourExitSouth.getWidth()/2);
            g.drawImage(ourExitSouth, (int)x, (int)y, null);
            g.setColor(Color.WHITE);

            
            g.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
            g.drawString("Milky Way", (int)(SCREEN_W/2)-(width/2), (int)SCREEN_H-5);
        }
    }
    public static void drawSprites(Graphics2D g)
    {
        for (int r = 0; r < ourSprites.size(); r++)
        {
            ourSprites.get(r).render(g);
        }
    }
    public static void drawDrawings(Graphics2D g)
    {
        for (int r = ourDrawings.size()-1; r >= 0; r--)
        {
            ourDrawings.get(r).render(g);

        }
    }
    public static void drawFade(Graphics2D g)
    {
        if (ourFadeFactor >= 0)
        {
            g.setColor(new Color(0, 0, 0, ourFadeFactor));
            g.fillRect(0, 0, SCREEN_W, SCREEN_H);
        }
    }
    public static void drawMap(Graphics2D g)
    {
        double mapScale = .01;
        int x = SCREEN_W-ourSectorMap.getWidth()-5;
        int y = 5;
        g.drawImage(ourSectorMap, x, y, null);
        int sx = (int)(ourSprites.get(0).getX()*mapScale);
        int sy = (int)(ourSprites.get(0).getY()*mapScale);
        g.setColor(Color.CYAN);
        g.fillOval((x+sx)-1, (y+sy)-1, 3, 3);
    }
    public static void drawRadar(Graphics2D g)
    {
        double mapWidth = 120;
        double mapScale = mapWidth / (double)ourCurrentSector.getWidth();
        int w = (int)(ourCurrentSector.getWidth()*mapScale);
        int h = (int)(ourCurrentSector.getHeight()*mapScale);
        int x = SCREEN_W-w-5;
        int y = 5;
        g.setColor(new Color(180, 255, 220, 50));
        g.fillRect(x, y, w, h);

        for (int s = 0; s < ourSprites.size(); s++)
        {
            int sx = (int)(ourSprites.get(s).getX()*mapScale);
            int sy = (int)(ourSprites.get(s).getY()*mapScale);
            g.setColor(ourSprites.get(s).getRadarColor());
            g.fillOval((x+sx)-1, (y+sy)-1, 3, 3);
        }
    }
    public static void drawPowerMeter(Graphics2D g)
    {
        int x = SCREEN_W - (ourHullMeter.getWidth()+10+ourPowerMeter.getWidth());
        int y = SCREEN_H - (ourPowerMeter.getHeight()+5);        
        int meterHeight = (int)(((Ship)ourSubject).getPowerPct()*59);
        int meterWidth = 20;
        int mx = x + 22, my = y + 62 - meterHeight;
        g.setColor(Color.CYAN);
        g.fillRect(mx, my, meterWidth, meterHeight);
        g.drawImage(ourPowerMeter, x, y, null);
    }
    public static void drawHullMeter(Graphics2D g)
    {
        int x = SCREEN_W - (ourHullMeter.getWidth()+5);
        int y = SCREEN_H - (ourHullMeter.getHeight()+5);
        int meterHeight = (int)(((Ship)ourSubject).getHullPct()*ourHullMeter.getHeight());
        int by = y + ourHullMeter.getHeight();
        int my = by - meterHeight;
        g.setColor(Color.RED);
        g.fillRect(x, my, ourHullMeter.getWidth(), meterHeight);
        g.drawImage(ourHullMeter, x, y, null);
    }
    public static void drawWeaponReadyMeter(Graphics2D g)
    {
//        int x = 80;
//        int y = SCREEN_H - ourWeaponPrepMeter.getHeight();
//        double prep = ((Ship)ourSubject).getWeaponReadyPct();
//        if (prep >= 1)
//        {
//            g.drawImage(ourWeaponReadyMeter, x, y, null);
//        }
//        else
//        {
//            int x1 = x + 5;
//            int y1 = y + (ourWeaponPrepMeter.getHeight()/2)-5;
//            g.setColor(Color.RED);
//            g.fillRect(x1, y1, (int)((ourWeaponReadyMeter.getWidth()-10)*prep), 10);
//            g.drawImage(ourWeaponPrepMeter, x, y, null);
//        }
        int x = 0;
        int y = SCREEN_H - (ourWeaponPrepMeter.getHeight()+20);
        double prep = ((Ship)ourSubject).getWeaponReadyPct();
        if (prep >= 1)
        {
            g.drawImage(ourWeaponReadyMeter, x, y, null);
        }
        else
        {
            int x1 = x + 5;
            int y1 = y + (ourWeaponPrepMeter.getHeight()/2)-5;
            g.setColor(Color.RED);
            g.fillRect(x1, y1, (int)((ourWeaponReadyMeter.getWidth()-10)*prep), 10);
            g.drawImage(ourWeaponPrepMeter, x, y, null);
        }
    }
    public static void drawCurrentWeapon(Graphics2D g)
    {
        String text = ((Ship)ourSubject).getCurrentWeapon().getName();
        g.setColor(Color.WHITE);
        g.setFont(ourFont.deriveFont(Font.PLAIN, 16));
        g.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
        int height = g.getFontMetrics().getHeight();
        g.drawString(text, 0, SCREEN_H);
    }
    public static void drawPaused(Graphics2D g)
    {
        String text = "PAUSED";
        g.setColor(Color.WHITE);
        g.setFont(ourFont.deriveFont(Font.PLAIN, 36));
        g.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
        int width = g.getFontMetrics().stringWidth(text);
        int height = g.getFontMetrics().getHeight();
        int x = (SCREEN_W/2)-(width/2);
        int y = (SCREEN_H/2)-(height/2);
        g.drawString(text, x, y);
    }

    public static void init() throws IOException
    {
        // Initialize graphics stuff:
        NullRepaintManager.install();
        ourScreenManager = new ScreenManager();
        DisplayMode[] modes = ourScreenManager.getCompatibleDisplayModes();
        DisplayMode selectedMode = new DisplayMode(SCREEN_W, SCREEN_H, 32, DisplayMode.REFRESH_RATE_UNKNOWN);
        for (DisplayMode m: modes)
        {
            if (ourScreenManager.displayModesMatch(m, selectedMode))
            {
                selectedMode = m;
                System.out.println("{" + m.getWidth() + "," + m.getHeight() + " " + m.getBitDepth() + "-bit @" + m.getRefreshRate() + "} selected.");
            }
        }
        ourScreenManager.setFullScreen(selectedMode);
        ourMainFrame = ourScreenManager.getFullScreenWindow();
        BufferedImage cursorImg;
        try {
            cursorImg = ImageIO.read(getFile("interface/cursor.png"));
            ourCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0,0), "Astro Cursor");
            ourBlankCursor = Toolkit.getDefaultToolkit().createCustomCursor(new BufferedImage(1,1,BufferedImage.TYPE_4BYTE_ABGR), new Point(0,0), "Blank Cursor");
            setShowInterface(false);
        } catch (IOException ex) {
            Logger.getLogger(Astro.class.getName()).log(Level.SEVERE, null, ex);
        }
        ourFont = getFontFile("digital.ttf");
        ourExitWest = getImageFromFile("interface/travel_left.png");
        ourExitEast = getImageFromFile("interface/travel_right.png");
        ourExitNorth = getImageFromFile("interface/travel_up.png");
        ourExitSouth = getImageFromFile("interface/travel_down.png");
        ourPowerMeter = getImageFromFile("interface/power_meter.png");
        ourHullMeter = getImageFromFile("interface/hull_integrity_x.png");
        ourWeaponPrepMeter = getImageFromFile("interface/ready_meter_sm_1.png");
        ourWeaponReadyMeter = getImageFromFile("interface/ready_meter_sm_2.png");


        // Initialize sounds:
        ourSoundManager = new SoundManager(20);
        ourMidiPlayer = new MidiPlayer();
        ourMidiPlayer.play(ourMidiPlayer.getSequence("extern/midi/Star Mission.MID"), true);

        // Initialize input stuff:
        ourMainFrame.addKeyListener(new KeyAdapter(){
            @Override
            public void keyPressed(KeyEvent e)
            {
                if (ourKeyMap[e.getKeyCode()]) return;

                if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
                    ourStop = true;

                if (e.getKeyCode() == KeyEvent.VK_F1)
                    setShowInterface(!ourInterfaceShown);                    

                if (e.getKeyCode() == KeyEvent.VK_F2)
                    setShowStatBars(!ourStatBarsShown);

                if (e.getKeyCode() == KeyEvent.VK_P)
                    setPaused(!ourPaused);

                ourKeyMap[e.getKeyCode()] = true;
                // Send a signal to each currently controlled Controllable:
                for (int r = ourSprites.size()-1; r >= 0; r--)
                {
                    if (ourSprites.get(r) instanceof Controllable)
                    {
                        Controllable c = (Controllable)ourSprites.get(r);
                        if (c.isControlled())
                            c.keyPressed(e.getKeyCode());
                    }
                }
            }
            @Override
            public void keyReleased(KeyEvent e)
            {
                ourKeyMap[e.getKeyCode()] = false;
                // Send a signal to each currently controlled Controllable:
                for (int r = 0; r < ourSprites.size(); r++)
                {
                    if (ourSprites.get(r) instanceof Controllable)
                    {
                        Controllable c = (Controllable)ourSprites.get(r);
                        if (c.isControlled())
                            c.keyReleased(e.getKeyCode());
                    }
                }
            }
        });
        try {
            ourRobot = new Robot();
        } catch (AWTException ex) {
            Logger.getLogger(Astro.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    public static File randomFileFromFolder(String folderName)
    {
        Random rand = new Random();
        File folder = new File("extern/" + folderName);
        File[] files = folder.listFiles();

        int index = rand.nextInt(files.length);
        return files[index];
    }


    public static BufferedImage randomPlanetGraphic()
    {
        Random rand = new Random();
        File size = randomFileFromFolder("planet_sizes");
        File txtr = randomFileFromFolder("planet_textures");
        try
        {
            BufferedImage source  = ImageIO.read(size);
            int w = source.getWidth();
            int h = source.getHeight();
            BufferedImage texture = ImageIO.read(txtr);

            BufferedImage planet = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);
            BufferedImage fullTexture = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);

            TexturePaint tp = new TexturePaint(texture, new Rectangle2D.Float(0,0,texture.getWidth(), texture.getHeight()));

            Rectangle2D fillRect = new Rectangle2D.Float(0,0,w,h);
            Graphics2D g = fullTexture.createGraphics();
            g.setPaint(tp);
            g.fill(fillRect);

            g = planet.createGraphics();
            g.drawImage(source, 0, 0, w, h, null);

            int textureFraction = 5;

            double rMult = rand.nextDouble();
            double gMult = rand.nextDouble();
            double bMult = rand.nextDouble();

            Color s, t, p;
            int red, green, blue, alpha;
            for (int x = 0; x < w; x++)
            {
                for (int y = 0; y < h; y++)
                {
                    int rgb = planet.getRGB(x, y);
                    s = new Color(rgb);
                    t = new Color(fullTexture.getRGB(x, y));
                    
                    red = ((s.getRed()*(textureFraction-1))+t.getRed())/textureFraction;
                    green = ((s.getGreen()*(textureFraction-1))+t.getGreen())/textureFraction;
                    blue = ((s.getBlue()*(textureFraction-1))+t.getBlue())/textureFraction;
                    alpha = (rgb >> 24) & 0xff;

                    red*=rMult;
                    green*=gMult;
                    blue*=bMult;

                    p = new Color(red, green, blue, alpha);
                    planet.setRGB(x, y, p.getRGB());
                }
            }

            return planet;
        } catch (IOException ex)
        {
            Logger.getLogger(Astro.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }


    public static BufferedImage newRandomPlanetGraphic()
    {
        Random rand = new Random();
        File size = randomFileFromFolder("planets");
        File txtr = randomFileFromFolder("planet_textures");
        try
        {
            BufferedImage source  = ImageIO.read(size);
            int w = source.getWidth();
            int h = source.getHeight();
            BufferedImage texture = ImageIO.read(txtr);

            double rMult = rand.nextDouble();
            double gMult = rand.nextDouble();
            double bMult = rand.nextDouble();
            // Color the texture.
            for (int x = 0; x < texture.getWidth(); x++)
            {
                for (int y = 0; y < texture.getHeight(); y++)
                {
                    Color c = new Color(texture.getRGB(x, y));
                    int gray = (c.getRed()+c.getGreen()+c.getBlue())/3;
                    int r = (int)(c.getRed()*rMult);
                    int g = (int)(c.getGreen()*gMult);
                    int b = (int)(c.getBlue()*bMult);
                    c = new Color(r, g, b);
                    texture.setRGB(x, y, c.getRGB());
                }
            }

            BufferedImage planet = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);
            // Fill the fullTexture.
            TexturePaint tp = new TexturePaint(texture, new Rectangle2D.Float(0,0,texture.getWidth(), texture.getHeight()));
            Ellipse2D fillCircle = new Ellipse2D.Float(1,1,w-3,h-3);
            Graphics2D g = planet.createGraphics();
            g.setPaint(tp);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.fill(fillCircle);

            // Draw the source sphere.
            g.drawImage(source, 0, 0, w, h, null);

            return planet;
        } catch (IOException ex)
        {
            Logger.getLogger(Astro.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
