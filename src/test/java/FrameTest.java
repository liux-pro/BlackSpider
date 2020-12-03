import pro._91code.blackspider.bean.SpiderDatagramFrame;
import pro._91code.blackspider.dao.FrameDao;

import java.io.IOException;

public class FrameTest {
    public void outPutImage() throws IOException {
        FrameDao frameDao = new FrameDao();
        SpiderDatagramFrame frame = frameDao.getFrame();


    }
}
