


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

public class SwtTest {




    static int i=0;
    public static void main(String[] args) throws IOException {
        Display display = new Display();
        Shell shell = new Shell(display);

        FrameDao frameDao = new FrameDao();
        final SpiderDatagramFrame frame = frameDao.getFrame();
        final  ImageData imageData = frame.getImageData();

        System.out.println("read");


        Canvas canvas = new Canvas(shell, SWT.NONE);

        canvas.addPaintListener(new PaintListener() {
            @Override
            public void paintControl(PaintEvent e) {

                if (i==100) System.exit(0);


                org.eclipse.swt.graphics.PaletteData swtPalette = new PaletteData(0xff, 0xff00, 0xff0000);


                org.eclipse.swt.graphics.Image swtImage = new org.eclipse.swt.graphics.Image(Display.getDefault(), imageData);
                e.gc.drawImage(swtImage, 0, 0, swtImage.getImageData().width, swtImage.getImageData().height, 0, 0, shell.getSize().x, shell.getSize().y);
                swtImage.dispose();

            }
        });


        display.timerExec(1, new Runnable() {
            @Override
            public void run() {
                if (canvas.isDisposed()) {
                    return;
                }
                canvas.redraw();
                display.timerExec(1, this);
                System.out.println("rd");
            }
        });

        shell.pack();
        shell.open();

        canvas.setSize(1920, 1080);
        shell.setSize(1920, 1080);
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        display.dispose();
        System.out.println(i);

    }


}
