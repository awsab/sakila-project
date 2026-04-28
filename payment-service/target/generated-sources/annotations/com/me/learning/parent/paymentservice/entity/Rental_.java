package com.me.learning.parent.paymentservice.entity;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SetAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.Instant;

@StaticMetamodel(Rental.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class Rental_ {

	public static final String RENTAL_DATE = "rentalDate";
	public static final String RETURN_DATE = "returnDate";
	public static final String LAST_UPDATE = "lastUpdate";
	public static final String PAYMENTS = "payments";
	public static final String ID = "id";

	
	/**
	 * @see com.me.learning.parent.paymentservice.entity.Rental#rentalDate
	 **/
	public static volatile SingularAttribute<Rental, Instant> rentalDate;
	
	/**
	 * @see com.me.learning.parent.paymentservice.entity.Rental#returnDate
	 **/
	public static volatile SingularAttribute<Rental, Instant> returnDate;
	
	/**
	 * @see com.me.learning.parent.paymentservice.entity.Rental#lastUpdate
	 **/
	public static volatile SingularAttribute<Rental, Instant> lastUpdate;
	
	/**
	 * @see com.me.learning.parent.paymentservice.entity.Rental#payments
	 **/
	public static volatile SetAttribute<Rental, Payment> payments;
	
	/**
	 * @see com.me.learning.parent.paymentservice.entity.Rental#id
	 **/
	public static volatile SingularAttribute<Rental, Integer> id;
	
	/**
	 * @see com.me.learning.parent.paymentservice.entity.Rental
	 **/
	public static volatile EntityType<Rental> class_;

}

