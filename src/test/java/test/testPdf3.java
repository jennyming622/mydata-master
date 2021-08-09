package test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;

public class testPdf3 {
	private static SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static void main(String[] args) throws DocumentException, IOException {
		//页面大小
		Rectangle rect = new Rectangle(PageSize.A4); 
		//页面背景色  
		rect.setBackgroundColor(BaseColor.WHITE);  
		  
		Document doc = new Document(rect);  
		  
		PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream("createSamplePDF.pdf"));  
		TableHeader event = new TableHeader();
		writer.setPageEvent(event);
		
		//PDF版本(默认1.4)  
		writer.setPdfVersion(PdfWriter.PDF_VERSION_1_2);  
		
		//文档属性  
		doc.addTitle("Title@sample");  
		doc.addAuthor("Author@rensanning");  
		doc.addSubject("Subject@iText sample");  
		doc.addKeywords("Keywords@iText");  
		doc.addCreator("Creator@iText");  
		  
		//页边空白 左 右 上 下
		doc.setMargins(20, 20, 70, 20);  
		  
		doc.open();  
		//doc.add(new Paragraph("Hello World"));  
		
		BaseFont bfChinese = BaseFont.createFont( "kaiu.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED );
		Font chineseFont = new Font(bfChinese, 14, Font.NORMAL);		
        //document.add(new Phrase(responseString,chineseFont));
		PdfPTable table = new PdfPTable(2);
		table.setSplitLate(false);
		PdfPCell cell;
		for(int i=0;i<30;i++) {
			cell = new PdfPCell(new Phrase("國民身分證統一編號",chineseFont));
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("P122883855",chineseFont));
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase("父親姓名",chineseFont));
			table.addCell(cell); 
			cell = new PdfPCell(new Phrase("薛東永",chineseFont));
			table.addCell(cell);
			 
			cell = new PdfPCell(new Phrase("母親姓名",chineseFont));
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("薛黃桂美",chineseFont)); 
			table.addCell(cell);
		}
	    doc.add(table);
		doc.close();
		 
		//图片水印  
		/*PdfReader reader = new PdfReader("createSamplePDF.pdf");  
		PdfStamper stamp = new PdfStamper(reader, new FileOutputStream("createSamplePDF2.pdf"));  
		addWatermark(stamp,"內政部戶政司");
		stamp.close();
		reader.close();*/
	}
	 private static void addWatermark(PdfStamper pdfStamper
	          , String waterMarkName) {
	            PdfContentByte content = null;
	            BaseFont base = null;
	            Rectangle pageRect = null;
	            PdfGState gs = new PdfGState();
	            try {
	                // 设置字体
	            base = BaseFont.createFont("STSongStd-Light", "UniGB-UCS2-H",  BaseFont.NOT_EMBEDDED);
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
	 
	 
		public static class TableHeader extends PdfPageEventHelper {
			public void onEndPage(PdfWriter writer, Document document) {
				try {					
			        PdfContentByte cb = writer.getDirectContent();  
			        
			        Image imgSoc = Image.getInstance("/Users/mac/Desktop/Eclipse_Work/workspace_oxygen/mydata/WebContent/resources/dist/img/ris-logo.png");
					imgSoc.scaleToFit(100, 300);
					imgSoc.setAbsolutePosition(100, 780);
					cb.addImage(imgSoc);
					
			        cb.saveState();  
			        cb.beginText();
			        
			        BaseFont bf = null;  
			        try {  
			            bf = BaseFont.createFont("kaiu.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);  
			        } catch (Exception e) {  
			            e.printStackTrace();  
			        }  
			          
			          
			        //Header  
			        float x = document.right(20); 
			        float y = document.top(-20);  
			          
			        //左
					/*Image imgSoc = Image.getInstance("/Users/mac/Desktop/Eclipse_Work/workspace_oxygen/mydata/WebContent/resources/dist/img/ris-logo.png");
					imgSoc.scaleToFit(100, 300);
					imgSoc.setAbsolutePosition(100, 780);
					cb.addImage(imgSoc);*/
					
					//中
					cb.setFontAndSize(bf, 25);
					String title = "戶籍資料查詢";
			        cb.showTextAligned(PdfContentByte.ALIGN_CENTER,  
			        		title, 
			        		(document.right() + document.left())/2,y, 0);
					
			        //右  
			        y = document.top(-10);  
			        cb.setFontAndSize(bf, 8);  
					String textstr = "報表產製時間："+sdf1.format(new Date());
			        cb.showTextAligned(PdfContentByte.ALIGN_RIGHT,  
			        						textstr,  
			                           x, y, 0);  
			        cb.endText();  
			        cb.restoreState();  
				} catch (Exception de) {
					throw new ExceptionConverter(de);
				}
			}
		}
}
