/**
 * 
 */
package com.riease.common.sysinit.security;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;

import com.google.code.kaptcha.BackgroundProducer;
import com.google.code.kaptcha.GimpyEngine;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.text.WordRenderer;

/**
 * @author Cid
 *
 */
public class CalKaptcha extends DefaultKaptcha {

//	Logger log = Logger.getLogger(CalKaptcha.class);
	
	private int width = 200;
	private int height = 50;
	
	public BufferedImage createImage(String text) {
//		long t1 = System.currentTimeMillis();
		WordRenderer wordRenderer = getConfig().getWordRendererImpl();
		GimpyEngine gimpyEngine = getConfig().getObscurificatorImpl();
		BackgroundProducer backgroundProducer = getConfig().getBackgroundImpl();
		boolean isBorderDrawn = getConfig().isBorderDrawn();
		this.width = getConfig().getWidth();
		this.height = getConfig().getHeight();

		BufferedImage bi = wordRenderer.renderWord(text, width, height);
		bi = gimpyEngine.getDistortedImage(bi);
		bi = backgroundProducer.addBackground(bi);
		Graphics2D graphics = bi.createGraphics();
		if (isBorderDrawn) {
			drawBox(graphics);
		}
//		long t2 = System.currentTimeMillis();
//		log.info("create image time ... " + (t2-t1));
		return bi;
	}

	private void drawBox(Graphics2D graphics) {
		Color borderColor = getConfig().getBorderColor();
		int borderThickness = getConfig().getBorderThickness();

		graphics.setColor(borderColor);

		if (borderThickness != 1) {
			BasicStroke stroke = new BasicStroke((float) borderThickness);
			graphics.setStroke(stroke);
		}

		Line2D line1 = new Line2D.Double(0, 0, 0, width);
		graphics.draw(line1);
		Line2D line2 = new Line2D.Double(0, 0, width, 0);
		graphics.draw(line2);
		line2 = new Line2D.Double(0, height - 1, width, height - 1);
		graphics.draw(line2);
		line2 = new Line2D.Double(width - 1, height - 1, width - 1, 0);
		graphics.draw(line2);
	}
	
}
