package sts.devices.agentclientvlc.readervideo;

import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;

import javax.swing.*;

public class MultiReaderVideo {

    private final JFrame window;
    private final EmbeddedMediaPlayerComponent component;

    public MultiReaderVideo() {

        MediaPlayerFactory factory = new MediaPlayerFactory(
                "--no-video-title-show",
                "--no-plugins-cache",
                "--avcodec-hw=none",
                "--quiet"
        );

        component = new EmbeddedMediaPlayerComponent(
                factory, null, null, null, null);

        window = new JFrame();
        window.setSize(780, 470);
        window.setLocationRelativeTo(null);
        window.setAlwaysOnTop(true);
        window.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        window.setContentPane(component);
    }

    public void startStream(String title, String url, int close) {

        SwingUtilities.invokeLater(() -> {

            window.setTitle(title);
            window.setVisible(true);
            window.toFront();
            window.requestFocus();

            component.mediaPlayer().media().play(
                    url,
                    ":rtsp-tcp",
                    ":network-caching=1000"
            );
            new javax.swing.Timer(close*1000, e -> close()) {{
                setRepeats(false);
            }}.start();
        });
    }



    public void close() {

        SwingUtilities.invokeLater(() -> {
            component.mediaPlayer().controls().stop();
            window.dispose();
        });
    }

    public void release() {
        component.release();
    }
}