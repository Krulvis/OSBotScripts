package api.util;

import api.ATMethodProvider;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Krulvis on 21-Apr-17.
 */
public class ImageUtils extends ATMethodProvider {

    public ImageUtils(ATMethodProvider parent) {
        init(parent);
    }

    public void trayMessage(final String title, final String message, final TrayIcon.MessageType type) {
        new Thread() {
            @Override
            public void run() {
                if (!SystemTray.isSupported()) {
                    return;
                }
                TrayIcon[] icons = SystemTray.getSystemTray().getTrayIcons();
                if (icons == null || icons.length == 0) {
                    addTrayIcon(title);
                }
                icons = SystemTray.getSystemTray().getTrayIcons();
                for (TrayIcon icon : icons) {
                    icon.displayMessage(title, message, type);
                    return;
                }
            }
        }.start();
    }

    public void addTrayIcon(String SCRIPT_NAME) {
        if (!SystemTray.isSupported()) {
            return;
        }
        TrayIcon[] icons = SystemTray.getSystemTray().getTrayIcons();
        if (icons != null && icons.length > 0) {
            return;
        }
        try {
            final Image logo = getImage("http://i.imgur.com/mK4eQXd.png");
            TrayIcon trayIcon = new TrayIcon(logo, SCRIPT_NAME);
            SystemTray tray = SystemTray.getSystemTray();
            trayIcon.setImageAutoSize(true);
            tray.add(trayIcon);
        } catch (Exception ex) {
        }
    }

    public ImageIcon getImageIcon(String s) {
        try {
            ImageIcon cached = getImageIconCache(s);
            if (cached != null && cached.getIconHeight() != -1) {
                System.out.println("Loaded cached image for " + s);
                return cached;
            }
            URL u = new URL(s);
            HttpURLConnection c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.setRequestProperty("Content-length", "0");
            c.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9b5) Gecko/2008032620 Firefox/3.0b5");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            c.setConnectTimeout(10000);
            c.setReadTimeout(10000);
            c.connect();
            int status = c.getResponseCode();
            switch (status) {
                case 200:
                case 201:
                    ByteArrayOutputStream bais = new ByteArrayOutputStream();
                    InputStream is = null;
                    try {
                        is = c.getInputStream();
                        byte[] byteChunk = new byte[4096];
                        int n;
                        while ((n = is.read(byteChunk)) > 0) {
                            bais.write(byteChunk, 0, n);
                        }
                    } catch (IOException e) {
                    } finally {
                        if (is != null) {
                            is.close();
                        }
                    }
                    byte[] bytes = bais.toByteArray();
                    saveImage(s, bytes);
                    return new ImageIcon(bytes);
            }
        } catch (Exception ex) {
        }
        return null;
    }

    public Image getImage(String s) {
        try {
            return getImageIcon(s).getImage();
        } catch (Exception e) {
            return null;
        }
    }

    public final String FOLDER = System.getProperty("user.home") + File.separator + "OSBot" + File.separator + "data" + File.separator + "at_images" + File.separator;
    public final String PROGRESS_FOLDER = System.getProperty("user.home") + File.separator + "OSBot" + File.separator + "data" + File.separator + "at_progress" + File.separator;

    public String hash(String s) {
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(s.getBytes(), 0, s.length());
            return new BigInteger(1, m.digest()).toString(16);
        } catch (NoSuchAlgorithmException ex) {
        }
        return null;
    }

    public ImageIcon getImageIconCache(String s) {
        String hash = hash(s);
        if (hash == null)
            return null;
        File dir = new File(FOLDER);
        if (!dir.exists()) {
            dir.mkdirs();
            if (!dir.exists()) {
                return null;
            }
        }
        File file = new File(dir, hash + ".png");
        if (file.exists()) {
            return new ImageIcon(file.getAbsolutePath());
        }
        return null;
    }

    public void saveImage(String s, byte[] bytes) {
        FileOutputStream fos = null;
        try {
            String hash = hash(s);
            if (hash == null)
                return;
            File dir = new File(FOLDER);
            if (!dir.exists()) {
                dir.mkdirs();
                if (!dir.exists()) {
                    return;
                }
            }
            File file = new File(dir, hash + ".png");
            fos = new FileOutputStream(file);
            fos.write(bytes);
            fos.close();
            System.out.println("Cached image at " + file.getAbsolutePath());
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        } finally {
            try {
                fos.close();
            } catch (IOException ex) {
            }
        }
    }
}
