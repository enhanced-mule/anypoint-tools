package org.enhancedmule.anypoint.tools.exchange;

import com.kloudtek.util.URLBuilder;
import org.enhancedmule.anypoint.tools.HttpException;
import org.enhancedmule.anypoint.tools.Organization;
import org.enhancedmule.anypoint.tools.util.JsonHelper;
import org.enhancedmule.anypoint.tools.util.PaginatedList;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class AssetList extends PaginatedList<ExchangeAssetOverview, Organization> {
    private static final Logger logger = LoggerFactory.getLogger(AssetList.class);
    private final String filter;

    public AssetList(Organization org, String filter) throws HttpException {
        this(org, filter, 50);
    }

    public AssetList(Organization org, String filter, int limit) throws HttpException {
        super(org, limit);
        this.filter = filter;
        download();
    }

    @Override
    protected @NotNull URLBuilder buildUrl() {
        URLBuilder urlBuilder = new URLBuilder("/exchange/api/v1/assets")
                .param("organizationId", parent.getId());
        if (filter != null) {
            urlBuilder.param("search", filter);
        }
        return urlBuilder;
    }

    @Override
    protected void parseJson(String json, JsonHelper jsonHelper) {
        list = jsonHelper.readJsonList(ExchangeAssetOverview.class, json, parent);
    }

    public List<ExchangeAssetOverview> getAssets() {
        return list;
    }

    public void setAssets(List<ExchangeAssetOverview> assetOverviews) {
        list = assetOverviews;
    }

    public void delete() throws HttpException {
        for (ExchangeAssetOverview assetOverview : this) {
            for (AssetVersion assetVersion : assetOverview.getAsset().getVersions()) {
                assetVersion.delete();
            }
        }
    }
}
