package com.complete.todayspace.domain.product.service;

import com.complete.todayspace.domain.common.S3Provider;
import com.complete.todayspace.domain.payment.entity.Payment;
import com.complete.todayspace.domain.payment.entity.State;
import com.complete.todayspace.domain.payment.repository.PaymentRepository;
import com.complete.todayspace.domain.payment.service.PaymentService;
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

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ImageProductRepository imageProductRepository;
    private final S3Provider s3Provider;
    private final WishRepository wishRepository;
    private final PaymentService paymentService;

    @Transactional
    public void createProduct(User user, CreateProductRequestDto requestDto) {

        Product saveProduct = new Product(requestDto, user);

        productRepository.save(saveProduct);

        for (String fileUrl : requestDto.getImages()) {
            ImageProduct imageProduct = new ImageProduct(fileUrl, saveProduct);
            imageProductRepository.save(imageProduct);
        }
    }

    @Transactional
    public void editProduct(Long id, Long productId, EditProductRequestDto requestDto) {

        Product product = findByProduct(productId);

        if (!isProductOwner(productId, id)) {
            throw new CustomException(ErrorCode.NOT_OWNER_PRODUCT);
        }

        product.updateProduct(requestDto);
    }

    @Transactional
    public void updateProduct(Long id, Long productId) {

        Product product = findByProduct(productId);

        if (!isProductOwner(productId, id)) {
            throw new CustomException(ErrorCode.NOT_OWNER_PRODUCT);
        }

        product.updateUpdatedAt();
        productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(Long id, Long productId) {

        Product product = findByProduct(productId);
        if (!isProductOwner(productId, id)) {
            throw new CustomException(ErrorCode.NOT_OWNER_PRODUCT);
        }

        List<ImageProduct> imageProducts = imageProductRepository.findByProductId(productId);

        for (ImageProduct imageProduct : imageProducts) {
            String filePath = imageProduct.getFilePath();
            s3Provider.deleteFile(filePath);
        }

        productRepository.delete(product);

    }

    @Transactional(readOnly = true)
    public ProductDetailResponseDto getProduct(Long productId) {

        Product product = findByProduct(productId);

        List<ImageProduct> imageProducts = imageProductRepository.findByProductId(productId);

        List<ImageProductDto> imageUrlList = imageProducts.stream()
            .map(imageProduct -> new ImageProductDto(
                imageProduct.getId(),
                s3Provider.getS3Url(imageProduct.getFilePath())
            ))
            .toList();

        Payment payment = paymentService.findByProductId(product.getId());
        boolean paymentState = payment != null && payment.getState() == State.COMPLATE;

        String paymentUser = null;
        if (payment != null && payment.getUser().getUsername() != null) {
            paymentUser = payment.getUser().getUsername();
        }

        return new ProductDetailResponseDto(product.getId(), product.getUser().getId(),
            product.getUser().getUsername(), product.getUser().getProfileImage(),
            product.getPrice(), product.getTitle(), product.getContent(), product.getAddress(),
            product.getState(), product.getUpdatedAt(), imageUrlList, paymentState, paymentUser);
    }

    public Page<ProductResponseDto> getResponseDto(Pageable pageable,
        PageParamDto pageParamDto) {

        if (Boolean.TRUE.equals(pageParamDto.getTopWished())) {
            return getTopWishedProducts();
        } else if (pageParamDto.getSearch() != null && pageParamDto.getRegion() == null) {
            return getProductSearch(pageable, pageParamDto.getSearch());
        } else if (pageParamDto.getRegion() != null && pageParamDto.getSearch() == null) {
            return getProductRegion(pageable, pageParamDto.getRegion());
        } else if (pageParamDto.getSearch() != null && pageParamDto.getRegion() != null) {
            return getProductSearchRegion(pageable, pageParamDto.getSearch(),
                pageParamDto.getRegion());
        } else {
            return getProductPage(pageable);
        }
    }

    @Transactional(readOnly = true)
    public Page<ProductResponseDto> getProductSearch(Pageable pageable, String search) {

        Page<ProductResponseDto> page = productRepository.findProductsByTitleLike(pageable, ("%" + search + "%"));

        if (page.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        return page;
    }

    @Transactional(readOnly = true)
    public Page<ProductResponseDto> getProductRegion(Pageable pageable, String region) {

        Page<ProductResponseDto> page;

        if (!isAddressValid(region)) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        if (region.equals("ALL")) {

            page = productRepository.findProducts(pageable);
        } else {

            page = productRepository.findAllByAddress(pageable, Address.valueOf(region));
        }

        if (page.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

//        return getProductImageResponseDtoPage(page);

        return page;
    }

    @Transactional(readOnly = true)
    public Page<ProductResponseDto> getProductSearchRegion(Pageable pageable, String search,
        String region) {

        Page<ProductResponseDto> page;

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

        return page;
    }

    @Transactional(readOnly = true)
    public Page<ProductResponseDto> getProductPage(Pageable pageable) {

        System.out.println("여기서부터 전체 조회");
        Page<ProductResponseDto> page = productRepository.findProducts(pageable);
        System.out.println("여기까지 전체 조회");

        if (page.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

//        return getProductImageResponseDtoPage(page);

        return page;
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

            Payment payment = paymentService.findByProductId(product.getId());
            boolean paymentState = payment != null && payment.getState() == State.COMPLATE;

            return new ProductResponseDto(product.getId(), product.getPrice(), product.getTitle(),
                s3Provider.getS3Url(firstImage.getFilePath()), paymentState);
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

            Payment payment = paymentService.findByProductId(product.getId());
            boolean paymentState = payment != null && payment.getState() == State.COMPLATE;

            return new ProductResponseDto(product.getId(), product.getPrice(), product.getTitle(),
                s3Provider.getS3Url(firstImage.getFilePath()), paymentState);
        });
    }

    public Page<ProductResponseDto> getTopWishedProducts() {
        int size = 4;

        Page<Product> page = wishRepository.findTopWishedProducts(PageRequest.of(1, size));

        return getProductImageResponseDtoPage(page);
    }
}

