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
public class QnTemplateCriteria implements Criteria<QnTemplateCriteria> {

    @Getter
    private List<Integer> sids;
    @Getter
    private List<Integer> cids;
    @Getter
    private String description;

    public QnTemplateCriteria sids(List<Integer> sids) {
        this.sids = sids;
        return this;
    }

    public QnTemplateCriteria cids(List<Integer> cids) {
        this.cids = cids;
        return this;
    }

    public QnTemplateCriteria description(String description) {
        this.description = description;
        return this;
    }


    @Override
    public QnTemplateCriteria build() {
        return this;
    }
}
