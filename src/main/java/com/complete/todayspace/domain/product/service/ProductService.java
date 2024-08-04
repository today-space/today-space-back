package com.complete.todayspace.domain.product.service;

import com.complete.todayspace.domain.common.S3Provider;
import com.complete.todayspace.domain.product.dto.*;
import com.complete.todayspace.domain.product.entity.Address;
import com.complete.todayspace.domain.product.entity.ImageProduct;
import com.complete.todayspace.domain.product.entity.Product;
import com.complete.todayspace.domain.product.repository.ImageProductRepository;
import com.complete.todayspace.domain.product.repository.ProductRepository;
import com.complete.todayspace.domain.user.entity.User;
import com.complete.todayspace.domain.wish.repository.WishRepository;
import com.complete.todayspace.global.exception.CustomException;
import com.complete.todayspace.global.exception.ErrorCode;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ImageProductRepository imageProductRepository;
    private final S3Provider s3Provider;
    private final WishRepository wishRepository;


    @Transactional
    public void createProduct(User user, CreateProductRequestDto requestDto,
        List<MultipartFile> productImage) {

        List<String> fileUrls = s3Provider.uploadFile("product", productImage);

        Product saveProduct = new Product(requestDto.getTitle(), requestDto.getPrice(),
            requestDto.getContent(), requestDto.getAddress(), requestDto.getState(), user);

        productRepository.save(saveProduct);

        for (String fileUrl : fileUrls) {
            ImageProduct imageProduct = new ImageProduct(fileUrl, saveProduct);
            imageProductRepository.save(imageProduct);
        }
    }

    @Transactional
    public void editProduct(Long id, Long productsId, EditProductRequestDto requestDto) {

        Product product = findByProduct(productsId);

        if (!isProductOwner(productsId, id)) {
            throw new CustomException(ErrorCode.NOT_OWNER_PRODUCT);
        }

        product.updateProduct(requestDto.getPrice(), requestDto.getTitle(), requestDto.getContent(),
            requestDto.getAddress(), requestDto.getState());
    }

    @Transactional
    public void updateProduct(Long id, Long productsId) {

        Product product = findByProduct(productsId);

        if (!isProductOwner(productsId, id)) {
            throw new CustomException(ErrorCode.NOT_OWNER_PRODUCT);
        }

        product.updateUpdatedAt();
        productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(Long id, Long productsId) {

        Product product = findByProduct(productsId);
        if (!isProductOwner(productsId, id)) {
            throw new CustomException(ErrorCode.NOT_OWNER_PRODUCT);
        }

        List<ImageProduct> imageProducts = imageProductRepository.findByProductId(productsId);

        for (ImageProduct imageProduct : imageProducts) {
            String filePath = imageProduct.getFilePath();
            s3Provider.deleteFile(filePath);
        }

        productRepository.delete(product);

    }

    @Transactional(readOnly = true)
    public ProductDetailResponseDto getProduct(Long productsId) {

        Product product = findByProduct(productsId);

        List<ImageProduct> imageProducts = imageProductRepository.findByProductId(productsId);

        List<ImageProductDto> imageUrlList = imageProducts.stream()
            .map(imageProduct -> new ImageProductDto(
                imageProduct.getId(),
                s3Provider.getS3Url(imageProduct.getFilePath())
            ))
            .toList();

        return new ProductDetailResponseDto(product.getId(), product.getUser().getUsername(), product.getUser().getProfileImage(),
            product.getPrice(), product.getTitle(), product.getContent(), product.getAddress(),
            product.getState(), product.getUpdatedAt(), imageUrlList);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponseDto> getProductSearch(Pageable pageable, String search) {

        Page<Product> page = productRepository.findProductsByTitleLike(pageable, search);

        if (page.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        return getProductImageResponseDtoPage(page);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponseDto> getProductRegion(Pageable pageable, String region) {

        Page<Product> page;

        if (!isAddressValid(region)) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        if (region.equals("ALL")) {
            page = productRepository.findAll(pageable);
        } else {
            page = productRepository.findAllByAddress(pageable, Address.valueOf(region));
        }

        if (page.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        return getProductImageResponseDtoPage(page);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponseDto> getProductSearchRegion(Pageable pageable, String search,
        String region) {

        Page<Product> page;

        if (!isAddressValid(region)) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        if (region.equals("ALL")) {
            page = productRepository.findByTitleContainingIgnoreCase(pageable, search);
        } else {
            page = productRepository.findByTitleContainingIgnoreCaseAndAddress(pageable,
                search,
                Address.valueOf(region));
        }

        if (page.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        return getProductImageResponseDtoPage(page);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponseDto> getProductPage(Pageable pageable) {

        Page<Product> page = productRepository.findAll(pageable);

        if (page.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        return getProductImageResponseDtoPage(page);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponseDto> getMyProductList(Long id, int page) {

        int size = 6;
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Product> productPage = productRepository.findByUserId(id, pageable);

        return productPage.map((product) -> {

            List<ImageProduct> images = imageProductRepository.findByProductIdOrderByCreatedAtAsc(
                product.getId());

            ImageProduct firstImage = images.isEmpty() ? null : images.get(0);

            if (firstImage == null) {
                throw new CustomException(ErrorCode.NO_REPRESENTATIVE_IMAGE_FOUND);
            }

            return new ProductResponseDto(product.getId(), product.getPrice(), product.getTitle(),
                s3Provider.getS3Url(firstImage.getFilePath()));
        });
    }

    private boolean isAddressValid(String address) {
        for (Address addressString : Address.values()) {
            if (addressString.name().equalsIgnoreCase(address)) {
                return true;
            }
        }
        return false;
    }

    public Product findByProduct(Long productId) {
        return productRepository.findById(productId).orElseThrow(
            () -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND)
        );
    }

    private boolean isProductOwner(Long productId, Long userId) {
        return productRepository.existsByIdAndUserId(productId, userId);
    }

    private Page<ProductResponseDto> getProductImageResponseDtoPage(Page<Product> page) {

        return page.map((product) -> {

            List<ImageProduct> images = imageProductRepository.findByProductIdOrderByCreatedAtAsc(
                product.getId());

            ImageProduct firstImage = images.isEmpty() ? null : images.get(0);

            if (firstImage == null) {
                throw new CustomException(ErrorCode.NO_REPRESENTATIVE_IMAGE_FOUND);
            }

            return new ProductResponseDto(product.getId(), product.getPrice(), product.getTitle(),
                s3Provider.getS3Url(firstImage.getFilePath()));
        });
    }

    public Page<ProductResponseDto> getTopWishedProducts() {
        int size = 4;

        Page<Product> page = wishRepository.findTopWishedProducts(PageRequest.of(1, size));

        return getProductImageResponseDtoPage(page);
    }
}

