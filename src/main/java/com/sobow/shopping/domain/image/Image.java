package com.sobow.shopping.domain.image;

import com.sobow.shopping.domain.product.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.sql.Blob;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    
    // ---- Identifier ----------------------------------------
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // ---- Basic columns -------------------------------------
    @Column(nullable = false)
    private String fileName;
    
    @Column(nullable = false)
    private String fileType;
    
    @Lob
    @Column(nullable = false)
    private Blob file;
    
    // ---- Associations --------------------------------------
    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    // ---- Domain methods ------------------------------------
    public void linkTo(Product product) {
        this.product = product;
    }
    
    // ---- Derived / non-persistent --------------------------
    public String getDownloadUrl() {
        return "/api/images/" + this.id;
    }
    
    // ---- Setter methods ------------------------------------
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
    
    public void setFile(Blob file) {
        this.file = file;
    }
}
