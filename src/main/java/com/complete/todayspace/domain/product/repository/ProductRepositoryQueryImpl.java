package com.complete.todayspace.domain.product.repository;

import com.complete.todayspace.domain.payment.entity.QPayment;
import com.complete.todayspace.domain.payment.entity.State;
import com.complete.todayspace.domain.product.dto.ProductResponseDto;
import com.complete.todayspace.domain.product.dto.QProductResponseDto;
import com.complete.todayspace.domain.product.entity.Address;
import com.complete.todayspace.domain.product.entity.QImageProduct;
import com.complete.todayspace.domain.product.entity.QProduct;
import com.complete.todayspace.global.exception.CustomException;
import com.complete.todayspace.global.exception.ErrorCode;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
                .orderBy(product.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        productList.forEach(this::validateProductImage);

        return PageableExecutionUtils.getPage(
                productList,
                pageable,
                () -> Optional.ofNullable(
                        jpaQueryFactory
                                .select(product.count())
                                .from(product)
                                .where(product.user.id.eq(userId))
                                .fetchOne()
                ).orElse(0L)
        );
    }

    @Override
    public Page<ProductResponseDto> findProductsByTitleLike(Pageable pageable, String search) {
        QProduct product = QProduct.product;
        QImageProduct imageProduct  = QImageProduct.imageProduct;
        QPayment payment = QPayment.payment;

        List<OrderSpecifier<?>> orderSpecifiers = getOrderSpecifier(pageable.getSort(), product);

        List<ProductResponseDto> products = jpaQueryFactory
            .select(Projections.constructor(
                ProductResponseDto.class,
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
            ))
            .from(product)
            .leftJoin(product.payment, payment)
            .where(product.title.like("%" + search + "%"))
            .orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        products.forEach(this::validateProductImage);

        long total = jpaQueryFactory.selectFrom(product)
            .where(product.title.like("%" + search + "%"))
            .fetch().size();

        return new PageImpl<>(products, pageable, total);
    }

    @Override
    public Page<ProductResponseDto> findByTitleContainingIgnoreCaseAndAddress(Pageable pageable, String search,
        Address address) {

        QProduct product = QProduct.product;
        QImageProduct imageProduct  = QImageProduct.imageProduct;
        QPayment payment = QPayment.payment;

        List<OrderSpecifier<?>> orderSpecifiers = getOrderSpecifier(pageable.getSort(), product);

        List<ProductResponseDto> products = jpaQueryFactory
            .select(Projections.constructor(
                ProductResponseDto.class,
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
            ))
            .from(product)
            .where(product.address.eq(address).and(product.title.like("%" + search + "%")))
            .leftJoin(product.payment, payment)
            .orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        products.forEach(this::validateProductImage);

        long total = jpaQueryFactory.selectFrom(product)
            .where(product.address.eq(address).and(product.title.like("%" + search + "%")))
            .fetch().size();

        return new PageImpl<>(products, pageable, total);

    }

    @Override
    public Page<ProductResponseDto> findByTitleContainingIgnoreCase(Pageable pageable, String search) {

        QProduct product = QProduct.product;
        QImageProduct imageProduct  = QImageProduct.imageProduct;
        QPayment payment = QPayment.payment;

        List<OrderSpecifier<?>> orderSpecifiers = getOrderSpecifier(pageable.getSort(), product);

        List<ProductResponseDto> products = jpaQueryFactory
            .select(Projections.constructor(
                ProductResponseDto.class,
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
            ))
            .from(product)
            .where(product.title.like("%" + search + "%"))
            .leftJoin(product.payment, payment)
            .orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        products.forEach(this::validateProductImage);

        long total = jpaQueryFactory.selectFrom(product)
            .where(product.title.like("%" + search + "%"))
            .fetch().size();

        return new PageImpl<>(products, pageable, total);

    }

    @Override
    public Page<ProductResponseDto> findProducts(Pageable pageable) {

        QProduct product = QProduct.product;
        QImageProduct imageProduct  = QImageProduct.imageProduct;
        QPayment payment = QPayment.payment;

        List<OrderSpecifier<?>> orderSpecifiers = getOrderSpecifier(pageable.getSort(), product);

        List<ProductResponseDto> products = jpaQueryFactory
            .select(Projections.constructor(
                ProductResponseDto.class,
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
            ))
            .from(product)
            .leftJoin(product.payment, payment)
            .orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        products.forEach(this::validateProductImage);

        long total = jpaQueryFactory.selectFrom(product)
            .fetch().size();

        return new PageImpl<>(products, pageable, total);

    }

    @Override
    public Page<ProductResponseDto> findAllByAddress(Pageable pageable, Address address) {

        QProduct product = QProduct.product;
        QImageProduct imageProduct  = QImageProduct.imageProduct;
        QPayment payment = QPayment.payment;

        List<OrderSpecifier<?>> orderSpecifiers = getOrderSpecifier(pageable.getSort(), product);

        List<ProductResponseDto> products = jpaQueryFactory
            .select(Projections.constructor(
                ProductResponseDto.class,
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
            ))
            .from(product)
            .where(product.address.eq(address))
            .leftJoin(product.payment, payment)
            .orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        products.forEach(this::validateProductImage);

        long total = jpaQueryFactory.selectFrom(product)
            .where(product.address.eq(address))
            .fetch().size();

        return new PageImpl<>(products, pageable, total);
    }

    private void validateProductImage(ProductResponseDto productDto) {
        if (productDto.getImagePath() == null) {
            throw new CustomException(ErrorCode.NO_REPRESENTATIVE_IMAGE_FOUND);
        }
    }

    private List<OrderSpecifier<?>> getOrderSpecifier(Sort sort, QProduct product) {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();


        sort.forEach(order -> {

            Order direction;
            String property = order.getProperty();

            if (order.isAscending()) {
                direction = Order.ASC;
            } else {
                direction = Order.DESC;
            }

            if (property.equals("price")) {
                orderSpecifiers.add(new OrderSpecifier<>(direction, product.price));
            } else if (property.equals("updatedAt")) {
                orderSpecifiers.add(new OrderSpecifier<>(direction, product.updatedAt));
            }
        });

        return orderSpecifiers;
    }

}
