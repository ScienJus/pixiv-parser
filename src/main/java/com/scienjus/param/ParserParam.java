package com.scienjus.param;

import com.scienjus.callback.WorkCallback;
import com.scienjus.config.PixivParserConfig;
import com.scienjus.filter.WorkFilter;

/**
 * @author XieEnlong
 * @date 2015/12/15.
 */
public class ParserParam {

    private int limit = PixivParserConfig.NO_LIMIT;

    private WorkFilter filter;

    private WorkCallback callback;

    public ParserParam withLimit(int limit) {
        this.limit = limit;
        return this;
    }

    public ParserParam withFilter(WorkFilter filter) {
        this.filter = filter;
        return this;
    }

    public ParserParam withCallback(WorkCallback callback) {
        this.callback = callback;
        return this;
    }

    public int getLimit() {
        return limit;
    }

    public WorkFilter getFilter() {
        return filter;
    }

    public WorkCallback getCallback() {
        return callback;
    }
}
