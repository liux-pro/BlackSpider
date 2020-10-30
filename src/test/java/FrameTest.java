import bean.SpiderDatagramFrame;
import dao.FrameDao;

import java.io.FileOutputStream;
import java.io.IOException;

public class FrameTest {
    public void outPutImage() throws IOException {
        FrameDao frameDao = new FrameDao();
        SpiderDatagramFrame frame = frameDao.getFrame();


    }
}
