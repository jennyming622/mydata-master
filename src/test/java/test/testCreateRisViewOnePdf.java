package test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;

import tw.gov.ndc.emsg.mydata.gspclient.bean.UserInfoEntity;
import tw.gov.ndc.emsg.mydata.web.PersonalRestController.TableHeaderForRisReviewOne;

public class testCreateRisViewOnePdf {

	public static void main(String[] args) throws DocumentException, IOException {
		// 個人戶籍資料查詢 ris_review_one
		String responeStr = "{\"httpCode\":\"200\",\"httpMessage\":\"OK\",\"rdCode\":\"RS7009\",\"rdMessage\":\"查詢作業完成\",\"responseData\":{\"personHouseholdData12\":{\"marriage_code\":\"有偶\",\"education_mark\":\"大學畢業\",\"person_id\":\"A999999999\",\"mother_name\":\"N/A\",\"query_note\":\"\",\"id_card_apply_code\":\"換證\",\"incident_yyymmdd\":\"\",\"move_in_yyymmdd\":\"0970818\",\"father_name\":\"N/A\",\"living_style_code\":\"無\",\"living_race_type\":\"\",\"special_mark\":\"非特殊人口\",\"householdAddress\":{\"neighbor\":\"001\",\"neighbor_char\":\"鄰\",\"street_doorplate\":\"大竹北路99999號\",\"village\":\"宏竹里\",\"from_site_id\":\"桃園市蘆竹區\"},\"foster_parent_mark\":\"無養父母\",\"special_inci_code\":\"\",\"register_content\":\"N/A\",\"id_card_apply_yyymmdd\":\"N/A\","
				+ "\"military_code\":\"國兵除役\",\"foster_mother_name\":\"\",\"seal_apply_code\":\"未登記印鑑\",\"birth_place_code\":\"臺灣省桃園縣\",\"seal_apply_yyymmdd\":\"0000000\",\"foster_father_name\":\"\",\"personal_mark\":\"現住人口\",\"spouse_name\":\"李匿名\",\"birth_order_sex\":\"次男\",\"person_name\":\"林匿名\",\"birth_yyymmdd\":\"0691010\",\"household_head_id\":\"H223344556\",\"household_id\":\"N/A\"}}}\n";
		ObjectMapper om = new ObjectMapper();
		String filename = "/Users/mac/Desktop/API.7QovE2Gev6decTmp.pdf";
		String filename1 = "/Users/mac/Desktop/API.7QovE2Gev6dec.pdf";
		BaseFont bfChinese = BaseFont.createFont("kaiu.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
		Font chineseFont = new Font(bfChinese, 12, Font.NORMAL);
		Font chineseFontTitle = new Font(bfChinese, 16, Font.NORMAL);
		Document document = new Document(PageSize.A4, 20, 20, 70, 30);
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
		TableHeaderForRisReviewOne event = new TableHeaderForRisReviewOne();
		writer.setPageEvent(event);
		Map<String, String> dataMap = new HashMap<String, String>();
		if (StringUtils.isNotEmpty(responeStr)) {
			JsonNode json2 = om.readTree(responeStr);
			JsonNode responseData = json2.get("responseData");
			JsonNode personHouseholdDataC = responseData.get("personHouseholdData12");
			JsonNode householdAddress = personHouseholdDataC.get("householdAddress");
			// spouse_name
			String spouse_name = personHouseholdDataC.get("spouse_name").asText();
			dataMap.put("spouse_name", spouse_name);
			String person_id = personHouseholdDataC.get("person_id").asText();
			dataMap.put("person_id", person_id);
			/*
			 * String household_id = personHouseholdDataC.get("household_id").asText();
			 * dataMap.put("household_id", household_id);
			 */
			String person_name = personHouseholdDataC.get("person_name").asText();
			dataMap.put("person_name", person_name);
			String birth_yyymmdd = personHouseholdDataC.get("birth_yyymmdd").asText();
			dataMap.put("birth_yyymmdd", birth_yyymmdd);
			String birth_place_code = personHouseholdDataC.get("birth_place_code").asText();
			dataMap.put("birth_place_code", birth_place_code);
			String marriage_code = personHouseholdDataC.get("marriage_code").asText();
			dataMap.put("marriage_code", marriage_code);
			String move_in_yyymmdd = personHouseholdDataC.get("move_in_yyymmdd").asText();
			dataMap.put("move_in_yyymmdd", move_in_yyymmdd);

			// 戶籍地址
			// 臺灣省苗栗縣頭份市
			String from_site_id = householdAddress.get("from_site_id").asText();
			// 千秋里
			String village = householdAddress.get("village").asText();
			// 0009 鄰
			String neighbor = householdAddress.get("neighbor").asText();
			String neighbor_char = householdAddress.get("neighbor_char").asText();
			// 庸街１５５巷６之１號
			String street_doorplate = householdAddress.get("street_doorplate").asText();
			dataMap.put("address", from_site_id + village + neighbor + neighbor_char + street_doorplate);
			// 教育程度註記(空白)
			String education_mark = personHouseholdDataC.get("education_mark").asText();
			dataMap.put("education_mark", education_mark);
			// 個人記事(空白)
			String query_note = personHouseholdDataC.get("query_note").asText();
			dataMap.put("query_note", query_note);
			// 註記
		}
		document.open();
		if (dataMap != null && dataMap.size() > 0) {
			bfChinese = BaseFont.createFont("kaiu.ttf", BaseFont.IDENTITY_H,
					BaseFont.EMBEDDED);
			// document.add(new Phrase(responseString,chineseFont));
			PdfPTable table = new PdfPTable(2);
			table.setSplitLate(false);
			table.setSplitRows(false);
			PdfPCell cell;
			/*
			 * cell = new PdfPCell(new Phrase("戶號", chineseFont)); table.addCell(cell); cell
			 * = new PdfPCell(new Phrase(dataMap.get("household_id"), chineseFont));
			 * table.addCell(cell);
			 */

			cell = new PdfPCell(new Phrase("身分證統一編號", chineseFont));
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(dataMap.get("person_id"), chineseFont));
			table.addCell(cell);

			cell = new PdfPCell(new Phrase("姓名", chineseFont));
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(dataMap.get("person_name"), chineseFont));
			table.addCell(cell);

			if (dataMap.get("spouse_name") != null
					&& dataMap.get("spouse_name").toString().length() > 0) {
				cell = new PdfPCell(new Phrase("配偶姓名", chineseFont));
				table.addCell(cell);
				cell = new PdfPCell(new Phrase(dataMap.get("spouse_name"), chineseFont));
				table.addCell(cell);
			}
			/*
			 * cell = new PdfPCell(new Phrase("父親姓名", chineseFont)); table.addCell(cell);
			 * cell = new PdfPCell(new Phrase(dataMap.get("father_name"), chineseFont));
			 * table.addCell(cell);
			 * 
			 * cell = new PdfPCell(new Phrase("母親姓名", chineseFont)); table.addCell(cell);
			 * cell = new PdfPCell(new Phrase(dataMap.get("mother_name"), chineseFont));
			 * table.addCell(cell);
			 */

			cell = new PdfPCell(new Phrase("出生日期", chineseFont));
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(dataMap.get("birth_yyymmdd"), chineseFont));
			table.addCell(cell);

