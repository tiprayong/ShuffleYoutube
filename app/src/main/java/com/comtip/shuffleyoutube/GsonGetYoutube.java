package com.comtip.shuffleyoutube;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TipRayong on 20/7/2559.
 */
public class GsonGetYoutube {
    public GsonGetYoutube(List<Item> items, String nextPageToken) {
        this.items = items;
        this.nextPageToken = nextPageToken;
    }


    @SerializedName("nextPageToken") private String nextPageToken;

    public String getNextPageToken() {
        return nextPageToken;
    }

    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }


    @SerializedName("items") private List <Item> items = new ArrayList<Item>();
    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    class  Item {
     @SerializedName("snippet") private snippet snippet;

        public Item.snippet getSnippet() {
            return snippet;
        }

        public void setSnippet(Item.snippet snippet) {
            this.snippet = snippet;
        }

        class snippet {
            @SerializedName("title") private String title;
            @SerializedName("resourceId") private resourceId resourceId;

            public Item.snippet.resourceId getResourceId() {
                return resourceId;
            }

            public void setResourceId(Item.snippet.resourceId resourceId) {
                this.resourceId = resourceId;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            class  resourceId {
            @SerializedName("videoId") private  String videoId;

                public String getVideoId() {
                    return videoId;
                }

                public void setVideoId(String videoId) {
                    this.videoId = videoId;
                }
            }
        }


    }

}
