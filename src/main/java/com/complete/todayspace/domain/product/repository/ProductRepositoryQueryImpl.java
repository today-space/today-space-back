package com.complete.todayspace.domain.product.repository;

import com.complete.todayspace.domain.payment.entity.QPayment;
import com.complete.todayspace.domain.payment.entity.State;
import com.complete.todayspace.domain.product.dto.ProductResponseDto;
import com.complete.todayspace.domain.product.dto.QProductResponseDto;
import com.complete.todayspace.domain.product.entity.QImageProduct;
import com.complete.todayspace.domain.product.entity.QProduct;
import com.complete.todayspace.global.exception.CustomException;
import com.complete.todayspace.global.exception.ErrorCode;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryQueryImpl implements ProductRepositoryQuery {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<ProductResponseDto> findMyProductList(Long userId, Pageable pageable) {

        QProduct product = QProduct.product;
        QImageProduct imageProduct = QImageProduct.imageProduct;
        QPayment payment = QPayment.payment;

        List<ProductResponseDto> productList = jpaQueryFactory
                .select(new QProductResponseDto(
                        product.id,
                        product.price,
                        product.title,
                        JPAExpressions
                                .select(imageProduct.filePath)
                                .from(imageProduct)
                                .where(imageProduct.id.eq(
                                        JPAExpressions
                                                .select(imageProduct.id.min())
                                                .from(imageProduct)
                                                .where(imageProduct.product.id.eq(product.id))
                                )),
                        payment.state.when(State.COMPLATE).then(true).otherwise(false)
                )).from(product)
                .leftJoin(payment).on(payment.product.id.eq(product.id))
                .where(product.user.id.eq(userId))
                .orderBy(product.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        productList.forEach(this::validateProductImage);

        long total = jpaQueryFactory
                .select(product.id)
                .from(product)
                .where(product.user.id.eq(userId))
                .fetch()
                .size();

        return new PageImpl<>(productList, pageable, total);
    }

    private void validateProductImage(ProductResponseDto productDto) {
        if (productDto.getImagePath() == null) {
            throw new CustomException(ErrorCode.NO_REPRESENTATIVE_IMAGE_FOUND);
        }
    }

}
