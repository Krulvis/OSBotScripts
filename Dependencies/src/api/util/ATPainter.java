package api.util;

import api.ATScript;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.util.GraphicUtilities;

import java.awt.*;
import java.text.DecimalFormat;

import static java.awt.Color.BLACK;
import static java.awt.Color.GREEN;
import static java.awt.Color.RED;

/**
 * Created by Krulvis on 29-May-17.
 */
public abstract class ATPainter<S extends ATScript> {

    public S script;
    private int length = 5;
    public int x = 20, y = 248, yP = 13, w = 190;
    public int yy = yP;
    public int imageX = 2, imageY = 205;
    public Rectangle hide = new Rectangle(495, 225, 20, 20);
    public boolean useLayout = true, useImage = false;
    public Image showImage = null;
    public Image paintImage = null;
    public Image progressBar = null;
    public boolean showPaint = true, hideUsername = true;

    public ATPainter(S script) {
        this.script = script;
        this.useLayout = false;
    }

    public ATPainter(S script, int length) {
        this.script = script;
        this.length = length;
    }

    public void onRepaint(Graphics2D g) {
        try {
            if (useLayout) {
                drawStandard(g, length);
            } else if (useImage) {
                drawImage(g);
            }
            //TODO add break pattern
            //AntiBan.paintBreak(g);
            paint(g);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public abstract void paint(Graphics2D g);

    public void hideChatboxName(Graphics g) {
        hideChatboxName(g, BLACK);
    }

    private String name;

    public void hideChatboxName(Graphics g, Color color) {
        if (name == null) {
            name = script.myPlayer().getName();
        }
        if (name != null) {
            g.setColor(color);
            g.fillRect(10, 459, name.length() * 7, 15);
        }
    }

    public static void drawString(Graphics g, String s1, String s2, int x, int y, Color color1, Color color2) {
        drawString(g, s1, x, y, color1, BLACK);
        int l = g.getFontMetrics().stringWidth(s1);
        drawString(g, s2, x + l, y, color2, BLACK);
    }

    public static void drawPerHour(Graphics g, String s, int amount, int perhour, int x, int y) {
        drawString(g, s + formatAmount(amount) + ", (" + formatAmount(perhour) + ")/hr", x, y);
    }

    public static void drawString(Graphics g, String s, int x, int y) {
        drawString(g, s, x, y, Color.ORANGE, BLACK);
    }

    public static void drawString(Graphics g, String s, int x, int y, Color color) {
        drawString(g, s, x, y, color, BLACK);
    }

    public static void drawStringCenter(Graphics g, String s, int x, int y, Color color) {
        drawString(g, s, x - (g.getFontMetrics().stringWidth(s) / 2), y, color, BLACK);
    }

    public void drawTitle(Graphics g, String s, int x, int y) {
        Font oldFont = g.getFont();
        Font newFont = new Font(oldFont.getName(), oldFont.getStyle(), oldFont.getSize() + 2);
        g.setFont(newFont);
        drawString(g, s, x, y, RED_PALE, RED);
        g.setFont(oldFont);
    }

    public static void drawString(Graphics g, String s, int x, int y, Color color, Color underColor) {
        Color old = g.getColor();
        g.setColor(underColor);
        g.drawString(s, x <= 0 ? 0 : x - 1, y <= 0 ? 0 : y - 1);
        g.setColor(color);
        g.drawString(s, x, y);
        g.setColor(old);
    }


    public static void drawButton(Graphics g, String s, Rectangle rect) {
        drawButton(g, s, rect, LIGHT_GREEN);
    }

    public static void drawButton(Graphics g, String s, Rectangle rect, Color c) {
        Graphics2D g2 = (Graphics2D) g;
        Color old = g2.getColor();
        g2.setColor(c);
        g2.fill(rect);
        drawStringCenter(g, s, (int) rect.getCenterX(), (int) rect.getCenterY(), BLACK);
        g.setColor(old);
    }

    public static String format(int i) {
        return DECIMAL_FORMAT.format(i);
    }

    public static String formatAmount(int amount) {
        boolean needsMin = amount < 0;
        amount = Math.abs(amount);
        if (amount / 10000000 > 0) {
            return Math.round((double) amount / 1000000.0) + "m";
        }
        if (amount / 1000 > 0) {
            return Math.round((double) amount / 1000.0) + "k";
        }
        return (needsMin ? "-" : "") + amount + "";
    }

    private DynamicColor custom = new DynamicColor(0.40f, 0.75f, 0.01f);

    public void drawStandard(Graphics g1, int lines) {
        Graphics2D g = (Graphics2D) g1;
        if (hideUsername) {
            hideChatboxName(g);
        }
        g.setStroke(new BasicStroke(1.0f));

        Color custom = this.custom.get();
        int x = this.x - 3, y = this.y - 28, yP = this.yP, w = this.w;

        g.setColor(custom);
        g.drawRect(x, y, w, 51 + (lines * 10));

        g.setColor(BLACK_AA);
        g.fillRect(x, y, w - 1, 51 + (lines * 10) - 1);
        y += 3;
        String title = script.getName() + " " + script.getVersion();
        drawTitle(g, title, x - 10 + w / 2 - g.getFontMetrics().stringWidth(title) / 2, y += yP);
        drawString(g, "Runtime: " + script.timer, x, y + yP);
    }

    public void drawImage(Graphics g1) {

        if (!showPaint && showImage != null) {
            g1.drawImage(showImage, imageX, imageY, null);
        } else if (paintImage != null) {
            if (hideUsername) {
                hideChatboxName(g1);
            }
            g1.drawImage(paintImage, imageX, imageY, null);
        } else {
        }

    }

    public void drawPath(Graphics g, Position[] tiles) {
        Graphics2D g2 = (Graphics2D) g;
        for (Position p : tiles) {
            if (p != null) {
                drawPosition(g2, p, WHITE, BLACK);
                short[] coordinates = GraphicUtilities.getMinimapScreenCoordinate(script.bot, p.getX(), p.getY());
                Rectangle center = new Rectangle(coordinates[0], coordinates[1], 2, 2);
                g2.draw(center);
            }
        }
    }

    public void drawPosition(Graphics g, Position t) {
        drawPosition((Graphics2D) g, t, GREEN, RED_OPAQUE);
    }

    public void drawPosition(Graphics g, Position t, Color draw, Color fill) {
        Color c = g.getColor();
        if (t != null && t.getPolygon(script.bot) != null) {
            Polygon p = t.getPolygon(script.bot);
            if (p == null)
                return;
            g.setColor(draw);
            g.drawPolygon(p);
            if (fill != null) {
                g.setColor(fill);
                g.fillPolygon(p);
            }
        }
        g.setColor(c);
    }

    public static final Font BIG20 = new Font("Calibri", Font.BOLD, 25);
    public static final Font SMALL14 = new Font("Calibri", Font.BOLD, 14);

    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("###,###,###.###");

    public static final Color WHITE = new Color(0x88FFFFFF, true);
    public static final Color LIGHT_BLUE = new Color(0, 136, 255, 50);
    public static final Color LIGHT_GREEN = new Color(89, 255, 11, 38);
    public static final Color RED_OPAQUE = new Color(255, 64, 11, 50);
    public static final Color DARK_GREEN = new Color(18, 107, 39, 92);
    public static final Color DARK_BLUE = new Color(22, 72, 107, 151);
    public static final Color BLACK_AA = new Color(0xBB000000, true);
    public static final Color BLACK_B = new Color(0xCC000000, true);
    public static final Color GRAY = Color.WHITE.darker();
    public static final Color RED_PALE = new Color(0xFF453A);
    public static final Color GREEN_PALE = new Color(0x75FF4F);
    public static final Color BLACK_A = new Color(0xBB000000, true);
    public static final Color DARK_ORANGE = Color.ORANGE.darker();
    public static final Color DARK_RED = Color.RED.darker();


}
