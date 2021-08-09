package test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

public class testPdf {

	public static void main(String[] args) throws FileNotFoundException, DocumentException {
		Document document = new Document();  
		//Step 2—Get a PdfWriter instance.  
		PdfWriter.getInstance(document, new FileOutputStream("createSamplePDF.pdf"));  
		//Step 3—Open the Document.  
		document.open();  
		//Step 4—Add content.  
		document.add(new Paragraph("Hello World"));  
		//Step 5—Close the Document.  
		document.close();  
	}

}
