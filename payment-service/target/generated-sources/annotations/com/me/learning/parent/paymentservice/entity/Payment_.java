package com.me.learning.parent.paymentservice.entity;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.math.BigDecimal;
import java.time.Instant;

@StaticMetamodel(Payment.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class Payment_ {

	public static final String AMOUNT = "amount";
	public static final String LAST_UPDATE = "lastUpdate";
	public static final String ID = "id";
	public static final String PAYMENT_DATE = "paymentDate";
	public static final String RENTAL = "rental";

	
	/**
	 * @see com.me.learning.parent.paymentservice.entity.Payment#amount
	 **/
	public static volatile SingularAttribute<Payment, BigDecimal> amount;
	
	/**
	 * @see com.me.learning.parent.paymentservice.entity.Payment#lastUpdate
	 **/
	public static volatile SingularAttribute<Payment, Instant> lastUpdate;
	
	/**
	 * @see com.me.learning.parent.paymentservice.entity.Payment#id
	 **/
	public static volatile SingularAttribute<Payment, Integer> id;
	
	/**
	 * @see com.me.learning.parent.paymentservice.entity.Payment#paymentDate
	 **/
	public static volatile SingularAttribute<Payment, Instant> paymentDate;
	
	/**
	 * @see com.me.learning.parent.paymentservice.entity.Payment
	 **/
	public static volatile EntityType<Payment> class_;
	
	/**
	 * @see com.me.learning.parent.paymentservice.entity.Payment#rental
	 **/
	public static volatile SingularAttribute<Payment, Rental> rental;

}

