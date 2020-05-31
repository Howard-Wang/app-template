package com.company.app.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * 分页工具类
 * @author YunJ
 */
public final class PaginationUtil {
    private PaginationUtil() {
    }

    /**
     * 分页信息转换
     * 将 Pageable 中的分页信息转换为 mybatis plus 查询时可用的 page
     */
    public static <T> Page<T> getPage(Pageable pageable) {
        int pageNumber = pageable.getPageNumber();
        // 因为 mybatis 分页是从第 1 页开始的
        if (pageNumber == 0) {
            pageNumber = 1;
        }
        Page<T> page = new Page<>(pageNumber, pageable.getPageSize());
        List<OrderItem> items = new ArrayList<>();
        Sort sort = pageable.getSort();
        List<Sort.Order> orders = sort.toList();
        for (Sort.Order order : orders) {
            String property = order.getProperty();
            if (StringUtils.isEmpty(property)) {
                continue;
            }
            OrderItem item;
            if (order.getDirection().isAscending()) {
                item = OrderItem.asc(property);
            } else {
                item = OrderItem.desc(property);
            }
            items.add(item);
        }
        page.addOrder(items);
        return page;
    }

    public static HttpHeaders generatePaginationHttpHeaders(IPage page, String baseUrl) {

        HttpHeaders headers = new HttpHeaders();
        if (page == null) {
            return headers;
        }

        headers.add("X-Total-Count", Long.toString(page.getTotal()));
        String link = "";
        if ((page.getCurrent() + 1) < page.getPages()) {
            link = "<" + generateUri(baseUrl, page.getCurrent() + 1, page.getSize()) + ">; rel=\"next\",";
        }
        // prev link
        if ((page.getCurrent()) > 0) {
            link += "<" + generateUri(baseUrl, page.getCurrent() - 1, page.getSize()) + ">; rel=\"prev\",";
        }
        // last and first link
        long lastPage = 0;
        if (page.getTotal() > 0) {
            lastPage = page.getTotal() - 1;
        }
        link += "<" + generateUri(baseUrl, lastPage, page.getSize()) + ">; rel=\"last\",";
        link += "<" + generateUri(baseUrl, 0, page.getSize()) + ">; rel=\"first\"";
        headers.add(HttpHeaders.LINK, link);
        return headers;
    }

    private static String generateUri(String baseUrl, long page, long size) {
        return UriComponentsBuilder.fromUriString(baseUrl).queryParam("page", page).queryParam("size", size).toUriString();
    }

    public static HttpHeaders generateSearchPaginationHttpHeaders(String query, Page page, String baseUrl) {
        String escapedQuery;
        try {
            escapedQuery = URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", Long.toString(page.getTotal()));
        String link = "";
        if ((page.getCurrent() + 1) < page.getTotal()) {
            link = "<" + generateUri(baseUrl, page.getCurrent() + 1, page.getSize()) + "&query=" + escapedQuery + ">; rel=\"next\",";
        }
        // prev link
        if ((page.getCurrent()) > 0) {
            link += "<" + generateUri(baseUrl, page.getCurrent() - 1, page.getSize()) + "&query=" + escapedQuery + ">; rel=\"prev\",";
        }
        // last and first link
        long lastPage = 0;
        if (page.getTotal() > 0) {
            lastPage = page.getTotal() - 1;
        }
        link += "<" + generateUri(baseUrl, lastPage, page.getSize()) + "&query=" + escapedQuery + ">; rel=\"last\",";
        link += "<" + generateUri(baseUrl, 0, page.getSize()) + "&query=" + escapedQuery + ">; rel=\"first\"";
        headers.add(HttpHeaders.LINK, link);
        return headers;
    }
}
