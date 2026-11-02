package com.doritech.tmsservice.serviceImpl;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.doritech.tmsservice.service.CertificateService;
import com.lowagie.text.Document;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfWriter;

@Service
public class CertificatePdfServiceImpl implements CertificateService {

	// ---------- Reference size of the template these coordinates were measured on ----------
	private static final double REF_WIDTH = 2000.0;

	// ---------- Coordinates (pixels on the 2000 x 1414 template) ----------
	private static final int CENTER_X = 990; // visual center of the design

	private static final int NAME_Y = 565; // participant name baseline
	private static final int NAME_MAX_W = 1100;
	private static final int NAME_FONT = 60;

	private static final int TEST_Y = 632; // test name baseline (old "Company Name." row)
	private static final int TEST_MAX_W = 900;
	private static final int TEST_FONT = 56;

	private static final int DATE_X = 140; // right after the "Date:" label (ends at x~122)
	private static final int DATE_Y = 1170; // same baseline as "Date:"

	private static final int CERT_NO_X = 35;
	private static final int CERT_NO_Y = 1225;

	// Set to true only if you have NOT cleaned the "Name:" / "Company Name." text
	// out of the PNG.
	private static final boolean COVER_PLACEHOLDERS = false;

	@Override
	public byte[] generateCertificate(String userName, String testName, String certificateNumber, String issueDate) {

		try {
			ClassPathResource resource = new ClassPathResource("certificates/certificate-template.png");

			BufferedImage img;
			try (InputStream in = resource.getInputStream()) {
				img = ImageIO.read(in);
			}
			if (img == null) {
				throw new RuntimeException("Invalid certificate template");
			}

			// If the PNG ever has a different size, everything scales with it.
			double s = img.getWidth() / REF_WIDTH;

			Graphics2D g = img.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

			if (COVER_PLACEHOLDERS) {
				coverPlaceholders(g, img, s);
			}

			// 1. PARTICIPANT NAME
			g.setColor(Color.BLACK);
			g.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, px(NAME_FONT, s)));
			drawCenteredText(g, userName, px(CENTER_X, s), px(NAME_Y, s), px(NAME_MAX_W, s));

			// 2. TEST NAME
			g.setColor(new Color(160, 95, 20));
			g.setFont(new Font("Serif", Font.BOLD, px(TEST_FONT, s)));
			drawCenteredText(g, testName, px(CENTER_X, s), px(TEST_Y, s), px(TEST_MAX_W, s));

			// 3. ISSUE DATE (next to the "Date:" label)
			g.setColor(Color.BLACK);
			g.setFont(new Font("Serif", Font.PLAIN, px(34, s)));
			g.drawString(issueDate == null ? "" : issueDate, px(DATE_X, s), px(DATE_Y, s));

			// 4. CERTIFICATE NUMBER (below the date)
			g.setFont(new Font("Serif", Font.PLAIN, px(23, s)));
			g.drawString("Certificate No: " + certificateNumber, px(CERT_NO_X, s), px(CERT_NO_Y, s));

			g.dispose();

			// 5. IMAGE -> PDF
			ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
			ImageIO.write(img, "png", imageStream);

			ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();
			Document document = new Document(PageSize.A4.rotate(), 0, 0, 0, 0);
			PdfWriter.getInstance(document, pdfStream);
			document.open();

			Image pdfImage = Image.getInstance(imageStream.toByteArray());
			pdfImage.scaleToFit(document.getPageSize().getWidth(), document.getPageSize().getHeight());
			pdfImage.setAbsolutePosition((document.getPageSize().getWidth() - pdfImage.getScaledWidth()) / 2,
					(document.getPageSize().getHeight() - pdfImage.getScaledHeight()) / 2);

			document.add(pdfImage);
			document.close();

			return pdfStream.toByteArray();

		} catch (Exception e) {
			throw new RuntimeException("Certificate generation failed: " + e.getMessage(), e);
		}
	}

	// Scales a reference coordinate to the real image size.
	private int px(int value, double scale) {
		return (int) Math.round(value * scale);
	}

	// Temporary fallback: hides "Name:" and "Company Name." with a flat patch of
	// background color.
	// It will slightly cut the faint watermark logo, so cleaning the PNG is the
	// better fix.
	private void coverPlaceholders(Graphics2D g, BufferedImage img, double s) {
		Color bg = new Color(img.getRGB(px(450, s), px(700, s)));
		g.setColor(bg);
		g.fillRect(px(615, s), px(485, s), px(500, s), px(75, s)); // "Name:"
		g.fillRect(px(615, s), px(565, s), px(500, s), px(80, s)); // "Company Name."
	}

	// Centers text on centerX and shrinks the font until it fits maxWidth.
	private void drawCenteredText(Graphics2D g, String text, int centerX, int y, int maxWidth) {
		if (text == null) {
			text = "";
		}
		Font font = g.getFont();
		while (g.getFontMetrics().stringWidth(text) > maxWidth && font.getSize() > 18) {
			font = font.deriveFont((float) font.getSize() - 2);
			g.setFont(font);
		}
		int textWidth = g.getFontMetrics().stringWidth(text);
		g.drawString(text, centerX - textWidth / 2, y);
	}
}