package view;

import bean.SpiderDatagramFrame;
import dao.FrameDao;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

import static config.Debug.DEBUG;

class MainWindow extends JFrame {
    private static MainWindow mainWindow;
    private static Timer timer;
    public final Thread thread;
    private final FrameDao frameDao = new FrameDao();
    private final LinkedBlockingQueue<SpiderDatagramFrame> queue = new LinkedBlockingQueue<>(100);
    Insets insets;

    public MainWindow() throws IOException {

        thread = new Thread(() -> {
            while (true) {
                SpiderDatagramFrame frame = frameDao.getFrame();

                if (queue.remainingCapacity() == 0) {
                    queue.poll();
                }
                queue.offer(frame);
            }
        });


        Listener listener = new Listener();
        setLayout(new BorderLayout());
        add(listener, BorderLayout.CENTER);
        timer = new Timer(30, listener);

        setTitle("BlackSpider");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        insets = getInsets();
        insets.set(0, 0, 0, 0);

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        setSize((screenSize.width >> 2) * 3, (screenSize.height >> 2) * 3);
    }

    public static void main(String[] args) throws IOException {


        mainWindow = new MainWindow();
        mainWindow.setExtendedState(JFrame.MAXIMIZED_BOTH);
        timer.start();
        mainWindow.thread.start();
        if (DEBUG) {
            System.out.println("it works!");
        }
    }


    class Listener extends JPanel implements ActionListener {
        final private BufferedImage bufferedImage;
        final private Graphics bufferedGraphics;


        Listener() {


            bufferedImage = new BufferedImage(1366, 768, BufferedImage.TYPE_3BYTE_BGR);
            bufferedGraphics = bufferedImage.getGraphics();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            repaint();
        }


        @Override
        public void paint(Graphics g) {
            SpiderDatagramFrame frame;
            int size = queue.size();
            int count = 0;
            while (count++ < size) {
                try {
                    frame = queue.take();

                    BufferedImage bufferedImage = frame.getBufferedImage();

                    bufferedGraphics.drawImage(bufferedImage, frame.getPaintX1(), frame.getScreenHeight() - (frame.getPaintY1() + bufferedImage
                            .getHeight()), bufferedImage.getWidth(), bufferedImage.getHeight(), null);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //since DEBUG is "final",the java compiler will auto remove unreachable branch as well as if statement itself.
            if (DEBUG) {
                long l = System.nanoTime();
                ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g.drawImage(this.bufferedImage, 0, 0, this.getWidth(), this.getHeight(), null);

                System.out.println("绘制用时" + (System.nanoTime() - l) / 100000 + "毫秒");
            } else {
                ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g.drawImage(this.bufferedImage, 0, 0, this.getWidth(), this.getHeight(), null);
            }

        }
    }
}