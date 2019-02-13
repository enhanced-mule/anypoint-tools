package org.enhancedmule.anypoint.tools.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kloudtek.util.URLBuilder;
import org.enhancedmule.anypoint.tools.AnypointObject;
import org.enhancedmule.anypoint.tools.HttpException;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class PaginatedList<X, Z extends AnypointObject> implements Iterable<X> {
    protected Z parent;
    protected int limit = 100;
    protected int offset = 0;
    protected int total = -1;
    protected List<X> list;

    public PaginatedList(Z parent) {
        this.parent = parent;
    }

    public PaginatedList(Z parent, int limit) {
        this(parent);
        this.limit = limit;
    }

    @NotNull
    protected abstract URLBuilder buildUrl();

    public void download() throws HttpException {
        String url = buildUrl().param("limit", limit).param("offset", offset).toString();
        String json = parent.getClient().getHttpHelper().httpGet(url);
        JsonHelper jsonHelper = parent.getClient().getJsonHelper();
        parseJson(json, jsonHelper);
    }

    @SuppressWarnings("unchecked")
    protected void parseJson(String json, JsonHelper jsonHelper) {
        jsonHelper.readJson(this, json);
        for (X obj : list) {
            if (obj instanceof AnypointObject<?>) {
                ((AnypointObject) obj).setParent(parent);
            }
        }
    }

    @JsonProperty
    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int size() {
        return list != null ? list.size() : -1;
    }

    public List<X> getAll() {
        List<X> l = new ArrayList<>();
        this.forEach(l::add);
        return l;
    }

    @NotNull
    @Override
    public Iterator<X> iterator() {
        return new PaginatingIterator();
    }

    public class PaginatingIterator implements Iterator<X> {
        private Iterator<X> iterator;

        public PaginatingIterator() {
            iterator = list.iterator();
        }

        @Override
        public boolean hasNext() {
            boolean hasNext = iterator.hasNext();
            if (!hasNext) {
                offset = offset + limit;
                try {
                    download();
                    if (list.isEmpty()) {
                        return false;
                    }
                    iterator = list.iterator();
                } catch (HttpException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
            return true;
        }

        @Override
        public X next() {
            return iterator.next();
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }
}
