package test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;

public class testpdf2 {

	public static void main(String[] args) throws DocumentException, IOException {
		//页面大小
		Rectangle rect = new Rectangle(PageSize.A4); 
		//页面背景色  
		rect.setBackgroundColor(BaseColor.WHITE);  
		  
		Document doc = new Document(rect);  
		  
		PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream("createSamplePDF.pdf"));  
		  
		//PDF版本(默认1.4)  
		writer.setPdfVersion(PdfWriter.PDF_VERSION_1_2);  
		  
		//文档属性  
		doc.addTitle("Title@sample");  
		doc.addAuthor("Author@rensanning");  
		doc.addSubject("Subject@iText sample");  
		doc.addKeywords("Keywords@iText");  
		doc.addCreator("Creator@iText");  
		  
		//页边空白 左 右 上 下
		doc.setMargins(10, 20, 30, 40);  
		  
		doc.open();  
		doc.add(new Paragraph("Hello World"));  
		
		doc.close();
		
		
		//图片水印  
		PdfReader reader = new PdfReader("createSamplePDF.pdf");  
		PdfStamper stamp = new PdfStamper(reader, new FileOutputStream("createSamplePDF2.pdf"));  
		addWatermark(stamp,"國家發展委員會");
		stamp.close();
		reader.close();
	}
	 private static void addWatermark(PdfStamper pdfStamper
	          , String waterMarkName) {
	            PdfContentByte content = null;
	            BaseFont base = null;
	            Rectangle pageRect = null;
	            PdfGState gs = new PdfGState();
	            try {
	                // 设置字体
	            base = BaseFont.createFont("STSongStd-Light", 
	"UniGB-UCS2-H",
	        BaseFont.NOT_EMBEDDED);
	            } catch (DocumentException e) {
	                e.printStackTrace();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	            try {
	                if (base == null || pdfStamper == null) {
	                    return;
	                }
	                // 设置透明度为0.4
	                gs.setFillOpacity(0.4f);
	                gs.setStrokeOpacity(0.4f);
	                int toPage = pdfStamper.getReader().getNumberOfPages();
	                for (int i = 1; i <= toPage; i++) {
	                    pageRect = pdfStamper.getReader().
	                       getPageSizeWithRotation(i);
	                    // 计算水印X,Y坐标
	                    float x = pageRect.getWidth() / 2;
	                    float y = pageRect.getHeight() / 2;
	                    //获得PDF最顶层
	                    content = pdfStamper.getOverContent(i);
	                    content.saveState();
	                    // set Transparency
	                    content.setGState(gs);
	                    content.beginText();
	                    content.setColorFill(BaseColor.GRAY);
	                    content.setFontAndSize(base, 60);
	                    // 水印文字成45度角倾斜
	                    content.showTextAligned(Element.ALIGN_CENTER
	                            , waterMarkName, x,
	                            y, 45);
	                    content.endText(); 
	                }
	            } catch (Exception ex) {
	                ex.printStackTrace();
	            } finally {
	                content = null;
	                base = null;
	                pageRect = null;
	            }
	        }
}
