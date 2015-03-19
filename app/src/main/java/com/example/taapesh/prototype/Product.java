package com.example.taapesh.prototype;

import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigDecimal;

public class Product implements Parcelable {
    private String productName;
    private BigDecimal productPrice;
    private String productCode;

    // Product constructor
    public Product(String name, BigDecimal price, String code) {
        this.productName = name;
        this.productPrice = price;
        this.productCode = code;
    }

    // Product parcel constructor
    public Product(Parcel p){
        setProductName(p.readString());
        setProductPrice(new BigDecimal(p.readString()));
        setProductCode(p.readString());
    }

    public BigDecimal getProductPrice() {
        return productPrice;
    }

    public String getProductCode() {
        return productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setProductPrice(BigDecimal productPrice) {
        this.productPrice = productPrice;
    }

    public int describeContents() {
        return 0;
    }


    public void writeToParcel(Parcel des, int flags) {
        des.writeString(getProductName());
        des.writeString(getProductPrice().toString());
        des.writeString(getProductCode());

    }

    public static final Parcelable.Creator<Product> CREATOR = new Creator<Product>() {

        public Product createFromParcel(Parcel source) {
            return new Product(source);
        }

        public Product[] newArray(int size) {
            return new Product[size];
        }

    };
}
