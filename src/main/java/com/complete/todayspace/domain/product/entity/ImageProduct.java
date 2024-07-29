package com.complete.todayspace.domain.product.entity;

import com.complete.todayspace.global.entity.CreatedTimestamp;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "table_image_product")
@Getter
@NoArgsConstructor
public class ImageProduct extends CreatedTimestamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long orders;

    @Column
    private String filePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    public ImageProduct(String fileUrls, Product saveProduct) {
        this.filePath = fileUrls;
        this.product = saveProduct;
    }
}