			cell = new PdfPCell(new Phrase("出生地", chineseFont));
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(dataMap.get("birth_place_code"), chineseFont));
			table.addCell(cell);

			cell = new PdfPCell(new Phrase("婚姻狀況", chineseFont));
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(dataMap.get("marriage_code"), chineseFont));
			table.addCell(cell);

			/*
			 * cell = new PdfPCell(new Phrase("身分證請領日期", chineseFont)); table.addCell(cell);
			 * cell = new PdfPCell(new Phrase(dataMap.get("id_card_apply_yyymmdd"),
			 * chineseFont)); table.addCell(cell);
			 */

			cell = new PdfPCell(new Phrase("遷入日期", chineseFont));
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(dataMap.get("move_in_yyymmdd"), chineseFont));
			table.addCell(cell);

			cell = new PdfPCell(new Phrase("戶籍地址", chineseFont));
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(dataMap.get("address"), chineseFont));
			table.addCell(cell);

			cell = new PdfPCell(new Phrase("教育程度註記", chineseFont));
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(dataMap.get("education_mark"), chineseFont));
			table.addCell(cell);

			cell = new PdfPCell(new Phrase("個人註記", chineseFont));
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(dataMap.get("query_note"), chineseFont));
			table.addCell(cell);

			/*
			 * cell = new PdfPCell(new Phrase("註記", chineseFont)); table.addCell(cell); cell
			 * = new PdfPCell(new Phrase(dataMap.get("register_content"), chineseFont));
			 * table.addCell(cell);
			 */
			document.add(table);
		} else {
			document.add(new Phrase("查詢失敗！", chineseFont));
		}
		document.close();
		System.out.println("==文字水印 Start==:");
		System.out.println("==filename==:"+filename);
		System.out.println("==filename1==:"+filename1);
		// 文字水印
		PdfReader reader = new PdfReader(filename);
		PdfStamper stamp = new PdfStamper(reader, new FileOutputStream(filename1));
		addWatermark(stamp, "內政部戶政司");
		stamp.close();
		reader.close();
		System.out.println("==文字水印 End==:");
	}

	private static void addWatermark(PdfStamper pdfStamper, String waterMarkName) {
		PdfContentByte content = null;
		BaseFont base = null;
		Rectangle pageRect = null;
		PdfGState gs = new PdfGState();
		try {
			// 設置字體
			base = BaseFont.createFont("kaiu.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			if (base == null || pdfStamper == null) {
				return;
			}
			// passwd
			//pdfStamper.setEncryption(passwd.getBytes(), passwd.getBytes(), PdfWriter.ALLOW_PRINTING, PdfWriter.ENCRYPTION_AES_128 | PdfWriter.DO_NOT_ENCRYPT_METADATA);

			// 設置透明度為0.4
			gs.setFillOpacity(0.4f);
			gs.setStrokeOpacity(0.4f);
			int toPage = pdfStamper.getReader().getNumberOfPages();
			for (int i = 1; i <= toPage; i++) {
				pageRect = pdfStamper.getReader().getPageSizeWithRotation(i);
				// 计算水印X,Y座標
				float x = pageRect.getWidth() / 2;
				float y = pageRect.getHeight() / 2;
				// 获得PDF最顶层
				content = pdfStamper.getOverContent(i);
				content.saveState();
				// set Transparency
				content.setGState(gs);
				content.beginText();
				content.setColorFill(BaseColor.GRAY);
				content.setFontAndSize(base, 60);
				// 水印文字成45度角倾斜
				content.showTextAligned(Element.ALIGN_CENTER, waterMarkName, x, y, 45);
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
