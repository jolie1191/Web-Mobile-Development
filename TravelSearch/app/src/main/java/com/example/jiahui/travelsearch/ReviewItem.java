package com.example.jiahui.travelsearch;

public class ReviewItem {

    String name;
    String time;
    String review;
    String imageUrl;

    String rating;

    String authorUrl;

    public String getAuthorUrl() {
        return authorUrl;
    }

    public void setAuthorUrl(String authorUrl) {
        this.authorUrl = authorUrl;
    }


    public ReviewItem(String name, String time, String review, String imageUrl, String rating, String authorUrl) {
        this.name = name;
        this.time = time;
        this.review = review;
        this.imageUrl = imageUrl;
        this.rating = rating;
        this.authorUrl = authorUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }



}
