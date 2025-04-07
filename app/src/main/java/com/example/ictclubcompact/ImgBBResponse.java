package com.example.ictclubcompact;

import com.google.gson.annotations.SerializedName;

public class ImgBBResponse {
    @SerializedName("data")
    private Data data;

    public Data getData() {
        return data;
    }

    public static class Data {
        @SerializedName("url")
        private String url;

        @SerializedName("display_url")
        private String displayUrl;

        public String getUrl() {
            return url;
        }

        public String getDisplayUrl() {
            return displayUrl;
        }
    }
}