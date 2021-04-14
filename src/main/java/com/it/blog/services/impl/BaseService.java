package com.it.blog.services.impl;

import com.it.blog.utils.Constants;

public class BaseService {
    int chcekPage(int page) {
        if (page < Constants.Page.DEFUALT_PAGE) {
            page = Constants.Page.DEFUALT_PAGE;
        }
        return page;
    }

    int chcekSize(int size) {
        if (size < Constants.Page.DEFUALT_PAGE) {
            size = Constants.Page.DEFUALT_PAGE;
        }
        return size;
    }
}
