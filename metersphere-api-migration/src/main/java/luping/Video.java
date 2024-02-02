package luping;

import org.bytedeco.opencv.opencv_core.*;

import java.awt.*;

public class Video {
    private static final int FRAME_RATE = 30;
    private static final String OUTPUT_FILE = "screen-recording.mp4";
    private static final int SCREEN_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
    private static final int SCREEN_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;

    public static void main(String[] args) throws Exception {
        // 创建录屏对象
        ScreenGrabber grabber = new ScreenGrabber(new Rectangle(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT));
        // 创建视频录制器
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(OUTPUT_FILE, SCREEN_WIDTH, SCREEN_HEIGHT, FRAME_RATE);
        // 开始录制
        recorder.start();
        // 录屏并保存帧
        Frame frame;
        while ((frame = grabber.grab()) != null) {
            recorder.record(frame);
        }
        // 停止录制并释放资源
        recorder.stop();
        recorder.release();
    }
}