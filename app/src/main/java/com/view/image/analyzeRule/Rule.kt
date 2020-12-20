package com.view.image.analyzeRule

import java.io.Serializable

/**
 * @param sourceUrl 源地址
 * @param sourceName 网站名
 * @param sortUrl 分类
 * @param tabName 标签名字
 * @param tabHref 标签地址
 * @param tabReplace 标签地址替换
 * @param tabFrom 标签来自
 * @param homeList 首页列表
 * @param homeHref 首页地址规则
 * @param homeTitle 首页标题规则
 * @param homeSrc 首页图标规则
 * @param homeSrcReplaceByJS 首页图片地址替换规则js
 * @param imagePageList 图片列表规则
 * @param imagePageSrc 图片地址规则
 * @param imageNextPage 下一页
 * @param imageUrlReplaceByJS 图片地址替换规则js
 * @param reqMethod 请求方法
 * @param cookie 登录
 * @param js 加载的js库
 * @param jsMethod js方法
 * @param charset 编码
 * @param userAgent
 */
class Rule(
    var sourceUrl: String = "",
    var sourceName: String = "",
    var sortUrl: String = "",
    var tabName: String = "",
    var tabHref: String = "",
    var tabReplace: String = "",
    var tabFrom: String = "",
    var homeList: String = "",
    var homeHref: String = "",
    var homeTitle: String = "",
    var homeSrc: String = "",
    var homeSrcReplaceByJS: String = "",
    var imagePageList: String = "",
    var imagePageSrc: String = "",
    var imageNextPage: String = "",
    var imageUrlReplaceByJS: String = "",
    var reqMethod: String = "GET",
    var cookie: String = "",
    var js: String = "",
    var jsMethod: String = "",
    var charset: String = "utf-8",
    var userAgent: String? = null
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Rule

        if (sourceUrl != other.sourceUrl) return false
        if (sourceName != other.sourceName) return false
        if (sortUrl != other.sortUrl) return false
        if (tabName != other.tabName) return false
        if (tabHref != other.tabHref) return false
        if (tabReplace != other.tabReplace) return false
        if (tabFrom != other.tabFrom) return false
        if (homeList != other.homeList) return false
        if (homeHref != other.homeHref) return false
        if (homeTitle != other.homeTitle) return false
        if (homeSrc != other.homeSrc) return false
        if (homeSrcReplaceByJS != other.homeSrcReplaceByJS) return false
        if (imagePageList != other.imagePageList) return false
        if (imagePageSrc != other.imagePageSrc) return false
        if (imageNextPage != other.imageNextPage) return false
        if (imageUrlReplaceByJS != other.imageUrlReplaceByJS) return false
        if (reqMethod != other.reqMethod) return false
        if (cookie != other.cookie) return false
        if (js != other.js) return false
        if (jsMethod != other.jsMethod) return false
        if (charset != other.charset) return false
        if (userAgent != other.userAgent) return false

        return true
    }
}