package com.sobow.shopping.sandbox;

import com.sobow.shopping.domain.category.Category;
import com.sobow.shopping.domain.category.CategoryRepository;
import com.sobow.shopping.domain.image.Image;
import com.sobow.shopping.domain.image.ImageRepository;
import com.sobow.shopping.domain.product.Product;
import com.sobow.shopping.domain.product.ProductRepository;
import com.sobow.shopping.utils.TestFixtures;
import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.sql.Blob;
import java.sql.SQLException;
import javax.sql.rowset.serial.SerialBlob;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.io.ClassPathResource;

@DataJpaTest
@Disabled("sandbox only – excluded from build")
public class SandboxTests {
    
    @Autowired
    private ImageRepository imageRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Test
    public void sandboxTest1() {
        var productFixtures = new TestFixtures().withCategoryAndProductAndImageNullIds();
        
        Product product = productFixtures.productEntity();
        
        Image image = productFixtures.imageEntity();
        
        Category category = product.getCategory();
        
        Category saved = categoryRepository.save(category);
        
        Product found = productRepository.findById(saved.getId()).orElseThrow(() -> new EntityNotFoundException());
    }
    
    @Test
    public void sandboxTest2() throws IOException, SQLException {
        
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
        Image image = new Image("doge.png", "image/png", blob);
        
        // Image saved = imageRepository.save(image);
    }
}
