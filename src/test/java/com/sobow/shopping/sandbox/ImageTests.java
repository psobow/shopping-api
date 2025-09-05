package com.sobow.shopping.sandbox;

import com.sobow.shopping.domain.Image;
import com.sobow.shopping.repositories.ImageRepository;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.sql.Blob;
import java.sql.SQLException;
import javax.sql.rowset.serial.SerialBlob;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.io.ClassPathResource;

@DataJpaTest
public class ImageTests {
    
    @Autowired
    private ImageRepository imageRepository;
    
    @Test
    public void test() throws IOException, SQLException {
        
        // Load the file from resources
        ClassPathResource resource = new ClassPathResource("doge.png");
        byte[] bytes = Files.readAllBytes(resource.getFile().toPath());
        
        // Create Blob for DB
        Blob blob = new SerialBlob(bytes);
        
        long length = blob.length();
        
        // read blob as bytes
        byte[] allBytes = blob.getBytes(1, (int) length);
        
        // stream blob content
        InputStream stream = blob.getBinaryStream();
        
        // Create Image entity
        Image image = new Image();
        image.setFileName("doge.png");
        image.setFileType("image/png");
        image.setImage(blob);
        
        Image saved = imageRepository.save(image);
    }
}
