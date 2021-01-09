package io.github.fourfantastics.standby.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class VideoUtils {
	public ByteArrayOutputStream getThumbnailFromVideo(MultipartFile videoStream) {
		try {
			return getThumbnailFromVideo(videoStream.getInputStream());
		} catch (IOException e) {
			System.out.println(e);
			return null;
		}
	}
	
	public ByteArrayOutputStream getThumbnailFromVideo(InputStream videoStream) {
		FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(videoStream);
		Java2DFrameConverter converter = new Java2DFrameConverter();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		try {
			grabber.start();
			grabber.setVideoFrameNumber((int) (Math.random() * (grabber.getLengthInVideoFrames() / 10)));
			Frame frame = grabber.grabFrame();
			ImageIO.write(converter.convert(frame), "png", outputStream);
			grabber.stop();
			grabber.close();
		} catch (Exception e) {
			return null;
		}

		return outputStream;
	}
}
