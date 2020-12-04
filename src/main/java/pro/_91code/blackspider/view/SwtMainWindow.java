package pro._91code.blackspider.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import pro._91code.blackspider.bean.SpiderDatagramFrame;
import pro._91code.blackspider.dao.FrameDao;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

import static pro._91code.blackspider.config.Debug.DEBUG;

public class SwtMainWindow {


    public static void main(String[] args) throws IOException {
        final FrameDao frameDao = new FrameDao();
        final LinkedBlockingQueue<SpiderDatagramFrame> queue = new LinkedBlockingQueue<>(100);
        final ImageData bufferImageData = new ImageData(1366, 768, 24, new PaletteData(0xff0000, 0xff00, 0xff),
                1366, new byte[1366 * 768 * 3]);
        Thread thread = new Thread(() -> {
            while (true) {
                SpiderDatagramFrame frame = frameDao.getFrame();

                if (queue.remainingCapacity() == 0) {
                    queue.poll();
                }
                queue.offer(frame);
            }
        });
        thread.start();


        Display display = new Display();
        Shell shell = new Shell(display);


        System.out.println("read");


        Canvas canvas = new Canvas(shell, SWT.NONE);

        canvas.addPaintListener(new PaintListener() {
            @Override
            public void paintControl(PaintEvent e) {


                SpiderDatagramFrame frame;
                int size = queue.size();
                int count = 0;
                while (count++ < size) {
                    try {
                        frame = queue.take();

                        ImageData imageData = frame.getImageData();

//
//                        bufferedGraphics.drawImage(bufferedImage,
//                                frame.getPaintX1(),
//                                frame.getScreenHeight() - (frame.getPaintY1() + bufferedImage.getHeight()),
//                                bufferedImage.getWidth(),
//                                bufferedImage.getHeight(),
//                                null);
//                        byte[] image=frame.getImage();
                        int imageWidth = frame.getPaintX2() - frame.getPaintX1();
                        int imageHeight = frame.getPaintY2() - frame.getPaintY1();
//                        for (int i =0;i<imageHeight;i++) {
//                            bufferImageData.setPixels(
//                                    frame.getPaintX1(),
//                                    frame.getScreenHeight() - (frame.getPaintY1() + imageHeight)+i,
//                                    imageWidth,
//                                    image,
//                                    imageWidth*i
//                                    );
//                        }

                        for (int h = 0; h < imageHeight; h++) {
//                            bufferImageData.setPixels(
//                                    frame.getPaintX1(),
//                                    frame.getScreenHeight() - (frame.getPaintY1() + imageHeight) + h,
//                                    imageWidth,
//                                    imageData.data,
//                                    imageWidth * h
////                            );
                            System.arraycopy(imageData.data, h * imageWidth * 3, bufferImageData.data,
                                    3 * (frame.getPaintX1() + (frame.getScreenHeight() - frame.getPaintY2() + h) * bufferImageData.width),
                                    3 * imageWidth - 1
                            );
//                            System.out.println(3*(frame.getPaintX1()+(frame.getScreenHeight() - frame.getPaintY2()+h)*bufferImageData.width));
//                            System.out.println((h*imageWidth*3));
                        }
                    } catch (InterruptedException ee) {
                        ee.printStackTrace();
                    }
                }
                //since DEBUG is "final",the java compiler will auto remove unreachable branch as well as if statement itself.
                if (DEBUG) {
                    long l = System.nanoTime();
//
//                    java.awt.image.WritableRaster awtRaster = bufferedImage.getRaster();
//                    java.awt.image.DataBufferByte awtData = (DataBufferByte) awtRaster.getDataBuffer();
//                    byte[] rawData = awtData.getData();
//                    org.eclipse.swt.graphics.PaletteData swtPalette = new PaletteData(0xff, 0xff00, 0xff0000);
//
//                    int depth = 0x18;
//                    org.eclipse.swt.graphics.ImageData swtImageData = new ImageData(bufferedImage.getWidth()
//                            , bufferedImage.getHeight(), depth, swtPalette, bufferedImage
//                            .getWidth(), rawData);
                    org.eclipse.swt.graphics.Image swtImage = new org.eclipse.swt.graphics.Image(Display.getDefault(), bufferImageData);
                    e.gc.drawImage(swtImage, 0, 0, 1366, 768, 0, 0, shell.getSize().x, shell.getSize().y);
                    swtImage.dispose();

                    System.out.println("绘制用时" + (System.nanoTime() - l) / 100000 + "毫秒");
                } else {

                    org.eclipse.swt.graphics.Image swtImage = new org.eclipse.swt.graphics.Image(Display.getDefault(), bufferImageData);
                    e.gc.drawImage(swtImage, 0, 0, 1366, 768, 0, 0, shell.getSize().x, shell.getSize().y);
                    swtImage.dispose();

                }
            }
        });


        display.timerExec(30, new Runnable() {
            @Override
            public void run() {
                if (canvas.isDisposed()) {
                    return;
                }
                canvas.redraw();

                display.timerExec(30, this);
            }
        });


        shell.pack();
        shell.open();

        canvas.setSize(1366, 768);
        shell.setSize(1366, 768);
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        display.dispose();

    }


}
