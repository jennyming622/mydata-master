/**
 * 
 */
package com.riease.common.sysinit.security.jcaptcha;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.octo.captcha.CaptchaFactory;
import com.octo.captcha.component.image.backgroundgenerator.BackgroundGenerator;
import com.octo.captcha.component.image.backgroundgenerator.EllipseBackgroundGenerator;
import com.octo.captcha.component.image.backgroundgenerator.FunkyBackgroundGenerator;
import com.octo.captcha.component.image.backgroundgenerator.GradientBackgroundGenerator;
import com.octo.captcha.component.image.backgroundgenerator.MultipleShapeBackgroundGenerator;
import com.octo.captcha.component.image.backgroundgenerator.UniColorBackgroundGenerator;
import com.octo.captcha.component.image.color.RandomListColorGenerator;
import com.octo.captcha.component.image.color.SingleColorGenerator;
import com.octo.captcha.component.image.fontgenerator.FontGenerator;
import com.octo.captcha.component.image.fontgenerator.RandomFontGenerator;
import com.octo.captcha.component.image.textpaster.DecoratedRandomTextPaster;
import com.octo.captcha.component.image.textpaster.GlyphsPaster;
import com.octo.captcha.component.image.textpaster.TextPaster;
import com.octo.captcha.component.image.textpaster.glyphsvisitor.GlyphsVisitors;
import com.octo.captcha.component.image.textpaster.glyphsvisitor.OverlapGlyphsUsingShapeVisitor;
import com.octo.captcha.component.image.textpaster.glyphsvisitor.TranslateAllToRandomPointVisitor;
import com.octo.captcha.component.image.textpaster.glyphsvisitor.TranslateGlyphsVerticalRandomVisitor;
import com.octo.captcha.component.image.textpaster.textdecorator.LineTextDecorator;
import com.octo.captcha.component.image.textpaster.textdecorator.TextDecorator;
import com.octo.captcha.component.image.wordtoimage.ComposedWordToImage;
import com.octo.captcha.component.image.wordtoimage.WordToImage;
import com.octo.captcha.component.word.wordgenerator.RandomWordGenerator;
import com.octo.captcha.component.word.wordgenerator.WordGenerator;
import com.octo.captcha.engine.CaptchaEngine;
import com.octo.captcha.engine.GenericCaptchaEngine;
import com.octo.captcha.image.gimpy.GimpyFactory;
import com.octo.captcha.service.captchastore.CaptchaStore;
import com.octo.captcha.service.captchastore.FastHashMapCaptchaStore;
import com.octo.captcha.service.image.DefaultManageableImageCaptchaService;
import com.octo.captcha.service.image.ImageCaptchaService;

/**
 * 以spring配置Jcaptcha
 * @author wesleyzhuang
 *
 */
@Configuration
public class AppJcaptchaConfig {

	private static Logger logger = LoggerFactory.getLogger(AppJcaptchaConfig.class);
	
	@Value("${jcaptcha.minGuarantedStorageDelayInSeconds:180}")
	private int minGuarantedStorageDelayInSeconds;
	
	@Value("${jcaptcha.maxCaptchaStoreSize:100000}")
	private int maxCaptchaStoreSize;
	
	@Value("${jcaptcha.captchaStoreLoadBeforeGarbageCollection:75000}")
	private int captchaStoreLoadBeforeGarbageCollection;
	
	@Value("${jcaptcha.font.minSize:50}")
	private int minFontSize;
	
	@Value("${jcaptcha.font.maxSize:50}")
	private int maxFontSize;
	
	@Value("${jcaptcha.background.width:200}")
	private int backgroundWidth;
	
	@Value("${jcaptcha.background.height:70}")
	private int backgroundHeight;
	
	@Value("${jcaptcha.background.color:#FFFFFF}")
	private String backgroundColor;
	
	@Value("${jcaptcha.text.minLength:7}")
	private int textMinLength;
	
	@Value("${jcaptcha.text.maxLength:7}")
	private int textMaxLength;
	
	private static String WORD = "ABCDEFGHIJKLMNPQRSTUVWXYZ123456789";
	
	@Bean
	public ImageCaptchaService imageCaptchaService() {
		DefaultManageableImageCaptchaService service = 
				new DefaultManageableImageCaptchaService(
						captchaStore(),
						captchaEngine(),
						minGuarantedStorageDelayInSeconds,
						maxCaptchaStoreSize,
						captchaStoreLoadBeforeGarbageCollection);
		return service;
	}
	
