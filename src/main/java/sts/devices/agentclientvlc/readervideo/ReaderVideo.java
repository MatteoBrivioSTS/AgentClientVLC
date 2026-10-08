package sts.devices.agentclientvlc.readervideo;

import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ReaderVideo {
    private final JFrame window;
    private final EmbeddedMediaPlayerComponent component;

    public ReaderVideo() {
        MediaPlayerFactory factory = new MediaPlayerFactory(
                "--no-video-title-show", "--no-plugins-cache",
                "--avcodec-hw=none","--quiet");
        component = new EmbeddedMediaPlayerComponent(factory, null, null, null, null);
        component.mediaPlayer().events().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
            @Override
            public void playing(MediaPlayer mp) {
                SwingUtilities.invokeLater(ReaderVideo.this::bringToFront);
            }
        });
        window = new JFrame("Stream");
        window.setBounds(100, 100, 1024, 768);
        window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        window.setContentPane(component);
        window.addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { close(); }
        });

    }

    public void startStream(String title, String url) {
        SwingUtilities.invokeLater(() -> {
            window.setTitle(title);
            bringToFront();
            component.mediaPlayer().media().play(url, ":rtsp-tcp", ":rtsp-frame-buffer-size=1000000");
        });
    }

    public void close() {
        SwingUtilities.invokeLater(() -> {
            component.mediaPlayer().controls().stop();
            window.setVisible(false);
        });
    }

    public void release() {
        SwingUtilities.invokeLater(() -> {
            window.dispose();
            component.release();
        });
    }
    private void bringToFront() {
        window.setVisible(true);
        window.setExtendedState(JFrame.NORMAL);   // se era minimizzata
        window.setAlwaysOnTop(true);
        window.toFront();
        window.requestFocus();
        window.setAlwaysOnTop(false);
    }

}
