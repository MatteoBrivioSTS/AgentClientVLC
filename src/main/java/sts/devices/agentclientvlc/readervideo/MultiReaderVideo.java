package sts.devices.agentclientvlc.readervideo;

import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

//public class MultiReaderVideo {
//
//    private final JFrame window;
//    private final EmbeddedMediaPlayerComponent component;
//
//    public MultiReaderVideo() {
//
//        MediaPlayerFactory factory = new MediaPlayerFactory(
//                "--no-video-title-show",
//                "--no-plugins-cache",
//                "--avcodec-hw=none",
//                "--quiet"
//        );
//
//        component = new EmbeddedMediaPlayerComponent(
//                factory, null, null, null, null);
//
//        window = new JFrame();
//        window.setSize(780, 470);
//        window.setLocationRelativeTo(null);
//        window.setAlwaysOnTop(true);
//        window.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
//        window.setContentPane(component);
//    }
//
//    public void startStream(String title, String url, int close) {
//
//        SwingUtilities.invokeLater(() -> {
//
//            window.setTitle(title);
//            window.setVisible(true);
//            window.toFront();
//            window.requestFocus();
//
//            component.mediaPlayer().media().play(
//                    url,
//                    ":rtsp-tcp",
//                    ":network-caching=1000"
//            );
//            new javax.swing.Timer(close*1000, e -> close()) {{
//                setRepeats(false);
//            }}.start();
//        });
//    }
//
//
//
//    public void close() {
//
//        SwingUtilities.invokeLater(() -> {
//            component.mediaPlayer().controls().stop();
//            window.dispose();
//        });
//    }
//
//    public void release() {
//        SwingUtilities.invokeLater(() -> { window.dispose(); component.release(); });
//    }
//}

public class MultiReaderVideo {

    private static final int MAX_POPUPS = 6;

    private final MediaPlayerFactory factory = new MediaPlayerFactory(
            "--no-video-title-show", "--no-plugins-cache", "--avcodec-hw=none", "--quiet");

    // accesso solo dall'EDT
    private final Map<String, Popup> popups = new LinkedHashMap<>();

    public void startStream(String id, String title, String url, int closeSeconds) {
        SwingUtilities.invokeLater(() -> {
            Popup existing = popups.get(id);
            if (existing != null) {            // stessa camera: ricomincia e riporta in primo piano
                existing.restart(url, closeSeconds);
                return;
            }
            if (popups.size() >= MAX_POPUPS) { // chiudo il più vecchio
                popups.values().iterator().next().close();
            }
            Popup p = new Popup(id, title);
            popups.put(id, p);
            p.start(url, closeSeconds);
        });
    }

    public void close(String id) {
        SwingUtilities.invokeLater(() -> {
            Popup p = popups.get(id);
            if (p != null) p.close();
        });
    }

    public void release() {
        SwingUtilities.invokeLater(() -> {
            new ArrayList<>(popups.values()).forEach(Popup::close);
            factory.release();
        });
    }

    private class Popup {
        private final String id;
        private final JFrame window = new JFrame();
        private final EmbeddedMediaPlayerComponent component =
                new EmbeddedMediaPlayerComponent(factory, null, null, null, null);
        private javax.swing.Timer timer;

        Popup(String id, String title) {
            this.id = id;
            window.setTitle(title);
            window.setSize(780, 470);
            window.toFront();
            int offset = popups.size() * 40;
            window.setLocation(100 + offset, 100 + offset);
            window.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            window.setContentPane(component);
            window.addWindowListener(new WindowAdapter() {
                @Override public void windowClosing(WindowEvent e) { close(); }
            });
        }

        void start(String url, int closeSeconds) {
            window.setVisible(true);
            toFront();
            component.mediaPlayer().media().play(url, ":rtsp-tcp", ":network-caching=1000");
            armTimer(closeSeconds);
        }

        void restart(String url, int closeSeconds) {
            component.mediaPlayer().media().play(url, ":rtsp-tcp", ":network-caching=1000");
            toFront();
            armTimer(closeSeconds);
        }

        private void armTimer(int seconds) {
            if (timer != null) timer.stop();
            if (seconds <= 0) return;
            timer = new javax.swing.Timer(seconds * 1000, e -> close());
            timer.setRepeats(false);
            timer.start();
        }

        private void toFront() {
            window.setAlwaysOnTop(true);
            window.toFront();
            window.requestFocus();
            window.setAlwaysOnTop(false);
        }

        void close() {
            if (timer != null) timer.stop();
            popups.remove(id, this);
            component.mediaPlayer().controls().stop();
            window.dispose();
            component.release();   // libera il player nativo di questo popup
        }
    }
}