	@Bean
	public CaptchaStore captchaStore() {
		CaptchaStore store = new FastHashMapCaptchaStore();
		return store;
	}
	
	@Bean
	public CaptchaEngine captchaEngine() {
		CaptchaFactory[] fs = new CaptchaFactory[] {
			captchaFactory()	
		};
		CaptchaEngine engine = new GenericCaptchaEngine(fs);
		return engine;
	}
	
	@Bean
	public SampleGimpyFactory captchaFactory() {
		SampleGimpyFactory factory = new SampleGimpyFactory(
				wordGenerator(),
				wordToImage());
		return factory;
	}
	
	@Bean
	public WordGenerator wordGenerator() {
		//toddlist.properties -> 包含在 jcaptcha-2.0-alpha-1-SNAPSHOT.jar 中。
		//DictionaryReader dictionary = new FileDictionary("toddlist");
		//WordGenerator generator = new DictionaryWordGenerator(dictionary);
		WordGenerator generator = new RandomWordGenerator(WORD);
		return generator;
	}
	
	@Bean
	public WordToImage wordToImage() {
		FontGenerator fontGenerator = new RandomFontGenerator(
				minFontSize, maxFontSize, 
				new Font[] { new Font("nyala", 1, 50), new Font("Bell MT", 0, 50), new Font("Credit valley", 1, 50) }, 
				false);
		
		//BackgroundGenerator backGenerator = new UniColorBackgroundGenerator(backgroundWidth, backgroundHeight, parseHexColor(backgroundColor));
		BackgroundGenerator backGenerator = backgroundGenerator();
		TextPaster textPaster = textPaster();
		
		WordToImage word2image = 
				new ComposedWordToImage(
						fontGenerator,
						backGenerator,
						textPaster);
		
		return word2image;
	}
	
	@Bean
	public TextPaster textPaster() {
//		TextPaster textPaster = new GlyphsPaster(
//				textMinLength, textMaxLength, 
//				new RandomListColorGenerator(
//					new Color[] { 
//						//new Color(23, 170, 27), 
//						//new Color(220, 34, 11), 
//						//new Color(23, 67, 172) 
//						new Color(50, 50, 50)
//					}), 
//				new GlyphsVisitors[] { 
//					new TranslateGlyphsVerticalRandomVisitor(1.0D), 
//					new OverlapGlyphsUsingShapeVisitor(3.0D), 
//					new TranslateAllToRandomPointVisitor() }
//				);
		SingleColorGenerator lineColor = new SingleColorGenerator(new Color(50, 50, 50));
		//SingleColorGenerator lineColor = new SingleColorGenerator(new Color(163, 163, 163));
		SingleColorGenerator color = new SingleColorGenerator(new Color(50, 50, 50));
		TextDecorator decorator1 = new LineTextDecorator(new Integer(1), lineColor, AlphaComposite.SRC_OVER);
		TextDecorator[] decorators = new TextDecorator[] { decorator1 };
		TextPaster textPaster = new DecoratedRandomTextPaster(textMinLength, textMaxLength, color, decorators);
		return textPaster;
	}
	
    /* 背景生成器 */
    @Bean
    public BackgroundGenerator backgroundGenerator(){
        return new UniColorBackgroundGenerator(backgroundWidth,backgroundHeight,parseHexColor(backgroundColor));
        /* 生成效果像蛇皮 */
        //return new EllipseBackgroundGenerator(backgroundWidth,backgroundHeight);
        /* 背景由紅黃綠三種顏色組成 */
        //return new FunkyBackgroundGenerator(backgroundWidth,backgroundHeight);
        /* 背景為漸變色 */
        //return new GradientBackgroundGenerator(backgroundWidth,backgroundHeight,new Color(255,0,0),new Color(0,255,0));
        //return new MultipleShapeBackgroundGenerator(backgroundWidth,backgroundHeight);
    }	
	
	/*
	 * 將CSS使用的16進位表示法的色碼，轉換為java.awt.Color物件
	 */
	private Color parseHexColor(String hexColor) {
		Integer val = Integer.parseInt(hexColor.substring(1),16);
		Color color = new Color(val); 
		return color;
	}
	
}
