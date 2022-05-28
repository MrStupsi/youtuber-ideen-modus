package de.hglabor.youtuberideen.bastighg;

import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MineskinUtil {

    private MineskinUtil() {
        throw new IllegalStateException("Created instance of Util class");
    }

    /**
     *
     * <p>Generates a new skin using Mineskin's RESTful API.</p>
     *
     * <p><strong>This operation is Thread-Blocking!</strong></p>
     *
     * @param skinImage The Skin Image to sign and upload
     * @return The API Response from Mineskin as a {@link JSONObject}
     * @throws IOException if at any time an {@link IOException} occurs
     */
    public static JSONObject mineskinUpload(BufferedImage skinImage) throws IOException {
        final byte[] imageBuffer; // Declare the buffer for the raw image bytes

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(skinImage, "png", baos); // Write the image in PNG format to the OutputStream

            baos.flush();
            imageBuffer = baos.toByteArray(); // Initialize the buffer with the raw PNG image data
        }

        CloseableHttpClient client = HttpClientBuilder.create().build(); // Create a new HttpClient

        HttpPost post = new HttpPost("https://api.mineskin.org/generate/upload?visibility=1"); // Create the Empty POST request

        // Here we write the image to the Request payload
        post.setEntity(
                EntityBuilder.create()
                        // Mineskin looks for the parameter "file". The data is a PNG image. We don't need to supply a filename.
                        .setContentType(ContentType.IMAGE_PNG)
                        .setBinary(imageBuffer)
                        .build()
        );

        // Execute the POST request and parse the result as a JSON object
        JSONObject object = null;
        try {
            object = (JSONObject) new JSONParser().parse(EntityUtils.toString(client.execute(post).getEntity()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        client.close();

        return object;
    }
}