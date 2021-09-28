package com.yk.es.normal;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2021/09/14 16:01:45
 */
public class Page<T>
{
    private List<T> dataList = new ArrayList<>();

    private int startRow;

    private int currentPage = 1;

    private int pageSize = 10;

    private int totalRows;

    private int totalPage;

    public int getTotalPage()
    {
        return this.totalPage;
    }

    public int getStartRow()
    {
        return this.startRow;
    }

    public List<T> getDataList()
    {
        return dataList;
    }

    public void setDataList(List<T> dataList)
    {
        this.dataList = dataList;
    }


    public int getCurrentPage()
    {
        return this.currentPage;
    }

    public int getPageSize()
    {
        return pageSize;
    }

    public int getTotalRows()
    {
        return totalRows;
    }

    public Page(int totalRows, int currentPage, int pageSize)
    {
        this.totalRows = totalRows;
        this.currentPage = currentPage;
        this.pageSize = pageSize;

        if (this.totalRows % this.pageSize == 0)
        {
            this.totalPage = this.totalRows / this.pageSize;
        }
        else
        {
            this.totalPage = this.totalRows / this.pageSize + 1;
        }

        if (currentPage < 0)
        {
            this.currentPage = 1;
        }
        else if (currentPage > this.totalPage)
        {
            this.currentPage = this.totalPage;
        }

        this.startRow = this.pageSize * (this.currentPage - 1);
    }
}
