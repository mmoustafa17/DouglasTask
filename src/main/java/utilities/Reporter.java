/**
 * Copyright (c) Douglas 2020.
 * All Rights Reserved.
 *
 * ver          Developer          Date        Comments
 * ----- ---------------------  ----------  ----------------------------------------
 * 1.00  Eng. Mohamed Moustafa  16/09/2020  - Script created.
 */
package utilities;

import static org.monte.media.FormatKeys.EncodingKey;
import static org.monte.media.FormatKeys.FrameRateKey;
import static org.monte.media.FormatKeys.KeyFrameIntervalKey;
import static org.monte.media.FormatKeys.MediaTypeKey;
import static org.monte.media.FormatKeys.MimeTypeKey;
import static org.monte.media.VideoFormatKeys.CompressorNameKey;
import static org.monte.media.VideoFormatKeys.DepthKey;
import static org.monte.media.VideoFormatKeys.ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE;
import static org.monte.media.VideoFormatKeys.QualityKey;

import java.awt.AWTException;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.monte.media.Format;
import org.monte.media.FormatKeys;
import org.monte.media.FormatKeys.MediaType;
import org.monte.media.VideoFormatKeys;
import org.monte.media.math.Rational;
import org.monte.screenrecorder.ScreenRecorder;

import io.qameta.allure.Allure; 

public class Reporter {

	private static final Boolean RECORD_VIDEO = true;
	private static ScreenRecorder screenRecorder;
	private static final String RECORDING_FOLDER = System.getProperty("user.dir")+ "\\ScreenRecorder"; //System.getProperty("allureResultsFolderPath").trim() + "/" + "recordings/";


	/**
	 * Method to add system out print ln into allure report and console 
	 * @param text insert text to be displayed in console and allure report
	 */
	public static void Log(String text) {
		Allure.step(text);
		System.out.println(text);
	}


	/**
	 * Method to start the recording video 
	 */
	public static void startRecording() {
		// set the graphics configuration
		if (RECORD_VIDEO && screenRecorder == null && !GraphicsEnvironment.isHeadless()) {
			GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();

			try {
				screenRecorder = new ScreenRecorder(gc, gc.getBounds(), new Format(MediaTypeKey, MediaType.FILE, MimeTypeKey, FormatKeys.MIME_QUICKTIME), new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, VideoFormatKeys.ENCODING_QUICKTIME_ANIMATION, CompressorNameKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE, DepthKey, 24, FrameRateKey, Rational.valueOf(15), QualityKey, 1.0f, KeyFrameIntervalKey, 15 * 60), null, null, new File(RECORDING_FOLDER));
				screenRecorder.setMaxRecordingTime(3600000);
				// 3600000 milliseconds = 60 minutes = 1 hour
				screenRecorder.start();
			} catch (IOException | AWTException | NullPointerException e) {
				Reporter.Log(e.toString());
			}
		}
	}


	/**
	 * Method to stop the recording video 
	 */
	public static void stopRecording() {
		if (RECORD_VIDEO && screenRecorder != null) {
			try {
				screenRecorder.stop();
			} catch (IOException e) {
				Reporter.Log(e.toString());
			}
		}
	}


	/**
	 * Method to attach the recording video to allure report
	 * @throws IOException
	 */
	public static void attachRecording() throws IOException {
		if (RECORD_VIDEO && screenRecorder != null) {
			List<File> movies = screenRecorder.getCreatedMovieFiles();

			for (int i = 0; i < movies.size(); i++) {
				try {
					Helper.addAttachmenetsVideoToAllure( "Video Recording", "Execution Video #" + i + 1, new FileInputStream(movies.get(i)));

				} catch (FileNotFoundException e) {
					Reporter.Log(e.toString());
				}
			}
		}
	}

	public static Boolean getRecordVideo() {
		return RECORD_VIDEO;
	}
}
