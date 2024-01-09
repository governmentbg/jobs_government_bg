package indexbg.pjobs.bean;


import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.imageio.ImageIO;
import javax.inject.Named;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.indexbg.system.utils.JSFUtils;


@Named
@RequestScoped
public class PaintClass {

	//private static final Logger LOGGER = Logger.getLogger(PaintClass.class);
	
	private StreamedContent graphicText;
	
	@PostConstruct
	public void paintCptcha() {
//		if (LOGGER.isDebugEnabled()) {
//			LOGGER.debug("paintCptcha=====>");
//		}
		
//        Integer high = 9999;
//        Integer low = 1000;
//        Random generator = new Random();
//        Integer digits = generator.nextInt(high - low + 1) + low;

        try {
		
		String symbols = "qwertyuiopasdfghjklzxcvbnm1234567890";
		StringBuilder captcha = new StringBuilder();
        Integer high = symbols.length();
        Random generator = new Random();

        for(int i=0;i<=6;i++){
        	captcha.append(symbols.charAt(generator.nextInt(high)));
        }
        
        JSFUtils.getFacesContext().getExternalContext().getSessionMap().put("myCpatchaIgg", captcha);
        
        BufferedImage img = new BufferedImage(150,40,BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = img.createGraphics();
        graphics2D.setBackground(new Color(230, 230, 230));
        graphics2D.setColor(new Color(0,0,0));
        graphics2D.clearRect(0,0,150,40);
        graphics2D.setFont(new Font("Serif", Font.TRUETYPE_FONT, 25));
        graphics2D.drawString(captcha.toString(), 30, 30);
        
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(img,"png",os);
        
        graphicText = DefaultStreamedContent.builder().contentType("image/png").name("alabala").stream(() -> new ByteArrayInputStream(os.toByteArray())).build();
        
      //Graphic Text
//        BufferedImage bufferedImg = new BufferedImage(100, 25, BufferedImage.TYPE_INT_RGB);
//        Graphics2D g2 = bufferedImg.createGraphics();
//        g2.drawString("This is a text", 0, 10);
//        ByteArrayOutputStream os = new ByteArrayOutputStream();
//        ImageIO.write(bufferedImg, "png", os);
//        graphicText = new DefaultStreamedContent(new ByteArrayInputStream(os.toByteArray()), "image/png");
        
        
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

	public StreamedContent getGraphicText() {
		return graphicText;
	}

	public void setGraphicText(StreamedContent graphicText) {
		this.graphicText = graphicText;
	}

	
}
