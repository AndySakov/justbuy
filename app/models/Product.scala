package models

import java.time.LocalDateTime

/**
 * This is the Product model that defines the product table structure in the database
 * @param unique_id a unique ID that all products possess
 * @param name the name of this product
 * @param desc a brief description of this product
 * @param imgSrc the url pointing to the image for this product
 * @param price the price of this product
 * @param brand the brand of this product
 * @param category the category of the product
 * @param discount any available discount in percent
 * @param available is product available for purchase
 * @param stock amount of this product in stock
 * @param toc the time of creation of this product account accurate to milliseconds
 */
case class Product(unique_id: String, name: String, desc: String, imgSrc: String, price: Double, brand: String, category: String, discount: Double, available: Boolean, stock: Int, toc: LocalDateTime, merchant: String)
