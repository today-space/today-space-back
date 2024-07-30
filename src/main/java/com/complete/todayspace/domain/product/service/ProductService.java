package com.complete.todayspace.domain.product.service;

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
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
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
    private final S3Service s3Service;
    private final WishRepository wishRepository;

    @Value("${cloud.aws.s3.baseUrl}")
    private String s3baseUrl;

    @Transactional
    public void createProduct(User user, CreateProductRequestDto requestDto,
        List<MultipartFile> productImage) {

        List<String> fileUrls = s3Service.uploadFile(productImage);

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
            s3Service.deleteFile(filePath);
        }

        productRepository.delete(product);

    }

    @Transactional(readOnly = true)
    public ProductDetailResponseDto getProduct(Long productsId) {

        Product product = findByProduct(productsId);

        List<ImageProduct> imageProducts = imageProductRepository.findByProductId(productsId);

        List<ImageDto> imageUrlList = imageProducts.stream()
            .map(imageProduct -> new ImageDto(
                imageProduct.getId(),
                s3baseUrl + imageProduct.getFilePath()
            ))
            .toList();

        return new ProductDetailResponseDto(product.getId(), product.getUser().getUsername(),
            product.getPrice(), product.getTitle(), product.getContent(), product.getAddress(),
            product.getState(), product.getUpdatedAt(), imageUrlList);
    }

    @Transactional(readOnly = true)
    public Page<ProductImageResponseDto> getProductSearch(Pageable pageable, String search) {

        Page<Product> page = productRepository.findProductsByTitleLike(pageable, search);

        if (page.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        return getProductImageResponseDtoPage(page);
    }

    @Transactional(readOnly = true)
    public Page<ProductImageResponseDto> getProductRegion(Pageable pageable, String region) {

        if (!isAddressValid(region)) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        Page<Product> page;

        page = productRepository.findAllByAddress(pageable, Address.valueOf(region));

        if (page.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        return getProductImageResponseDtoPage(page);
    }

    @Transactional(readOnly = true)
    public Page<ProductImageResponseDto> getProductSearchRegion(Pageable pageable, String search,
        String region) {

        if (!isAddressValid(region)) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        Page<Product> page = productRepository.findByTitleContainingIgnoreCaseAndAddress(pageable,
            search,
            Address.valueOf(region));

        if (page.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        return getProductImageResponseDtoPage(page);
    }

    @Transactional(readOnly = true)
    public Page<ProductImageResponseDto> getProductPage(Pageable pageable) {

        Page<Product> page = productRepository.findAll(pageable);

        if (page.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        return getProductImageResponseDtoPage(page);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponseDto> getMyProductList(Long id, int page) {

        int size = 8;
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Product> productPage = productRepository.findByUserId(id, pageable);

        return productPage.map( (product) -> {

            List<ImageProduct> images = imageProductRepository.findByProductIdOrderByCreatedAtAsc(product.getId());

            ImageProduct firstImage = images.isEmpty() ? null : images.get(0);

            if (firstImage == null) {
                throw new CustomException(ErrorCode.NO_REPRESENTATIVE_IMAGE_FOUND);
            }

            return new ProductResponseDto(product.getId(), product.getPrice(), product.getTitle(), s3baseUrl + firstImage.getFilePath());
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

    private Page<ProductImageResponseDto> getProductImageResponseDtoPage(Page<Product> page) {
        List<Long> productIds = page.getContent().stream()
            .map(Product::getId)
            .toList();

        List<ImageProduct> imageProducts = imageProductRepository.findByProductIdIn(productIds);

        Map<Long, List<ImageDto>> imageMap = imageProducts.stream()
            .collect(Collectors.groupingBy(imageProduct -> imageProduct.getProduct().getId(),
                Collectors.mapping(imageProduct -> new ImageDto(
                    imageProduct.getId(),
                    s3baseUrl + imageProduct.getFilePath()
                ), Collectors.toList())));

        return page.map(product -> {
            List<ImageDto> productImages = imageMap.get(product.getId());
            ImageDto firstImage =
                (productImages != null && !productImages.isEmpty()) ? productImages.get(0) : null;
            return new ProductImageResponseDto(
                product.getId(),
                product.getPrice(),
                product.getTitle(),
                firstImage
            );
        });
    }

    public Page<ProductImageResponseDto> getTopWishedProducts() {
        int size = 4;

        Page<Product> page = wishRepository.findTopWishedProducts(PageRequest.of(1, size));

        return getProductImageResponseDtoPage(page);
    }
}

