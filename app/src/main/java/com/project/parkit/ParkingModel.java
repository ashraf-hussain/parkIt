package com.project.parkit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

 public class ParkingModel {

     @SerializedName("id")
     @Expose
     private Integer id;
     @SerializedName("lat")
     @Expose
     private String lat;
     @SerializedName("lng")
     @Expose
     private String lng;
     @SerializedName("name")
     @Expose
     private String name;
     @SerializedName("cost_per_minute")
     @Expose
     private String costPerMinute;
     @SerializedName("max_reserve_time_mins")
     @Expose
     private Integer maxReserveTimeMins;
     @SerializedName("min_reserve_time_mins")
     @Expose
     private Integer minReserveTimeMins;
     @SerializedName("is_reserved")
     @Expose
     private Boolean isReserved;
     @SerializedName("reserved_until")
     @Expose
     private String reservedUntil;

     public Boolean getReserved() {
         return isReserved;
     }

     public void setReserved(Boolean reserved) {
         isReserved = reserved;
     }

     public int getIcon() {
         return icon;
     }

     public void setIcon(int icon) {
         this.icon = icon;
     }

     private int icon;

     public Integer getId() {
         return id;
     }

     public void setId(Integer id) {
         this.id = id;
     }

     public String getLat() {
         return lat;
     }

     public void setLat(String lat) {
         this.lat = lat;
     }

     public String getLng() {
         return lng;
     }

     public void setLng(String lng) {
         this.lng = lng;
     }

     public String getName() {
         return name;
     }

     public void setName(String name) {
         this.name = name;
     }

     public String getCostPerMinute() {
         return costPerMinute;
     }

     public void setCostPerMinute(String costPerMinute) {
         this.costPerMinute = costPerMinute;
     }

     public Integer getMaxReserveTimeMins() {
         return maxReserveTimeMins;
     }

     public void setMaxReserveTimeMins(Integer maxReserveTimeMins) {
         this.maxReserveTimeMins = maxReserveTimeMins;
     }

     public Integer getMinReserveTimeMins() {
         return minReserveTimeMins;
     }

     public void setMinReserveTimeMins(Integer minReserveTimeMins) {
         this.minReserveTimeMins = minReserveTimeMins;
     }

     public Boolean getIsReserved() {
         return isReserved;
     }

     public void setIsReserved(Boolean isReserved) {
         this.isReserved = isReserved;
     }

     public String getReservedUntil() {
         return reservedUntil;
     }

     public void setReservedUntil(String reservedUntil) {
         this.reservedUntil = reservedUntil;
     }

 }
