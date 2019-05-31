package com.hujiang.notifycenter.utils.template.core.model.criteria;

import com.hujiang.notifycenter.utils.template.core.model.Criteria;

import lombok.Getter;

import java.util.List;

/**
 * @author yangkai
 * @date 2019-05-30
 * @email yangkai@hujiang.com
 * @description
 */
public class CrmTemplateCriteria implements Criteria<CrmTemplateCriteria> {

    @Getter
    private List<Integer> tids;
    @Getter
    private List<Integer> cids;
    @Getter
    private String appName;
    @Getter
    private String appId;
    @Getter
    private Integer type;

    public CrmTemplateCriteria tids(List<Integer> tids) {
        this.tids = tids;
        return this;
    }

    public CrmTemplateCriteria cids(List<Integer> cids) {
        this.cids = cids;
        return this;
    }

    public CrmTemplateCriteria appName(String appName) {
        this.appName = appName;
        return this;
    }

    public CrmTemplateCriteria appId(String appId) {
        this.appId = appId;
        return this;
    }

    public CrmTemplateCriteria type(Integer type) {
        this.type = type;
        return this;
    }

    @Override
    public CrmTemplateCriteria build() {
        return this;
    }
}
