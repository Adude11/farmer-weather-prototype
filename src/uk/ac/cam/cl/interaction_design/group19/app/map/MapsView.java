package uk.ac.cam.cl.interaction_design.group19.app.map;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.sql.SQLOutput;
import java.text.Format;
import java.text.MessageFormat;
import java.util.logging.SocketHandler;
import javax.imageio.ImageIO;
import javax.json.JsonArray;
//import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonStructure;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.json.JSONArray;
import org.json.JSONObject;
import uk.ac.cam.cl.interaction_design.group19.app.MetOfficeAPI;
import java.awt.Graphics2D;

public class MapsView extends JPanel {

    private static final int DESIRED_WIDTH = 310;
    private static final int DESIRED_HEIGHT = 420;

    public MapsView() throws IOException {

        // Get the images
        String jsonURL = MetOfficeAPI.BASE_URL + MetOfficeAPI.IMAGE_PATH + "?key=" + MetOfficeAPI.KEY;
        URL website = new URL(jsonURL);

        BufferedReader br = new BufferedReader(new InputStreamReader(website.openStream()));
        String jsonString = br.readLine();
        JSONObject jsonObj = new JSONObject(jsonString);

        JSONObject base = (JSONObject) jsonObj.get("Layers");

        JSONObject baseUrlJson = (JSONObject) base.get("BaseUrl");
        String baseUrl = (String) baseUrlJson.get("$");

        JSONArray layersJson = (JSONArray) base.get("Layer");
        JSONObject satelliteVis = (JSONObject) layersJson.get(2);
        JSONObject rainfall = (JSONObject) layersJson.get(3);

        JSONObject serviceSat = (JSONObject) satelliteVis.get("Service");
        JSONObject serviceRai = (JSONObject) rainfall.get("Service");

        JSONObject timesSat = (JSONObject) serviceSat.get("Times");
        JSONObject timesRai = (JSONObject) serviceRai.get("Times");

        JSONArray timeArraySat = (JSONArray) timesSat.get("Time");
        JSONArray timeArrayRai = (JSONArray) timesRai.get("Time");

        String timeSat = (String) timeArraySat.get(0);
        String timeRai = (String) timeArrayRai.get(0);

        // Draw Satellite image
        Object[] args = {"SATELLITE_Visible_N_Section", "png", timeSat, MetOfficeAPI.KEY};
        MessageFormat fmt = new MessageFormat("http://datapoint.metoffice.gov.uk/public/data/" +
                "layer/wxobs/{0}/{1}?TIME={2}Z&key={3}");
        String imageURL = fmt.format(args);

        BufferedImage image = ImageIO.read(new URL(imageURL));
        BufferedImage out = image.getSubimage(100, 40, image.getWidth()-190, image.getHeight()-80);

        // Draw Rain
        Object[] args2 = {"RADAR_UK_Composite_Highres", "png", timeRai, MetOfficeAPI.KEY};
        MessageFormat fmt2 = new MessageFormat("http://datapoint.metoffice.gov.uk/public/data/" +
                "layer/wxobs/{0}/{1}?TIME={2}Z&key={3}");
        String imageURL2 = fmt2.format(args2);

        BufferedImage image2 = ImageIO.read(new URL(imageURL2));
        BufferedImage out2 = image2.getSubimage(100, 40, image2.getWidth()-190, image2.getHeight()-80);

        BufferedImage newImg = new BufferedImage(DESIRED_WIDTH, DESIRED_HEIGHT, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = newImg.createGraphics();

        // Clear the image (optional)
        g.setComposite(AlphaComposite.Clear);
        g.fillRect(0,0, DESIRED_WIDTH, DESIRED_HEIGHT);

        // Draw the background image
        g.setComposite(AlphaComposite.SrcOver);
        g.drawImage(out, 0, 0, null);

        // Draw the overlay image
        float alpha = 1.0f;
        g.setComposite(AlphaComposite.SrcOver.derive(alpha));
        g.drawImage(out2, 0, 0, null);
        g.dispose();

        JLabel imageLabel2 = new JLabel(new ImageIcon(newImg));

        this.add(imageLabel2);
        this.setLocation(200, 200);
        this.setVisible(true);
    }
}