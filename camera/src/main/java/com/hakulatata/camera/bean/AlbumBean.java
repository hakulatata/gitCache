package com.hakulatata.camera.bean;

/**
 * Created by Silver on 2017/4/6.
 */

public class AlbumBean {
    private String path;
    private String firstImgPath;
    private String name;
    private int count;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;

        int lastIndexOf = this.path.lastIndexOf("/");
        this.name = this.path.substring(lastIndexOf + 1);
    }

    public String getFirstImgPath() {
        return firstImgPath;
    }

    public void setFirstImgPath(String firstImgPath) {
        this.firstImgPath = firstImgPath;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
