package com.example.scopedemo.model;

/*
 * Indicates that a DTO may contain meta information to give the user more understanding of
 *  the underlying workings of the application.
 */
public interface ProvidesInfo {
    MetaInfo getMetaInfo();
    void setMetaInfo(MetaInfo metaInfo);
}
