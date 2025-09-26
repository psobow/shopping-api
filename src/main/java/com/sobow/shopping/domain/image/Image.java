package com.sobow.shopping.domain.image;

import com.sobow.shopping.domain.product.Product;
import com.sobow.shopping.exceptions.ImageProcessingException;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.sql.Blob;
import java.util.Objects;
import javax.sql.rowset.serial.SerialBlob;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "images")
public class Image {
    
    // ---- Construction (builder) ----------------------------
    @Builder
    public Image(String fileName, String fileType, Blob file) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.file = file;
    }
    
    // ---- Identifier & Basic columns ------------------------
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String fileName;
    
    @Column(nullable = false)
    private String fileType;
    
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(nullable = false)
    private Blob file;
    
    // ---- Associations --------------------------------------
    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    // ---- Domain methods ------------------------------------
    public void updateFrom(MultipartFile patch) {
        Objects.requireNonNull(patch, "Image patch must not be null");
        if (patch.getOriginalFilename() != null) this.fileName = patch.getOriginalFilename();
        if (patch.getContentType() != null) this.fileType = patch.getContentType();
        try {
            if (patch.getBytes() != null) this.file = new SerialBlob(patch.getBytes());
        } catch (Exception e) {
            throw new ImageProcessingException("Failed to process image file: " + patch.getOriginalFilename(), e);
        }
    }
    
    public void linkTo(Product product) {
        this.product = product;
    }
    
    // ---- Derived / non-persistent --------------------------
    public String getDownloadUrl() {
        return "/api/images/" + this.id;
    }
}
