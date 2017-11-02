package com.example.unreallover.mediaplayer.Entities;

/**
 * Created by Unreal Lover on 2017/11/1.
 */

import java.io.Serializable;

/**
 * Created by Unreal Lover on 2017/10/2.
 */

public class Music implements Serializable {
    private static final long serialVersionUID = -7060210544600464481L;
    private long id;
    private String title;
    private String artist;
    private long duration;
    private String fileName;
    private String album;
    private long albumId;
    private long fileSize;
    private String path;

    public Music() {
        super();
    }


    public Music(long id, String title, String artist, long duration, String fileName) {
        super();
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.duration = duration;
        this.fileName = fileName;
    }
    //存取Music的id
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    //存取Music的title
    public String gettitle() {
        return title;
    }

    public void settitle(String title) {
        this.title = title;
    }
    //存取file的文件名
    public String getartist() {
        return artist;
    }

    public void setartist(String artist) {
        this.artist = artist;
    }
    //存储文件长度
    public long getduration() {
        return duration;
    }

    public void setduration(long duration) {
        this.duration = duration;
    }
    //存取完成情况
    public String getfileName() {
        return fileName;
    }

    public void setfileName(String fileName) {
        this.fileName = fileName;
    }
    //存取大小单位
    public void setalbum(String album){
        this.album = album;
    }

    public String getalbum(){
        return album;
    }
    //存取实际进度（换算后）
    public void setalbumId(long albumId){
        this.albumId = albumId;
    }

    public long getalbumId(){
        return albumId;
    }
    //存取下载速度
    public void setfileSize(long fileSize){
        this.fileSize = fileSize;
    }

    public long getfileSize(){
        return fileSize;
    }
    //存取下载速度的大小单位
    public void setpath(String path){
        this.path = path;
    }

    public String getpath(){
        return path;
    }

    @Override
    public String toString() {
        return "Music [id=" + id + ", title=" + title + ", artist=" + artist + ", duration=" + duration + ", fileName="
                + fileName + "]";
    }

}