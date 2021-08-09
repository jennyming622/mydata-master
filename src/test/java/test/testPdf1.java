package test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;

public class testPdf1 {

	public static void main(String[] args) throws FileNotFoundException, DocumentException {
		//页面大小  
		//Rectangle rect = new Rectangle(PageSize.A4.rotate());  
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
		  
		//页边空白  
		doc.setMargins(10, 20, 30, 40);  
		  
		doc.open();  
		doc.add(new Paragraph("Hello World"));  
		
		doc.close();
	}

}
