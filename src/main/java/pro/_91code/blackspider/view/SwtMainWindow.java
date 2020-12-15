package pro._91code.blackspider.view;

import com.jogamp.opengl.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.*;
import pro._91code.blackspider.bean.SpiderImage;
import pro._91code.blackspider.dao.FrameDao;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingQueue;

import static com.jogamp.opengl.GL.*;
import static com.jogamp.opengl.GL2ES3.GL_QUADS;
import static pro._91code.blackspider.config.Debug.DEBUG;

public class SwtMainWindow {
    public static int width = 0;
    public static int height = 0;
    static int[] tex = {0};


    public static void main(String[] args) throws IOException {

        final FrameDao frameDao = new FrameDao();
        final LinkedBlockingQueue<SpiderImage> queue = new LinkedBlockingQueue<>(100);
        final byte[] exchangeBuffer = new byte[1366 * 768 * 3];

        Thread thread = new Thread(() -> {
            while (true) {
                SpiderImage frame = frameDao.getFrame();

                if (queue.remainingCapacity() == 0) {
                    queue.poll();
                }
                queue.offer(frame);
            }
        });
        thread.start();


        final Display display = new Display();
        Shell shell = new Shell(display);
        shell.setLayout(new FillLayout());
        Composite comp = new Composite(shell, SWT.NONE);
        comp.setLayout(new FillLayout());
        GLData data = new GLData();
        data.doubleBuffer = true;
        final GLCanvas canvas = new GLCanvas(comp, SWT.NONE, data);

        ImageData imageData = new ImageData(SwtMainWindow.class.getClassLoader().getResourceAsStream("BlackSpider.png"));
        shell.setImage(new Image(display,imageData));

        canvas.setCurrent();
        final GLContext context = GLDrawableFactory.getFactory(GLProfile.getGL2GL3()).createExternalGLContext();
        context.makeCurrent();
        GL2 gl = context.getGL().getGL2();
        gl.glGenTextures(1, tex, 0);
        gl.glBindTexture(GL_TEXTURE_2D, tex[0]);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        gl.glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, 1366, 768, 0, GL_RGB, GL_UNSIGNED_BYTE, null);
        gl.glBindTexture(GL_TEXTURE_2D, 0);
        gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);


        context.release();


        canvas.addListener(SWT.Resize, new Listener() {
            @Override
            public void handleEvent(Event event) {
                Rectangle bounds = canvas.getBounds();
                width = bounds.width;
                height = bounds.height;
                canvas.setCurrent();
                context.makeCurrent();
                GL2 gl = context.getGL().getGL2();
                gl.glViewport(0, 0, bounds.width, bounds.height);
                context.release();
            }
        });
        shell.addShellListener(new ShellAdapter() {
            @Override
            public void shellClosed(ShellEvent e) {
                System.exit(0);
            }
        });
        final long[] l = {System.currentTimeMillis()};
        display.timerExec(30, new Runnable() {
            @Override
            public void run() {
                long l1 = System.currentTimeMillis();
                System.out.println(l1- l[0]);
                l[0] =System.currentTimeMillis();
                if (!canvas.isDisposed()) {

                    canvas.setCurrent();
                    context.makeCurrent();
                    GL2 gl = (GL2) context.getGL();


                    SpiderImage image;
                    int size = queue.size();
                    int count = 0;
                    while (count++ < size) {
                        try {

                            image = queue.take();


//                            for (int h = 0; h < image.getImageHeight(); h++) {

//                                System.arraycopy(image.getImage(), h * image.getLineBytesSize(), bufferImageData.data,
//                                        3 * (image.getPaintX1() + (image.getScreenHeight() - image.getPaintY2() + h) * bufferImageData.width),
//                                        image.getLineBytesSizeNoPadding()
//
//                                );
//                                System.arraycopy(image.data, h * imageWidth * 3, bufferImageData.data,
//                                        3 * (frame.getPaintX1() + (frame.getScreenHeight() - frame.getPaintY2() + h) * bufferImageData.width),
//                                        3 * imageWidth - 1
//
//                                );
                            //https://zhuanlan.zhihu.com/p/25119530
                            gl.glPolygonMode(GL.GL_FRONT, GL2GL3.GL_FILL);

                            gl.glBindTexture(GL_TEXTURE_2D, tex[0]);

                            gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);

                            //since DEBUG is "final",the java compiler will auto remove unreachable branch as well as if statement itself.
                            if (DEBUG) {
                                for (int h = 0; h < image.getImageHeight(); h++) {
                                    System.arraycopy(image.getImage(),h*image.getLineBytesSize(),
                                            exchangeBuffer,(image.getImageHeight()-h-1)*image.getLineBytesSize(),
                                            image.getLineBytesSizeNoPadding()
                                    );
                                }
                                gl.glTexSubImage2D(GL_TEXTURE_2D, 0, image.getPaintX1(), image.getPaintY1(), image.getImageWidth(), image
                                        .getImageHeight(), GL_RGB, GL_UNSIGNED_BYTE, ByteBuffer.wrap(exchangeBuffer));
                            } else {
                                for (int h = 0; h < image.getImageHeight(); h++) {
                                    System.arraycopy(image.getImage(),h*image.getLineBytesSize(),
                                            exchangeBuffer,(image.getImageHeight()-h-1)*image.getLineBytesSize(),
                                            image.getLineBytesSizeNoPadding()
                                    );
                                }

                                gl.glTexSubImage2D(GL_TEXTURE_2D, 0, image.getPaintX1(), image.getPaintY1(), image.getImageWidth(), image
                                        .getImageHeight(), GL_RGB, GL_UNSIGNED_BYTE, ByteBuffer.wrap(exchangeBuffer));
                            }


//                            }
                        } catch (InterruptedException ee) {
                            ee.printStackTrace();
                        }
                    }

                    gl.glBindTexture(GL_TEXTURE_2D, tex[0]);
                    gl.glEnable(GL_TEXTURE_2D);
                    gl.glBegin(GL_QUADS);
                    gl.glTexCoord2i(0, 0);
                    gl.glVertex2f(-1, -1);
                    gl.glTexCoord2i(1, 0);
                    gl.glVertex2f(1, -1);
                    gl.glTexCoord2i(1, 1);
                    gl.glVertex2f(1, 1);
                    gl.glTexCoord2i(0, 1);
                    gl.glVertex2f(-1, 1);
                    gl.glBindTexture(GL_TEXTURE_2D, 0);
                    gl.glEnd();
                    gl.glDisable(GL_TEXTURE_2D);


                    canvas.swapBuffers();
                    context.release();

                    display.timerExec(1000/30, this);
                }
            }
        });


        shell.setSize(1366, 768);
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        display.dispose();

    }


}
