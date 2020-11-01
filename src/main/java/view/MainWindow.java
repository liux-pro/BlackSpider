package view;

import bean.SpiderDatagramFrame;
import dao.FrameDao;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

class MainWindow extends JFrame {
    private boolean forceRefresh;
    private static MainWindow mainWindow;
    private static Timer timer;
    public final Thread thread;
    Insets insets ;
    private final FrameDao frameDao = new FrameDao();
    private final LinkedBlockingQueue<SpiderDatagramFrame> queue = new LinkedBlockingQueue<>(100);

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
        getContentPane().add(listener);
        timer = new Timer(30, listener);

        setTitle("BlackSpider");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        insets = getInsets();

      setSize(1366 + insets.left + insets.right, 768 + insets.bottom + insets.top);

    }

    public static void main(String[] args) throws IOException {


        mainWindow = new MainWindow();
        mainWindow.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                mainWindow.forceRefresh=true;
            }
        });
        mainWindow.setExtendedState(JFrame.MAXIMIZED_BOTH);
        timer.start();
        mainWindow.thread.start();

        System.out.println("6");
    }


    class Listener extends JPanel implements ActionListener {
        final private BufferedImage bufferedImage;
        final private Graphics bufferedGraphics;


        Listener() {


            bufferedImage = new BufferedImage(1366, 768, BufferedImage.TYPE_3BYTE_BGR);
            bufferedGraphics = bufferedImage.getGraphics();
            this.setDoubleBuffered(true);
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

                    bufferedGraphics.drawImage(bufferedImage, frame.getPaintX1(), frame.getScreenHeight() - (frame.getPaintY1() + bufferedImage.getHeight()), bufferedImage.getWidth(), bufferedImage.getHeight(), null);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (size > 0 || mainWindow.forceRefresh) {
                mainWindow.forceRefresh=false;
                long l = System.nanoTime();

                ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g.drawImage(this.bufferedImage, 0, 0, MainWindow.this.getWidth()-insets.left-insets.right, MainWindow.this.getHeight()-insets.top-insets.bottom, null);
                System.out.println("绘制用时" + (System.nanoTime() - l) / 100000 + "毫秒");
            }


            //super.paintComponent(g);

        }
    }
}