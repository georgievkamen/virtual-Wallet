package com.team9.virtualwallet.models;

import org.springframework.data.domain.Pageable;

import java.util.List;

public class Pages<T>  {

    private static final int PAGE_BUTTONS_COUNT = 5;

    private final List<T> content;

    private final long total;

    private final Pageable pageable;

    private int beginIndex;

    private int endIndex;

    public Pages(List<T> content, long total, Pageable pageable) {
        this.content = content;
        this.total = total;
        this.pageable = pageable;
        addPagination();
    }

    public List<T> getContent() {
        return content;
    }

    public long getTotal() {
        return total;
    }

    public Pageable getPageable() {
        return pageable;
    }

    public int getBeginIndex() {
        return beginIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public int getTotalPages() {
        return getSize() == 0 ? 1 : (int) Math.ceil((double) total / (double) getSize());
    }

    public boolean hasNext() {
        return getNumber() + 1 < getTotalPages();
    }

    public boolean isLast() {
        return !hasNext();
    }

    public int getSize() {
        return pageable.isPaged() ? pageable.getPageSize() : content.size();
    }

    public int getNumber() {
        return pageable.isPaged() ? pageable.getPageNumber() : 0;
    }

    private void addPagination() {

        int currentPage = pageable.getPageNumber();
        int lastPage = getTotalPages();

        if (getTotalPages() > 1) {
            if (currentPage + PAGE_BUTTONS_COUNT <= lastPage) {
                beginIndex = Math.max(1, currentPage);
                endIndex = beginIndex + PAGE_BUTTONS_COUNT + 1;
            } else {
                endIndex = lastPage;
                beginIndex = Math.max(1, endIndex - PAGE_BUTTONS_COUNT + 1);
            }
        }
    }
}
